import Build_gradle.IOSVersion
import Build_gradle.LinuxVersion
import Build_gradle.MacOSVersion
import Build_gradle.WindowsVersion
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.gms)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

fun readProperties(fileName: String): Properties? {
    val propsFile = project.rootProject.file(fileName)
    if (!propsFile.exists()) {
        return null
    }
    if (!propsFile.canRead()) {
        throw GradleException("Cannot read $fileName")
    }
    return Properties().apply {
        propsFile.inputStream().use { load(it) }
    }
}

inline fun updateProperties(fileName: String, block: Properties.() -> Unit) {
    val propsFile = project.rootProject.file(fileName)
    val props = readProperties(propsFile.name)!!
    block(props)
    propsFile.outputStream().use {
        val date = LocalDateTime.now()
        props.store(it, "Updated at $date")
    }
}

open class PlatformVersion(
    open val versionName: String
)

data class PlatformVersionWithCode(
    override val versionName: String,
    val versionCode: Int
) : PlatformVersion(versionName)

typealias AndroidVersion = PlatformVersionWithCode
typealias IOSVersion = PlatformVersion
typealias WindowsVersion = PlatformVersion
typealias MacOSVersion = PlatformVersion
typealias LinuxVersion = PlatformVersionWithCode

sealed class Platform<VersionType : PlatformVersion> {
    object Android : Platform<PlatformVersionWithCode>()
    object IOS : Platform<IOSVersion>()
    object Windows : Platform<WindowsVersion>()
    object MacOS : Platform<MacOSVersion>()
    object Linux : Platform<LinuxVersion>()
}

fun <VersionType : PlatformVersion> getVersionForPlatform(platform: Platform<VersionType>?): VersionType {
    val versionProperties = readProperties("version.properties")!!

    fun getAndReplaceVersion(key: String): String {
        val versionName = versionProperties.getProperty("VERSION_NAME")
        return versionProperties.getProperty(key).replace("\$VERSION_NAME", versionName)
    }

    @Suppress("UNCHECKED_CAST")
    return when (platform) {
        Platform.Android -> PlatformVersionWithCode(
            getAndReplaceVersion("VERSION_ANDROID"),
            versionProperties.getProperty("VERSION_ANDROID_CODE").toInt()
        ) as VersionType

        Platform.IOS -> IOSVersion(getAndReplaceVersion("VERSION_NAME")) as VersionType
        Platform.Windows -> WindowsVersion(getAndReplaceVersion("VERSION_WIN")) as VersionType
        Platform.MacOS -> MacOSVersion(getAndReplaceVersion("VERSION_MAC")) as VersionType
        Platform.Linux -> LinuxVersion(
            getAndReplaceVersion("VERSION_LIN"),
            versionProperties.getProperty("VERSION_LIN_RELEASE").toInt()
        ) as VersionType
        else -> PlatformVersion(versionProperties.getProperty("VERSION_NAME")) as VersionType
    }
}

kotlin {
    jvmToolchain(23)

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "webApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
                configDirectory = File(project.rootDir, "webpack.config.d")
            }
        }
        compilerOptions {
            freeCompilerArgs.add("-Xwasm-attach-js-exception")
        }
        binaries.executable()
    }

    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true

            export(libs.kmpnotifier)

            // Room - Required when using NativeSQLiteDriver
            linkerOpts.add("-lsqlite3")
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xadd-light-debug=enable"
        }
    }

    @Suppress("UnusedPrivateProperty")
    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
                optIn("kotlin.uuid.ExperimentalUuidApi")
            }
        }

        commonMain.dependencies {
            // Compose - Base
            implementation(compose.components.resources)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.runtime)

            // Compose - Utilities
            implementation(libs.compose.windowSizeClass)
            implementation(libs.compose.filekit)

            // Compose - Navigation
            implementation(libs.compose.navigation)

            // Compose - View Model
            implementation(libs.compose.viewModel)

            // Compose - Zoomable
            implementation(libs.zoomable)

            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.ktor3)

            // Logging library
            implementation(libs.napier)

            // Ktor client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // KotlinX dependencies
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)

            // KotlinX IO
            implementation(libs.kotlinx.io)

            // Settings storage
            implementation(libs.multiplatformSettings.base)
            implementation(libs.multiplatformSettings.coroutines)
            implementation(libs.multiplatformSettings.makeObservable)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        val platformMain by creating {
            dependsOn(commonMain.get())

            dependencies {
                // Room
                implementation(libs.room.bundledSqlite)
                implementation(libs.room.runtime)
            }
        }

        val mobileMain by creating {
            dependsOn(platformMain)

            dependencies {
                // Push Notifications
                implementation(libs.kmpnotifier)
            }
        }

        val androidMain by getting {
            dependsOn(mobileMain)

            dependencies {
                implementation(libs.androidx.activity.compose)

                implementation(libs.compose.ui)
                implementation(libs.compose.ui.tooling.preview)

                // Compose - Maps
                implementation(libs.compose.maps.base)
                implementation(libs.compose.maps.utils)

                // Ktor client
                implementation(libs.ktor.client.android)

                // KotlinX coroutines
                implementation(libs.kotlinx.coroutines.android)

                // Instant Apps Support
                implementation(libs.play.instantapps)

                // App Update Check
                implementation(libs.play.appupdate)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(mobileMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                // Ktor client
                implementation(libs.ktor.client.darwin)

                // KmpIO (only used for zip)
                implementation(libs.kmpio)

                // XML Parsing
                implementation(libs.ksoup)
            }
        }

        val desktopMain by getting {
            dependsOn(platformMain)

            dependencies {
                implementation(compose.desktop.currentOs)

                // Ktor client
                implementation(libs.ktor.client.java)

                // XML Parsing
                implementation(libs.ksoup)

                // Mapbox SDK
                implementation(libs.mapbox.core)
                implementation(libs.mapbox.services)

                // Semantic Versioning
                implementation(libs.semver)
            }
        }

        wasmJsMain {
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.room.ktx)
    debugImplementation(compose.uiTooling)

    // Room Compilers
    add("kspCommonMainMetadata", libs.room.compiler)

    add("kspAndroid", libs.room.compiler)

    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)

    add("kspDesktop", libs.room.compiler)
}

android {
    namespace = "org.escalaralcoiaicomtat.android"

    compileSdk = 35

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.escalaralcoiaicomtat.android"
        minSdk = 24
        targetSdk = 35

        val version = getVersionForPlatform(Platform.Android)

        versionName = version.versionName
        versionNameSuffix = "_instant"
        versionCode = version.versionCode

        val localProperties = readProperties("local.properties")
        val mapsApiKey = localProperties?.getProperty("MAPS_API_KEY") ?: System.getenv("MAPS_API_KEY")
        if (mapsApiKey == null) System.err.println("WARNING! Missing MAPS_API_KEY")
        resValue("string", "maps_api_key", mapsApiKey ?: "")
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = System.getenv("KEYSTORE_ALIAS")
            keyPassword = System.getenv("KEYSTORE_ALIAS_PASSWORD")

            storeFile = File(rootDir, "keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_23
        targetCompatibility = JavaVersion.VERSION_23
    }

    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        buildTypes.release.proguard {
            isEnabled = false
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "org.escalaralcoiaicomtat.app"
            packageVersion = getVersionForPlatform<PlatformVersion>(null).versionName

            description = "Escalar Alcoià i Comtat"
            copyright = "© ${
                Calendar.getInstance().get(Calendar.YEAR)
            } Escalar Alcoià i Comtat. All rights reserved."
            vendor = "Escalar Alcoià i Comtat"

            val iconsDir = File(rootDir, "icons")

            windows {
                iconFile.set(
                    File(iconsDir, "icon.ico")
                )
                dirChooser = true
                perUserInstall = true
                menuGroup = "Escalar Alcoia i Comtat"
                packageName = "Escalar Alcoia i Comtat"
                upgradeUuid = "1193b0ce-a276-42cc-b38a-f066b5cfe81e"
                val version = getVersionForPlatform(Platform.Windows)
                msiPackageVersion = version.versionName
                exePackageVersion = version.versionName
            }
            linux {
                iconFile.set(
                    File(iconsDir, "icon.png")
                )
                debMaintainer = "app.linux@escalaralcoiaicomtat.org"
                menuGroup = "Escalar Alcoià i Comtat"
                appCategory = "Sports"
                val version = getVersionForPlatform(Platform.Linux)
                appRelease = version.versionCode.toString()
                debPackageVersion = version.versionName
                rpmPackageVersion = version.versionName
            }
            macOS {
                iconFile.set(
                    File(iconsDir, "icon.icns")
                )
                bundleID = "org.escalaralcoiaicomtat.app"
                dockName = "Escalar Alcoià i Comtat"
                appStore = true
                appCategory = "public.app-category.sports"
                val version = getVersionForPlatform(Platform.MacOS)
                dmgPackageVersion = version.versionName
                pkgPackageVersion = version.versionName
                packageBuildVersion = version.versionName
                dmgPackageBuildVersion = version.versionName
                pkgPackageBuildVersion = version.versionName
            }
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

buildkonfig {
    packageName = "build"

    val localProperties = readProperties("local.properties")

    defaultConfigs {
        buildConfigField(STRING, "BASE_URL", System.getenv("BASE_URL"), nullable = true)
        buildConfigField(STRING, "MAPBOX_ACCESS_TOKEN", null, nullable = true)
        buildConfigField(BOOLEAN, "FILE_BASED_CACHE", "false")

        val defaultVersion = getVersionForPlatform<PlatformVersion>(null)
        buildConfigField(STRING, "VERSION_NAME", defaultVersion.versionName)
        buildConfigField(INT, "VERSION_CODE", null, nullable = true)

        val date = object {
            val now = Calendar.getInstance()
            val year = now.get(Calendar.YEAR)
            val month = now.get(Calendar.MONTH).plus(1).toString().padStart(2, '0')
            val day = now.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
            val hour = now.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
            val minute = now.get(Calendar.MINUTE).toString().padStart(2, '0')
            val second = now.get(Calendar.SECOND).toString().padStart(2, '0')
        }
        buildConfigField(
            STRING,
            "BUILD_DATE",
            "${date.year}/${date.month}/${date.day} ${date.hour}:${date.minute}:${date.second}"
        )
    }

    targetConfigs {
        create("android") {
            val version = getVersionForPlatform(Platform.Android)
            buildConfigField(STRING, "VERSION_NAME", version.versionName)
            buildConfigField(INT, "VERSION_CODE", version.versionCode.toString(), nullable = true)
        }
        create("ios") {
            val version = getVersionForPlatform(Platform.IOS)
            buildConfigField(STRING, "VERSION_NAME", version.versionName)
        }

        create("desktop") {
            buildConfigField(
                STRING,
                "MAPBOX_ACCESS_TOKEN",
                localProperties?.getProperty("MAPBOX_ACCESS_TOKEN") ?: System.getenv("MAPBOX_ACCESS_TOKEN"),
                nullable = true
            )
            buildConfigField(BOOLEAN, "FILE_BASED_CACHE", "true")
        }
        create("macos") {
            val version = getVersionForPlatform(Platform.MacOS)
            buildConfigField(STRING, "VERSION_NAME", version.versionName)
        }
        create("linux") {
            val version = getVersionForPlatform(Platform.Linux)
            buildConfigField(STRING, "VERSION_NAME", version.versionName)
            buildConfigField(INT, "VERSION_CODE", version.versionCode.toString(), nullable = true)
        }
        create("mingw") {
            val version = getVersionForPlatform(Platform.Windows)
            buildConfigField(STRING, "VERSION_NAME", version.versionName)
        }
    }
}

// Disable the warning for expect/actual classes
tasks.withType(KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

fun increaseNumberInProperties(key: String) {
    var code = 0
    updateProperties("version.properties") {
        code = getProperty(key).toInt() + 1
        setProperty(key, code.toString())
    }

    println("Increased $key to $code")
}

fun updatePListFile(key: String, value: String) {
    val iosAppRoot = project.rootProject.file("iosApp")
    val iosApp = File(iosAppRoot, "iosApp")
    val infoFile = File(iosApp, "Info.plist")
    val info = infoFile.readLines()
    val keyLineNumber = info.indexOfFirst { it.contains("<key>$key</key>") }
    if (keyLineNumber == -1) {
        System.err.println("Key $key not found in Info.plist file")
        return
    }
    val valueLine = info[keyLineNumber + 1]
    val regex = "([ \\t]*)<(.+)>(.*)<\\/.+>".toRegex()
    regex.find(valueLine)?.let {
        val (indent, type, _) = it.destructured
        val newValueLine = "$indent<$type>$value</$type>"
        val newInfo = info.toMutableList()
        newInfo[keyLineNumber + 1] = newValueLine
        infoFile.writeText(newInfo.joinToString("\n"))
    }
}

fun updateXCodeProjectOption(key: String, value: String) {
    val iosAppRoot = project.rootProject.file("iosApp")
    val iosApp = File(iosAppRoot, "iosApp.xcodeproj")
    val projectFile = File(iosApp, "project.pbxproj")
    if (!projectFile.exists()) {
        System.err.println("project.pbxproj file not found")
        return
    }
    val project = projectFile.readLines().map { line ->
        if (line.contains("$key =")) {
            val regex = "([ \\t]*)$key = (.+);".toRegex()
            regex.find(line)?.let {
                val (indent, _) = it.destructured
                "$indent$key = $value;"
            }
        } else {
            line
        }
    }
    projectFile.delete()
    projectFile.createNewFile()
    projectFile.writeText(project.joinToString("\n"))
}

val increaseVersionCode = tasks.register("increaseVersionCode") {
    doFirst {
        increaseNumberInProperties("VERSION_ANDROID_CODE")
    }
}
val increaseLinuxRelease = tasks.register("increaseLinuxRelease") {
    doFirst {
        increaseNumberInProperties("VERSION_LIN_RELEASE")
    }
}
val updateIOSVersion = tasks.register("updateIOSVersion") {
    doFirst {
        properties["version"]?.toString()?.let {
            updatePListFile("CFBundleShortVersionString", it)
            updateXCodeProjectOption("MARKETING_VERSION", it)
        }
        properties["code"]?.toString()?.let {
            updatePListFile("CFBundleVersion", it)
            updateXCodeProjectOption("CURRENT_PROJECT_VERSION", it)
        }
    }
}
val updateVersionName = tasks.register("updateVersionName") {
    doFirst {
        val version = properties.getValue("version").toString()
        updateProperties("version.properties") {
            // Store the full version name for general
            setProperty("VERSION_NAME", version)
            // Android, iOS and Linux support suffixes (such as -beta01)
            setProperty("VERSION_ANDROID", version)
            setProperty("VERSION_IOS", version)
            setProperty("VERSION_LIN", version)
            // Windows and MacOS do not support suffixes
            val stripped = version.substringBefore('-')
            setProperty("VERSION_WIN", stripped)
            setProperty("VERSION_MAC", stripped)
        }
    }
}

tasks.findByName("bundleRelease")?.dependsOn?.add(increaseVersionCode)
tasks.findByName("packageDeb")?.dependsOn?.add(increaseLinuxRelease)

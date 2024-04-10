import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.gradle.TargetConfigDsl
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

fun readProperties(fileName: String): Properties {
    val propsFile = project.rootProject.file(fileName)
    if (!propsFile.exists()) {
        throw GradleException("$fileName doesn't exist")
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
    val props = readProperties(propsFile.name)
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
    val versionProperties = readProperties("version.properties")

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
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    targets.all {
        compilations.all {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }
    targets.withType<KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xadd-light-debug=enable"
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
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

            // Compose - Navigation
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.transitions)

            // Compose - Zoomable
            implementation(libs.zoomable)

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

            // Okio
            implementation(libs.okio)

            // SQLDelight
            implementation(libs.sqldelight.coroutines)

            // Settings storage
            implementation(libs.multiplatformSettings.base)
            implementation(libs.multiplatformSettings.coroutines)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        val androidMain by getting {
            dependsOn(commonMain.get())

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

                // SQLDelight
                implementation(libs.sqldelight.driver.android)

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
            dependsOn(commonMain.get())
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                // Ktor client
                implementation(libs.ktor.client.darwin)

                // SQLDelight
                implementation(libs.sqldelight.driver.native)

                // KmpIO (only used for zip)
                implementation(libs.kmpio)

                // XML Parsing
                implementation(libs.ksoup)
            }
        }

        val desktopMain by getting {
            dependsOn(commonMain.get())

            dependencies {
                implementation(compose.desktop.currentOs)

                // Ktor client
                implementation(libs.ktor.client.java)

                // SQLDelight
                implementation(libs.sqldelight.driver.sqlite)

                // XML Parsing
                implementation(libs.ksoup)

                // Mapbox SDK
                implementation(libs.mapbox.core)
                implementation(libs.mapbox.services)

                // Semantic Versioning
                implementation(libs.semver)
            }
        }
    }
}

android {
    namespace = "org.escalaralcoiaicomtat.android"

    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.escalaralcoiaicomtat.android"
        minSdk = 24
        targetSdk = 34

        val version = getVersionForPlatform(Platform.Android)

        versionName = version.versionName
        versionNameSuffix = "_instant"
        versionCode = version.versionCode

        val localProperties = readProperties("local.properties")
        resValue("string", "maps_api_key", localProperties.getProperty("MAPS_API_KEY"))
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

sqldelight {
    databases {
        create("Database") {
            packageName.set("database")
        }
    }
}

buildkonfig {
    packageName = "build"

    val localProperties = readProperties("local.properties")

    defaultConfigs {
        buildConfigField(STRING, "MAPBOX_ACCESS_TOKEN", null, nullable = true)
        buildConfigField(STRING, "GITHUB_TOKEN", localProperties.getProperty("GITHUB_TOKEN"))

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
            val version = getVersionForPlatform(Platform.Android)
            buildConfigField(STRING, "VERSION_NAME", version.versionName)
        }

        fun TargetConfigDsl.commonDesktop() {
            buildConfigField(STRING, "MAPBOX_ACCESS_TOKEN", localProperties.getProperty("MAPBOX_ACCESS_TOKEN"))
        }
        create("macos") {
            commonDesktop()
            val version = getVersionForPlatform(Platform.MacOS)
            buildConfigField(STRING, "VERSION_NAME", version.versionName)
        }
        create("linux") {
            commonDesktop()
            val version = getVersionForPlatform(Platform.Linux)
            buildConfigField(STRING, "VERSION_NAME", version.versionName)
            buildConfigField(INT, "VERSION_CODE", version.versionCode.toString(), nullable = true)
        }
        create("mingw") {
            commonDesktop()
            val version = getVersionForPlatform(Platform.Windows)
            buildConfigField(STRING, "VERSION_NAME", version.versionName)
        }
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

val increaseVersionCode = task("increaseVersionCode") {
    doFirst {
        increaseNumberInProperties("VERSION_ANDROID_CODE")
    }
}
val increaseLinuxRelease = task("increaseLinuxRelease") {
    doFirst {
        increaseNumberInProperties("VERSION_LIN_RELEASE")
    }
}

tasks.findByName("bundleRelease")?.dependsOn?.add(increaseVersionCode)
tasks.findByName("packageDeb")?.dependsOn?.add(increaseLinuxRelease)

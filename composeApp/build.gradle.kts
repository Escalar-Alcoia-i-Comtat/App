import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
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
    alias(libs.plugins.sentry.android)
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

val versionProperties = readProperties("version.properties")!!

val appVersionName: String = versionProperties.getProperty("VERSION_NAME")
val appVersionCode: String = versionProperties.getProperty("VERSION_CODE")

kotlin {
    jvmToolchain(21)

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "escalaralcoiaicomtat"
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
            }
        }
        compilerOptions {
            // Doesn't seem to be supported
            // freeCompilerArgs.add("-Xwasm-attach-js-exception")

            freeCompilerArgs.add("-Xwasm-use-new-exception-proposal")
            // freeCompilerArgs.add("-Xwasm-generate-dwarf")
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

            // export(libs.kmpnotifier)

            // Room - Required when using NativeSQLiteDriver
            linkerOpts += "-lsqlite3"
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xadd-light-debug=enable"
        }
    }

    @Suppress("UnusedPrivateProperty", "unused")
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlin.uuid.ExperimentalUuidApi")
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }

        commonMain.dependencies {
            // Compose - Base
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.runtime)

            // Compose - Utilities
            implementation(libs.compose.windowSizeClass)
            implementation(libs.compose.filekit)

            // Compose - Markdown
            implementation(libs.compose.markdown.coil3)
            implementation(libs.compose.markdown.m3)

            // Compose - Navigation
            implementation(libs.compose.navigation)

            // Compose - Rich Text Editor
            implementation(libs.compose.richeditor)

            // Compose - View Model
            implementation(libs.compose.viewModel)

            // Compose - Zoom Image
            implementation(libs.zoomimage.coil3)

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
            implementation(libs.kotlinx.coroutines.test)
        }

        // Desktop & mobile
        val platformMain by creating {
            dependsOn(commonMain.get())

            dependencies {
                // Room
                implementation(libs.room.bundledSqlite)
                implementation(libs.room.runtime)

                // Sentry for Kotlin Multiplatform is not available for WASM
                // implementation(libs.sentry)
            }
        }

        // Desktop & Android
        val jvmMain by creating {
            dependsOn(platformMain)
        }

        // Desktop & Web
        val largeFormatMain by creating {
            dependsOn(commonMain.get())

            dependencies {
                implementation(libs.compose.mapCompose)

                // XML Parsing
                implementation(libs.ksoup)
            }
        }

        val mobileMain by creating {
            dependsOn(platformMain)

            dependencies {
                // Push Notifications
                // api(libs.kmpnotifier)
            }
        }

        val androidMain by getting {
            dependsOn(mobileMain)
            dependsOn(jvmMain)

            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)

                implementation(libs.compose.ui)
                implementation(libs.compose.ui.tooling.preview)

                // Compose - Maps
                implementation(libs.compose.googleMaps.base)
                implementation(libs.compose.googleMaps.utils)

                // Ktor client
                implementation(libs.ktor.client.android)

                // KotlinX coroutines
                implementation(libs.kotlinx.coroutines.android)

                // WorkManager
                implementation(libs.androidx.work.runtime)

                // App Update Check
                implementation(libs.play.appupdate)

                implementation(libs.sentry)

                // Firebase (analytics+push)
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.analytics)
                implementation(libs.firebase.messaging)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.androidx.test.runner)

                implementation(libs.mockk.android)
                implementation(libs.mockk.agent)
                implementation(libs.mockk.core)
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.androidx.test.runner)

                implementation(libs.mockk.android)
                implementation(libs.mockk.agent)
                implementation(libs.mockk.core)
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

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest.get())
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }

        val desktopMain by getting {
            dependsOn(jvmMain)
            dependsOn(largeFormatMain)

            dependencies {
                implementation(compose.desktop.currentOs)

                // Ktor client
                implementation(libs.ktor.client.cio)

                // Mapbox SDK
                implementation(libs.mapbox.core)
                implementation(libs.mapbox.services)

                // Semantic Versioning
                implementation(libs.semver)

                implementation(libs.sentry)
            }
        }

        wasmJsMain {
            dependsOn(largeFormatMain)

            dependencies {
                implementation(libs.kotlinx.browser)

                implementation(npm("jszip", "3.10.1"))
            }
            languageSettings {
                optIn("kotlin.js.ExperimentalWasmJsInterop")
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
    compileSdk = 36

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.escalaralcoiaicomtat.android"
        minSdk = 24
        targetSdk = 36

        versionName = appVersionName
        versionCode = appVersionCode.toInt()

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    @Suppress("UnstableApiUsage")
    androidResources {
        generateLocaleConfig = true
    }

    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "org.escalaralcoiaicomtat.app.MainKt"

        buildTypes.release.proguard {
            isEnabled = false
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)

            packageName = "org.escalaralcoiaicomtat.app"
            packageVersion = appVersionName

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
                msiPackageVersion = appVersionName
                exePackageVersion = appVersionName
            }
            linux {
                iconFile.set(
                    File(iconsDir, "icon.png")
                )
                debMaintainer = "app.linux@escalaralcoiaicomtat.org"
                menuGroup = "Escalar Alcoià i Comtat"
                appCategory = "Sports"
                appRelease = appVersionCode
                debPackageVersion = appVersionName
                rpmPackageVersion = appVersionName
            }
            macOS {
                iconFile.set(
                    File(iconsDir, "icon.icns")
                )
                bundleID = "org.escalaralcoiaicomtat.app"
                dockName = "Escalar Alcoià i Comtat"
                appStore = true
                appCategory = "public.app-category.sports"
                dmgPackageVersion = appVersionName
                pkgPackageVersion = appVersionName
                packageBuildVersion = appVersionName
                dmgPackageBuildVersion = appVersionName
                pkgPackageBuildVersion = appVersionName
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
        buildConfigField(STRING, "MAPBOX_ACCESS_TOKEN", localProperties?.getProperty("MAPBOX_ACCESS_TOKEN") ?: System.getenv("MAPBOX_ACCESS_TOKEN"), nullable = true)
        buildConfigField(STRING, "SENTRY_DSN", localProperties?.getProperty("SENTRY_DSN") ?: System.getenv("SENTRY_DSN"), nullable = true)

        buildConfigField(STRING, "VERSION_NAME", appVersionName)
        buildConfigField(INT, "VERSION_CODE", appVersionCode)

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
}

// Prevent Sentry dependencies from being included in the Android app through the AGP.
sentry {
    autoInstallation {
        enabled.set(false)
    }
}

// Disable the warning for expect/actual classes
tasks.withType(KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

tasks.matching {
    it.name == "kspDebugKotlinAndroid" || it.name == "kspReleaseKotlinAndroid"
}.configureEach {
    dependsOn(
        "generateResourceAccessorsForAndroidDebug",
        "generateResourceAccessorsForAndroidMain",
        "generateActualResourceCollectorsForAndroidMain",
        "generateResourceAccessorsForMobileMain",
        "generateResourceAccessorsForPlatformMain",
        "generateComposeResClass",
        "generateResourceAccessorsForCommonMain",
        "generateExpectResourceCollectorsForCommonMain",
        "generateBuildKonfig",
        "generateResourceAccessorsForJvmMain"
    )
}

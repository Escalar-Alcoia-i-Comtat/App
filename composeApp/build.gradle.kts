import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.time.LocalDateTime
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.moko)
    alias(libs.plugins.sqldelight)
}

fun readProperties(fileName: String): Properties {
    val propsFile = project.rootProject.file(fileName)
    if (!propsFile.canRead()) {
        throw GradleException("Cannot read $fileName")
    }
    return Properties().apply {
        propsFile.inputStream().use { load(it) }
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

            export(libs.moko.base)
            export(libs.moko.graphics) // toUIColor here
        }
    }

    targets.withType<KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += "-Xadd-light-debug=enable"
            freeCompilerArgs += "-Xexpect-actual-classes"
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // Compose - Base
            @OptIn(ExperimentalComposeLibrary::class)
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

            // Compose - Resources
            api(libs.moko.base)
            api(libs.moko.compose)

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
            implementation(libs.multiplatformSettings)
        }

        commonTest.dependencies {
            implementation(libs.moko.test)
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
            }
        }
    }
}

android {
    namespace = "org.escalaralcoiaicomtat.android"

    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.escalaralcoiaicomtat.android"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionName = "2.1.0-dev01"
        versionNameSuffix = "_instant"

        val versionProperties = readProperties("version.properties")
        versionCode = versionProperties.getProperty("VERSION_CODE").toInt()

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

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.escalaralcoiaicomtat.app"
            packageVersion = "1.0.0"
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

multiplatformResources {
    multiplatformResourcesPackage = "resources"
}

val increaseVersionCode = task("increaseVersionCode") {
    doFirst {
        val versionPropsFile = project.rootProject.file("version.properties")
        val versionProps = readProperties(versionPropsFile.name)
        val code = versionProps.getProperty("VERSION_CODE").toInt() + 1
        versionProps["VERSION_CODE"] = code.toString()
        versionPropsFile.outputStream().use {
            val date = LocalDateTime.now()
            versionProps.store(it, "Updated at $date")
        }
        println("Increased version code to $code")
    }
}

tasks.findByName("bundleRelease")?.dependsOn?.add(increaseVersionCode)

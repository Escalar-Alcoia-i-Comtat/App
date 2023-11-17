import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.moko)
    alias(libs.plugins.sqldelight)
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
            implementation(libs.voyager.transitions)

            // Compose - Resources
            api(libs.moko.base)
            api(libs.moko.compose)

            // Logging library
            implementation(libs.napier)

            // Ktor client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // KotlinX dependencies
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            // SQLDelight
            implementation(libs.sqldelight.coroutines)
        }

        commonTest.dependencies {
            implementation(libs.moko.test)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)

            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)

            // Ktor client
            implementation(libs.ktor.client.okhttp)

            // KotlinX coroutines
            implementation(libs.kotlinx.coroutines.android)

            // SQLDelight
            implementation(libs.sqldelight.driver.android)
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
            }
        }

        val desktopMain by getting {
            dependsOn(commonMain.get())

            dependencies {
                implementation(compose.desktop.currentOs)

                // Ktor client
                implementation(libs.ktor.client.okhttp)

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
        applicationId = "org.escalaralcoiaicomtat.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "2.1.0-dev01"
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

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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

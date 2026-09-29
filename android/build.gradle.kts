import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.gms)
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

android {
    namespace = "org.escalaralcoiaicomtat.android"
    compileSdk = 37

    defaultConfig {
        applicationId = "org.escalaralcoiaicomtat.android"
        minSdk = 24
        targetSdk = 37

        versionName = appVersionName
        versionCode = appVersionCode.toInt()

        val localProperties = readProperties("local.properties")
        val mapsApiKey = localProperties?.getProperty("MAPS_API_KEY") ?: System.getenv("MAPS_API_KEY")
        if (mapsApiKey == null) System.err.println("WARNING! Missing MAPS_API_KEY")
        resValue("string", "maps_api_key", mapsApiKey ?: "")
    }
    buildFeatures {
        resValues = true
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
}

dependencies {
    implementation(projects.composeApp)

    debugImplementation(libs.compose.ui.tooling)
}

// Prevent Sentry dependencies from being included in the Android app through the AGP.
sentry {
    autoInstallation {
        enabled.set(false)
    }
}

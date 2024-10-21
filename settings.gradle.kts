rootProject.name = "EscalarAlcoiaiComtat"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")

        // Zoomable Snapshot Builds
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

include(":composeApp")
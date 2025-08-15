# Requirements

## General

To use the app, you **must** add the following parameters to the `local.properties` file:
```properties
SENTRY_DSN=https://abc@def.ingest.us.sentry.io/123
MAPS_API_KEY=AIza...
MAPBOX_ACCESS_TOKEN=pk.ey...
```

## Android

A `google-services.json` file must be obtained and added to `composeApp`.

# Building

## Android

Compile APK and AAB:
```shell
./gradlew :composeApp:bundleRelease :composeApp:assembleRelease
```

## Desktop

Build version for current OS:
```shell
./gradlew :composeApp:packageReleaseDistributionForCurrentOS
```

## WASM (Web)

Build distribution:
```shell
./gradlew :composeApp:wasmJsBrowserDistribution
```

# Translation contributions

Translate the app to your own language through [Weblate](https://hosted.weblate.org/engage/escalar-alcoia-i-comtat/).

[![Estat de la traducció](https://hosted.weblate.org/widget/escalar-alcoia-i-comtat/multi-auto.svg)](https://hosted.weblate.org/engage/escalar-alcoia-i-comtat/)

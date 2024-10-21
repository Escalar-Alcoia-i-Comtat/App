FROM ghcr.io/alvr/alpine-android:android-35-jdk21 AS builder

WORKDIR /app
COPY . /app

# Maps not necessary on web, just set empty string
ENV MAPS_API_KEY=''

RUN ./gradlew wasmJsBrowserDistribution --no-daemon --no-build-cache

FROM httpd:2.4-alpine

COPY --from=builder /app/composeApp/build/dist/wasmJs/productionExecutable /usr/local/apache2/htdocs/

# Stage 1: Cache Gradle dependencies
FROM gradle:latest AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY build.gradle.kts gradle.properties /home/gradle/app/
COPY settings.gradle.kts gradle.properties /home/gradle/app/

RUN mkdir -p /home/gradle/app/gradle
COPY gradle/libs.versions.toml /home/gradle/app/gradle/

RUN mkdir -p /home/gradle/app/composeApp
COPY composeApp/build.gradle.kts /home/gradle/app/composeApp/

WORKDIR /home/gradle/app
RUN gradle clean kotlinUpgradeYarnLock build -i --stacktrace

# Stage 2: Build Application
FROM gradle:latest AS build
ARG BASE_URL

ENV BASE_URL=$BASE_URL

COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /usr/src/app/
WORKDIR /usr/src/app
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Build the distribution
RUN gradle :composeApp:wasmJsBrowserDistribution --no-daemon

# Stage 3: Create the Runtime Image
FROM httpd:2.4-alpine

COPY ./httpd.conf /usr/local/apache2/conf/httpd.conf
COPY --from=build /home/gradle/src/composeApp/build/dist/wasmJs/productionExecutable/ /usr/local/apache2/htdocs/

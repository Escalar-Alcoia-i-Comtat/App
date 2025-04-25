# Build Application
FROM eclipse-temurin:23-alpine AS build

COPY . /usr/src/app/
WORKDIR /usr/src/app

# Build the distribution
RUN ./gradlew :composeApp:wasmJsBrowserDistribution --stacktrace --no-daemon

# Create the Runtime Image
FROM httpd:2.4-alpine

COPY ./httpd.conf /usr/local/apache2/conf/httpd.conf
COPY --from=build /usr/src/app/composeApp/build/dist/wasmJs/productionExecutable/ /usr/local/apache2/htdocs/

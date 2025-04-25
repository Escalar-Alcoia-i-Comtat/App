# Create the Runtime Image
FROM httpd:2.4-alpine

COPY ./httpd.conf /usr/local/apache2/conf/httpd.conf
COPY ./composeApp/build/dist/wasmJs/productionExecutable/ /usr/local/apache2/htdocs/

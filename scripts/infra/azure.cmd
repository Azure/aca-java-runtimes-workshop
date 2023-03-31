set "PROJECT=java-runtimes"
set "RESOURCE_GROUP=rg-${PROJECT}"
set "LOCATION=eastus"
set "TAG=java-runtimes"

set "LOG_ANALYTICS_WORKSPACE=logs-java-runtimes"
set "CONTAINERAPPS_ENVIRONMENT=env-java-runtimes"

REM If you're using a dev container, you should manually set this to
REM a unique value (like your name) to avoid conflicts with other users.
set "UNIQUE_IDENTIFIER=%USERNAME%"
set "REGISTRY=javaruntimesregistry${UNIQUE_IDENTIFIER}"
set "IMAGES_TAG=1.0"

set "POSTGRES_DB_ADMIN=javaruntimesadmin"
set "POSTGRES_DB_PWD=java-runtimes-p#ssw0rd-12046"
set "POSTGRES_DB_VERSION=14"
set "POSTGRES_SKU=Standard_B1ms"
set "POSTGRES_TIER=Burstable"
set "POSTGRES_DB=db-stats-${UNIQUE_IDENTIFIER}"
set "POSTGRES_DB_SCHEMA=stats"
set "POSTGRES_DB_CONNECT_STRING=jdbc:postgresql://${POSTGRES_DB}.postgres.database.azure.com:5432/${POSTGRES_DB_SCHEMA}?ssl=true&sslmode=require"

set "QUARKUS_APP=quarkus-app"
set "MICRONAUT_APP=micronaut-app"
set "SPRING_APP=springboot-app"
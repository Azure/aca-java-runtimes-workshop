#!/usr/bin/env bash
##############################################################################
# Usage: ./deploy.sh
# Build & deploy apps manually (for testing)
# Make sure you have the correct environment variables set. For that, first run: source ./azure.sh env
##############################################################################
# Dependencies: Azure CLI, jq
##############################################################################

set -e
cd $(dirname ${BASH_SOURCE[0]})
cd ../..

echo "Retrieving credentials..."

# tag::adocRegistryCredentials[]
REGISTRY_USERNAME=$(
  az acr credential show \
    --name "$REGISTRY" \
    --query "username" \
    --output tsv
)
echo "REGISTRY_USERNAME=$REGISTRY_USERNAME"

REGISTRY_PASSWORD=$(
  az acr credential show \
    --name "$REGISTRY" \
    --query "passwords[0].value" \
    --output tsv
)
echo "REGISTRY_PASSWORD=$REGISTRY_PASSWORD"
# end::adocRegistryCredentials[]

echo "Logging in to Docker registry..."
echo $REGISTRY_PASSWORD | docker login --username $REGISTRY_USERNAME --password-stdin $REGISTRY_URL

echo "Enabling ingress for each container app..."

# tag::adocIngressUpdate[]
az containerapp ingress enable \
  --name "$QUARKUS_APP" \
  --resource-group "$RESOURCE_GROUP" \
  --target-port 8701 \
  --type external

az containerapp ingress enable \
  --name "$MICRONAUT_APP" \
  --resource-group "$RESOURCE_GROUP" \
  --target-port 8702 \
  --type external

az containerapp ingress enable \
  --name "$SPRING_APP" \
  --resource-group "$RESOURCE_GROUP" \
  --target-port 8703 \
  --type external
# end::adocIngressUpdate[]

echo "Building and deploying Quarkus app..."
pushd quarkus-app

./mvnw package

docker build -f src/main/docker/Dockerfile.jvm -t "$REGISTRY_URL/$PROJECT/$QUARKUS_APP:latest" .

echo "Pushing the Quarkus app..."

docker push "$REGISTRY_URL/$PROJECT/$QUARKUS_APP:latest"
              
echo "Updating the Quarkus container app..."

az containerapp update \
  --name "$QUARKUS_APP" \
  --resource-group "$RESOURCE_GROUP" \
  --image "${REGISTRY_URL}/${PROJECT}/${QUARKUS_APP}:latest" \
  --query "properties.configuration.ingress.fqdn" \
  --output tsv

popd

echo "Retrieving the public URLs..."

# tag::adocIngressHosts[]
QUARKUS_HOST=$(
  az containerapp show \
    --name "$QUARKUS_APP" \
    --resource-group "$RESOURCE_GROUP" \
    --query "properties.configuration.ingress.fqdn" \
    --output tsv \
)
echo "QUARKUS_HOST=$QUARKUS_HOST"

MICRONAUT_HOST=$(
  az containerapp show \
    --name "$MICRONAUT_APP" \
    --resource-group "$RESOURCE_GROUP" \
    --query "properties.configuration.ingress.fqdn" \
    --output tsv \
)
echo "MICRONAUT_HOST=$MICRONAUT_HOST"

SPRING_HOST=$(
  az containerapp show \
    --name "$SPRING_APP" \
    --resource-group "$RESOURCE_GROUP" \
    --query "properties.configuration.ingress.fqdn" \
    --output tsv \
)
echo "SPRING_HOST=$SPRING_HOST"
# end::adocIngressHosts[]

echo "Done."

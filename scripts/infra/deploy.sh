#!/usr/bin/env bash
##############################################################################
# Usage: ./deploy.sh
# Build & deploy apps manually (for testing)
##############################################################################
# Dependencies: Azure CLI, GitHub CLI, jq
##############################################################################

set -e
cd $(dirname ${BASH_SOURCE[0]})
cd ../..

echo "Logging in to Docker registry..."
echo $REGISTRY_PASSWORD | docker login --username $REGISTRY_USERNAME --password-stdin $REGISTRY_URL

echo "Building an deploying Quarkus app..."
pushd quarkus-app

./mvnw package

docker build -f src/main/docker/Dockerfile.jvm -t $REGISTRY_URL/$PROJECT/$QUARKUS_APP:latest .

docker push $REGISTRY_URL/$PROJECT/$QUARKUS_APP:latest

az containerapp ingress enable \
  --name ${QUARKUS_APP} \
  --resource-group ${RESOURCE_GROUP} \
  --target-port 8701 \
  --type external
              
az containerapp update \
  --name ${QUARKUS_APP} \
  --resource-group ${RESOURCE_GROUP} \
  --image ${REGISTRY_URL}/${PROJECT}/${QUARKUS_APP}:latest

popd

echo "Done."


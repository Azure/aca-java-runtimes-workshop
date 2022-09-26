#!/usr/bin/env bash
##############################################################################
# Usage: ./azure.sh setup|cleanup
# Setup or cleanup the Azure infrastructure for this project.
##############################################################################
# Dependencies: Azure CLI, GitHub CLI, jq
##############################################################################

set -e
pushd $(dirname ${BASH_SOURCE[0]})

project_name="java-runtimes"
environment="prod"
location="eastus"
resource_group_name=rg-${project_name}

showUsage() {
  script_name="$(basename "$0")"
  echo "Usage: ./$script_name setup|cleanup|env"
  echo "Setup or cleanup the Azure infrastructure for this project."
  echo
  echo "Options:"
  echo "  -s, --skip-login    Skip Azure and GitHub login steps"
  echo
}

setupRepo() {
  echo "Retrieving Azure subscription..."
  subscription_id=$(
    az account show \
      --query id \
      --output tsv \
      --only-show-errors \
    )
  echo "Creating Azure service principal..."
  service_principal=$(
    az ad sp create-for-rbac \
      --name="sp-${project_name}" \
      --role="Contributor" \
      --scopes="/subscriptions/$subscription_id" \
      --sdk-auth \
      --only-show-errors \
    )
  echo "Retrieving GitHub repository URL..."
  remote_repo=$(git config --get remote.origin.url)
  echo "Setting up GitHub repository secrets..."
  gh secret set AZURE_CREDENTIALS -b"$service_principal" -R $remote_repo
  gh secret set REGISTRY_USERNAME -b"$REGISTRY_USERNAME" -R $remote_repo
  gh secret set REGISTRY_PASSWORD -b"$REGISTRY_PASSWORD" -R $remote_repo
}

cleanupRepo() {
  echo "Retrieving GitHub repository URL..."
  remote_repo=$(git config --get remote.origin.url)
  # TODO: delete az sp
  gh secret delete AZURE_CREDENTIALS -R $remote_repo
  gh secret delete REGISTRY_USERNAME -R $remote_repo
  gh secret delete REGISTRY_PASSWORD -R $remote_repo
  echo "Cleanup complete."
}

createInfrastructure() {
  echo "Preparing environment '${environment}' of project '${project_name}'..."

# tag::adocEnvironmentVariables[]
  PROJECT="java-runtimes"
  RESOURCE_GROUP="rg-${PROJECT}"
  LOCATION="eastus"
  TAG="java-runtimes"

  LOG_ANALYTICS_WORKSPACE="logs-java-runtimes"
  CONTAINERAPPS_ENVIRONMENT="env-java-runtimes"

  UNIQUE_IDENTIFIER=$(whoami)
  REGISTRY="javaruntimesregistry${UNIQUE_IDENTIFIER}"
  IMAGES_TAG="1.0"

  POSTGRES_DB_ADMIN="javaruntimesadmin"
  POSTGRES_DB_PWD="java-runtimes-p#ssw0rd-12046"
  POSTGRES_DB_VERSION="13"
  POSTGRES_SKU="Standard_B2s"
  POSTGRES_TIER="Burstable"
  POSTGRES_DB="db-stats-${UNIQUE_IDENTIFIER}"
  POSTGRES_DB_SCHEMA="stats"
  POSTGRES_DB_CONNECT_STRING="postgresql://${POSTGRES_DB}.postgres.database.azure.com:5432/${POSTGRES_SCHEMA}?ssl=true&sslmode=require"

  QUARKUS_APP="quarkus-app"
  MICRONAUT_APP="micronaut-app"
  SPRING_APP="spring-app"
# end::adocEnvironmentVariables[]

# tag::adocResourceGroup[]
  az group create \
    --name "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --tags system="$TAG"
# end::adocResourceGroup[]

  echo "Resource group '$RESOURCE_GROUP' ready."

# tag::adocLogAnalytics[]
  az monitor log-analytics workspace create \
    --resource-group "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --tags system="$TAG" \
    --workspace-name "$LOG_ANALYTICS_WORKSPACE"
# end::adocLogAnalytics[]

# tag::adocLogAnalyticsSecrets[]
  LOG_ANALYTICS_WORKSPACE_CLIENT_ID=$(az monitor log-analytics workspace show  \
    --resource-group "$RESOURCE_GROUP" \
    --workspace-name "$LOG_ANALYTICS_WORKSPACE" \
    --query customerId  \
    --output tsv | tr -d '[:space:]')

  echo "LOG_ANALYTICS_WORKSPACE_CLIENT_ID=$LOG_ANALYTICS_WORKSPACE_CLIENT_ID"

  LOG_ANALYTICS_WORKSPACE_CLIENT_SECRET=$(az monitor log-analytics workspace get-shared-keys \
    --resource-group "$RESOURCE_GROUP" \
    --workspace-name "$LOG_ANALYTICS_WORKSPACE" \
    --query primarySharedKey \
    --output tsv | tr -d '[:space:]')

  echo "LOG_ANALYTICS_WORKSPACE_CLIENT_SECRET=$LOG_ANALYTICS_WORKSPACE_CLIENT_SECRET"
# end::adocLogAnalyticsSecrets[]

# tag::adocRegistry[]
  az acr create \
    --resource-group "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --tags system="$TAG" \
    --name "$REGISTRY" \
    --workspace "$LOG_ANALYTICS_WORKSPACE" \
    --sku Standard \
    --admin-enabled true
# end::adocRegistry[]

# tag::adocRegistryUpdate[]
  az acr update \
    --resource-group "$RESOURCE_GROUP" \
    --name "$REGISTRY" \
    --anonymous-pull-enabled true
# end::adocRegistryUpdate[]

# tag::adocRegistryShow[]
  REGISTRY_URL=$(az acr show \
    --resource-group "$RESOURCE_GROUP" \
    --name "$REGISTRY" \
    --query "loginServer" \
    --output tsv)

  echo "REGISTRY_URL=$REGISTRY_URL"
# end::adocRegistryShow[]

# tag::adocACAEnv[]
az containerapp env create \
    --resource-group "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --tags system="$TAG" \
    --name "$CONTAINERAPPS_ENVIRONMENT" \
    --logs-workspace-id "$LOG_ANALYTICS_WORKSPACE_CLIENT_ID" \
    --logs-workspace-key "$LOG_ANALYTICS_WORKSPACE_CLIENT_SECRET"
# end::adocACAEnv[]

# tag::adocACACreate[]
  az containerapp create \
    --resource-group "$RESOURCE_GROUP" \
    --tags system="$TAG" application="$QUARKUS_APP" \
    --image "mcr.microsoft.com/azuredocs/containerapps-helloworld:latest" \
    --name "$QUARKUS_APP" \
    --environment "$CONTAINERAPPS_ENVIRONMENT" \
    --ingress external \
    --target-port 80 \
    --min-replicas 0 \
    --env-vars QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION=validate \
               QUARKUS_HIBERNATE_ORM_SQL_LOAD_SCRIPT=no-file \
               QUARKUS_DATASOURCE_USERNAME="$POSTGRES_DB_ADMIN" \
               QUARKUS_DATASOURCE_PASSWORD="$POSTGRES_DB_PWD" \
               QUARKUS_DATASOURCE_REACTIVE_URL="$POSTGRES_DB_CONNECT_STRING"

  az containerapp create \
    --resource-group "$RESOURCE_GROUP" \
    --tags system="$TAG" application="$MICRONAUT_APP" \
    --image "mcr.microsoft.com/azuredocs/containerapps-helloworld:latest" \
    --name "$MICRONAUT_APP" \
    --environment "$CONTAINERAPPS_ENVIRONMENT" \
    --ingress external \
    --target-port 80 \
    --min-replicas 0

  az containerapp create \
    --resource-group "$RESOURCE_GROUP" \
    --tags system="$TAG" application="$SPRING_APP" \
    --image "mcr.microsoft.com/azuredocs/containerapps-helloworld:latest" \
    --name "$SPRING_APP" \
    --environment "$CONTAINERAPPS_ENVIRONMENT" \
    --ingress external \
    --target-port 80 \
    --min-replicas 0
# end::adocACACreate[]

# tag::adocPostgresCreate[]
  az postgres flexible-server create \
    --resource-group "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --tags system="$TAG" \
    --name "$POSTGRES_DB" \
    --admin-user "$POSTGRES_DB_ADMIN" \
    --admin-password "$POSTGRES_DB_PWD" \
    --public all \
    --tier "$POSTGRES_TIER" \
    --sku-name "$POSTGRES_SKU" \
    --storage-size 4096 \
    --version "$POSTGRES_DB_VERSION"
# end::adocPostgresCreate[]

# tag::adocPostgresSchema[]
  az postgres flexible-server db create \
    --resource-group "$RESOURCE_GROUP" \
    --server-name "$POSTGRES_DB" \
    --database-name "$POSTGRES_DB_SCHEMA"
# end::adocPostgresSchema[]

  pushd ../..
# tag::adocPostgresTable[]
  az postgres flexible-server execute \
    --name "$POSTGRES_DB" \
    --admin-user "$POSTGRES_DB_ADMIN" \
    --admin-password "$POSTGRES_DB_PWD" \
    --database-name "$POSTGRES_DB_SCHEMA" \
    --file-path "infrastructure/db-init/initialize-databases.sql"
# end::adocPostgresTable[]
  popd

# tag::adocPostgresSelect[]
  az postgres flexible-server execute \
    --name "$POSTGRES_DB" \
    --admin-user "$POSTGRES_DB_ADMIN" \
    --admin-password "$POSTGRES_DB_PWD" \
    --database-name "$POSTGRES_DB_SCHEMA" \
    --querytext "select * from Statistics"
# end::adocPostgresSelect[]

  echo "Environment '${environment}' of project '${project_name}' ready."
}

exportEnvironment() {
  export PROJECT="java-runtimes"
  export RESOURCE_GROUP="rg-${PROJECT}"
  export LOCATION="eastus"
  export TAG="java-runtimes"

  export LOG_ANALYTICS_WORKSPACE="logs-java-runtimes"
  export CONTAINERAPPS_ENVIRONMENT="env-java-runtimes"

  export UNIQUE_IDENTIFIER=$(whoami)
  export REGISTRY="javaruntimesregistry${UNIQUE_IDENTIFIER}"
  export IMAGES_TAG="1.0"

  export POSTGRES_DB_ADMIN="javaruntimesadmin"
  export POSTGRES_DB_PWD="java-runtimes-p#ssw0rd-12046"
  export POSTGRES_DB_VERSION="13"
  export POSTGRES_SKU="Standard_B2s"
  export POSTGRES_TIER="Burstable"
  export POSTGRES_DB="db-stats-${UNIQUE_IDENTIFIER}"
  export POSTGRES_DB_SCHEMA="stats"
  export POSTGRES_DB_CONNECT_STRING="postgresql://${POSTGRES_DB}.postgres.database.azure.com:5432/${POSTGRES_SCHEMA}?ssl=true&sslmode=require"

  export QUARKUS_APP="quarkus-app"
  export MICRONAUT_APP="micronaut-app"
  export SPRING_APP="spring-app"

  export LOG_ANALYTICS_WORKSPACE_CLIENT_ID=$(az monitor log-analytics workspace show  \
    --resource-group "$RESOURCE_GROUP" \
    --workspace-name "$LOG_ANALYTICS_WORKSPACE" \
    --query customerId  \
    --output tsv | tr -d '[:space:]')

  export LOG_ANALYTICS_WORKSPACE_CLIENT_SECRET=$(az monitor log-analytics workspace get-shared-keys \
    --resource-group "$RESOURCE_GROUP" \
    --workspace-name "$LOG_ANALYTICS_WORKSPACE" \
    --query primarySharedKey \
    --output tsv | tr -d '[:space:]')

  export REGISTRY_URL=$(az acr show \
    --resource-group "$RESOURCE_GROUP" \
    --name "$REGISTRY" \
    --query "loginServer" \
    --output tsv)

  echo "Exported environment variables for project '${project_name}'."
}

deleteInfrastructure() {
  echo "Deleting environment '${environment}' of project '${project_name}'..."
  az group delete --yes --name "${RESOURCE_GROUP}"
  echo "Environment '${environment}' of project '${project_name}' deleted."
}

skip_login=false
args=()

while [ $# -gt 0 ]; do
  case $1 in
    -s|--skip-login)
      skip_login=true
      shift
      ;;
    --help)
      showUsage
      exit 0
      ;;
    -*|--*)
      showUsage
      echo "Unknown option $1"
      exit 1
      ;;
    *)
      # save positional arg
      args+=("$1")
      shift
      ;;
  esac
done

# restore positional args
set -- "${args[@]}"

command=${1}

if ! command -v az &> /dev/null; then
  echo "Azure CLI not found."
  echo "See https://aka.ms/tools/azure-cli for installation instructions."
  exit 1
fi

if ! command -v gh &> /dev/null; then
  echo "GitHub CLI not found."
  echo "See https://cli.github.com for installation instructions."
  exit 1
fi

if [ "$skip_login" = false ]; then
  echo "Logging in to Azure..."
  az login
  echo "Logging in to GitHub..."
  gh auth login
  echo "Login successful."
fi

if [ "$command" = "setup" ]; then
  createInfrastructure
  # setupRepo
elif [ "$command" = "cleanup" ]; then
  deleteInfrastructure
  # cleanupRepo
elif [ "$command" = "env" ]; then
  exportEnvironment
else
  showUsage
  echo "Error, unknown command '${command}'"
  exit 1
fi

set +e
popd

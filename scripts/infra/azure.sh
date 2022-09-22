#!/usr/bin/env bash
##############################################################################
# Usage: ./azure.sh setup|cleanup
# Setup or cleanup the Azure infrastructure for this project.
##############################################################################
# Dependencies: Azure CLI, GitHub CLI, jq
##############################################################################

set -e

project_name="java-runtimes"
environment="prod"
location="eastus2"
resource_group_name=rg-${project_name}-${environment}

showUsage() {
  script_name="$(basename "$0")"
  echo "Usage: ./$script_name setup|cleanup"
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

  export RESOURCE_GROUP=${resource_group_name}
  export LOCATION=${location}
  export TAG="java-runtimes"
  export LOG_ANALYTICS_WORKSPACE="logs-java-runtimes"
  export UNIQUE_IDENTIFIER=$(whoami)
  export REGISTRY="javaruntimesregistry"$UNIQUE_IDENTIFIER
  export IMAGES_TAG="1.0"

  az group create \
    --name ${resource_group_name} \
    --location ${location} \
    --tags system=$TAG \
    --output none
  echo "Resource group '${resource_group_name}' ready."

  az monitor log-analytics workspace create \
    --resource-group "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --tags system=$TAG \
    --workspace-name "$LOG_ANALYTICS_WORKSPACE"

  export LOG_ANALYTICS_WORKSPACE_CLIENT_ID=$(az monitor log-analytics workspace show  \
    --resource-group "$RESOURCE_GROUP" \
    --workspace-name "$LOG_ANALYTICS_WORKSPACE" \
    --query customerId  \
    --output tsv | tr -d '[:space:]')

  echo $LOG_ANALYTICS_WORKSPACE_CLIENT_ID

  export LOG_ANALYTICS_WORKSPACE_CLIENT_SECRET=$(az monitor log-analytics workspace get-shared-keys \
    --resource-group "$RESOURCE_GROUP" \
    --workspace-name "$LOG_ANALYTICS_WORKSPACE" \
    --query primarySharedKey \
    --output tsv | tr -d '[:space:]')

  echo $LOG_ANALYTICS_WORKSPACE_CLIENT_SECRET

  az acr create \
    --resource-group "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --tags system="$TAG" \
    --name "$REGISTRY" \
    --workspace "$LOG_ANALYTICS_WORKSPACE" \
    --sku Standard \
    --admin-enabled true

  az acr update \
    --resource-group "$RESOURCE_GROUP" \
    --name "$REGISTRY" \
    --anonymous-pull-enabled true



  echo "Environment '${environment}' of project '${project_name}' ready."
}

deleteInfrastructure() {
  echo "Deleting environment '${environment}' of project '${project_name}'..."
  az group delete --yes --name "rg-${project_name}-${environment}"
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

command=${0}

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
  # setupRepo
  createInfrastructure
elif [ "$command" = "cleanup" ]; then
  deleteInfrastructure
  # cleanupRepo
else
  showUsage
  echo "Error, unknown command '${command}'"
  exit 1
fi

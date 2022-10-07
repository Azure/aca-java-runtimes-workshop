#!/usr/bin/env bash
##############################################################################
# Usage: ./scaling.sh
# Switch auto scaling modes
# Make sure you have the correct environment variables set.
# For that, first run: source ./azure.sh env
##############################################################################
# Dependencies: Azure CLI
##############################################################################

set -e
cd $(dirname ${BASH_SOURCE[0]})
cd ../..

command=${1}

showUsage() {
  script_name="$(basename "$0")"
  echo "Usage: ./$script_name setup|restore|ping"
  echo "Setup auto-scaling mode for apps."
  echo
  echo "Commands:"
  echo "  setup    Setup auto-scaling as in the docs"
  echo "  restore  Restore default auto-scaling"
  echo "  ping     Send a curl to the 3 apps"
  echo 
}

if [ -z "$command" ]; then
  showUsage
  exit 1
fi

if [ "$command" == "setup" ]; then

  echo "Setting up auto-scaling..."

# tag::adocAutoScalingCpu[]
  az containerapp update \
    --name "$QUARKUS_APP" \
    --resource-group "$RESOURCE_GROUP" \
    --scale-rule-name "cpu-scaling" \
    --scale-rule-type "cpu" \
    --scale-rule-metadata type=Utilization value=10 \
    --min-replicas 1 \
    --max-replicas 10
# end::adocAutoScalingCpu[]

# tag::adocAutoScalingMemory[]
  az containerapp update \
    --name "$MICRONAUT_APP" \
    --resource-group "$RESOURCE_GROUP" \
    --scale-rule-name "memory-scaling" \
    --scale-rule-type "memory" \
    --scale-rule-metadata type=Utilization value=15 \
    --min-replicas 1 \
    --max-replicas 10
# end::adocAutoScalingMemory[]

  echo "Done."

elif [ "$command" == "restore" ]; then

  echo "Restoring default auto-scaling..."
  
  az containerapp update \
    --name "$QUARKUS_APP" \
    --resource-group "$RESOURCE_GROUP" \
    --scale-rule-name "http-scaling" \
    --scale-rule-metadata concurrentRequests=10 \
    --min-replicas 0 \
    --max-replicas 10

  az containerapp update \
    --name "$MICRONAUT_APP" \
    --resource-group "$RESOURCE_GROUP" \
    --scale-rule-name "http-scaling" \
    --scale-rule-metadata concurrentRequests=10 \
    --min-replicas 0 \
    --max-replicas 10

  az containerapp update \
    --name "$SPRING_APP" \
    --resource-group "$RESOURCE_GROUP" \
    --scale-rule-name "http-scaling" \
    --scale-rule-metadata concurrentRequests=10 \
    --min-replicas 0 \
    --max-replicas 10

  echo "Done."

elif [ "$command" == "ping" ]; then

  echo "Pinging apps..."
# tag::adocPingApps[]  
  curl https://${QUARKUS_HOST}/quarkus
  echo 
  curl https://${MICRONAUT_HOST}/micronaut
  echo 
  curl https://${SPRING_HOST}/springboot
  echo 
# end::adocPingApps[]

else
  showUsage
  exit
fi

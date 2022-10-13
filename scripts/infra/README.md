# Provisioning and deployment scripts

These scripts are mainly for testing/proctor usage, it's not meant for usage during the workshop.

## Provisioning infrastructure

1. Run `./azure.sh login` to log in to Azure and Github

2. Run `./azure.sh setup` to:
  - Create Azure infrastructure
  - Add GitHub secrets to the repository (for CI/CD)

Alternatively, you can use the `-s` option to only create the Azure infrastructure and skip the repository setup, with
`./azure.sh setup -s`.

## Export environment

To export all environment variables needed for the workshop in your current shell, you can use the command:

```sh
source ./env.sh
```

> Note: if you want to force a specific `UNIQUE_IDENTIFIER`, you can set it before running the command with `export UNIQUE_IDENTIFIER=<your-unique-identifier>`.

## Manual deployment

To test deployment without using the CI/CD GitHub Actions workflow, you can use the command:

```sh
source ./azure.sh env  # Exports the needed environment variables
./deploy.sh
```

It will:
- Build and package apps
- Build and push Docker images
- Deploy to container apps

## Cleaning up everything

Run `./azure.sh cleanup` to:
- Remove Azure infrastructure
- Clean up GitHub secrets
  
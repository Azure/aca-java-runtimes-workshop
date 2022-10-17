# Azure Container Apps Java Runtimes Workshop

[Check the Workshop](https://azure.github.io/aca-java-runtimes-workshop).

## Quarkus

```shell
cd quarkus-app
mvn test                          # Execute the tests
mvn quarkus:dev                   # Execute the application
curl 'localhost:8701/quarkus'     # Invokes the hello endpoint
```

To build a native application (you need GraalVM installed):
```shell
mvn -Pnative clean package
./target/*-runner
```

To build a Docker image with the native application (you need to build the native image on Linux):
```shell
docker build -t quarkus-app-native -f src/main/docker/Dockerfile.native .
```

## Micronaut

```shell
cd micronaut-app
mvn test                          # Execute the tests
docker compose -f infrastructure/postgres.yaml up
mvn mn:run                        # Execute the application
curl 'localhost:8702/micronaut'   # Invokes the hello endpoint
```

To build a native application (you need GraalVM installed):
```shell
mvn package -Dpackaging=native-image
./target/micronaut-app
```

To build a Docker image with the native application (you need to build the native image on Linux):
```shell
docker build -t micronaut-app-native -f src/main/docker/Dockerfile.native .
```

## SpringBoot

```shell
cd springboot-app
docker compose -f infrastructure/postgres.yaml up
mvn test                          # Execute the tests
mvn spring-boot:run               # Execute the application
curl 'localhost:8703/springboot'  # Invokes the hello endpoint
```

To build a native application (you need GraalVM installed):
```shell
mvn -Pnative clean package
./target/springboot-app
```

To build a Docker image with the native application (you need to build the native image on Linux):
```shell
docker build -t springboot-app-native -f src/main/docker/Dockerfile.native .
```

## Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution. For details, visit https://cla.opensource.microsoft.com.

When you submit a pull request, a CLA bot will automatically determine whether you need to provide
a CLA and decorate the PR appropriately (e.g., status check, comment). Simply follow the instructions
provided by the bot. You will only need to do this once across all repos using our CLA.

This project has adopted the [Microsoft Open Source Code of Conduct](https://opensource.microsoft.com/codeofconduct/).
For more information see the [Code of Conduct FAQ](https://opensource.microsoft.com/codeofconduct/faq/) or
contact [opencode@microsoft.com](mailto:opencode@microsoft.com) with any additional questions or comments.

## Trademarks

This project may contain trademarks or logos for projects, products, or services. Authorized use of Microsoft 
trademarks or logos is subject to and must follow 
[Microsoft's Trademark & Brand Guidelines](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks/usage/general).
Use of Microsoft trademarks or logos in modified versions of this project must not cause confusion or imply Microsoft sponsorship.
Any use of third-party trademarks or logos are subject to those third-party's policies.

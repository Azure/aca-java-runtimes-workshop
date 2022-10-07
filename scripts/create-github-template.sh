#!/usr/bin/env bash
##############################################################################
# Usage: ./create-github-template.sh
# Creates a temporary folder in ~/temp/template with all the needed files to create a new project template.
##############################################################################

TEMPLATE_HOME=~/temp/template
mkdir -p $TEMPLATE_HOME

cp -r ../ $TEMPLATE_HOME

rm -rf $TEMPLATE_HOME/.idea
rm -rf $TEMPLATE_HOME/.vscode
rm -rf $TEMPLATE_HOME/docs
rm -rf $TEMPLATE_HOME/scripts/infra
rm -rf $TEMPLATE_HOME/scripts/create-github-template.sh
rm -rf $TEMPLATE_HOME/bootstrap.sh
rm -rf $TEMPLATE_HOME/CODE_OF_CONDUCT.md
rm -rf $TEMPLATE_HOME/LICENSE
rm -rf $TEMPLATE_HOME/SECURITY.md
rm -rf $TEMPLATE_HOME/SUPPORT.md
rm -rf $TEMPLATE_HOME/.github/workflows/docs.yml

### Fix the registry name of the workflows
sed 's/javaruntimesregistrysinedied/<YOUR_REGISTRY_URL>/' $TEMPLATE_HOME/.github/workflows/deploy.yml >> $TEMPLATE_HOME/.github/workflows/deploy-new.yml
rm $TEMPLATE_HOME/.github/workflows/deploy.yml
mv $TEMPLATE_HOME/.github/workflows/deploy-new.yml $TEMPLATE_HOME/.github/workflows/deploy.yml
sed 's/javaruntimesregistrysinedied/<YOUR_REGISTRY_URL>/' $TEMPLATE_HOME/.github/workflows/deploy-native.yml >> $TEMPLATE_HOME/.github/workflows/deploy-native-new.yml
rm $TEMPLATE_HOME/.github/workflows/deploy-native.yml
mv $TEMPLATE_HOME/.github/workflows/deploy-native-new.yml $TEMPLATE_HOME/.github/workflows/deploy-native.yml

### Removing the Micronaut files
rm -rf $TEMPLATE_HOME/micronaut-app/target
rm -rf $TEMPLATE_HOME/micronaut-app/src/main/java/io/containerapps/javaruntime/workshop/micronaut/Micronaut*
rm -rf $TEMPLATE_HOME/micronaut-app/src/main/java/io/containerapps/javaruntime/workshop/micronaut/Statistics*
touch $TEMPLATE_HOME/micronaut-app/src/main/java/io/containerapps/javaruntime/workshop/micronaut/.gitkeep
rm -rf $TEMPLATE_HOME/micronaut-app/src/main/resources/application.yml
touch $TEMPLATE_HOME/micronaut-app/src/main/resources/application.yml
rm -rf $TEMPLATE_HOME/micronaut-app/src/test/java/io/containerapps/javaruntime/workshop/micronaut/Micronaut*
touch $TEMPLATE_HOME/micronaut-app/src/test/java/io/containerapps/javaruntime/workshop/micronaut/.gitkeep

### Removing the Quarkus files
rm -rf $TEMPLATE_HOME/quarkus-app/target
rm -rf $TEMPLATE_HOME/quarkus-app/*.sql
rm -rf $TEMPLATE_HOME/quarkus-app/src/main/java/io/containerapps/javaruntime/workshop/quarkus/Quarkus*
rm -rf $TEMPLATE_HOME/quarkus-app/src/main/java/io/containerapps/javaruntime/workshop/quarkus/Statistics*
touch $TEMPLATE_HOME/quarkus-app/src/main/java/io/containerapps/javaruntime/workshop/quarkus/.gitkeep
rm -rf $TEMPLATE_HOME/quarkus-app/src/main/resources/application.properties
touch $TEMPLATE_HOME/quarkus-app/src/main/resources/application.properties
rm -rf $TEMPLATE_HOME/quarkus-app/src/test/java/io/containerapps/javaruntime/workshop/quarkus/Quarkus*
touch $TEMPLATE_HOME/quarkus-app/src/test/java/io/containerapps/javaruntime/workshop/quarkus/.gitkeep

### Removing the SpringBoot files
rm -rf $TEMPLATE_HOME/springboot-app/target
rm -rf $TEMPLATE_HOME/springboot-app/src/main/java/io/containerapps/javaruntime/workshop/springboot/Springboot*
rm -rf $TEMPLATE_HOME/springboot-app/src/main/java/io/containerapps/javaruntime/workshop/springboot/Statistics*
touch $TEMPLATE_HOME/springboot-app/src/main/java/io/containerapps/javaruntime/workshop/springboot/.gitkeep
rm -rf $TEMPLATE_HOME/springboot-app/src/main/resources/application.properties
touch $TEMPLATE_HOME/springboot-app/src/main/resources/application.properties
rm -rf $TEMPLATE_HOME/springboot-app/src/test/java/io/containerapps/javaruntime/workshop/springboot/Springboot*
touch $TEMPLATE_HOME/springboot-app/src/test/java/io/containerapps/javaruntime/workshop/springboot/.gitkeep


echo -e "package io.containerapps.javaruntime.workshop.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootApplication.class, args);
	}

}
" >> $TEMPLATE_HOME/springboot-app/src/main/java/io/containerapps/javaruntime/workshop/springboot/SpringbootApplication.java

### Creating a new pom.xml without docs
rm $TEMPLATE_HOME/pom.xml

echo -e "<?xml version=\"1.0\"?>
<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\"
    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">
  <modelVersion>4.0.0</modelVersion>
  <groupId>io.containerapps.javaruntime.workshop</groupId>
  <artifactId>parent</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>pom</packaging>
  <name>Azure Container Apps and Java Runtimes Workshop</name>
  <modules>
    <module>micronaut-app</module>
    <module>springboot-app</module>
    <module>quarkus-app</module>
  </modules>
</project>
" >> $TEMPLATE_HOME/pom.xml

### Removing the .git folder
rm -rf $TEMPLATE_HOME/.git

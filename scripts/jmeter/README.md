# Azure Container Apps Java Runtimes Workshop :: JMeter Scripts

## Load Testing with JMeter

[JMeter](https://jmeter.apache.org/) is used to do load testing.
Thanks to the [JMeter Maven plugin](https://github.com/jmeter-maven-plugin/jmeter-maven-plugin), you can execute the following commands:

* `mvn jmeter:configure jmeter:gui -DguiTestFile=src/test/jmeter/load.jmx`: Executes the GUI so you can visualize the `src/test/jmeter/load.jmx` file
* `mvn clean verify`: runs the load test

> **NOTE:** You need to make sure your services are up and running.

The [`src/test/jmeter/load.jmx`](jmeter/src/test/jmeter/load.jmx) script executes some load tests on the 3 services by invoking the different REST endpoints.

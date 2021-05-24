GeographicCoordinateWS
======================

See LICENSE for this software's licensing terms.

I'll start out by saying this isn't meant to be a serious application.  I created this for one purpose:  to serve as a guide to myself for future SOAP and REST projects.

GeographicCoordinateWS is a Spring Boot application that exposes the functionality of my [GeographicCoordinate](https://github.com/kloverde/java-GeographicCoordinate) library's various calculators as SOAP and REST Web services.

This application has no official release, and it never will.  Undocumented changes could be made at any time.


## Development Environment

1. Java 15
2. Gradle 7.0

I set everything up to target Java 15.  What?  Java 15 is too new, you say?

This application and all its first-party dependencies will most likely build just fine on older versions of Java.  Just change the build configuration in the projects.

If you go back far enough, I know of one issue you'll encounter.  When building [GeographicCoordinate](https://github.com/kloverde/java-GeographicCoordinate), the JUnit tests will fail because [Java 8 has a floating point bug](https://bugs.openjdk.java.net/browse/JDK-8039915).  It was fixed in Java 9, but I haven't tried building any of this on Java 9.  Good luck.


## Building and Running

### Build/publish GeographicCoordinate

Get it [here](https://github.com/kloverde/java-GeographicCoordinate).  Follow the build instructions listed in that project's README.MD, then publish it:

```shell
gradle publishtomavenlocal
```

### Build/publish the SOAP client API

```shell
cd gcws-client-api-soap
gradle publishtomavenlocal
```

### Build/publish the REST client API

```shell
cd gcws-client-api-rest
gradle publishtomavenlocal
```

### Build the application

```
cd gcws-application
gradle build
```

You can specify whether the WSDL is generated dynamically or loaded from the API jar via the `wsdl.dynamic` property in `application.properties`.  The JARed WSDL uses localhost as the endpoint.  That might not work for you if you intend to actually deploy this application somewhere, so the dynamic WSDL might be a better choice.


Run with:

```shell
java -jar GeographicCoordinateWS-2.0.0.jar
```

A script which launches the application with remote debugging enabled can be found in the root directory.


## Using the SOAP Web Service

With the application running locally, the WSDL is retrievable from http://localhost:8080/GeographicCoordinateWS/soap/GeographicCoordinateWS.wsdl.


## Using the REST Web Service

### Swagger UI

Use SwaggerUI to see available endpoints and request/response formatting.

http://localhost:8080/swagger-ui/

* For reasons known only to the Swagger people, the trailing forward slash actually matters.


### Response Codes

The REST service responds with JSON appropriate to the request type, and supports the following HTTP status codes:

* 200:  Request was successfully processed
* 400:  Invalid request
* 500:  The back end threw an exception which it could not recover from


## Donations

https://paypal.me/KurtisLoVerde/10

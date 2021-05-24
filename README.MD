GeographicCoordinateWS
======================

See LICENSE for this software's licensing terms.

I'll start out by saying this isn't meant to be a serious application.  I created this for one purpose:  to serve as a guide to myself for future SOAP and REST projects.

GeographicCoordinateWS is a Spring Boot application that exposes the functionality of my [GeographicCoordinate](https://github.com/kloverde/java-GeographicCoordinate) library's various calculators as SOAP and REST Web services.

This application has no official release, and it never will.  Undocumented changes could be made at any time.


## Development Environment

1. Java 10
2. Gradle 7.0

Java 10 or later is required unless you want to invest the time to get things working on older Java.  This is due to a dependency on version 4.2 of my [GeographicCoordinate](https://github.com/kloverde/java-GeographicCoordinate) library, which is compiled for Java 10 and has required APIs that aren't present in earlier versions of GeographicCoordinate.  Rebuilding v4.2 on older Java might or might not succeed with JUnit tests enabled due to a floating point bug in *some, but not all* earlier JREs.  Note that I said JREs, not my library.  See GeographicCoordinate 4.0's release notes for more information about that.  You'll also need to tweak `build.gradle` in GeographicCoordinateWS.


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
java -jar GeographicCoordinateWS-0.0.0.jar
```

A batch script which launches the application with remote debugging enabled can be found in the root directory.


## Using the SOAP Web Service

With the application running locally, the WSDL is retrievable from http://localhost:8080/GeographicCoordinateWS/soap/GeographicCoordinateWS.wsdl.


## Using the REST Web Service

### Swagger UI

http://localhost:8080/swagger-ui/

* For reasons known only to the Swagger people, the trailing forward slash actually matters.

### Blah blah, this is getting removed

The REST service responds with JSON appropriate to the request type, and supports the following HTTP status codes:

* 200:  Request was successfully processed
* 400:  Request was malformed
* 422:  Request was correctly formed but semantically incorrect.  An example of this would be if a provided latitude is out of range.

In the event of an error, the JSON response consists of an error message and a copy of the HTTP status code.  You shouldn't inspect the JSON to determine if there was an error; you should use the actual HTTP status code for that.  The status code is made available in the JSON in case you want to use it for display purposes.

A high-level summary of supported requests is provided below.  See the `WsRestController` javadoc for more detail.


#### Distance

`GET` /GeographicCoordinateWS/rest/distance/[unit]/[coordinates]

http://localhost:8080/GeographicCoordinateWS/rest/distance/miles/35.048983:118.987977,35.084629:119.025986,35.110199:119.053642


#### Initial Bearing

`GET` /GeographicCoordinateWS/rest/initialBearing/compassType/[compass type]/from/[coordinate]/to/[coordinate]

http://localhost:8080/GeographicCoordinateWS/rest/initialBearing/compassType/32/from/74.12:89.34/to/79.56:83.78


#### Back Azimuth

`GET` GeographicCoordinateWS/rest/backAzimuth/compassType/[compass type]/initialBearing/[bearing]

http://localhost:8080/GeographicCoordinateWS/rest/backAzimuth/compassType/16/initialBearing/119.1123


## Donations

https://paypal.me/KurtisLoVerde/10

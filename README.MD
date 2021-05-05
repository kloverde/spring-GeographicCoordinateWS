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

Other versions of Gradle (earlier or later) may or may not work.  I've come to learn that Gradle is still going through growing pains, unfortunately.


## Building and Running

This is Gradle we're talking about, so say the typical prayer if you aren't using the exact version I used.  This is known to build with Gradle 7.0.

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

You can specify whether the WSDL is generated dynamically or loaded from disk by modifying the `wsdl.dynamic` property in `application.properties`.  The included WSDL uses localhost as the endpoint.  That won't work for you if you intend to actually deploy this application somewhere, so you'll want to overwrite the provided WSDL with a regenerated one.

Eventually I might change the build script so that it generates a WAR, but for now, it generates a Spring Boot uber jar.  Build in your IDE or, if you want to build from the command line:

```shell
gradle bootJar
```

Run with:

```shell
java -jar GeographicCoordinateWS-0.0.0.jar
```

A batch script which launches the application with remote debugging enabled can be found in the root directory.


## Using the SOAP Web Service

With the application running locally, the WSDL is retrievable from http://localhost:8080/GeographicCoordinateWS/soap/GeographicCoordinateWS.wsdl.


## Using the REST Web Service

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

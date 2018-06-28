GeographicCoordinateWS
======================

See LICENSE for this software's licensing terms.  At first glance, it resembles the BSD-3 license, but it contains a significant difference which prohibits certain use.  Read it carefully.

GeographicCoordinateWS is a Spring Boot application that exposes the functionality of my [GeographicCoordinate](https://github.com/kloverde/java-GeographicCoordinate) library's various calculators as SOAP and REST Web services.

It should be noted that although this application is considered feature-complete, and I don't know what else I'd want to do to it, I haven't officially released it yet.  This means that undocumented changes could be made at any time.


## Development Environment

1. Java 10
2. Gradle 4.7
3. Eclipse Oxygen.3a (4.7.3a)

Java 10 or later is required unless you want to invest the time to get things working on older Java.  This is due to a dependency on version 4.2 of my [GeographicCoordinate](https://github.com/kloverde/java-GeographicCoordinate) library, which is compiled for Java 10 and has required APIs that aren't present in earlier versions of GeographicCoordinate.  Rebuilding v4.2 on older Java might or might not succeed with JUnit tests enabled due to a floating point bug in *some, but not all* earlier JREs.  Note that I said JREs, not my library.  See GeographicCoordinate 4.0's release notes for more information about that.  You'll also need to tweak `build.gradle` in GeographicCoordinateWS.

Other versions of Gradle (earlier or later) may or may not work.  I've come to learn that Gradle is still going through growing pains, unfortunately.

Your Eclipse version shouldn't matter, but if you want to make use of the included pre-configured project files, you'll need certain plugins.  See the [Eclipse Setup](#eclipse-setup) section for information about that.


## Eclipse Setup

In the root directory, you'll find a subdirectory named `.eclipse_project_files`, which contains... you guessed it, Eclipse project files.  They enable you to import a pre-configured project that "just works" so that you don't have to fuss around.

If you're an Eclipse user, you'll know that this is not where Eclipse keeps them - rather, they belong in the project root directory.  I archived the project files in a different location so that they could be saved without interfering with people who like to set up projects their own way.

If you want to use them, read `.eclipse_project_files/README.txt` for a description of the Eclipse plugins you'll need to install.  Copy `.classpath`, `.project` and `.derived` to the project root directory, then import the project.

Eclipse seems to think it knows more about my project than I do.  Once the project is imported, Eclipse might do something annoying:  it might automatically remove the `generated-sources` directory from the build path, thus modifying the .classpath file you copied just a moment before.  It does this because the directory doesn't exist; the directory is created by running the `genJaxb` target.  So, after generating the JAXB classes, you'll need to overwrite .classpath with the version you initially copied.  After that, you're in business.  If you know how to get Eclipse to stop doing that, let me know.


## Building and Running

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

Okay, so this might not really qualify as a REST API.  `GET` is the only HTTP verb used (the others have no purpose in this context), and it's not like a `/books` URL where `/books` gives you a list of all books and `/books/sports` narrows it down to a list of sports books.  In fact, there's no discoverability at all because there's nothing to discover - all of the path variables are inputs into equations.  So I guess you can call it "RESTish".

Each of the SOAP operations have a RESTish version which return JSON.  A high-level summary is provided below.  See the `WsRestController` javadoc for more detail.


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

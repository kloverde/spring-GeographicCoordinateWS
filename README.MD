GeographicCoordinateWS
======================

See LICENSE for this software's licensing terms.  At first glance, it resembles the BSD-3 license, but it contains a significant difference which prohibits certain use.  Read it carefully.

GeographicCoordinateWS is a Spring Boot application that exposes the functionality of my [GeographicCoordinate](https://github.com/kloverde/java-GeographicCoordinate) library as SOAP and REST Web services.

Although fully functional, the application is still considered a work in progress.


## Development Environment

1. Java 10
2. Gradle 4.7
3. Eclipse Oxygen.3a (4.7.3a)

Java 10 or later is required unless you want to take the time to get older versions to work.  This is due to a dependency on my [GeographicCoordinate](https://github.com/kloverde/java-GeographicCoordinate) library, which requires Java 10.  See GeographicCoordinate 4.0's release notes for an explanation about why that is.  If you absolutely must use an older version of Java, you can use a pre-4.0 release of GeographicCoordinate if you want to -- bugs and all -- but only if the `Specification-Version` matches the one packaged with GeographicCoordinateWS.  You'll also need to tweak `build.gradle`.

Other versions of Gradle (earlier or later) may or may not work.  I've come to learn that Gradle is still going through growing pains, unfortunately.

Your Eclipse version shouldn't matter, but if you want to make use of the included pre-configured project files, you'll need certain plugins.  See the [IDE Setup](#ide-setup) section for information about that.


## IDE Setup

In the root directory, you'll find a subdirectory named `.eclipse_project_files`, which contains... you guessed it, Eclipse project files.  They enable you to import a pre-configured project that "just works" so that you don't have to fuss around.

If you're an Eclipse user, you'll know that this is not where Eclipse keeps them - rather, they belong in the project root directory.  I archived the project files in a different location so that they could be saved without interfering with people who like to set up projects their own way.

If you want to use them, read `.eclipse_project_files/README.txt` for a description of the Eclipse plugins you'll need to install.  Copy `.classpath` and `.project` to the project root directory, then import the project.

Eclipse seems to think it knows more about my project than I do.  Once the project is imported, Eclipse might do something annoying:  it might automatically remove the `generated-sources` directory from the build path, thus modifying the .classpath file you copied just a moment before.  It does this because the directory doesn't exist; the directory is created by running the `genJaxb` target.  So, after generating the JAXB classes, you'll need to overwrite .classpath with the version you initially copied.  After that, you're in business.  If you know how to get Eclipse to stop doing that, let me know.


## Building and Running

You can specify whether the WSDL is generated dynamically or loaded from disk by modifying the `wsdl.dynamic` property in `application.properties`.  The included WSDL uses localhost as the endpoint.  That won't work for you if you intend to actually deploy this application somewhere, so you'll want to overwrite the provided WSDL with a regenerated one.

Eventually I'm going to change the build script so that it generates a WAR, but for now, it generates a Spring Boot uber jar.  Build in your IDE or, if you want to build from the command line:

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

#### Distance

http://localhost:8080/GeographicCoordinateWS/rest/distance/miles/30.111:120.222,86.333:-110.122


## Donations

https://paypal.me/KurtisLoVerde/10

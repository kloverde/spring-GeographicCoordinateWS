GeographicCoordinateWS
======================

See LICENSE for this software's licensing terms.  At first glance, it resembles the BSD-3 license, but it contains a significant difference which prohibits certain use.  Read it carefully.

This is my newest project.  It's a work in progress, and is in a rather early stage.  When finished, this will be a Spring Boot application that exposes the functionality of my [GeographicCoordinate](https://github.com/kloverde/java-GeographicCoordinate) library over Web services.

Completed so far:

* The schemas
* POJO service classes which handle requests & responses
* Build system

Spring has not been added yet.


## Development Environment

1. Java 10
2. Gradle 4.7
3. Eclipse Oxygen.3a (4.7.3a)


Java 10 or later is required unless you want to take the time to get older versions to work.  Version 4.0 of [my GeographicCoordinate library](https://github.com/kloverde/java-GeographicCoordinate) is packaged with this application, and v4.0 is restricted to Java 10.  See GeographicCoordinate's release notes for an explanation about why that is.  You can use an older release of GeographicCoordinate if you want to, but only if the `Specification-Version` matches the one packaged with GeographicCoordinateWS.  You'll also need to tweak `build.gradle`.

Other versions of Gradle (earlier or later) may or may not work.  I've come to learn that Gradle is still going through growing pains, unfortunately.

Your Eclipse version shouldn't matter, but if you want to make use of the included pre-configured project files, you'll need certain plugins.  See the [IDE Setup](#ide-setup) section for information about that.


## IDE Setup

In the root directory, you'll find a subdirectory named `.eclipse_project_files`, which contains... you guessed it, Eclipse project files.  They enable you to import a pre-configured project that "just works" so that you don't have to fuss around.

If you're an Eclipse user, you'll know that this is not where Eclipse keeps them - rather, they belong in the project root directory.  I archived the project files in a different location so that they could be saved without interfering with people who like to set up projects their own way.

If you want to use them, read `.eclipse_project_files/README.txt` for a description of the Eclipse plugins you'll need to use a particular set of project files.  Copy `.classpath` and `.project` to the project root directory, then import the project.

Eclipse seems to think it knows more about my project than I do.  Once the project is imported, Eclipse might do something annoying:  it might automatically remove the `generated-sources` directory from the build path, thus modifying the .classpath file you copied just a moment before.  The directory won't exist until you run the `genJaxb` target.  So, after generating the JAXB classes, you'll need to overwrite .classpath with the version you initially copied.  After that, you're in business.  If you know how to get Eclipse to stop doing that, let me know.

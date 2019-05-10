
Java and Spring Tutorials
================

This project is **a collection of small and focused tutorials** - each covering a single and well defined area of development in the Java ecosystem.
A strong focus of these is, of course, the Spring Framework - Spring, Spring Boot and Spring Security.
In additional to Spring, the following technologies are in focus: `core Java`, `Jackson`, `HttpClient`, `Guava`.


Building the project
====================
To do the full build, do: `mvn install -Pdefault -Dgib.enabled=false`


Building a single module
====================
To build a specific module run the command: `mvn clean install -Dgib.enabled=false` in the module directory


Running a Spring Boot module
====================
To run a Spring Boot module run the command: `mvn spring-boot:run -Dgib.enabled=false` in the module directory

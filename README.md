# dd22480_lab_2

[![Build Status](https://travis-ci.org/Cpptz/dd22480_lab_2.svg?branch=master)](https://travis-ci.org/Cpptz/dd22480_lab_2)
[![codecov](https://codecov.io/gh/Cpptz/dd22480_lab_2/branch/master/graph/badge.svg)]<(https://codecov.io/gh/Cpptz/dd22480_lab_2)


An implementation of a small continous integration server.


## Description
The CI server handles HTTP requests on events. 
The CI server supports compiling a project, executing the automated tests of a project and notification of the CI results by setting the commit status on the repository on Github.


## Documentation
### Platform and Dependencies
This project uses *Java 8*.
On Debian-based systems, you could use the following two command lines to install the *JRE* and the *JDK*.
```bash
sudo apt install openjdk-8-jdk
sudo apt install openjdk-8-jdk
```
Our dependencies are handled with *Maven*, see [pom.xml](pom.xml). 


We have one main dependency: [Junit 5](https://junit.org/junit5/) 

### Specification


### Test
We have written unit tests for methods of:
* [ContinousIntegrationServer.java](src/main/java/main/ContinousIntegrationServer.java) in [ContinousIntegrationServerTest.java](src/test/java/main/ContinousIntegrationServerTest.java )
* [Parser.java](src/test/java/main/Parser.java) in [ParserTest.java](src/test/java/main/ParserTest.java)
* [Pipeline.java](src/test/java/main/Pipeline.java) in [PipelineTest.java](src/test/java/main/PipelineTest.java)

### How to Run It
#### Terminal
On Debian-based systems, you could use the following  command line to install *Maven*.
```bash
sudo apt install maven
```
Then at the root folder, you can launch all tests by running
```bash
mvn test -B
```

#### IDE
Most of use *Intellij*.  

You just have to import the project and select the file you want to run by right clicking 
on it on the folder view on the left of the window.

You can also use the built-in maven tool on the right of the window to run all tests.

## Contributions

All of us agreed on a [guide](CONTRIBUTING.md) for contribution

This is what we have achieved 
* Cyril Pottiez


* Sara Ersson
	
	
* Viktor Widin


* Robin Gunning
    
    
* Fredrik Norrman


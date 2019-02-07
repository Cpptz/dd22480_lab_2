# dd22480_lab_2

[![Build Status](https://travis-ci.org/Cpptz/dd22480_lab_2.svg?branch=master)](https://travis-ci.org/Cpptz/dd22480_lab_2)
[![codecov](https://codecov.io/gh/Cpptz/dd22480_lab_2/branch/master/graph/badge.svg)](https://codecov.io/gh/Cpptz/dd22480_lab_2)



An implementation of a small continous integration (CI) server which supports compiling a project, executing the automated tests of a project and notification of the CI results by setting the commit status on the repository on Github.


## Description
The CI server handles HTTP requests on events. When there is a request the CI server sends the request to getRequestBody() to obtain the json data. The json data is parsed and saved as a commit. A pipeline is created holding a Git object, the repository URL and the unique commit ID. The pipeline clones the repository, does a checkout, compiles the repository and tests it. The pipeline result is notified by setting the commit status on the repository on Github.


## Documentation
### Platform and Dependencies
This project uses *Java 8*.
On Debian-based systems, you could use the following two command lines to install the *JRE* and the *JDK*.
```bash
sudo apt install openjdk-8-jdk
sudo apt install openjdk-8-jdk
```
Our dependencies are handled with *Maven*, see [pom.xml](pom.xml). 


We have many dependencies : 
* [Junit 5](https://junit.org/junit5/) 
* [JGit](https://www.eclipse.org/jgit/)
* [gson](https://github.com/google/gson)
* [Apache Http Components](https://hc.apache.org/)

### Test
We have written unit tests for methods of:
* [ContinousIntegrationServer.java](./src/main/java/ci/ContinuousIntegrationServer.java) in [ContinousIntegrationServerTest.java](./src/test/java/ci/ContinuousIntegrationServerTest.java)
* [Parser.java](./src/main/java/ci/Parser.java) in [ParserTest.java](./src/test/java/ci/ParserTest.java)
* [Pipeline.java](./src/main/java/ci/Pipeline.java) in [PipelineTest.java](./src/test/java/ci/PipelineTest.java)
* [SavePipelineResult.java](./src/main/java/ci/SavePipelineResult.java) in [SavePipelineResultTest.java](./src/main/java/ci/SavePipelineResultTest.java)

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
Can can also lauch the server using the following command:
```bash
mvn exec:java -Dexec.mainClass="ci.ContinuousIntegrationServer"
```

#### IDE
Most of use *Intellij*.  

You just have to import the project and select the file you want to run by right clicking 
on it on the folder view on the left of the window.

You can also use the built-in maven tool on the right of the window to run all tests.

## Contributions

All of us agreed on a [guide](CONTRIBUTING.md) for contribution

This is what we have achieved 

* Sara Ersson
	* sendStatus()
	* sendStatusTest()
	* README
	
* Viktor Widin
	* /history endpoint
	* /webhook endpoint

* Robin Gunning
    	* Parsing of the github payload
	* /file endpoint
	* Hosting server
    
* Fredrik Norrman

* Cyril Pottiez
	* runPipeline()
	* write specifications
	* Github merger



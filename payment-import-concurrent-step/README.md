Spring Batch Integration Sample
===============================

This is a sample for the [Spring Batch Integration][] module.

[Spring Batch Integration]: https://github.com/spring-projects/spring-batch/tree/master/spring-batch-integration

The sample is based on the sample create for the book [Spring Integration in Action](http://www.amazon.com/Spring-Integration-Action-Mark-Fisher/dp/1935182439/). You can find that sample at:

* https://github.com/spring-projects/Spring-Integration-in-Action/tree/master/siia-examples/batch

## Objective

This sample uses **Spring Batch Integration** to more easily use *Spring Batch* and *Spring Integration* together. The application will poll a directory for a file that contains 27 payment records. *Spring Batch* will subsequently process those payments. If an error occurs the Job is resubmitted.

## Running the Sample

You can run the application by either

* running the "Main" class from within STS (Right-click on Main class --> Run As --> Java Application)
* or from the command line:

	$ mvn package
	$ mvn exec:java

Or via one line:

	$ mvn clean package exec:java
	
As a result the final console output should be:

	DONE!!
	exitStatus: COMPLETED; # of payments imported: 27
	Sent '2' notifications:
	#1 Subject: 'Execution has STARTED', Message: 'Execution has STARTED'.
	#2 Subject: 'Execution has COMPLETED', Message: 'Execution has COMPLETED'.


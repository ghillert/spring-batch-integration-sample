Spring Batch Integration Samples
================================

[![Build Status](https://travis-ci.org/ghillert/spring-batch-integration-sample.svg)](https://travis-ci.org/ghillert/spring-batch-integration-sample)

This project contains samples for the [Spring Batch Integration][] module.

[Spring Batch Integration]: https://github.com/spring-projects/spring-batch/tree/master/spring-batch-integration

The samples are based on the sample originally created for the book [Spring Integration in Action](http://www.amazon.com/Spring-Integration-Action-Mark-Fisher/dp/1935182439/). You can find that sample at:

* https://github.com/spring-projects/Spring-Integration-in-Action/tree/master/siia-examples/batch

## Objective

This sample uses **Spring Batch Integration** to more easily use *Spring Batch* and *Spring Integration* together. The application will poll a directory for a file that contains 27 payment records. *Spring Batch* will subsequently process those payments. If an error occurs the Job is resubmitted.

## Provided Samples

In order to illustrate the various concurrent processing techniques, we provide the following samples:

* Batch Integration - Payment Import
* Batch Integration - Payment Import using Concurrent Step
* Batch Integration - Payment Import using Async Processor and Writer
  - without Spring Integration
  - with Spring Integration
* Batch Integration - Payment Import with Remote Chunking

## Building and Testing

Use JDK 17 or later and run `mvn clean package` from the project root.
This runs five tests across the four sample modules, including both async
processor variants.

The samples still use Spring Framework 4 and Spring Batch 3. Their CGLIB and
XStream dependencies require reflective access to JDK internals, so each module
configures the necessary `--add-opens` options for the Surefire test JVM. These
options apply only to Maven tests; running a sample directly requires the same
JVM options from its POM.

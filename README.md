Spring Batch Integration Samples
================================

[![Build Status](https://travis-ci.org/ghillert/spring-batch-integration-sample.svg)](https://travis-ci.org/ghillert/spring-batch-integration-sample)

This project contains samples for the [Spring Batch Integration][] module.

[Spring Batch Integration]: https://github.com/spring-projects/spring-batch/tree/master/spring-batch-integration

The samples are based on the sample originally created for the book [Spring Integration in Action](http://www.amazon.com/Spring-Integration-Action-Mark-Fisher/dp/1935182439/). You can find that sample at:

* https://github.com/spring-projects/Spring-Integration-in-Action/tree/master/siia-examples/batch

## Objective

These samples demonstrate how Spring Batch and Spring Integration work
together to import payments.

Spring Integration polls an input directory and launches a Spring Batch job
when it finds a payment file. The job imports the bundled 27 payment records
into an embedded database. Job execution notifications are collected by a
stub mail sender; no SMTP server is required.

## Technology

- Java 17 or later
- Spring Boot 4.1.1 dependency management
- Spring Batch 6
- Spring Integration 7
- Embedded HSQLDB
- JUnit Jupiter
- SLF4J and Logback

Batch jobs and steps use Java configuration. Spring Integration flows remain
configured in XML.

## Provided Samples

| Module | Demonstrates |
| --- | --- |
| `payment-import` | Basic payment import |
| `payment-import-concurrent-step` | Concurrent chunk processing using local worker threads |
| `payment-import-async-processor` | Asynchronous item processing and writing, with and without a Spring Integration gateway |
| `payment-import-remote-chunking` | Manager/worker chunk processing through Spring Integration channels |

The remote-chunking sample runs its manager and worker in the same JVM.
It demonstrates the messaging pattern without requiring an external broker
or a separate worker deployment.

## Building and Testing

Install JDK 17 or later and Apache Maven, then run from the project root:

```shell
mvn clean package
```

This compiles and packages all four modules and runs five integration tests,
including both async-processor variants.

To test one module:

```shell
mvn -pl payment-import test
```

## Running the Samples

Run the following commands from the project root.

**Basic** payment import:

```shell
mvn -pl payment-import compile exec:java
```

**Concurrent chunk** processing:

```shell
mvn -pl payment-import-concurrent-step compile exec:java
```

**Asynchronous** processing:

```shell
mvn -pl payment-import-async-processor compile exec:java
```

At the prompt, select:

1. AsyncItemProcessor without Spring Integration
2. AsyncItemProcessor with Spring Integration

**Remote chunking**:

```shell
mvn -pl payment-import-remote-chunking compile exec:java
```

Each sample reports the job result, imported payment count, and collected
mail notifications.

## Configuration and Input Data

Each module contains:

- `src/main/java/org/springframework/batch/integration/samples/payments/config/CommonConfig.java`:
  database, transaction manager, and Batch infrastructure configuration.
- `src/main/java/org/springframework/batch/integration/samples/payments/config/BatchConfig.java`:
  job, step, reader, writer, and processing configuration.
- `src/main/resources/META-INF/spring/integration-context.xml`:
  file polling, job launch, notification, and messaging flows.
- `src/main/resources/data/paymentImport/payment.input`:
  the bundled payment input file.
- `src/main/resources/database/dbinit.sql`:
  application tables and initial account data.

The input directory is configured in the Integration XML. Maven copies the
bundled data into `target/classes` when building the module.

The embedded database is recreated for each application context. Payment
data and Batch execution history do not survive application restarts.

## Logging

Each module configures logging in `src/main/resources/logback.xml`.

Spring Framework and Spring Integration log at `WARN`; sample application
classes log at `INFO`. Set a specific framework logger to `DEBUG` when
investigating configuration or message-flow problems.

## Restart Behavior

The Integration flows automatically request a restart when a job fails.

These samples are demonstrations, not production payment-processing
implementations. In particular, the concurrent sample disables reader
checkpoint state. A restart after partial success can reprocess committed
payments and repeat account balance updates.

Production use requires a restart strategy and duplicate protection, plus
tests covering partial failures and recovery.
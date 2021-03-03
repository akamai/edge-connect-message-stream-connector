# Edge Connect Message Stream Connector

* [Overview](#overview)

* [Get started](#get-started)

* [Examples](#examples)

* [Notice](#notice)

# Overview

Connects a Message Stream data to a third-party data storage provider, in order to store and analyze the data using cloud analytics services.

# Get started

### Azure Configuration

Credentials to the Azure Storage of your choice must be provided in the ``/src/main/resources/blob.properties`` file.

- for authentication, a full _connectionString_ and _accountKey_ are required. Access keys can be found in the "Storage Account"
  settings page (look for the "Access Keys" section) in the Microsoft Azure Portal (https://portal.azure.com).

- set _accountName_ to match your Azure Storage account name (ex. "connector1").

- set _containerName_ to match your Azure Blob Storage container's name (ex. "my-blob-storage").

If _append_ is enabled (set to _true_, default is _false_), a new file will be created every 5 minutes, otherwise a file is created every time the
scheduler pulls new messages from the IoT Edge Connect Message Stream service.

### AWS Configuration

To authenticate with the Amazon's Kinesis Data Stream service, you need to configure your user's credentials in the form of either:

- environment variables;
- or default credential's profiles file.

User guide: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html

Additionally, you need to create (or use existing) AWS Kinesis Data Stream beforehand using the console or AWS CLI.

Once you have your Data Stream setup ready, provide its name when instantiating ``KinesisRecordProducer`` class. By default, our code sample tries to
connect and authorize with a Data Stream called "CM".

# Examples

Project compiles and runs with Java 8 and 11. There are two main Java files to run the examples:

1. ``com.akamai.ec.SampleBlobStorageApp.java`` - connects to Azure's Blob Storage;
2. ``com.akamai.ec.SampleKinesisApp.java`` - connects to AWS's Kinesis.

**Note:** To avoid possible Netty server exceptions, which do not affect project's functionality, you may try to run the above examples with the
following JVM commands:

```-Dio.netty.tryReflectionSetAccessible=true --add-opens java.base/jdk.internal.misc=ALL-UNNAMED```

# Notice

Copyright © 2020 Akamai Technologies, Inc.

Your use of Akamai's products and services is subject to the terms and provisions outlined
in [Akamai's legal policies](https://www.akamai.com/us/en/privacy-policies/).
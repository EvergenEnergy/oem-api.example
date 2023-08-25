# Java example SQS client

This is a simple example client for writing and reading to and from an AWS managed SQS server, written in Java. 


This example code has been sourced from the AWS documentation for interacting with SQS queues in Java: [Documentation](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-sqs-messages.html)

## Getting Started

Installation pre-requisites for running this demo are:
- Java
- [Maven](https://maven.apache.org/) build and project management tool
- Docker
- [LocalStack](https://github.com/localstack/localstack)

Additionally, you will need to configure your LocalStack after installation. This is done with the command: `aws configure --profile localstack` and setting your details as follows:
AWS Access Key ID: 123
AWS Secret Access Key: 123 
Default region name: ap-southeast-2
Default output format:
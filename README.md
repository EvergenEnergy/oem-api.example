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

## Service checklist

Dear developer, please check does the service have configured:

* Liveness, Readiness and Startup Probes? [https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/]
* K8s requests and limits

Also, it's good to have some metrics in place.
An [Alert Cookbook](https://www.notion.so/evergen/Alert-cookbook-c51876bc699c4767b95936015b362d6b) is available in Notion.

### Prerequisites

* Go v1.20+
* golangci-lint v1.52+

## Project Layout

Project starts in `main.go` with simple initialization and transfers controls to workers with the help of [evergen.go.app](https://bitbucket.org/evergenengineering/evergen.go.app/src/main/) library. Main func should be kept minimalistic and not import a lot of internal packages.

Functions and interfaces configured for external use and importing should be placed under `pkg/` directory; code that is not intended for importing should be kept in `internal/` directory (as it's also enforced by Go compiler: read more [here](https://go.dev/doc/go1.4#internalpackages) and [here](https://github.com/golang-standards/project-layout/tree/master/pkg)).

## Running the tests

The flag `-short` will skip integration tests which require running Docker.

```sh
go test -race -short -v ./...
```

It is however advised to use `make`:

```sh
make test.unit
```

The following will run integration tests only:

```sh
make test.integration
```

Make sure to add `Integration` suffix to the test functions you implemenent (e.g, `TestBuild_LoaderPerformsInitialScheduling_Integration`). `test.integration` rule relies on this convention when it chooses what tests to run.

Run this to run all the tests (this option is obviously the longest, but provides a complete picture of the coverage):

```sh
make test.all
```

## Linting

This project uses golangci-lint for checking coding style.

Install the latest version of golangci-lint.

```sh
golangci-lint run
```

## Deployment

Automatically deployed to dev and stage by Bitbucket Pipeline on merge to main. Production deployment will wait for manual approval.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags).

To push a new tag:

```sh
# Use annotated tags
git tag -a v0.1.1 -m "My change"

# Tags have to be specifically pushed
git push origin v0.1.1
```

## Security Context

In most cases container not required to write any data to disk and run as root.
Ensure you application is running on ports > 1024, because only root can run apps on port <= 1024. Use common 8080 port for your application to listen and 8300 to expose Prometheus metrics.

* Set FS as RO
* Run main root process as non-root user (as nobody in this example).

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: <Pod name>
spec:
  containers:
  - name: <container name>
    image: <image>
    securityContext:
+      readOnlyRootFilesystem: true
+      runAsNonRoot: true
+      runAsUser: 65534
```

In case of debugging required it is always possible to manually edit the deployment which will recreate pods with root and RW fs.

## Deployment Details (for reference only)

We user bitbucket pipelines + argo cd for deployment. Bitbucket pipeline is responsible for linting, testing, building and pushing docker images to ECR and then it triggers argo synchronization to deploy images to k8s. We use common Helm chart for deploying to k8s. You should provide Helm values in `eks` directory, also you can specify custom secrets with sops also in `eks` directory.

See more details in our [CICD2 Migration Guide](https://www.notion.so/evergen/CICDv2-migration-guide-a2ae33624b1c410380cc043c6ce8e618).

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc

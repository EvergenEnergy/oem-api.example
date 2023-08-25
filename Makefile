# Makefile for starting LocalStack and running a Java project

MAIN_CLASS = org.example.sqs.App

start-localstack:
	docker-compose up -d
	aws sqs create-queue --queue-name example-queue --endpoint-url http://localhost:4566 --profile localstack

run-java-project:
	mvn exec:java -Dexec.mainClass=$(MAIN_CLASS) 

demo: start-localstack run-java-project

clean:
	docker-compose down

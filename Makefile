MAIN_CLASS = org.example.sqs.App

start-localstack:
	docker compose -f docker-compose.yml up -d
	aws sqs create-queue --queue-name example-queue --endpoint-url http://localhost:4566 --profile localstack

run-java-project:
	mvn exec:java -Dexec.mainClass=$(MAIN_CLASS) 

demo: start-localstack run-java-project

clean:
	docker compose -f docker-compose.yml down -v
	docker compose -f docker-compose.yml rm -s -f -v

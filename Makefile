MAIN_CLASS = org.example.sqs.App

start-localstack:
	docker compose -f docker-compose.yml up -d
	aws sqs create-queue --queue-name example-queue --endpoint-url http://localhost:4566 --profile localstack

demo: start-localstack package run-packaged-jar clean

clean:
	docker compose -f docker-compose.yml down -v
	docker compose -f docker-compose.yml rm -s -f -v

package:
	mvn clean package

run-packaged-jar:
	java -jar target/java-sqs-client-demo-1.0.jar
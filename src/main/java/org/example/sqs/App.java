package org.example.sqs;

import org.example.sqs.models.*;
import java.util.ArrayList;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.builder.CloudEventBuilder;


public class App 
{

    public static void localDemo() {
        // Should match the values entered in the localstack configuration step
        String awsAccessKeyId = "123";
        String awsSecretAccessKey = "123";
        String localstackSqsQueueUrl = "http://localhost:4566/000000000000/example-queue";

        String messageBody = "Test message";

        // Create AWS credentials
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);

        // Create an SQS client with try-with-resources for automatic resource management
        try (SqsClient sqs = SqsClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.AP_SOUTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()) {

            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(localstackSqsQueueUrl)
                    .messageBody(messageBody)
                    .build();
            
            sqs.sendMessage(sendMessageRequest);
            System.out.println("Sent message: " + messageBody);

            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(localstackSqsQueueUrl)
                    .maxNumberOfMessages(10)
                    .messageAttributeNames("All")
                    .waitTimeSeconds(1)
                    .build();

            ReceiveMessageResponse receiveMessageResponse = sqs.receiveMessage(receiveMessageRequest);

            for (Message message : receiveMessageResponse.messages()) {
                // Handle each message as needed
                System.out.println("Received message: " + message.body());

                // Delete the message from the queue
                DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                        .queueUrl(localstackSqsQueueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build();
                sqs.deleteMessage(deleteRequest);
            }

        } catch (Exception e) {
            System.err.println("Error during SQS operations: " + e.getMessage());
            e.printStackTrace();
        }

        System.exit(0);
   }
   
   public static void remoteConnection() {
       // Create an SQS client with the default credentials provider chain
       try (SqsClient sqsTelemetryClient = SqsClient.builder()
               .region(Region.AP_SOUTHEAST_2) // Replace with your desired AWS region
               .build()) {

            // Specify the URL of the target SQS queue
            String queueUrl = "https://sqs.ap-southeast-2.amazonaws.com/406871981087/telemetry-mockVendor-dev";

            String payload = getExampleCloudEvent();

            // Create a message
            SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(payload)
                .build();

            // Send the message to the SQS queue
            SendMessageResponse sendMessageResult = sqsTelemetryClient.sendMessage(sendMessageRequest);

            // Print the message ID to confirm that the message was sent
            System.out.println("Message sent with ID: " + sendMessageResult.messageId());

        } catch (Exception e) {
            System.err.println("Error during SQS operations: " + e.getMessage());
            e.printStackTrace();
        }

        System.exit(0);
   }

   public static String getExampleCloudEvent() {
    TelemetryData telemetryData = new TelemetryData();

        telemetryData.siteID = "Site123";
        ArrayList<HybridInverterData> newHyrbidInverters = new ArrayList<HybridInverterData>();
        telemetryData.hybridInverters = newHyrbidInverters;

        HybridInverterData hybridInverter = new HybridInverterData();
        hybridInverter.deviceID = "Device123";
        telemetryData.hybridInverters.add(hybridInverter);
    
    String jsonString = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonString = objectMapper.writeValueAsString(telemetryData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CloudEvent event = CloudEventBuilder.v1()
                .withId("12345")
                .withType("example.event.type")
                .withSource(URI.create("http://example.com/source"))
                .withDataContentType("application/json")
                .withData(jsonString.getBytes())
                .build();

        byte[]serialized = EventFormatProvider
            .getInstance()
            // .resolveFormat(ContentType.JSON)
            .resolveFormat("application/cloudevents+json")
            .serialize(event);

        String serialisedCloudEvent = new String(serialized, StandardCharsets.UTF_8);
        System.out.println("CloudEvent: " + serialisedCloudEvent);
        
        return serialisedCloudEvent;
   }

    public static void main(String[] args) {
        localDemo();

        // Uncomment to run demo with Evergen's development environment SQS queues.
        // remoteConnection();
    }
}

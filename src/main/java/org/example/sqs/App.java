package org.example.sqs;

import org.example.sqs.models.*;
import java.util.ArrayList;
import java.net.URI;
import java.io.UnsupportedEncodingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.core.builder.CloudEventBuilder;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;


public class App 
{

    public static void localDemo() {
        // Should match the values entered in the localstack configuration step
        String awsAccessKeyId = "123";
        String awsSecretAccessKey = "123";
        String localstackSqsQueueUrl = "http://localhost:4566/000000000000/example-queue";

        String messageBody = "Test message";

        // Create AWS credentials
        BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);

        // Create an SQS client
        AmazonSQS sqs = AmazonSQSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(localstackSqsQueueUrl, "ap-southeast-2"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        SendMessageRequest sendMessageRequest = new SendMessageRequest(localstackSqsQueueUrl, messageBody);
        sqs.sendMessage(sendMessageRequest);

        System.out.println("Sent message: " + messageBody);

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(localstackSqsQueueUrl)
                    .withAttributeNames("All")
                    .withMaxNumberOfMessages(10)
                    .withMessageAttributeNames("All")
                    .withWaitTimeSeconds(10); // Adjust the wait time as needed

            ReceiveMessageResult receiveMessageResult = sqs.receiveMessage(receiveMessageRequest);

            for (Message message : receiveMessageResult.getMessages()) {
                // Handle each message as needed
                System.out.println("Received message: " + message.getBody());

                // Delete the message from the queue
                sqs.deleteMessage(localstackSqsQueueUrl, message.getReceiptHandle());
            }

        System.exit(0);
   }
   
   public static void remoteConnection() {
       // Create an SQS client with the default credentials provider chain
       AmazonSQS sqsTelemetryClient = AmazonSQSClientBuilder.standard()
       .withRegion("ap-southeast-2") // Replace with your desired AWS region
       .build();

        // Specify the URL of the target SQS queue
        String queueUrl = "https://sqs.ap-southeast-2.amazonaws.com/406871981087/telemetry-mockVendor-dev";

        String payload = getExampleCloudEvent();

        // // Create a message
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
            .withQueueUrl(queueUrl)
            .withMessageBody(payload);

        // // Send the message to the SQS queue
        SendMessageResult sendMessageResult = sqsTelemetryClient.sendMessage(sendMessageRequest);

        // Print the message ID to confirm that the message was sent
        System.out.println("Message sent with ID: " + sendMessageResult.getMessageId());

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

        String serialisedCloudEvent = "";
        try {
            serialisedCloudEvent = new String(serialized, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("CloudEvent: " + serialisedCloudEvent);
        
        return serialisedCloudEvent;
   }

    public static void main(String[] args) {
        localDemo();

        // Uncomment to run demo with Evergen's development environment SQS queues.
        // remoteConnection();
    }
}

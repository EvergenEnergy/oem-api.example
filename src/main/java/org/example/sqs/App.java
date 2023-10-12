package org.example.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.QueueAttributeName;

public class App 
{
    public static void main(String[] args) {
        // Create an SQS client with the default credentials provider chain
        AmazonSQS sqsTelemetryClient = AmazonSQSClientBuilder.standard()
            .withRegion("ap-southeast-2") // Replace with your desired AWS region
            .build();

        // Specify the URL of the target SQS queue
        String queueUrl = "https://sqs.ap-southeast-2.amazonaws.com/406871981087/telemetry-mockVendor-dev";
        
        // Retrieve and print the queue attributes
        GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest()
            .withQueueUrl(queueUrl)
            .withAttributeNames(QueueAttributeName.All.toString());

        GetQueueAttributesResult queueAttributesResult = sqsTelemetryClient.getQueueAttributes(getQueueAttributesRequest);

        // Print the queue attributes
        System.out.println("Approximate Number of Messages: " + queueAttributesResult.getAttributes().get(QueueAttributeName.ApproximateNumberOfMessages.toString()));
        System.out.println("Visibility Timeout: " + queueAttributesResult.getAttributes().get(QueueAttributeName.VisibilityTimeout.toString()));


        // Create a message
        String messageBody = "Hello, Test 2";
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
            .withQueueUrl(queueUrl)
            .withMessageBody(messageBody);

        // Send the message to the SQS queue
        SendMessageResult sendMessageResult = sqsTelemetryClient.sendMessage(sendMessageRequest);

        // Print the message ID to confirm that the message was sent
        System.out.println("Message sent with ID: " + sendMessageResult.getMessageId());
        System.exit(0);
    }
}
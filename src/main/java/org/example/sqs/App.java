package org.example.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;

public class App 
{

    public static AmazonSQS createSQSClient(String awsAccessKeyId, String awsSecretAccessKey, String localstackSqsEndpoint) {
        // Create AWS credentials
        BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);

        // Create an SQS client
        AmazonSQS sqs = AmazonSQSClient.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(localstackSqsEndpoint, "ap-southeast-2"))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        return sqs;
    }

    public static void readMessagesFromQueue(AmazonSQS sqs, String queueUrl, String queueName) {
        while (true) {
            // Receive messages from the local SQS queue (up to 10 messages at a time)
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
                    .withAttributeNames("All")
                    .withMaxNumberOfMessages(10)
                    .withMessageAttributeNames("All")
                    .withWaitTimeSeconds(1); // Adjust the wait time as needed

            ReceiveMessageResult receiveMessageResult = sqs.receiveMessage(receiveMessageRequest);

            for (Message message : receiveMessageResult.getMessages()) {
                // Handle each message as needed
                System.out.println("Received message: " + message.getBody());

                // Delete the message from the queue
                sqs.deleteMessage(queueUrl, message.getReceiptHandle());
                
                // Returning now as only one example message written to sqs queue
                return;
            }
        }
    }

    public static void writeMessageToQueue(AmazonSQS sqs, String queueUrl, String queueName, String messageBody) {
        // Send a message to the queue
        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, messageBody);
        sqs.sendMessage(sendMessageRequest);

        System.out.println("Sent message: " + messageBody);
    }
    public static void main( String[] args )
    {
        String awsAccessKeyId = "123";
        String awsSecretAccessKey = "123";
        String localstackSqsEndpoint = "http://localhost:4566/000000000000/sample-queue";
        String queueName = "example-queue";

        AmazonSQS sqs = createSQSClient(awsAccessKeyId, awsSecretAccessKey, localstackSqsEndpoint);

        // Get the URL of the local SQS queue
        GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
        String queueUrl = sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
        
        writeMessageToQueue(sqs, queueUrl, queueName, "Test message written to SQS");

        readMessagesFromQueue(sqs, queueUrl, queueName);

        System.exit(0);
    }
}
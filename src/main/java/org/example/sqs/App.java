package org.example.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

public class App 
{

    public static AmazonSQS createSQSClient(String awsAccessKeyId, String awsSecretAccessKey, String localstackSqsEndpoint, Boolean explicitCredentials) {
        if(explicitCredentials == false) {
            return AmazonSQSClientBuilder.defaultClient();
        }
        
        // Else, use IAM credentials:
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
    
    public static void localDemo() {
         // Should match the values entered in the localstack configuration step
         String awsAccessKeyId = "123";
         String awsSecretAccessKey = "123";
         String localstackSqsEndpoint = "http://localhost:4566/000000000000/";
         String queueName = "example-queue";
 
         AmazonSQS sqs = createSQSClient(awsAccessKeyId, awsSecretAccessKey, localstackSqsEndpoint, true);
 
         // Get the URL of the local SQS queue
         GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
         String queueUrl = sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
         
         writeMessageToQueue(sqs, queueUrl, queueName, "Test message written to SQS");
 
         readMessagesFromQueue(sqs, queueUrl, queueName);
 
         System.exit(0);
    }
    
    public static void remoteConnectionHelloWorld() {
        // Set it to false if using temporary IAM credentials from the EC2 instance running this service. This is the recommended approach.
        // Or set this as true if using explicit IAM credentials.
        Boolean explicitIAMCredentialsRequired = false;
        
        // If using explicit credentials:
        // Input Access Key Id and Secret for AWS IAM role provided to evergen: arn:aws:iam::000000000000:role/role_name
        String awsAccessKeyId = ""; 
        String awsSecretAccessKey = "";
        
        String queueUrlEvergen = "https://sqs.ap-southeast-2.amazonaws.com/406871981087/";
        String telemetryQueue = "telemetry-OEM_X-dev";
        String commandsQueue = "commands-OEM_X-dev";

        AmazonSQS sqsTelemetryClient = createSQSClient(awsAccessKeyId, awsSecretAccessKey, queueUrlEvergen, explicitIAMCredentialsRequired);
        AmazonSQS sqsCommandsClient = createSQSClient(awsAccessKeyId, awsSecretAccessKey, queueUrlEvergen, explicitIAMCredentialsRequired);

        // Get the URL of the local SQS queue
        GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(telemetryQueue);
        String queueTelemetry = sqsTelemetryClient.getQueueUrl(getQueueUrlRequest).getQueueUrl();
        String queueCommands = sqsCommandsClient.getQueueUrl(getQueueUrlRequest).getQueueUrl();
        
        // Telemetry should be sent through as a serialised object in the prescribed format in the documentation. 
        // But this can be used to test connectivity. Reception of this can be confirmed via Slack with Evergen.
        writeMessageToQueue(sqsTelemetryClient, queueTelemetry, telemetryQueue, "Hello World!");

        // Test the reception of a sample command.
        readMessagesFromQueue(sqsTelemetryClient, queueCommands, commandsQueue);

        System.exit(0);
    }

    public static void main( String[] args )
    {
        localDemo();

        // Uncomment to run demo with Evergen's development environment SQS queues.
        // remoteConnectionHelloWorld();
    }
}
package com.rolandopalermo.facturacion.ec.config;

import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import lombok.Getter;

@Getter
public class SQSServiceConfig {

    private static SQSServiceConfig instance = null;

    private AmazonSQS sqs = null;

    protected SQSServiceConfig() {
        init();
    }

    public static SQSServiceConfig getInstance() {
        if (instance == null) {
            instance = new SQSServiceConfig();
        }

        return instance;
    }
 
    private void init() {
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (~/.aws/credentials), and is in valid format.",
                    e);
		}

		sqs = AmazonSQSClientBuilder.standard()
							.withCredentials(credentialsProvider)
							.withRegion(Regions.US_EAST_1)
							.build();
    }

    public void sendMessage(String message, String messageGroupId) {

        String queueUrl = sqs.getQueueUrl("sri-dev.fifo").getQueueUrl();

        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, message);
        sendMessageRequest.setMessageGroupId(messageGroupId);
        sendMessageRequest.setMessageDeduplicationId(messageGroupId + ".fifo");
        sqs.sendMessage(sendMessageRequest);

        // Receive messages
        System.out.println("Receiving messages from MyQueue.\n");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message m : messages) {
            System.out.println("  Message");
            System.out.println("    MessageId:     " + m.getMessageId());
            System.out.println("    ReceiptHandle: " + m.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + m.getMD5OfBody());
            System.out.println("    Body:          " + m.getBody());
            for (Entry<String, String> entry : m.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
        }
        System.out.println();

        // // Delete a message
        // System.out.println("Deleting a message.\n");
        // String messageReceiptHandle = messages.get(0).getReceiptHandle();
        // sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
    }

}
package com.rolandopalermo.facturacion.ec.manager;

import java.lang.reflect.Type;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.message.SQSMessage;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.dto.AutorizacionRequestDTO;
import com.rolandopalermo.facturacion.ec.dto.ReceptionStorageDTO;
import com.rolandopalermo.facturacion.ec.web.bo.SaleDocumentBO;
import com.rolandopalermo.facturacion.ec.web.bo.SriBOv2;
import com.rolandopalermo.facturacion.ec.web.domain.SaleDocument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class SQSManager {

    private AmazonSQS sqs = null;

    private String queueUrl;

    private SQSConnection connection;

	@Value("${sri.wsdl.recepcion}")
	private String wsdlRecepcion;

	@Value("${sri.wsdl.autorizacion}")
    private String wsdlAutorizacion;

    @Value("${sales.ruta}")
    private String urlBase;

    @Autowired
    private SriBOv2 sriBo;

    @Autowired
    private SaleDocumentBO saleDocumentBO;

    public SQSManager() {
        initialize();
    }
 
    private void initialize() {
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
                            

        queueUrl = sqs.getQueueUrl("sri-dev.fifo").getQueueUrl();

        try {
            
            connection = new SQSConnectionFactory(new ProviderConfiguration(), sqs)
                    .createConnection(credentialsProvider.getCredentials());
            
            createConsumer();

        } catch (JMSException e) {
            e.printStackTrace();
		}

    }

    private void createConsumer() throws JMSException {

        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Queue queue = session.createQueue("sri-dev.fifo");
        
        MessageConsumer consumer = session.createConsumer(queue);

        consumer.setMessageListener(new SQSListener());

        connection.start();

        for (String url : sqs.listQueues().getQueueUrls()) {
            System.out.println("URL: " + url);
        }

    }

    public SendMessageResult sendMessage(String message, String messageGroupId) {
 
        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, message);
        sendMessageRequest.setMessageGroupId(messageGroupId);
        sendMessageRequest.setMessageDeduplicationId(messageGroupId + ".fifo");
        return sqs.sendMessage(sendMessageRequest);

        // LOG
        // receiveMessage(true);
        

    }

    // private List<Message> receiveMessage(boolean log) {

    //     // Receive messages
    //     System.out.println("Receiving messages from MyQueue.\n");
    //     ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
    //     List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();

    //     if (log) {

    //         if (messages.isEmpty()) {
    //             System.out.print("==============================");
    //             System.out.print("No hay mensajes recibidos");
    //             System.out.print("==============================");
    //         } else {
    //             for (Message m : messages) {
    //                 System.out.println("  Message");
    //                 System.out.println("    MessageId:     " + m.getMessageId());
    //                 System.out.println("    ReceiptHandle: " + m.getReceiptHandle());
    //                 System.out.println("    MD5OfBody:     " + m.getMD5OfBody());
    //                 System.out.println("    Body:          " + m.getBody());
    //                 for (Entry<String, String> entry : m.getAttributes().entrySet()) {
    //                     System.out.println("  Attribute");
    //                     System.out.println("    Name:  " + entry.getKey());
    //                     System.out.println("    Value: " + entry.getValue());
    //                 }
    //             }
    //         }

    //         System.out.println();
    //     }

    //     return messages;
    // }

    public void deleteMessage(String messageReceiptHandle) {

        // Delete a message
        System.out.println("Deleting a message.\n");
        sqs.deleteMessage(queueUrl, messageReceiptHandle);

    }

    class SQSListener implements MessageListener {
transient
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();

        @Override
        public void onMessage(javax.jms.Message message) {

            try {

                System.out.println("Received: " + ((TextMessage) message).getText());
                System.out.println("RECEIPT: " + ((SQSMessage) message).getReceiptHandle() );

                String body = ((TextMessage) message).getText();

                Map<String, String> data = gson.fromJson(body, type);

                ReceptionStorageDTO request = new ReceptionStorageDTO();
                request.setRuc(data.get("ruc"));

                int saleDocumentId = Integer.parseInt(data.get("saleDocumentId"));

                SaleDocument saleDocument = saleDocumentBO.getLastSaleDocumentByDocumentId(saleDocumentId);

                request.setSaleDocumentId(saleDocumentId);
                
                sriBo.enviarDocumento(request, wsdlRecepcion, urlBase);

                autorizar(saleDocument.getClaveAcceso());

                String receiptHandle = ((SQSMessage) message).getReceiptHandle();
                deleteMessage(receiptHandle);

            } catch (NegocioException e) {

                if (e.getCode() == SaleDocument.INCORRECTO) {
                    // ACTUALIZAR EStADO DE DOCUMENTO
                    System.out.println("EL DOCUMENTO FUE DEVUELTO");
                    String receiptHandle = ((SQSMessage) message).getReceiptHandle();
                    deleteMessage(receiptHandle);
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("EL DOCUMENTO NO LLEGO A SU DESTINO");
            }

        }

        private void autorizar(String claveAcceso) {
            try {
                AutorizacionRequestDTO request = new AutorizacionRequestDTO();
                request.setClaveAcceso(claveAcceso);
                sriBo.autorizar(request, wsdlAutorizacion, urlBase);
            } catch (Exception e) {
                // INICIAR SEGUNDA COLA
                System.out.println("EL DOCUMENTO NO AUTORIZADO");
            }
        }
    }

}
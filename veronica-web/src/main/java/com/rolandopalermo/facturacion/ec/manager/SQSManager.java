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
import com.rolandopalermo.facturacion.ec.web.bo.CompanyBO;
import com.rolandopalermo.facturacion.ec.web.bo.SaleDocumentBO;
import com.rolandopalermo.facturacion.ec.web.bo.SriBOv2;
import com.rolandopalermo.facturacion.ec.web.domain.Company;
import com.rolandopalermo.facturacion.ec.web.domain.SaleDocument;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class SQSManager {

	private static final Logger logger = Logger.getLogger(SQSManager.class);

    private AmazonSQS sqs = null;

    private String queueUrl;

    private SQSConnection connection;

	// @Value("${sri.wsdl.recepcion}")
	// private String wsdlRecepcion;

	// @Value("${sri.wsdl.autorizacion}")
    // private String wsdlAutorizacion;

    @Value("${sales.ruta}")
    private String urlBase;

    @Autowired
    private SriBOv2 sriBo;

    @Autowired
    private SaleDocumentBO saleDocumentBO;

    @Autowired
    private CompanyBO companyBO;

    /*

    Rutas de servicios de entornos de prueba
    o produccion.

    */

	@Value("${sri.soap.recepcion.wsdl.test}")
    private String wsdlReceptionTest;
    
	@Value("${sri.soap.recepcion.wsdl.production}")
    private String wsdlReceptionProduction;

	@Value("${sri.soap.autorizacion.wsdl.test}")
    private String wsdlAuthorizationTest;
    
	@Value("${sri.soap.autorizacion.wsdl.production}")
    private String wsdlAuthorizationProduction;

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

        } catch (Exception e) {
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

    public SendMessageResult sendMessage(Map<String, String> message, String messageGroupId) {
        
		Gson gson = new Gson();
        String strMessage = gson.toJson(message);
        
        logger.debug("SQS MESSAGE" + strMessage);

        SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, strMessage);
        sendMessageRequest.setMessageGroupId(messageGroupId);
        sendMessageRequest.setMessageDeduplicationId(messageGroupId + ".fifo");
        return sqs.sendMessage(sendMessageRequest);

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
        
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){
            private static final long serialVersionUID = 1L;
        }.getType();

        @Override
        public void onMessage(javax.jms.Message message) {

            try {

                logger.debug("Received: " + ((TextMessage) message).getText());
                logger.debug("RECEIPT: " + ((SQSMessage) message).getReceiptHandle() );

                // 1. GET MESSAGE DATA
                String body = ((TextMessage) message).getText();
                String receiptHandle = ((SQSMessage) message).getReceiptHandle();

                // 2. PARSE DATA
                Map<String, String> data = gson.fromJson(body, type);

                String action = data.get("action");

                if (action == null) {
                    deleteMessage(receiptHandle);
                    return;
                }

                logger.debug("ACTION: " + action);

                int saleDocumentId = Integer.parseInt(data.get("saleDocumentId"));
                String ruc = data.get("ruc");
                
                Company company = companyBO.getCompany(ruc);
                SaleDocument saleDocument = saleDocumentBO.getLastSaleDocumentByDocumentId(saleDocumentId);

                if ( action.equals("SEND") ) {

                    // 3. VALIDATE SALE DOCUMENTE EXIST
                    if (saleDocument == null) {
    
                        // deleteMessage(receiptHandle);
                        message.acknowledge();
                        logger.error("SALE DOCUMENT " + saleDocumentId + " NOT REGISTER");
                        throw new NegocioException("SALE DOCUMENT " + saleDocumentId + " NOT REGISTER");
                    
                    }
    
                    // 4. VALIDATE IF COMPANY WAS REGISTERED
                    if (company == null) {
    
                        saleDocument.setSaleDocumentState(SaleDocument.INCORRECTO);
                        saleDocumentBO.updateSaleDocument(saleDocument);
                        sriBo.actualizarDocumentoSale(urlBase, saleDocument, 1, "Compañia no registrada");
    
                        // deleteMessage(receiptHandle);
                        message.acknowledge();
                        logger.error("La compañia " + ruc + " no registrada");
                        throw new NegocioException("La compañia " + ruc + " no registrada");
    
                    }
    
                    // 5. SEND XML DOCUMENTS TO SRI SYSTEM
                    String wsdlRecepcion = company.getFlagEnvironment() == 0 ? wsdlReceptionTest : wsdlReceptionProduction;
                    sriBo.enviarDocumento(saleDocument, wsdlRecepcion, urlBase);
                    logger.debug("SaleDocument enviado: " + saleDocumentId);


                    data.put("action", "AUTHORIZE");

                    String messageGroupId = String.format("group_%d_%d_%d_AUTHORIZE", saleDocument.getId(), company.getCompanyId(), saleDocument.getSaleDocumentId());

                    sendMessage(data, messageGroupId);
                    message.acknowledge();

                } else if (action.equals("AUTHORIZE")) {

                    // 6. READ RESPONSE FROM SRI IF XML DOCUMENT WAS CORRECT
                    String wsdlAutorizacion = company.getFlagEnvironment() == 0 ? wsdlAuthorizationTest : wsdlAuthorizationProduction;
                    sriBo.autorizar(saleDocument, wsdlAutorizacion, urlBase);

                    // 7. DELETE MESSAGA FROM QUEUE
                    // deleteMessage(receiptHandle);
                    message.acknowledge();

                    logger.debug("SaleDocument autentificado: " + saleDocumentId);

                }

                // deleteMessage(receiptHandle);



            } catch (NegocioException e) {

                logger.error(e.getStackTrace());

                if (e.getCode() == SaleDocument.INCORRECTO) {
                    // X.1. ACTUALIZAR ESTADO DE DOCUMENTO FALLIDO
                    try {
                        message.acknowledge();
                    } catch (JMSException e1) {
                        e1.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                logger.error("EL DOCUMENTO NO LLEGO A SU DESTINO");
            }

        }

        /*
        @Deprecated
        private void autorizar(String claveAcceso, String wsdlAutorizacion) {
            try {
                AutorizacionRequestDTO request = new AutorizacionRequestDTO();
                request.setClaveAcceso(claveAcceso);
                sriBo.autorizar(request, wsdlAutorizacion, urlBase);
            } catch (Exception e) {
                // INICIAR SEGUNDA COLA
                logger.error("EL DOCUMENTO NO AUTORIZADO: " + claveAcceso);
            }
        }
        */
    }

}
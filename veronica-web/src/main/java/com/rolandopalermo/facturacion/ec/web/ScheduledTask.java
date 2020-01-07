package com.rolandopalermo.facturacion.ec.web;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.amazonaws.AmazonServiceException;
import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.web.bo.SaleDocumentBO;
import com.rolandopalermo.facturacion.ec.web.bo.SriBOv2;
import com.rolandopalermo.facturacion.ec.web.domain.Company;
import com.rolandopalermo.facturacion.ec.web.domain.SaleDocument;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTask {

    private static final Logger log = Logger.getLogger(ScheduledTask.class);

    @Value("${sri.soap.autorizacion.wsdl.test}")
    private String wsdlAuthorizationTest;

    @Value("${sri.soap.autorizacion.wsdl.production}")
    private String wsdlAuthorizationProduction;

    @Value("${sales.ruta}")
    private String urlBase;

    @Autowired
    SaleDocumentBO saleDocumentBO;

    @Autowired
    private SriBOv2 sriBo;

    @Scheduled(fixedRate = 1000 * 60 * 1)
    public void verifyAuthorizerDocuments() {

        List<SaleDocument> salesDocuments = saleDocumentBO.fetchSentDocument();

        log.debug("Revisar documentos pendientes: " + salesDocuments.size());

        for (SaleDocument saleDocument : salesDocuments) {

            log.debug(saleDocument.getClaveAcceso());

            Company company = saleDocument.getCompany();

            String wsdlAutorizacion = company.getFlagEnvironment() == 0 ? wsdlAuthorizationTest
                    : wsdlAuthorizationProduction;

            try {
                sriBo.autorizar(saleDocument, wsdlAutorizacion, urlBase);
            } catch (AmazonServiceException | NegocioException | JAXBException | IOException e) {
                log.error(e.getMessage());
            }
        }

    }

}
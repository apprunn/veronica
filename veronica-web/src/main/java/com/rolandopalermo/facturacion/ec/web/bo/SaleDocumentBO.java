package com.rolandopalermo.facturacion.ec.web.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.List;

import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.web.domain.Company;
import com.rolandopalermo.facturacion.ec.web.domain.SaleDocument;
import com.rolandopalermo.facturacion.ec.web.repositories.SaleDocumentRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleDocumentBO {

    private static final Logger log = Logger.getLogger(CompanyBO.class);
    
    @Autowired
    SaleDocumentRepository saleDocumentRepository;

    public SaleDocument getSaleDocumentById(int id) throws NegocioException {
        try {
            return saleDocumentRepository.findById(id).get();
        } catch (Exception e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public SaleDocument getSaleDocumentByDocumentId(int saleDocumentId) throws NegocioException {
        try {
            return saleDocumentRepository.findBySaleDocumentId(saleDocumentId).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new NegocioException("Comprobante electronico no registrado");
        } catch (Exception e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public List<SaleDocument> getSaleDocumentByCompany(int id) throws NegocioException {
        try {
            return saleDocumentRepository.findByCompanyId(id);
        } catch (Exception e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public byte [] getSaleDocumentFile(String path) throws NegocioException {
        try {
            File file = new File(path);
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public SaleDocument saveSaleDocument(Company company, int saleDocumentId, byte [] saleXML) throws NegocioException {
        try {
            
            // Save sale document in storage

            String directory = "saleDocuments/" + company.getRuc();
            String fileName = saleDocumentId + ".xml";

            // Crear directorios
            new File(directory).mkdirs();

            String path = directory + "/" + fileName;

            File file = new File(path);

            file.createNewFile();

            FileOutputStream oFile = new FileOutputStream(file, false);
            oFile.write(saleXML);
            oFile.close();

            // Save in database

            SaleDocument saleDocument = new SaleDocument();
            saleDocument.setCompany(company);
            saleDocument.setSaleDocumentPath(path);
            saleDocument.setSaleDocumentId(saleDocumentId);
            saleDocument.setVersion(1);
            
            return saleDocumentRepository.save(saleDocument);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NegocioException(e.getMessage());
        }
    }


}
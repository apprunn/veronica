package com.rolandopalermo.facturacion.ec.web.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public SaleDocument getLastSaleDocumentByDocumentId(int saleDocumentId) throws NegocioException {
        try {
            return saleDocumentRepository.findTopBySaleDocumentIdOrderByVersionDesc(saleDocumentId);
        } catch (IndexOutOfBoundsException e) {
            throw new NegocioException("Comprobante electronico no registrado");
        } catch (Exception e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public SaleDocument getLastSaleDocumentByClaveAcceso(String claveAcceso) throws NegocioException {
        try {
            return saleDocumentRepository.findTopByClaveAccesoOrderByVersionDesc(claveAcceso);
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

    public SaleDocument updateSaleDocument(SaleDocument saleDocument) throws NegocioException {
        try {
            return saleDocumentRepository.save(saleDocument);
        } catch (Exception e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public SaleDocument saveSaleDocument(
        Company company, 
        String claveAcceso,
        int saleDocumentId, 
        String documentCode, 
        byte [] saleXML, 
        byte [] saleSignedXml, 
        boolean override) throws NegocioException {
        try {

            SaleDocument result = saleDocumentRepository.findTopBySaleDocumentIdOrderByVersionDesc(saleDocumentId);
            
            SaleDocument saleDocument = new SaleDocument();

            int version = 1;

            if (result != null) {
                version = result.getVersion() + 1;

                int state = result.getSaleDocumentState();

                if (state == SaleDocument.ENVIADO) {
                    throw new NegocioException("Este comprobante ya fue autorizado");
                }

                if (!override) {
                    if (state == SaleDocument.PENDIENTE) {
                        throw new NegocioException("Existe un comprobante pendiente, agregue el parametro override = true para sobreescribirlo");
                    } else if (state == SaleDocument.ENVIADO) {
                        throw new NegocioException("Existe un comprobante preparado para autorizar, agregue el parametro override = true para sobreescribirlo");
                    }
                }

            }

            saleDocument.setVersion(version);
            
            // Save sale document in storage

            String directory = "saleDocuments/" + company.getRuc();
            String fileName = saleDocumentId + "_v" + saleDocument.getVersion() + ".xml";
            String fileNameSigned = saleDocumentId + "_v" + saleDocument.getVersion() + "_signed.xml";

            // Crear directorios
            new File(directory).mkdirs();

            String path = directory + "/" + fileName;
            String pathSigned = directory + "/" + fileNameSigned;

            createFile(saleXML, path);
            createFile(saleSignedXml, pathSigned);

            // Save in database
            saleDocument.setCompany(company);
            saleDocument.setSaleDocumentPath(pathSigned);
            saleDocument.setSaleDocumentId(saleDocumentId);
            saleDocument.setSaleDocumentCode(documentCode);
            saleDocument.setXml(saleSignedXml);
            saleDocument.setClaveAcceso(claveAcceso);
            
            return saleDocumentRepository.save(saleDocument);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NegocioException(e.getMessage());
        }
    }

    private void createFile(byte [] data, String path) throws IOException {
        File file = new File(path);
        file.createNewFile();

        FileOutputStream oFile = new FileOutputStream(file, false);
        oFile.write(data);
        oFile.close();
    }

}
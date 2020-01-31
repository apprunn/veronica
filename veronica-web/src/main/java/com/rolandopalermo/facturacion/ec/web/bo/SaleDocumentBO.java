package com.rolandopalermo.facturacion.ec.web.bo;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.rolandopalermo.facturacion.ec.common.exception.InternalServerException;
import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.manager.S3Manager;
import com.rolandopalermo.facturacion.ec.web.domain.Company;
import com.rolandopalermo.facturacion.ec.web.domain.SaleDocument;
import com.rolandopalermo.facturacion.ec.web.repositories.SaleDocumentRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleDocumentBO {

    private static final Logger logger = Logger.getLogger(CompanyBO.class);
    
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
            return saleDocumentRepository.findTopBySaleDocumentId(saleDocumentId);
        } catch (IndexOutOfBoundsException e) {
            throw new NegocioException("Comprobante electronico no registrado");
        } catch (Exception e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public SaleDocument getLastSaleDocumentByClaveAcceso(String claveAcceso) throws NegocioException {
        try {
            return saleDocumentRepository.findTopByClaveAcceso(claveAcceso);
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

            // Call sala docuemnt update state

        } catch (Exception e) {
            throw new NegocioException(e.getMessage());
        }
    }

    public SaleDocument saveSaleDocument(
        Company company, 
        String claveAcceso,
        int saleDocumentId, 
        String documentCode, 
        byte [] saleSignedXml) throws NegocioException {
        try {

            SaleDocument result = saleDocumentRepository.findTopBySaleDocumentId(saleDocumentId);
            
            SaleDocument saleDocument;

            int version = 1;

            if (result != null) {
                version = result.getVersion() + 1;

                int state = result.getSaleDocumentState();

                if (state == SaleDocument.AUTORIZADO) {
                    return result;
                }

                saleDocument = result;

            } else {
				saleDocument = new SaleDocument();
				saleDocument.setCreatedAt(new Date());
			}
			

            saleDocument.setVersion(version);

            // Save in database
            saleDocument.setCompany(company);
            saleDocument.setSaleDocumentId(saleDocumentId);
            saleDocument.setSaleDocumentState(SaleDocument.PENDIENTE);
            saleDocument.setSaleDocumentCode(documentCode);
			saleDocument.setClaveAcceso(claveAcceso);
			saleDocument.setUpdatedAt(new Date());

            String [] nameXml = S3Manager.getInstance().uploadFile(saleSignedXml, "xml");
            System.out.println(nameXml[0]);

            saleDocument.setS3File(nameXml[0]);
			saleDocument.setPublicURL(nameXml[1]);
			
			byte [] data = generateBarcode(claveAcceso);
			if (data.length > 0) {

				String [] barcodeFile = S3Manager.getInstance().uploadFile(data, "jpg");
				saleDocument.setBarcodeClaveAcceso(barcodeFile[1]);

			}

            return saleDocumentRepository.save(saleDocument);

        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerException(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new NegocioException(e.getMessage());
        }
    }
	private byte[] generateBarcode(String value) {

		int width = 240;
		int height = 32;

		MultiFormatWriter barcodeWriter = new MultiFormatWriter();
		BitMatrix barcodeBitMatrix;

		try {
			barcodeBitMatrix = barcodeWriter.encode(value, BarcodeFormat.CODE_128, width, height);
		} catch (WriterException e) {
			logger.error(e.getMessage());
			return new byte[0];
		}

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int color = barcodeBitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB();
				bufferedImage.setRGB(x, y, color);
			}
		}

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			ImageIO.write(bufferedImage, "jpg", baos);
			baos.flush();
	
			byte [] imageInByte = baos.toByteArray();

			return imageInByte;

		} catch (IOException e) {
			logger.error(e.getMessage());
		} 

		return new byte[0];


	}

}
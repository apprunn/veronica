package com.rolandopalermo.facturacion.ec.web.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.modelo.certificado.Certificado;
import com.rolandopalermo.facturacion.ec.web.domain.Company;
import com.rolandopalermo.facturacion.ec.web.repositories.CompanyRepository;
import com.rolandopalermo.facturacion.ec.web.services.ApiClient;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import okhttp3.ResponseBody;
import retrofit2.Response;

@Service
public class CompanyBO {

    private static final Logger log = Logger.getLogger(CompanyBO.class);

    @Autowired
    private CompanyRepository companyRepository;

    public boolean registerCompany(Certificado certificado, String salesURL) throws NegocioException {

        try {

            // Obtener Datos de compañia
            Company company = companyRepository.findByRuc(certificado.getRuc());

            if (company == null) {
                // Create new data
				company = new Company();
                company.setCreatedAt(new Date());
                
                // Validar que todos los datos existan

                String errorMessage = "";
                String format = "Se requiere %s para crear una compañia\n";

                if (certificado.getCompanyId() == 0) {
                    errorMessage += String.format(format, "Identificador de compañia");
                }

                if (certificado.getCompanyName() == null || certificado.getCompanyName().isEmpty()) {
                    errorMessage += String.format(format, "Nombre de compañia");
                }
                
                if (certificado.getBranchId() == 0) {
                    errorMessage += String.format(format, "Identificador de sucursal");
                }

                if (certificado.getCertificado() == null || certificado.getCertificado().length == 0) {
                    errorMessage += String.format(format, "Certificado");
                }

                if (certificado.getClave() == null || certificado.getClave().isEmpty()) {
                    errorMessage += String.format(format, "Clave de certificado");
                }

                if (!errorMessage.isEmpty()) {
                    throw new NegocioException(errorMessage);
                }

            }

            if (certificado.getCertificado() != null && certificado.getCertificado().length > 0) {

                // Almacenar p12 en un archivo
                String directory = "certificates/" + certificado.getRuc();
                String fileName = "certicate.p12";

                // Crear directorios
                new File(directory).mkdirs();

                String path = directory + "/" + fileName;

                File file = new File(path);

                file.createNewFile();

                FileOutputStream oFile = new FileOutputStream(file, false);
                oFile.write(certificado.getCertificado());
                oFile.close();

                company.setCertificatePath(path);
            }

            // Escribir campos de modelo

            if (certificado.getBranchId() > 0) {
                company.setBranchId(certificado.getBranchId());
            }

            if (certificado.getClave() != null && !certificado.getClave().isEmpty()) {
                company.setCertificateKey(certificado.getClave());
            }

            if (certificado.getRuc() != null && !certificado.getRuc().isEmpty()) {
                company.setRuc(certificado.getRuc());
            }

            if (certificado.getCompanyName() != null && !certificado.getCompanyName().isEmpty()) {
                company.setCompanyName(certificado.getCompanyName());
            }
            
            if (certificado.getCompanyId() > 0) {
                company.setCompanyId(certificado.getCompanyId());
            }

            company.setFlagEnvironment(certificado.getFlagEnvironment());

			company.setUpdatedAt(new Date());

            companyRepository.save(company);

            updateSubisidiaryFlagTaxes(salesURL, certificado.getRuc(), 1);

        } catch (DataIntegrityViolationException e) {
            updateSubisidiaryFlagTaxes(salesURL, certificado.getRuc(), 0);
            log.error(e.getMessage());
            return false;
        } catch (Exception e) {
            updateSubisidiaryFlagTaxes(salesURL, certificado.getRuc(), 0);
            log.error(e.getMessage());
            throw new NegocioException(e.getMessage());
        }

        return true;
    }

    @Nullable
    public Company getCompany(String ruc) throws NegocioException {
        try {
            return companyRepository.findByRuc(ruc);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NegocioException(e.getMessage());
        }
    }

    // TODO: Requiere hacer rollback en caso de que falle
    public void updateSubisidiaryFlagTaxes(String urlBase, String ruc, int flagTaxes) {

        Map<String, Object> body = new HashMap<>();
        body.put("flagTaxes", flagTaxes);

        try {
            Response<ResponseBody> response = ApiClient.getSaleApi(urlBase)
                            .updateFlagTaxes(ruc, body)
                            .execute();

            if (response.isSuccessful()) {
                System.out.println("SUBSIDIARIA ACTUALIZADA");
            } else {
                throw new NegocioException("No se conecto con el servidor");
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
	}
	

}
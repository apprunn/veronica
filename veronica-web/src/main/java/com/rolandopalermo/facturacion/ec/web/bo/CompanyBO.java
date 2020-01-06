package com.rolandopalermo.facturacion.ec.web.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rolandopalermo.facturacion.ec.bo.FirmadorBO;
import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.modelo.certificado.Certificado;
import com.rolandopalermo.facturacion.ec.web.domain.Company;
import com.rolandopalermo.facturacion.ec.web.repositories.CompanyRepository;
import com.rolandopalermo.facturacion.ec.web.services.ApiClient;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import okhttp3.ResponseBody;
import retrofit2.Response;

@Service
public class CompanyBO {

    private static final Logger log = Logger.getLogger(CompanyBO.class);

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private FirmadorBO firmadorBO;

    public Company registerCompany(Certificado certificado, String salesURL, String token) throws NegocioException {

        try {

            // Almacenar p12 en un archivo
            String directory = "certificates/" + certificado.getRuc();
            String fileName = "certicate.p12";
            String path = directory + "/" + fileName;

            if (certificado.getCertificado() != null && certificado.getCertificado().length > 0) {

                if (!firmadorBO.verifySignature(certificado.getCertificado(), certificado.getClave())) {
                    throw new NegocioException("INVALID_SIGNATURE_OR_PASSWORD");
                }

                // Crear directorios
                new File(directory).mkdirs();

                File file = new File(path);

                file.createNewFile();

                FileOutputStream oFile = new FileOutputStream(file, false);
                oFile.write(certificado.getCertificado());
                oFile.close();

            }

            updateSubisidiaryFlagTaxes(salesURL, certificado.getRuc(), 1, token);

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

            company.setCertificatePath(path);

            company.setFlagEnvironment(certificado.getFlagEnvironment());

            company.setUpdatedAt(new Date());

            companyRepository.save(company);

            return company;

        } catch (Exception e) {

            if (e.getMessage().equals("JAPISALE_NOT_RESPONSE") || e.getMessage().contains("JAPISALE_NOT_FAILED")) {
                updateSubisidiaryFlagTaxes(salesURL, certificado.getRuc(), 0, token);
            }

            log.error(e.getMessage());
            
            throw new NegocioException(e.getMessage());
        }

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

    public void updateSubisidiaryFlagTaxes(String urlBase, String ruc, int flagTaxes, String token) throws NegocioException {

        Map<String, Object> body = new HashMap<>();
        body.put("flagTaxes", flagTaxes);

        try {
            Response<ResponseBody> response = ApiClient.getSaleApi(urlBase).updateFlagTaxes(ruc, body, token).execute();

            if (!response.isSuccessful()) {
                throw new NegocioException("JAPISALE_NOT_FAILED : " + response.code() + "\n" + response.errorBody().string());
            }

        } catch (IOException e) {
            
            throw new NegocioException("JAPISALE_NOT_RESPONSE");

        }


    }

}
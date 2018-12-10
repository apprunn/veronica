package com.rolandopalermo.facturacion.ec.web.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.modelo.certificado.Certificado;
import com.rolandopalermo.facturacion.ec.web.domain.Company;
import com.rolandopalermo.facturacion.ec.web.repositories.CompanyRepository;
import com.rolandopalermo.facturacion.ec.web.services.ApiClient;
import com.rolandopalermo.facturacion.ec.web.util.Util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

            // Almacenar datos de compañia en la base de datos
            List<Company> result = companyRepository.findByRuc(certificado.getRuc());

            Company company;

            if (result.isEmpty()) {
                // Create new data
				company = new Company();
				company.setCreatedAt(Util.getCurrentDateString());
            } else {
                // Update company
                company = result.get(0);
            }

            company.setBranchId(certificado.getBranchId());
            company.setCertificatePath(path);
            company.setCertificateKey(certificado.getClave());
            company.setRuc(certificado.getRuc());
            company.setCompanyName(certificado.getCompanyName());
			company.setCompanyId(certificado.getCompanyId());
			company.setUpdatedAt(Util.getCurrentDateString());

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

    public Company getCompany(String ruc) throws NegocioException {
        try {
            return companyRepository.findByRuc(ruc).get(0);
        } catch (IndexOutOfBoundsException e) {
            log.error(e.getMessage());
            throw new NegocioException("Compañia no encontrada");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NegocioException(e.getMessage());
        }
    }

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
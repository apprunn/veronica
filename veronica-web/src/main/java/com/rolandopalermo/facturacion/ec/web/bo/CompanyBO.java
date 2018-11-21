package com.rolandopalermo.facturacion.ec.web.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.modelo.certificado.Certificado;
import com.rolandopalermo.facturacion.ec.web.domain.Company;
import com.rolandopalermo.facturacion.ec.web.repositories.CompanyRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class CompanyBO {

    private static final Logger log = Logger.getLogger(CompanyBO.class);

    @Autowired
    private CompanyRepository companyRepository;

    public boolean registerCompany(Certificado certificado) throws NegocioException {

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

            companyRepository.save(company);

        } catch (DataIntegrityViolationException e) {
            return false;
        } catch (Exception e) {
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

}
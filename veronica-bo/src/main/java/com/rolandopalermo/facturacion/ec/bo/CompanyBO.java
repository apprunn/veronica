package com.rolandopalermo.facturacion.ec.bo;

import java.util.List;

import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.modelo.certificado.Certificado;
import com.rolandopalermo.facturacion.ec.domain.Company;

import com.rolandopalermo.facturacion.ec.bo.repository.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyBO {

    private static final Logger log = Logger.getLogger(CompanyBO.class);

    @Autowired
    private CompanyRepository companyRepository;

    public boolean registerCompany(Certificado certificado) throws NegocioException {

        try {

            // Almacenar p12 en un archivo

            String path = "\\sdfas\\sdffa";

            // Almacenar datos de compañia en la base de datos

            Company company = new Company();
            company.setBranchId(1);
            company.setCertificate(path);
            company.setCertificateKey(certificado.getClave());
            company.setRuc("2314124124");
            company.setCompanyName("App runn sac");
            company.setCompanyId(1);

            companyRepository.save(company);

        } catch (Exception e) {
            e.printStackTrace();
            throw new NegocioException(e.getMessage());
        }

        return true;
    }

    public List<Company> getCompany(int companyId) {
        return companyRepository.findByCompanyId(companyId);
    }

	// @Bean
	// public CommandLineRunner demo(final CompanyRepository repository) {

	// 	return new CommandLineRunner(){
		
	// 		@Override
	// 		public void run(String... args) throws Exception {
				
	// 			List<Company> companies = repository.findByCompanyId(1);

	// 			log.info("COMPANY FOUND WITH FIND BY COMPANY ID");
	// 			log.info(companies.get(0).toString());

	// 		}

	// 	};

	// }

}
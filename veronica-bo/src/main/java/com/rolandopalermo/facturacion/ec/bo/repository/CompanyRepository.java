package com.rolandopalermo.facturacion.ec.bo.repository;

import java.util.List;
import com.rolandopalermo.facturacion.ec.domain.Company;
import org.springframework.data.repository.CrudRepository;

public interface CompanyRepository extends CrudRepository<Company, Integer> {

    List<Company> findByCompanyId(int companyId);

}
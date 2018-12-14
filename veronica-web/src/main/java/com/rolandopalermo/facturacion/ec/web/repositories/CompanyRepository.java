package com.rolandopalermo.facturacion.ec.web.repositories;

import com.rolandopalermo.facturacion.ec.web.domain.Company;

import org.springframework.data.repository.CrudRepository;

public interface CompanyRepository extends CrudRepository<Company, Integer> {

    Company findByRuc(String ruc);

} 
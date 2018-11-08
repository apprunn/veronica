package com.rolandopalermo.facturacion.ec.web.repositories;

import java.util.List;

import com.rolandopalermo.facturacion.ec.web.domain.SaleDocument;

import org.springframework.data.repository.CrudRepository;

public interface SaleDocumentRepository extends CrudRepository<SaleDocument, Integer> {

    List<SaleDocument> findByCompanyId(int companyId);

    List<SaleDocument> findBySaleDocumentId(int saleDocumentId);

}
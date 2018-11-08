package com.rolandopalermo.facturacion.ec.web.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "fact_sale_document")
public class SaleDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotNull
    @ManyToOne
    private Company company;
    @NotNull
    private int saleDocumentId;
    @NotNull
    private String saleDocumentPath;
    private String saleDocumentCode;
    private int saleDocumentState;
    private int version = 1;

}
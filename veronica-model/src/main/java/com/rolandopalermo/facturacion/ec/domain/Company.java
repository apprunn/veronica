package com.rolandopalermo.facturacion.ec.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "fact_company")
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Integer id;
    protected String certificate;
    protected String certificateKey;
    protected Integer companyId;
    protected String companyName;
    protected String ruc;
    protected Integer branchId;

}
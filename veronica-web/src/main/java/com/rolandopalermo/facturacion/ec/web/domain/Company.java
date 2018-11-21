package com.rolandopalermo.facturacion.ec.web.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "fact_company")
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ruc"})
})
public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Integer id;
    @NotNull
    protected String certificatePath;
    @NotNull
    protected String certificateKey;
    @NotNull
    protected Integer companyId;
    @NotNull
    protected String companyName;
    @NotNull
    protected String ruc;
    @NotNull
    protected Integer branchId;

}
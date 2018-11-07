package com.rolandopalermo.facturacion.ec.modelo.certificado;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Certificado {

    @NotEmpty
    protected byte[] certificado;
    
    @NotEmpty 
    protected String clave;

    protected int companyId;

    protected int branchId;

    @NotEmpty
    protected String companyName;

    @NotEmpty
    protected String ruc;
}
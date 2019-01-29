package com.rolandopalermo.facturacion.ec.modelo.certificado;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Certificado {

    protected byte[] certificado;
    
    protected String clave;

    protected int companyId;

    protected int branchId;

    protected String companyName;

    @NotEmpty
    protected String ruc;

    @NotEmpty // Flag environment must be not Empty because int variable take 0
    protected int flagEnvironment;


}
package com.rolandopalermo.facturacion.ec.modelo.certificado;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Certificado {

    @NotEmpty
    protected String certificado;
    
    @NotEmpty 
    protected String clave;

    protected int companyId;

}
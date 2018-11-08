package com.rolandopalermo.facturacion.ec.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceptionStorageDTO {

    @NotEmpty
    private String ruc;
    private int saleDocumentId;

}
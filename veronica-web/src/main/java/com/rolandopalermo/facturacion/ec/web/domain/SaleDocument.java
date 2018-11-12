package com.rolandopalermo.facturacion.ec.web.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name = "fact_sale_document")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"saleDocumentId", "version"}))
@JsonIgnoreProperties(value = {"company", "xml"})
public class SaleDocument {

    public static final int PENDIENTE = 1;
    public static final int ENVIADO = 2;
    public static final int AUTORIZADO = 3;
    public static final int INCORRECTO = 4;
    public static final int NO_AUTORIZADO = 5;
    
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
    @NotNull
    private String claveAcceso;

    /*
    1 -> Pendiente
    2 -> Enviado
    3 -> Autorizado
    4 -> Incorrecto
     */
    private int saleDocumentState = PENDIENTE;
    private int version;

    private String s3File;

    // @Lob
    // @Basic(fetch = FetchType.LAZY)
    // private byte [] xml;

}
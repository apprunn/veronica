package com.rolandopalermo.facturacion.ec.web.controller;

import javax.validation.Valid;

import com.rolandopalermo.facturacion.ec.bo.CompanyBO;
import com.rolandopalermo.facturacion.ec.common.exception.InternalServerException;
import com.rolandopalermo.facturacion.ec.dto.GenericResponse;
import com.rolandopalermo.facturacion.ec.modelo.certificado.Certificado;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/api/v1/register")
@Api(description = "Permite registrar compañia")
public class RegisterCertificateController {

    private static final Logger logger = Logger.getLogger(RegisterCertificateController.class);

    @Autowired
    private CompanyBO companyBO;

    @ApiOperation(value = "Registra un certificado y clave de una respectiva compañia")
    @PostMapping(value = "/certificate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericResponse<String>> registrarCompany(
        @Valid
        @ApiParam(value = "Certificado, clave y id de compañia", required = true)
        @RequestBody Certificado certificado
    ) {
        try {
            boolean success = companyBO.registerCompany(certificado);
            return new ResponseEntity<GenericResponse<String>>(new GenericResponse<String>("Success: " + success), HttpStatus.OK);
        } catch (Exception e) {
            throw new InternalServerException(e.getMessage());
        }
    }

}
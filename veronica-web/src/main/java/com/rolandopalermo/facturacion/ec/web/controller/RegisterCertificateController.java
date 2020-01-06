package com.rolandopalermo.facturacion.ec.web.controller;

import java.util.Map;

import javax.validation.Valid;

import com.rolandopalermo.facturacion.ec.common.exception.BadRequestException;
import com.rolandopalermo.facturacion.ec.common.exception.InternalServerException;
import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.common.exception.ResourceNotFoundException;
import com.rolandopalermo.facturacion.ec.modelo.certificado.Certificado;
import com.rolandopalermo.facturacion.ec.web.bo.CompanyBO;
import com.rolandopalermo.facturacion.ec.web.domain.Company;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/api/v1/register")
@Api(description = "Controlador de compañias")
@CrossOrigin
public class RegisterCertificateController {

    private static final Logger logger = Logger.getLogger(RegisterCertificateController.class);

    @Autowired
    private CompanyBO companyBO;

	@Value("${sales.ruta}")
    private String baseURL;

    @ApiOperation(value = "Registra o actualiza una compañia")
    @PostMapping(value = "/company", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Company> registrarCompany(
        @Valid
        @ApiParam(value = "Certificado, clave y id de compañia", required = true)
        @RequestBody Certificado certificado,
        @RequestHeader Map<String, String> header
    ) {
        try {

            String token = header.get("authorization");
            Company company = companyBO.registerCompany(certificado, baseURL, token);
            return new ResponseEntity<>(company, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }
    }

    @ApiOperation(value = "Obtener la compañia")
    @GetMapping(value = "/company", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Company> obtenerCompany(
        @Valid
        @ApiParam(value = "RUC de companñia")
        @RequestParam String ruc
    ) {
        try {
            Company company = companyBO.getCompany(ruc);

            logger.debug("Buscar compania: " + ruc);

            if (company == null) {
                throw new NegocioException("Este RUC no existe en el sistema");
            }

            return new ResponseEntity<>(company, HttpStatus.OK);
        } catch (NegocioException e) {
            logger.error("REGISTER NOT FOUND", e);
            throw new ResourceNotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("REGISTER ERROR", e);
            throw new InternalServerException(e.getMessage());
        }
    }

}
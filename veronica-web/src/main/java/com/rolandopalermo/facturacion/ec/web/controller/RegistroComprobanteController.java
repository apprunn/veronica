package com.rolandopalermo.facturacion.ec.web.controller;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.rolandopalermo.facturacion.ec.bo.FirmadorBO;
import com.rolandopalermo.facturacion.ec.bo.GeneradorBO;
import com.rolandopalermo.facturacion.ec.common.exception.BadRequestException;
import com.rolandopalermo.facturacion.ec.common.exception.InternalServerException;
import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.common.exception.ResourceNotFoundException;
import com.rolandopalermo.facturacion.ec.manager.SQSManager;
import com.rolandopalermo.facturacion.ec.modelo.ComprobanteElectronico;
import com.rolandopalermo.facturacion.ec.modelo.factura.Factura;
import com.rolandopalermo.facturacion.ec.modelo.guia.GuiaRemision;
import com.rolandopalermo.facturacion.ec.modelo.notacredito.NotaCredito;
import com.rolandopalermo.facturacion.ec.modelo.notadebito.NotaDebito;
import com.rolandopalermo.facturacion.ec.modelo.retencion.ComprobanteRetencion;
import com.rolandopalermo.facturacion.ec.web.bo.CompanyBO;
import com.rolandopalermo.facturacion.ec.web.bo.SaleDocumentBO;
import com.rolandopalermo.facturacion.ec.web.domain.Company;
import com.rolandopalermo.facturacion.ec.web.domain.SaleDocument;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import static com.rolandopalermo.facturacion.ec.common.util.Constantes.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/register")
@Api(description = "Genera comprobante electrónico como XML, firma XML y encola envio de comprobante a la SRI")
public class RegistroComprobanteController {

	private static final Logger logger = Logger.getLogger(GeneracionController.class);

	@Autowired
	private GeneradorBO generadorBO;

	@Autowired
	private FirmadorBO firmadorBO;

	@Autowired
	private CompanyBO companyBO;

	@Autowired
	private SaleDocumentBO saleDocumentBO;

	@Resource(name = "sqs_manager")
	SQSManager sqsManager;

	@ApiOperation(value = "Genera,firmar y encola envio de factura en formato XML")
	@PostMapping(value = "/factura", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SaleDocument> generarFactura(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody Factura request,
			@RequestParam int saleDocumentId) {
		
		logger.debug("Factura: SaleDocument - " + saleDocumentId);
		return generarDocumentoElectronico(request, saleDocumentId, "FAC");
	}

	@ApiOperation(value = "Genera,firmar y encola envio de  guía de remisión en formato XML")
	@PostMapping(value = "/guia-remision", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SaleDocument> generarGuiaRemision(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody GuiaRemision request,
			@RequestParam int saleDocumentId) {
		
		logger.debug("Guia de remision: SaleDocument - " + saleDocumentId);
		return generarDocumentoElectronico(request, saleDocumentId, "GRM");
	}

	@ApiOperation(value = "Genera,firmar y encola envio de  nota de crédito en formato XML")
	@PostMapping(value = "/nota-credito", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SaleDocument> generarNotaCredito(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody NotaCredito request,
			@RequestParam int saleDocumentId) {

		logger.debug("Nota de credito: SaleDocument - " + saleDocumentId);
		return generarDocumentoElectronico(request, saleDocumentId, "NTC");
	}

	@ApiOperation(value = "Genera,firmar y encola envio de  nota de débito en formato XML")
	@PostMapping(value = "/nota-debito", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SaleDocument> generarNotaDebito(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody NotaDebito request,
			@RequestParam int saleDocumentId) {
			
		
		logger.debug("Nota de debito: SaleDocument - " + saleDocumentId);
		return generarDocumentoElectronico(request, saleDocumentId, "NTD");
	}

	@ApiOperation(value = "Genera,firmar y encola envio de n comprobante de retención en formato XML")
	@PostMapping(value = "/comprobante-retencion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SaleDocument> generarComprobanteRetencion(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody ComprobanteRetencion request,
			@RequestParam int saleDocumentId) {

		logger.debug("Comprobante de remision: SaleDocument - " + saleDocumentId);
		return generarDocumentoElectronico(request, saleDocumentId, "CRT");
	}

	private ResponseEntity<SaleDocument> generarDocumentoElectronico(ComprobanteElectronico request, int saleDocumentId, String documentCode) {

		Company company = obtenerCompany(request.getInfoTributaria().getRuc());

		byte[] content = convertirAXML(request);

		logger.debug("Formato correcto: saleDocument: " + saleDocumentId);

		byte[] signedContent = firmarComprobanteElectronico(content, company);

		logger.debug("Firma exitosa: SaleDocument: " + saleDocumentId);

		SaleDocument saleDocument = actualizarSaleDocument(company, request.getInfoTributaria().getClaveAcceso(), saleDocumentId, documentCode, signedContent);

		logger.debug("Actualizacion correcta: SaleDocument: " + saleDocumentId);

		// Send Message to SQS
		Map<String, String> message = new HashMap<>();

		message.put("ruc", company.getRuc());
		message.put("saleDocumentId", String.valueOf(saleDocumentId));
		message.put("companyId", String.valueOf(company.getCompanyId()));

		String messageGroupId = String.format("group_%d_%d_%d", saleDocument.getId(), company.getCompanyId(), saleDocument.getSaleDocumentId());

		Gson gson = new Gson();
		String strMessage = gson.toJson(message);

		sqsManager.sendMessage(strMessage, messageGroupId);

		logger.debug("Se envio el documento al SQS: SaleDocument: " + saleDocumentId);

		return new ResponseEntity<SaleDocument>(saleDocument, HttpStatus.OK);

	}

	@NotNull
	private Company obtenerCompany(String ruc) {
		// Obtener Company
		try {
			Company company = companyBO.getCompany(ruc);

			if (company == null) {
				throw new NegocioException("No se encontro compañia");
			}

			return company;
		} catch (NegocioException e) {
			logger.error("Obtener Compañia", e);
			throw new ResourceNotFoundException("No se encontro compañia");
		} catch (Exception e) {
			logger.error("generarDocumentoElectronico", e);
			throw new InternalServerException(e.getMessage());
		}
	}

	private  byte[] convertirAXML(ComprobanteElectronico request) {
		try {
			return generadorBO.generarXMLDocumentoElectronico(request);
		} catch (NegocioException e) {
			logger.error("Generar XML", e);
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			logger.error("generarDocumentoElectronico", e);
			throw new InternalServerException(e.getMessage());
		}
	}

	private byte[] firmarComprobanteElectronico(byte [] request, Company company) {

		if (!new File(company.getCertificatePath()).exists()) {
			throw new ResourceNotFoundException("No se pudo encontrar el certificado de firma digital.");
		}
		try {
			byte[] content = firmadorBO.firmarComprobanteElectronico(request, company.getCertificatePath(), company.getCertificateKey());
			return content;
		} catch (NegocioException e) {
			logger.error("firmarComprobanteElectronico", e);
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			logger.error("firmarComprobanteElectronico", e);
			throw new InternalServerException(e.getMessage());
		}
	}

	private SaleDocument actualizarSaleDocument(
		Company company, 
		String claveAcceso,
		int saleDocumentId, 
		String documentCode,
		byte [] saleSignedXml) {
		try {
			return saleDocumentBO.saveSaleDocument(company, claveAcceso, saleDocumentId, documentCode, saleSignedXml);
		} catch (NegocioException e) {
			logger.error("generarDocumentoElectronico", e);
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			logger.error("generarDocumentoElectronico", e);
			throw new InternalServerException(e.getMessage());
		}
	}

}
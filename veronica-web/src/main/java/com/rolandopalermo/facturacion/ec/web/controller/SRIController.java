package com.rolandopalermo.facturacion.ec.web.controller;

import static com.rolandopalermo.facturacion.ec.common.util.Constantes.API_DOC_ANEXO_1;

import java.io.File;
import java.io.StringWriter;
import java.util.Base64;

import javax.validation.Valid;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rolandopalermo.facturacion.ec.bo.SriBO;
import com.rolandopalermo.facturacion.ec.common.exception.BadRequestException;
import com.rolandopalermo.facturacion.ec.common.exception.InternalServerException;
import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.common.exception.ResourceNotFoundException;
import com.rolandopalermo.facturacion.ec.config.SQSServiceConfig;
import com.rolandopalermo.facturacion.ec.dto.AutorizacionRequestDTO;
import com.rolandopalermo.facturacion.ec.dto.RecepcionRequestDTO;
import com.rolandopalermo.facturacion.ec.dto.ReceptionStorageDTO;
import com.rolandopalermo.facturacion.ec.manager.S3Manager;
import com.rolandopalermo.facturacion.ec.modelo.ComprobanteElectronico;
import com.rolandopalermo.facturacion.ec.modelo.factura.Factura;
import com.rolandopalermo.facturacion.ec.modelo.guia.GuiaRemision;
import com.rolandopalermo.facturacion.ec.modelo.notacredito.NotaCredito;
import com.rolandopalermo.facturacion.ec.modelo.notadebito.NotaDebito;
import com.rolandopalermo.facturacion.ec.modelo.retencion.ComprobanteRetencion;
import com.rolandopalermo.facturacion.ec.web.bo.SaleDocumentBO;
import com.rolandopalermo.facturacion.ec.web.domain.SaleDocument;

import autorizacion.ws.sri.gob.ec.Autorizacion;
import autorizacion.ws.sri.gob.ec.AutorizacionComprobanteResponse;
import autorizacion.ws.sri.gob.ec.RespuestaComprobante;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import recepcion.ws.sri.gob.ec.Mensaje;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;

@RestController
@RequestMapping(value = "/api/v1/sri")
@Api(description = "Permite enviar o autorizar un comprobante electrónico.")
public class SRIController {

	private static final Logger logger = Logger.getLogger(SRIController.class);

	@Autowired
	private SriBO sriBO;

	@Autowired 
	SaleDocumentBO saleDocumentBO;

	@Value("${pkcs12.certificado.ruta}")
	private String rutaArchivoPkcs12;

	@Value("${pkcs12.certificado.clave}")
	private String claveArchivopkcs12;

	@Value("${sri.wsdl.recepcion}")
	private String wsdlRecepcion;

	@Value("${sri.wsdl.autorizacion}")
	private String wsdlAutorizacion;

	@Value("${sales.ruta}")
    private String baseURL;

	@ApiOperation(value = "Envía un comprobante electrónico a validar al SRI")
	@PostMapping(value = "/enviar", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RespuestaSolicitud> enviarComprobante(
			@ApiParam(value = "Comprobante electrónico codificado como base64", required = true) 
			@RequestBody RecepcionRequestDTO request) {
		if (!new File(rutaArchivoPkcs12).exists()) {
			throw new ResourceNotFoundException("No se pudo encontrar el certificado de firma digital.");
		}
		try {
			return new ResponseEntity<RespuestaSolicitud>(
					sriBO.enviarComprobante(request.getContenido(), wsdlRecepcion), HttpStatus.OK);
		} catch (NegocioException e) {
			logger.error("enviarComprobante", e);
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			logger.error("enviarComprobante", e);
			throw new InternalServerException(e.getMessage());
		}
	}

	@ApiOperation(value = "Envía un comprobante electronico almacenado a validar al SRI y lo autoriza")
	@PostMapping(value = "/enviar-storage", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RespuestaSolicitud> enviarComprobanteAlmacenado(
		@ApiParam(value = "Parametros de compañia y comprobante electronico", required = true)
		@RequestBody ReceptionStorageDTO request) {

		try {
			SaleDocument saleDocument = saleDocumentBO.getLastSaleDocumentByDocumentId(request.getSaleDocumentId());

			switch (saleDocument.getSaleDocumentState()) {
				case SaleDocument.INCORRECTO:
				throw new NegocioException("El documento es incorrecto, subir uno nuevo");
				case SaleDocument.ENVIADO:
				throw new NegocioException("El documento ya fue enviado");
				case SaleDocument.AUTORIZADO:
				throw new NegocioException("El documento ya fue autorizado");
			}
			
			byte [] contenido = S3Manager.getInstance().downloadFile(saleDocument.getS3File());
			
			RespuestaSolicitud respuestaSolicitud = sriBO.enviarComprobante(contenido, wsdlRecepcion);
			
			String estado = respuestaSolicitud.getEstado();
			if (estado.equals("DEVUELTA")) {
				String message = estado + ":\n";
				for (Mensaje mensaje : respuestaSolicitud.getComprobantes().getComprobante().get(0).getMensajes().getMensaje()) {
					message += mensaje.getMensaje() + "\n";
				}

				saleDocument.setSaleDocumentState(SaleDocument.INCORRECTO);
				saleDocumentBO.updateSaleDocument(saleDocument);

				throw new NegocioException(message);
			}

			saleDocument.setSaleDocumentState(SaleDocument.ENVIADO);
			saleDocumentBO.updateSaleDocument(saleDocument);

			return new ResponseEntity<RespuestaSolicitud>(respuestaSolicitud, HttpStatus.OK);
		} catch (NegocioException e) {
			logger.error("enviarComprobante", e);
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			logger.error("enviarComprobante", e);
			throw new InternalServerException(e.getMessage());
		}
	}

	@ApiOperation(value = "Solicita la validación de una clave de acceso")
	@PostMapping(value = "/autorizar", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RespuestaComprobante> autorizarComprobante(
			@ApiParam(value = "Clave de acceso del comprobante electrónico", required = true) 
			@RequestBody AutorizacionRequestDTO request) {
		try {

			SaleDocument saleDocument = saleDocumentBO.getLastSaleDocumentByClaveAcceso(request.getClaveAcceso());

			if (saleDocument == null) {
				throw new NegocioException("No hay documento registrado");
			} 

			RespuestaComprobante respuestaComprobante = sriBO.autorizarComprobante(request.getClaveAcceso(), wsdlAutorizacion);
			
			if (saleDocument.getSaleDocumentState() == SaleDocument.AUTORIZADO) {
				return new ResponseEntity<RespuestaComprobante>(
					respuestaComprobante, HttpStatus.OK);
			}


			if (!respuestaComprobante.getNumeroComprobantes().equals("0")) {
				Autorizacion autorizacion =  respuestaComprobante.getAutorizaciones().getAutorizacion().get(0);
				String estado = autorizacion.getEstado();
				if (estado.equals("AUTORIZADO")) {
					saleDocument.setSaleDocumentState(SaleDocument.AUTORIZADO);

					StringWriter sw = new StringWriter();
					JAXBContext jaxbContext = JAXBContext.newInstance(AutorizacionComprobanteResponse.class);
					Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
					AutorizacionComprobanteResponse au = new AutorizacionComprobanteResponse();
					au.setRespuestaAutorizacionComprobante(respuestaComprobante);
					jaxbMarshaller.marshal(au, sw);
					String xmlString = sw.toString();
					xmlString = xmlString.replace("&lt;", "<").replace("&gt;", ">").replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
					byte [] data = xmlString.getBytes("utf-8");

					// TODO: ENVIAR AL S3
					String name = S3Manager.getInstance().uploadFile(data);
					saleDocument.setS3File(name);

				} else {
					saleDocument.setSaleDocumentState(SaleDocument.NO_AUTORIZADO);
				}
			} else {
				saleDocument.setSaleDocumentState(SaleDocument.INCORRECTO);
			}

			saleDocumentBO.updateSaleDocument(saleDocument);

			return new ResponseEntity<RespuestaComprobante>(
				respuestaComprobante, HttpStatus.OK);
		} catch (NegocioException e) {
			logger.error("autorizarComprobante", e);
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			logger.error("autorizarComprobante", e);
			throw new InternalServerException(e.getMessage());
		}
	}

	@ApiOperation(value = "Genera, firma, envía y autoriza una factura electrónica")
	@PostMapping(value = "/emitir/factura", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RespuestaComprobante> emitirFactura(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody Factura request) {
		return emitirDocumentoElectronico(request);
	}

	@ApiOperation(value = "Genera, firma, envía y autoriza una  guía de remisión")
	@PostMapping(value = "/emitir/guia-remision", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RespuestaComprobante> emitirGuiaRemision(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody GuiaRemision request) {
		return emitirDocumentoElectronico(request);
	}

	@ApiOperation(value = "Genera, firma, envía y autoriza una nota de crédito")
	@PostMapping(value = "/emitir/nota-credito", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RespuestaComprobante> emitirNotaCredito(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody NotaCredito request) {
		return emitirDocumentoElectronico(request);
	}

	@ApiOperation(value = "Genera, firma, envía y autoriza una nota de débito")
	@PostMapping(value = "/emitir/nota-debito", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RespuestaComprobante> emitirNotaDebito(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody NotaDebito request) {
		return emitirDocumentoElectronico(request);
	}

	@ApiOperation(value = "Genera, firma, envía y autoriza un comprobante de retención")
	@PostMapping(value = "/emitir/comprobante-retencion", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RespuestaComprobante> emitirComprobanteRetencion(
			@Valid
			@ApiParam(value = API_DOC_ANEXO_1, required = true) 
			@RequestBody ComprobanteRetencion request) {
		return emitirDocumentoElectronico(request);
	}

	private ResponseEntity<RespuestaComprobante> emitirDocumentoElectronico(ComprobanteElectronico request) {
		try {
			return new ResponseEntity<RespuestaComprobante>(sriBO.emitirComprobante(request, rutaArchivoPkcs12,
					claveArchivopkcs12, wsdlRecepcion, wsdlAutorizacion), HttpStatus.OK);
		} catch (NegocioException e) {
			logger.error("emitirDocumentoElectronico", e);
			throw new BadRequestException(e.getMessage());
		} catch (Exception e) {
			logger.error("emitirDocumentoElectronico", e);
			throw new InternalServerException(e.getMessage());
		}
	}

}
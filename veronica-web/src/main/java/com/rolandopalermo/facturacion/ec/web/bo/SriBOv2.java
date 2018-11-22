package com.rolandopalermo.facturacion.ec.web.bo;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.amazonaws.AmazonServiceException;
import com.rolandopalermo.facturacion.ec.bo.SriBO;
import com.rolandopalermo.facturacion.ec.common.exception.NegocioException;
import com.rolandopalermo.facturacion.ec.dto.AutorizacionRequestDTO;
import com.rolandopalermo.facturacion.ec.dto.ReceptionStorageDTO;
import com.rolandopalermo.facturacion.ec.manager.S3Manager;
import com.rolandopalermo.facturacion.ec.web.domain.SaleDocument;
import com.rolandopalermo.facturacion.ec.web.services.ApiClient;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import autorizacion.ws.sri.gob.ec.Autorizacion;
import autorizacion.ws.sri.gob.ec.AutorizacionComprobanteResponse;
import autorizacion.ws.sri.gob.ec.RespuestaComprobante;
import okhttp3.ResponseBody;
import recepcion.ws.sri.gob.ec.Mensaje;
import recepcion.ws.sri.gob.ec.RespuestaSolicitud;
import retrofit2.Response;

@Service
public class SriBOv2 {

	private static final Logger logger = Logger.getLogger(SriBOv2.class);

    @Autowired
    SriBO sriBO;

    @Autowired
    SaleDocumentBO saleDocumentBO;

    public RespuestaSolicitud enviarDocumento(ReceptionStorageDTO request, String wsdlRecepcion, String urlBase) throws NegocioException {
        SaleDocument saleDocument = saleDocumentBO.getLastSaleDocumentByDocumentId(request.getSaleDocumentId());

        switch (saleDocument.getSaleDocumentState()) {
            case SaleDocument.INCORRECTO:
            throw new NegocioException("El documento es incorrecto, subir uno nuevo", SaleDocument.INCORRECTO);
            case SaleDocument.ENVIADO:
            throw new NegocioException("El documento ya fue enviado", SaleDocument.ENVIADO);
            case SaleDocument.AUTORIZADO:
            throw new NegocioException("El documento ya fue autorizado", SaleDocument.AUTORIZADO);
        }
        
        byte [] contenido = S3Manager.getInstance().downloadFile(saleDocument.getS3File());
        
        RespuestaSolicitud respuestaSolicitud = sriBO.enviarComprobante(contenido, wsdlRecepcion);
        
        String estado = respuestaSolicitud.getEstado();
        if (estado.equals("DEVUELTA") && !respuestaSolicitud.getComprobantes().getComprobante().isEmpty()) {
            String message = estado + ":\n";
            for (Mensaje mensaje : respuestaSolicitud.getComprobantes().getComprobante().get(0).getMensajes().getMensaje()) {
                message += mensaje.getMensaje() + "\n";
            }

            saleDocument.setSaleDocumentState(SaleDocument.INCORRECTO);
            saleDocumentBO.updateSaleDocument(saleDocument);

            String mensaje = respuestaSolicitud.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getMensaje();
            String aditional = respuestaSolicitud.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getInformacionAdicional();

            actualizarDocumentoSale(urlBase, saleDocument, 5, mensaje);

            logger.error(saleDocument.getSaleDocumentId());
            logger.error(mensaje);
            logger.error(aditional);

            throw new NegocioException(message, SaleDocument.INCORRECTO);
        }

        saleDocument.setSaleDocumentState(SaleDocument.ENVIADO);
        saleDocumentBO.updateSaleDocument(saleDocument);
        actualizarDocumentoSale(urlBase, saleDocument, 4, "ENVIADO");

        return respuestaSolicitud;
    }

    public RespuestaComprobante autorizar(AutorizacionRequestDTO request, String wsdlAutorizacion, String urlBase) throws NegocioException, JAXBException, AmazonServiceException, IOException {
        SaleDocument saleDocument = saleDocumentBO.getLastSaleDocumentByClaveAcceso(request.getClaveAcceso());

        if (saleDocument == null) {
            throw new NegocioException("No hay documento registrado", 0);
        } 

        RespuestaComprobante respuestaComprobante = sriBO.autorizarComprobante(request.getClaveAcceso(), wsdlAutorizacion);
        
        if (saleDocument.getSaleDocumentState() == SaleDocument.AUTORIZADO) {
            return respuestaComprobante;
        }

        if (!respuestaComprobante.getAutorizaciones().getAutorizacion().isEmpty()) {
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

                String [] name = S3Manager.getInstance().uploadFile(data);

                System.out.println("DOCUMENT FUE ALMACENADO");

                saleDocument.setS3File(name[0]);
                saleDocument.setPublicURL(name[1]);

                actualizarDocumentoSale(urlBase, saleDocument, 7, "EXITO");

            } else {
                // TODO REVISAR ESTE ESPACIO
                List<autorizacion.ws.sri.gob.ec.Mensaje> messages = autorizacion.getMensajes().getMensaje();

                String strMessage = "";

                if (messages.isEmpty()) {
                    strMessage = "No message";
                } else {
                    for (autorizacion.ws.sri.gob.ec.Mensaje m : messages) {
                        strMessage += "mensaje: " + m.getMensaje() + "\n";
                        strMessage += "adicional" + m.getInformacionAdicional() + "\n";
                    }
                }

                actualizarDocumentoSale(urlBase, saleDocument, 8, strMessage);
                saleDocument.setSaleDocumentState(SaleDocument.NO_AUTORIZADO);

                logger.error(saleDocument.getSaleDocumentId());
                logger.error(strMessage);

            }
        } else {
            actualizarDocumentoSale(urlBase, saleDocument, 1, "DATA NO ENVIADA");
            saleDocument.setSaleDocumentState(SaleDocument.INCORRECTO);

            logger.error(saleDocument.getSaleDocumentId());
            logger.error("NO DATA");
        }

        saleDocumentBO.updateSaleDocument(saleDocument);

        return respuestaComprobante;
    }

    public void actualizarDocumentoSale(String urlBase, SaleDocument saleDocument, int state, String message) {

        Map<String, Object> body = new HashMap<>();
        body.put("stateDocument", state);
        // Null keys
        body.put("msgSri", message);
        body.put("urlXml", saleDocument.getPublicURL());
        body.put("typeDocumentCode", saleDocument.getSaleDocumentCode());

        try {
            Response<ResponseBody> response = ApiClient.getSaleApi(urlBase)
                            .updateSaleDocuementState(saleDocument.getSaleDocumentId(), saleDocument.getCompany().getCompanyId(), body)
                            .execute();

            if (response.isSuccessful()) {
                System.out.println("SALE ACTUALIZADO");
            } else {
                throw new NegocioException("No se conecto con el servidor");
            }

        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

}
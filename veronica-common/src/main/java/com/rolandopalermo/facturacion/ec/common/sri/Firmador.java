package com.rolandopalermo.facturacion.ec.common.sri;

import org.w3c.dom.Document;

import es.mityc.firmaJava.libreria.xades.DataToSign;
import es.mityc.firmaJava.libreria.xades.EnumFormatoFirma;
import es.mityc.firmaJava.libreria.xades.XAdESSchemas;
import es.mityc.javasign.xml.refs.InternObjectToSign;
import es.mityc.javasign.xml.refs.ObjectToSign;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Rolando
 */
@Setter
@Getter
public class Firmador extends GenericXMLSignature {

	private String rutaDocumentoAFirmar;
	private String rutaDocumentoFirmado;

	public Firmador(String rutaDocumentoAFirmar, String rutaDocumentoFirmado, String pkcs12_resource,
			String pkcs12_pasword) {
		this.rutaDocumentoAFirmar = rutaDocumentoAFirmar;
		this.rutaDocumentoFirmado = rutaDocumentoFirmado;
		this.pkcs12_resource = pkcs12_resource;
		this.pkcs12_pasword = pkcs12_pasword;
	}

	@Override
	protected DataToSign createDataToSign() throws Exception {
		DataToSign dataToSign = new DataToSign();
		dataToSign.setXadesFormat(EnumFormatoFirma.XAdES_BES);
		dataToSign.setEsquema(XAdESSchemas.XAdES_132);
		dataToSign.setXMLEncoding("UTF-8");
		dataToSign.setEnveloped(true);
		dataToSign.addObject(new ObjectToSign(new InternObjectToSign("comprobante"), "Documento de ejemplo", null,
				"text/xml", null));
		Document docToSign = getDocument(getRutaDocumentoAFirmar());
		dataToSign.setDocument(docToSign);
		return dataToSign;
	}

	@Override
	protected String getSignatureFileName() {
		return rutaDocumentoFirmado;
	}

	public void firmar() throws Exception {
		execute();
	}

}
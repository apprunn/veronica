package com.rolandopalermo.facturacion.ec.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Rolando
 */
public class MarshallerUtil {

	public static void marshall(Object comprobante, String rutaArchivo) throws Exception {
		JAXBContext context = JAXBContext.newInstance(new Class[] { comprobante.getClass() });
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("jaxb.encoding", "UTF-8");
		marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
		OutputStream fos = new FileOutputStream(rutaArchivo);
		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
		marshaller.marshal(comprobante, out);
		fos.close();
		out.close();
	}

	public static <T> T unmarshall(String string, Class<T> clase) throws Exception {
		JAXBContext jaxbContext = null;
		Unmarshaller unmarshaller = null;
		StringReader reader = null;
		jaxbContext = JAXBContext.newInstance(clase);
		unmarshaller = jaxbContext.createUnmarshaller();
		reader = new StringReader(string);
		@SuppressWarnings("unchecked")
		T comprobante = (T) unmarshaller.unmarshal(reader);
		return comprobante;
	}

	public static <T> T unmarshall(File file, Class<T> clase) throws Exception {
		JAXBContext jaxbContext = null;
		Unmarshaller unmarshaller = null;
		jaxbContext = JAXBContext.newInstance(clase);
		unmarshaller = jaxbContext.createUnmarshaller();
		@SuppressWarnings("unchecked")
		T comprobante = (T) unmarshaller.unmarshal(file);
		return comprobante;
	}

	public static String getXMLValue(String xml, String tagName) {
		return xml.split("<" + tagName + ">")[1].split("</" + tagName + ">")[0];
	}
}

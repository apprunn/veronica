package com.rolandopalermo.facturacion.ec.test;

import org.junit.Test;

import com.rolandopalermo.facturacion.ec.common.sri.Firmador;

public class FirmaTest {

	@Test
	public void test() throws Exception {
		Firmador firmador = new Firmador("factura-dummy.xml", "factura-signed.xml", "D:\\Privado\\certificado.p12",
				"MAte2910");
		firmador.firmar();
	}

}
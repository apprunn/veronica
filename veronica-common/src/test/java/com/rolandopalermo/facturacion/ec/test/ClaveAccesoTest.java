package com.rolandopalermo.facturacion.ec.test;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.rolandopalermo.facturacion.ec.common.sri.ClaveDeAcceso;

public class ClaveAccesoTest {

	@Test
	public void test() {
		Date date = new Date();
		String tipoComprobante = "01";
		String ruc = "0992736033001";
		String ambiente = "1";
		String serie = "001001";
		String numeroComprobante = "000000118";
		String codigoNumerico = "12345678";
		String tipoEmision = "1";
		System.out.println(ClaveDeAcceso.generarClaveAcceso(date, tipoComprobante, ruc, ambiente, serie,
				numeroComprobante, codigoNumerico, tipoEmision));
		assertEquals(49, ClaveDeAcceso.generarClaveAcceso(date, tipoComprobante, ruc, ambiente, serie,
				numeroComprobante, codigoNumerico, tipoEmision).length());
	}

}

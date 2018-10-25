package com.rolandopalermo.facturacion.ec.test;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.rolandopalermo.facturacion.ec.common.sri.ClaveDeAcceso;
import com.rolandopalermo.facturacion.ec.common.util.MarshallerUtil;
import com.rolandopalermo.facturacion.ec.modelo.CampoAdicional;
import com.rolandopalermo.facturacion.ec.modelo.Impuesto;
import com.rolandopalermo.facturacion.ec.modelo.InfoTributaria;
import com.rolandopalermo.facturacion.ec.modelo.factura.Factura;
import com.rolandopalermo.facturacion.ec.modelo.factura.FacturaDetalle;
import com.rolandopalermo.facturacion.ec.modelo.factura.InfoFactura;
import com.rolandopalermo.facturacion.ec.modelo.factura.TotalImpuesto;

public class GeneracionTest {

	@Test
	public void test() throws Exception {
		Factura factura = new Factura();
		String codigoEstablecimiento = "001";
		String codigoPuntoEmision = "001";
		String serie = codigoEstablecimiento.concat(codigoPuntoEmision);
		String secuencialComprobante = String.format("%09d", new Object[] { Long.valueOf(117L) });
		String fecha = "10042018";
		String rucEmisor = "1791261151001";
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("ddMMyyyy");
		Date fechaEmision = formatoDeFecha.parse(fecha);
		String claveAcceso = ClaveDeAcceso.generarClaveAcceso(fechaEmision, "01", rucEmisor, "1", serie,
				secuencialComprobante, "00003085", "1");
		InfoTributaria infoTributaria = new InfoTributaria();
		infoTributaria.setRazonSocial("ROLANDOPALERMO CONSULTING CIA. LTDA.");
		infoTributaria.setNombreComercial("RP Consulting");
		infoTributaria.setRuc(rucEmisor);
		infoTributaria.setClaveAcceso(claveAcceso);
		infoTributaria.setCodDoc("01");
		infoTributaria.setEstab(codigoEstablecimiento);
		infoTributaria.setPtoEmi(codigoPuntoEmision);
		infoTributaria.setSecuencial(secuencialComprobante);
		infoTributaria.setDirMatriz("Cdla. Urdesa Central - Calle Cedros #108 y Víctor Emilio Estrada");
		infoTributaria.setAmbiente("1");
		infoTributaria.setTipoEmision("1");
		factura.setInfoTributaria(infoTributaria);
		// Información de factura
		InfoFactura infoFactura = new InfoFactura();
		infoFactura.setFechaEmision(fecha);
		infoFactura.setDirEstablecimiento("KM 2.5 Av. Juan Tanca Marengo S/N y Av. A. Freire");
		infoFactura.setContribuyenteEspecial("000");
		infoFactura.setObligadoContabilidad("SI");
		infoFactura.setTipoIdentificacionComprador("04");
		infoFactura.setRazonSocialComprador("HIVIMAR S.A.");
		infoFactura.setIdentificacionComprador("0990129185001");
		infoFactura.setTotalSinImpuestos(new BigDecimal("6316.80"));
		infoFactura.setTotalDescuento(new BigDecimal("0.00"));
		// ----------------------------------------------------------------------
		List<TotalImpuesto> listaTotalConImpuestos = new ArrayList<TotalImpuesto>();
		TotalImpuesto ti = new TotalImpuesto();
		ti.setCodigo("2");
		ti.setCodigoPorcentaje("2");
		ti.setValor(new BigDecimal("758.02"));
		ti.setBaseImponible(new BigDecimal("6316.80"));
		ti.setTarifa(new BigDecimal("12.00"));
		listaTotalConImpuestos.add(ti);
		infoFactura.setTotalImpuesto(listaTotalConImpuestos);
		// ----------------------------------------------------------------------
		infoFactura.setPropina(new BigDecimal("0.00"));
		infoFactura.setImporteTotal(new BigDecimal("7074.82"));
		infoFactura.setMoneda("DOLAR");
		factura.setInfoFactura(infoFactura);
		// ----------------------------------------------------------------------
		FacturaDetalle d1 = new FacturaDetalle();
		d1.setCodigoPrincipal("001");
		d1.setCodigoAuxiliar("001");
		d1.setDescripcion("SERVICIO DE CONSULTORÍA");
		d1.setCantidad(new BigDecimal("1.00"));
		d1.setPrecioUnitario(new BigDecimal("6316.80"));
		d1.setDescuento(new BigDecimal("0.00"));
		d1.setPrecioTotalSinImpuesto(new BigDecimal("6316.80"));
		// ----------------------------------------------------------------------
		List<Impuesto> listaImpuestos = new ArrayList<Impuesto>();
		Impuesto i1 = new Impuesto();
		i1.setCodigo("2");
		i1.setCodigoPorcentaje("2");
		i1.setTarifa(new BigDecimal("12.00"));
		i1.setBaseImponible(new BigDecimal("6316.80"));
		i1.setValor(new BigDecimal("758.02"));
		listaImpuestos.add(i1);
		// ----------------------------------------------------------------------
		d1.setImpuesto(listaImpuestos);
		List<FacturaDetalle> listaDetalles = new ArrayList<FacturaDetalle>();
		listaDetalles.add(d1);
		factura.setDetalle(listaDetalles);
		// ----------------------------------------------------------------------
		CampoAdicional c1 = new CampoAdicional();
		c1.setNombre("Dirección");
		c1.setValue("LOS PERALES Y AV. ELOY ALFARO");
		CampoAdicional c2 = new CampoAdicional();
		c2.setNombre("Teléfono");
		c2.setValue("2123123");
		CampoAdicional c3 = new CampoAdicional();
		c3.setNombre("Email");
		c3.setValue("gfeguiguren@sri.gob.ec");
		List<CampoAdicional> listaCamposAdicionales = new ArrayList<CampoAdicional>();
		listaCamposAdicionales.add(c1);
		listaCamposAdicionales.add(c2);
		listaCamposAdicionales.add(c3);
		factura.setCampoAdicional(listaCamposAdicionales);
		factura.setId("comprobante");
		factura.setVersion("1.0.0");
		// --
		MarshallerUtil.marshall(factura, "factura-dummy.xml");
	}

}

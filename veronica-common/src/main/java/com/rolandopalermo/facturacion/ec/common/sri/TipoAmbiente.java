package com.rolandopalermo.facturacion.ec.common.sri;

public class TipoAmbiente {

	public static TipoAmbiente[] values() {
		return (TipoAmbiente[]) $VALUES.clone();
	}

	private TipoAmbiente(String s, int i, String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public static final TipoAmbiente PRODUCCION;
	public static final TipoAmbiente PRUEBAS;
	private String code;
	private static final TipoAmbiente $VALUES[];

	static {
		PRODUCCION = new TipoAmbiente("PRODUCCION", 0, "2");
		PRUEBAS = new TipoAmbiente("PRUEBAS", 1, "1");
		$VALUES = (new TipoAmbiente[] { PRODUCCION, PRUEBAS });
	}
}

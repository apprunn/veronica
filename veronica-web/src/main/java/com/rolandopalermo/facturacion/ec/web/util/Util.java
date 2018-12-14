package com.rolandopalermo.facturacion.ec.web.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

	public static String getCurrentDateString() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
		return dateFormat.format(new Date());		

	}

}
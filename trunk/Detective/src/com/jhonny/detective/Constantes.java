package com.jhonny.detective;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class Constantes implements Serializable{
	
	private static final long serialVersionUID = 6275176897156490771L;
	
	
	/* FICHEROS */
	public static final String FICHERO_CONFIGURACION = "configuracion.spy";
	public static final String FICHERO_POSICIONES = "posiciones.spy";
	
	
	/* CONSTANTES DEL FICHERO DE PROPIEDADES */
	public static final String PROP_PASSWORD = "contrasena";
	public static final String PROP_TIEMPO_MINIMO_ACTUALIZACIONES = "tiempoMinimoActualizacion";
	public static final String PROP_DISTANCIA_MINIMA_ACTUALIZACIONES = "distanciaMinimaActualizacion";
	public static final String PROP_TIPO_CUENTA = "tipoCuenta";
	
	
	/* VALORES POR DEFECTO DE CONFIGURACION */
	public static final String DEFECTO_TIEMPO_MINIMO_ACTUALIZACIONES = "900000";
	public static final String DEFECTO_DISTANCIA_MINIMA_ACTUALIZACIONES = "5000";
	public static final String DEFECTO_TIPO_CUENTA = "1";
	public static final int DEFECTO_POS_DISTANCIA = 0;
	public static final int DEFECTO_POS_TIEMPO = 0;
	public static final Map<String, Integer> mapaDist = new HashMap<String, Integer>();
	public static final Map<String, Integer> mapaTmpo = new HashMap<String, Integer>();
	
	static{
		mapaDist.put("5000.0", 0);
		mapaDist.put("10000.0", 1);
		mapaDist.put("15000.0", 2);
		mapaDist.put("20000.0", 3);
		
		mapaTmpo.put("900000", 0);
		mapaTmpo.put("180000", 1);
		mapaTmpo.put("2700000", 2);
		mapaTmpo.put("3600000", 3);
	}
}

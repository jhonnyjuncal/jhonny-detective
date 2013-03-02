package com.jhonny.detective;

import java.io.Serializable;


public class Constantes implements Serializable{
	
	private static final long serialVersionUID = 6275176897156490771L;
	
	
	/* FICHEROS */
	public static final String FICHERO_CONFIGURACION = "config.properties";
	public static final String FICHERO_POSICIONES = "posiciones.spy";
	
	
	/* CONSTANTES DEL FICHERO DE PROPIEDADES */
	public static final String PROP_TIEMPO_MINIMO_ACTUALIZACIONES = "tiempoMinimoActualizacion";
	public static final String PROP_DISTANCIA_MINIMA_ACTUALIZACIONES = "distanciaMinimaActualizacion";
	public static final String PROP_NOMBRE_USUARIO = "usuario";
	public static final String PROP_TIPO_CUENTA = "tipoCuenta";
}

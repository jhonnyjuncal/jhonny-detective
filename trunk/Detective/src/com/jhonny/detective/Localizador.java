package com.jhonny.detective;

import java.util.Date;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;


public class Localizador implements LocationListener {
	
	public Context contexto;
	public View view;
	
	
	@Override
	public void onLocationChanged(Location location) {
		/* CUANDO LA POSICION GPS CAMBIA SEGUN EL CONSTRUCTOR DE DISTANCIA Y TIEMPO */
		ObjetoPosicion pos = new ObjetoPosicion();
		pos.setFecha(new Date());
		pos.setLatitud(location.getLatitude());
		pos.setLongitud(location.getLongitude());
		
		FileUtil.almacenaPosicionesAlFinalEnFichero(pos, contexto);
		FileUtil.cargaPosicionesAlmacenadas(contexto, view);
	}
	
	
	@Override
	public void onProviderDisabled(String arg0) {
		/* AL APAGAR EL GPS */
	}
	
	
	@Override
	public void onProviderEnabled(String arg0) {
		/* AL ENCENDER EL GPS */
	}
	
	
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		/* CUANDO EL ESTADO DEL GPS CAMBIA */
	}
}

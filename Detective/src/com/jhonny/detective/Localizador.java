package com.jhonny.detective;

import java.util.Date;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


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
		try{
			/* AL APAGAR EL GPS */
			TextView tv2 = (TextView)view.findViewById(R.id.textView2);
			tv2.setText(contexto.getResources().getString(R.string.txt_apagada));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public void onProviderEnabled(String arg0) {
		try{
			/* AL ENCENDER EL GPS */
			TextView tv2 = (TextView)view.findViewById(R.id.textView2);
			tv2.setText(contexto.getResources().getString(R.string.txt_encendida));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		/* CUANDO EL ESTADO DEL GPS CAMBIA */
		System.out.println("ha cambiado el estado");
	}
}

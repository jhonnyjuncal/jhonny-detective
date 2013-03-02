package com.jhonny.detective;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapaActivity extends FragmentActivity {
	
	GoogleMap mapa = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);
		
		// objeto mapa con el que trabajar
		mapa = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
	    
		// nivel del zoom
		CameraUpdate cu = CameraUpdateFactory.zoomTo(17);
		mapa.animateCamera(cu);
	    
		// latitud y longitud
		// javascript:void(prompt('',gApplication.getMap().getCenter())); con lo que quieres centrado en el mapa
		LatLng ubicacion = new LatLng(43.30035296262568, -8.27296793460846);
		cu = CameraUpdateFactory.newLatLng(ubicacion);
		mapa.moveCamera(cu);
		
		agregarMarcador(43.30035296262568, -8.27296793460846);
		agregarMarcador(43.40035296262568, -8.17296793460846);
		agregarMarcador(43.50035296262568, -8.07296793460846);
		
		// listener para el evento onclick de los marcadores
		mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
		    public boolean onMarkerClick(Marker marker) {
		        Toast.makeText(MapaActivity.this, "Marcador pulsado:\n" + marker.getTitle(), Toast.LENGTH_SHORT).show();
		        return false;
		    }
		});
	}
	
	
	private void agregarMarcador(double latitud, double longitud){
		mapa.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)).title("posicion guardada"));
	}
}

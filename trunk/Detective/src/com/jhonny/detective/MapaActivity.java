package com.jhonny.detective;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.Projection;


public class MapaActivity extends FragmentActivity {
	
	private GoogleMap mapa = null;
	private int contSalida = 0;
	private View view;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);
		contSalida = 0;
		
		Properties prefs = new Properties();
		Context ctx = this;
		
		try{
			// objeto mapa con el que trabajar
			mapa = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
    		
			
			// lectura del fichero de configuracion
			prefs = FileUtil.getFicheroAssetConfiguracion(ctx);
			
			String tipoCuenta = (String) prefs.get(Constantes.PROP_TIPO_CUENTA);
			
			PolylineOptions linea = new PolylineOptions();
			linea.color(Color.RED);
			linea.width(1);
			
			List<ObjetoPosicion> listaPosiciones = FileUtil.getListaAssetPosiciones(ctx, Integer.parseInt(tipoCuenta));
			for(int i=0; i<listaPosiciones.size(); i++){
				ObjetoPosicion obj = listaPosiciones.get(i);
				if(i == 0)
					agregarPrimerMarcador(obj.getLatitud(), obj.getLongitud(), obj.getFecha());
				else
					agregarMarcador(obj.getLatitud(), obj.getLongitud(), obj.getFecha());
				linea.add(new LatLng(obj.getLatitud(), obj.getLongitud()));
			}
			
			if(mapa != null){
				mapa.addPolyline(linea);
				
				// listener para el evento onclick de los marcadores
				mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
				    public boolean onMarkerClick(Marker marker) {
				    	marker.showInfoWindow();
				        return true;
				    }
				});
				
				mapa.setOnMapLongClickListener(new OnMapLongClickListener() {
					public void onMapLongClick(LatLng point) {
						Projection proj = mapa.getProjection();
						Point coord = proj.toScreenLocation(point);
				 
						Toast.makeText(MapaActivity.this,
							getResources().getString(R.string.label_latitud) + " " + point.latitude + "\n" +
							getResources().getString(R.string.label_longitud) + " " + point.longitude + "\n" +
							"X: " + coord.x + "\n" + "Y: " + coord.y, Toast.LENGTH_LONG).show();
					}
				});
				
				// nivel del zoom
				if(listaPosiciones != null && listaPosiciones.size() > 0){
					MapsInitializer.initialize(this);
					LatLng ubicacion = new LatLng(listaPosiciones.get(0).getLatitud(), listaPosiciones.get(0).getLongitud());
					
					CameraPosition camPos = new CameraPosition.Builder()
					.target(ubicacion)	//Centramos el mapa en Madrid
					.zoom(19)			//Establecemos el zoom en 19
					.bearing(45)		//Establecemos la orientación con el noreste arriba
					.tilt(70)			//Bajamos el punto de vista de la cámara 70 grados
					.build();
					
//					CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(ubicacion, 15);
					CameraUpdate cu = CameraUpdateFactory.newCameraPosition(camPos);
					mapa.animateCamera(cu);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public void onResume(){
		super.onResume();
		contSalida = 0;
		reiniciarFondoOpciones();
	}
	
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		if(contSalida == 0){
    			contSalida++;
    			Toast.makeText(this, getResources().getString(R.string.txt_salir_1_aviso), Toast.LENGTH_SHORT).show();
    			return true;
    		}else{
    			contSalida = 0;
    			Intent intent = new Intent();
    			intent.setAction(Intent.ACTION_MAIN);
    			intent.addCategory(Intent.CATEGORY_HOME);
    			startActivity(intent);
    		}
    	}
    	//para las demas cosas, se reenvia el evento al listener habitual
    	return super.onKeyDown(keyCode, event);
    }
	
	
	private void agregarMarcador(double latitud, double longitud, Date fecha){
		try{
			if(mapa != null){
				Locale locale = getResources().getConfiguration().locale;
				mapa.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud))
						.title(getResources().getString(R.string.label_fecha) + " "
						+ FileUtil.getFechaFormateada(fecha, locale))
						.snippet(getResources().getString(R.string.label_hora)
						+ FileUtil.getHoraFormateada(fecha, locale))
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
						);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	private void agregarPrimerMarcador(double latitud, double longitud, Date fecha){
		try{
			if(mapa != null){
				Locale locale = getResources().getConfiguration().locale;
				mapa.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud))
						.title(getResources().getString(R.string.label_fecha) + " "
						+ FileUtil.getFechaFormateada(fecha, locale))
						.snippet(getResources().getString(R.string.label_hora)
						+ FileUtil.getHoraFormateada(fecha, locale))
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
						);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		try{
	        getMenuInflater().inflate(R.menu.menu_mapa, menu);
		}catch(Exception ex){
    		ex.printStackTrace();
		}
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuMapa_legal:
				String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
				AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MapaActivity.this);
				LicenseDialog.setTitle(getResources().getString(R.string.menu_legalnotices));
				LicenseDialog.setMessage(LicenseInfo);
				LicenseDialog.show();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	private void reiniciarFondoOpciones(){
		try{
			LinearLayout layout_inicio = (LinearLayout)findViewById(R.id.opc_layout_inicio);
			layout_inicio.setBackgroundResource(R.color.gris_claro);
			
			LinearLayout layout_redes = (LinearLayout)findViewById(R.id.opc_layout_conf);
			layout_redes.setBackgroundResource(R.color.gris_claro);
			
			LinearLayout layout_conf = (LinearLayout)findViewById(R.id.opc_layout_pass);
			layout_conf.setBackgroundResource(R.color.gris_claro);
			
			LinearLayout layout_acerca = (LinearLayout)findViewById(R.id.opc_layout_borra);
			layout_acerca.setBackgroundResource(R.color.gris_claro);
			
			LinearLayout layout_terminos = (LinearLayout)findViewById(R.id.opc_layout_about);
			layout_terminos.setBackgroundResource(R.color.gris_claro);
			
			if(this.view != null)
				this.view.buildDrawingCache(true);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}

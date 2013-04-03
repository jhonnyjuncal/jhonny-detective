package com.jhonny.detective;

import java.util.List;
import java.util.Properties;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
//	AdView adView = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		System.out.println(" *** -- OK 1 -- *** ");
		super.onCreate(savedInstanceState);
		
		System.out.println(" *** -- OK 2 -- *** ");
		setContentView(R.layout.activity_mapa);
		
		System.out.println(" *** -- OK 3 -- *** ");
		try{
			// se crea la vista
//    		adView = new AdView(this, AdSize.BANNER, "a1513f4a3b63be1");
    		
    		// se obtiene el layout para el banner
//    		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout3);
    	    
    		// se añade la vista
//    		layout.addView(adView);
    		
    		// se inicia la solicitud para cargar el anuncio
//    		adView.loadAd(new AdRequest());
    		
    		
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
			
			// lectura del fichero de configuracion
			Properties prefs = new Properties();
			prefs = FileUtil.getFicheroAssetConfiguracion(this.getResources());
			
			String tipoCuenta = (String) prefs.get(Constantes.PROP_TIPO_CUENTA);
			Context ctx = this;
			
			List<ObjetoPosicion> listaPosiciones = FileUtil.getListaAssetPosiciones(ctx, Integer.parseInt(tipoCuenta));
			for(ObjetoPosicion obj : listaPosiciones){
				agregarMarcador(obj.getLatitud(), obj.getLongitud());
			}
			
			// listener para el evento onclick de los marcadores
			mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
			    public boolean onMarkerClick(Marker marker) {
			        Toast.makeText(MapaActivity.this, "Marcador pulsado:\n" + marker.getTitle(), Toast.LENGTH_SHORT).show();
			        return false;
			    }
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	private void agregarMarcador(double latitud, double longitud){
		mapa.addMarker(new MarkerOptions().position(new LatLng(latitud, longitud)).title("posicion guardada"));
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		try{
	        getMenuInflater().inflate(R.menu.activity_mapa, menu);
		}catch(Exception ex){
    		ex.printStackTrace();
		}
		return true;
	}
	
	
//	@Override
//	public void onDestroy(){
//		try{
//			adView.destroy();
//		}catch(Exception ex){
//    		ex.printStackTrace();
//		}
//	}
	
	
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
}

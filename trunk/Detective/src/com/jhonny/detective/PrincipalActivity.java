package com.jhonny.detective;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class PrincipalActivity extends Activity implements LocationListener{
	
	protected static Integer DISTANCIA_MINIMA_PARA_ACTUALIZACIONES;
	protected static Integer TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES;
	protected static Integer TIPO_CUENTA;
	protected static final String FICHERO_CONFIGURACION = "config.properties";
	public static LocationManager locationManager;
	
	WifiManager wifi = null;
	AdView adView = null;
	List<ObjetoPosicion> listaPosiciones = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try{
    		super.onCreate(savedInstanceState);
    		setContentView(R.layout.activity_principal);
    		
    		// se inicia el servicio de actualizacion de coordenadas
    		iniciaServicio();
    		
    		
    		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    		
    		// se crea la vista
    		adView = new AdView(this, AdSize.BANNER, "a1513f4a3b63be1");
    		
    		// se obtiene el layout para el banner
    		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout2);
    	    
    		// se añade la vista
    		layout.addView(adView);
    		
    		// se inicia la solicitud para cargar el anuncio
    		adView.loadAd(new AdRequest());
    		
    		
    		// carga los datos de la configuracion
    		cargaDatosConfiguracion();
    		
    		// estado actual de la wifi
    		informaEstadoActualGPS();
    		
    		// posiciones almacenadas
    		cargaPosicionesAlmacenadas();
    		
    		// informa estado de la wifi/3G
    		informaEstadoActualInternet();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
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
    
    
    @Override
    public void onDestroy(){
    	try{
    		guardaDatosConfiguracion();
    		adView.destroy();
    		super.onDestroy();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    public void muestraMapa(View view){
    	try{
    		if(R.id.button4 == view.getId()){
//    			String geoUriString = getResources().getString(R.string.mapa_ubicacion_inicial);
//    			if(listaPosiciones != null && listaPosiciones.size() > 0){
//    				ObjetoPosicion obj = listaPosiciones.get(0);
//    				String geoUriString = "geo:" + obj.getLatitud() + "," + obj.getLongitud();
//    				Uri geoUri = Uri.parse(geoUriString);
//    				Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
//    				startActivity(intent);
//    			}
    			
    			
    			// no funciona
    			if(listaPosiciones != null && listaPosiciones.size() > 0){
//    				ObjetoPosicion obj = listaPosiciones.get(0);
//    				Intent intent = new Intent("com.jhonny.detective.MapaActivity.class");
    				Intent intent = new Intent(this, MapaActivity.class);
//    				intent.setAction(Intent.ACTION_VIEW);
//    				intent.putExtra("lat", obj.getLatitud());
//    				intent.putExtra("long", obj.getLongitud());
    				startActivity(intent);
    			}
	    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    /**
     * muestra el estado actual del GPS
     */
    private void informaEstadoActualGPS(){
    	try{
    		estadoActualGPS();
    		
    		// listener del GPS
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
    				TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES,
    				DISTANCIA_MINIMA_PARA_ACTUALIZACIONES, 
    				this);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    private void estadoActualGPS(){
    	try{
    		// obtiene el objeto GPS
    		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    		boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    		
    		if(enabled == false){
    			TextView textoStatus = (TextView) findViewById(R.id.textView2);
    			textoStatus.setText(getResources().getString(R.string.txt_apagada));
    		}else{
    			TextView textoStatus = (TextView) findViewById(R.id.textView2);
    			textoStatus.setText(getResources().getString(R.string.txt_encendida));
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    /**
     * Carga el fichero de configuracion "config.properties"
     */
    private void cargaDatosConfiguracion(){
    	try{
    		// lectura del fichero de configuracion
    		Properties prefs = new Properties();
    		
    		try{
    			prefs = FileUtil.getFicheroAssetConfiguracion(this.getResources());
    		}catch(IOException io){
    			String mensaje = io.getMessage();
    			io.printStackTrace();
    			Toast.makeText(PrincipalActivity.this, mensaje, Toast.LENGTH_LONG).show();
    		}
    		
    		if(prefs != null){
    			/* TIEMPO PARA ACTUALIZACIONES */
    			String tiempo = (String) prefs.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES);
    			if(tiempo != null && tiempo.length() > 0)
    				TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = Integer.parseInt(tiempo);
    			
    			/* DISTANCIA PARA ACTUALIZACIONES */
    			String distancia = (String) prefs.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES);
    			if(distancia != null && distancia.length() > 0)
    				DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = Integer.parseInt(distancia);
    			
    			/* TIPO DE CUENTA */
    			String tipoCuenta = (String) prefs.get(Constantes.PROP_TIPO_CUENTA);
    			if(tipoCuenta != null && tipoCuenta.length() > 0){
    				TIPO_CUENTA = Integer.parseInt(tipoCuenta);
    				TextView textoCuenta = (TextView) findViewById(R.id.textView12);
    				textoCuenta.setText(tipoCuenta);
    			}
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    private void guardaDatosConfiguracion(){
    	try{
    		// lectura del fichero de configuracion
    		SharedPreferences prefs = getSharedPreferences(FICHERO_CONFIGURACION, Context.MODE_WORLD_WRITEABLE);
    		SharedPreferences.Editor editor = prefs.edit();
    		
    		if(prefs != null){
    			// hay que recoger los datos introducidos por el usuario en la configuracion
    			editor.putInt("tiempoMinimoActualizacion", TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES);
    			editor.putInt("distanciaMinimaActualizacion", DISTANCIA_MINIMA_PARA_ACTUALIZACIONES);
    			editor.commit();
    			finish();
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    public void cargaPosicionesAlmacenadas(){
    	int contador = 0;
    	try{
    		Context ctx = this;
    		
    		// lectura del fichero de configuracion
    		Properties prefs = new Properties();
    		prefs = FileUtil.getFicheroAssetConfiguracion(this.getResources());
    		
    		String tipoCuenta = (String) prefs.get(Constantes.PROP_TIPO_CUENTA);
    		
    		List<ObjetoPosicion> lista = new ArrayList<ObjetoPosicion>();
			if(lista != null){
				lista = FileUtil.getListaAssetPosiciones(ctx, Integer.parseInt(tipoCuenta));
				listaPosiciones = lista;
				contador = lista.size();
			}
			
			TextView textoPosiciones = (TextView) findViewById(R.id.textView6);
			textoPosiciones.setText(String.valueOf(contador));
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
    }
    
    
    private void informaEstadoActualInternet(){
    	try{
    		TextView textoWifi = (TextView) findViewById(R.id.textView8);
    		
    		if(wifi.isWifiEnabled())
    			textoWifi.setText(getResources().getString(R.string.txt_encendida));
    		else
    			textoWifi.setText(getResources().getString(R.string.txt_apagada));
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if(keyCode == KeyEvent.KEYCODE_BACK) {
    	  this.finish();
      }
      
      //para las demas cosas, se reenvia el evento al listener habitual
      return super.onKeyDown(keyCode, event);
    }
    
    
    private void iniciaServicio(){
    	try{
    		ServicioActualizacion.establecerActividadPrincipal(this);
    		Intent servicio = new Intent(this, ServicioActualizacion.class);
    		
    		// se ejecuta el servicio
    		if(startService(servicio) != null){
    			System.out.println("Servicio iniciado correctamente");
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    /* CUANDO LA POSICION GPS CAMBIA SEGUN EL CONSTRUCTOR DE DISTANCIA Y TIEMPO */
	public void onLocationChanged(Location location) {
		ObjetoPosicion pos = new ObjetoPosicion();
		pos.setFecha(new Date());
		pos.setLatitud(location.getLatitude());
		pos.setLongitud(location.getLongitude());
		
		System.out.println("-- " + pos.toString());
		FileUtil.almacenaPosicionesAlFinalEnFichero(pos, PrincipalActivity.this);
		cargaPosicionesAlmacenadas();
	}
	
	/* CUANDO EL ESTADO DEL GPS CAMBIA */
	public void onStatusChanged(String s, int i, Bundle b) {
	}
	
	/* AL APAGAR EL GPS */
	public void onProviderDisabled(String s) {
		estadoActualGPS();
	}
	
	/* AL ENCENDER EL GPS */
	public void onProviderEnabled(String s) {
		estadoActualGPS();
	}
}



//class DownloadTask2 extends AsyncTask<String, Void, Object> implements Serializable{
//	
//	private static final long serialVersionUID = -2537374909989113250L;
//	String res = null;
//	private Context contexto;
//	
//	protected void onPostExecute(Object result){
//		PrincipalActivity.pd.dismiss();
//		Toast.makeText(contexto, "Clima: " + res, Toast.LENGTH_LONG).show();
//		super.onPostExecute(result);
//	}
//
//	@Override
//	protected Object doInBackground(String... params) {
//		res = WebServiceUtil.enviaDatosAlServidor();
//		return 1;
//	}
//	
//	public void setContexto(Context contexto){
//		this.contexto = contexto;
//	}
//	
//	public Context getContexto(){
//		return contexto;
//	}
//}

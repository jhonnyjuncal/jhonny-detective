package com.jhonny.detective;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class PrincipalActivity extends FragmentActivity {
	
	protected static String PASS;
	protected static float DISTANCIA_MINIMA_PARA_ACTUALIZACIONES;
	protected static long TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES;
	protected static Integer TIPO_CUENTA;
	protected static final String FICHERO_CONFIGURACION = "config.properties";
	
	WifiManager wifi = null;
	AdView adView = null;
	static List<ObjetoPosicion> listaPosiciones = null;
	
	
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
    		FileUtil.cargaPosicionesAlmacenadas((Context)this, getWindow().getDecorView());
			
    		// informa estado de la wifi/3G
    		informaEstadoActualInternet();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	try{
	        getMenuInflater().inflate(R.menu.activity_principal, menu);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
    	
    	try{
	    	switch(item.getItemId()){
	    		case R.id.ppal_menu_settings1:
	    			// Configuracion
	    			intent = new Intent(this, ConfiguracionActivity.class);
					startActivity(intent);
	    			return true;
	    		case R.id.ppal_menu_settings2:
	    			// Cambiar contraseña
	    			intent = new Intent(this, ContrasenaActivity.class);
					startActivity(intent);
	    			return true;
	    		case R.id.ppal_menu_settings3:
	    			// Borrar posiciones almacenadas
	    			FileUtil.borraFicheroActualDePosiciones(this);
	    			FileUtil.cargaPosicionesAlmacenadas((Context)this, getWindow().getDecorView());
	    			Toast.makeText(this, getResources().getString(R.string.txt_coordenadas_borradas_ok)
	    					, Toast.LENGTH_LONG).show();
	    			return true;
	    		case R.id.ppal_menu_settings4:
	    			// Acerca de...
	    			intent = new Intent(this, AcercaActivity.class);
					startActivity(intent);
	    			return true;
	    		default:
	    			return super.onOptionsItemSelected(item);
	        }
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return false;
    }
    
    
    @Override
    public void onDestroy(){
    	try{
    		Map<String, String> valores = new HashMap<String, String>();
    		
    		valores.put(Constantes.PROP_PASSWORD, PASS);
    		valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, String.valueOf(TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES));
    		valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, String.valueOf(DISTANCIA_MINIMA_PARA_ACTUALIZACIONES));
    		valores.put(Constantes.PROP_TIPO_CUENTA, String.valueOf(TIPO_CUENTA));
    		
    		FileUtil.guardaDatosConfiguracion(valores, (Context)this);
    		adView.destroy();
    		super.onDestroy();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    public void muestraMapa(View view){
    	try{
    		if(R.id.button1 == view.getId()){
    			if(listaPosiciones != null && listaPosiciones.size() > 0){
    				Intent intent = new Intent(this, MapaActivity.class);
    				startActivity(intent);
    			}else
    				Toast.makeText(this, getResources().getString(R.string.txt_sin_posiciones), Toast.LENGTH_LONG).show();
	    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    public void muestraPosiciones(View view){
    	try{
    		if(R.id.button2 == view.getId()){
    			if(listaPosiciones != null && listaPosiciones.size() > 0){
    				Intent intent = new Intent(this, PosicionesActivity.class);
    				startActivity(intent);
    			}else
    				Toast.makeText(this, getResources().getString(R.string.txt_sin_posiciones), Toast.LENGTH_LONG).show();
	    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    /**
     * muestra el estado actual del GPS
     */
    private void informaEstadoActualGPS(){
    	Localizador localizador = null;
    	boolean enabled = false;
    	
    	try{
    		// listener del GPS
    		LocationManager locationManager = FileUtil.getLocationManager();
    		
    		if(locationManager == null){
    			localizador = FileUtil.getLocalizador();
    			
    			if(localizador == null){
    				localizador = new Localizador();
    				localizador.view = getWindow().getDecorView();
    				localizador.contexto = (Context)this;
    				
    				FileUtil.setLocalizador(localizador);
    			}
    			
    			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
    					TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES, 
    					localizador);
    			FileUtil.setLocationManager(locationManager);
    		}
    		
    		if(locationManager != null)
    			enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    		
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
    		Context ctx = this;
    		
    		try{
    			prefs = FileUtil.getFicheroAssetConfiguracion(ctx);
    		}catch(IOException io){
    			String mensaje = io.getMessage();
    			io.printStackTrace();
    			Toast.makeText(PrincipalActivity.this, mensaje, Toast.LENGTH_LONG).show();
    		}
    		
    		if(prefs != null){
    			/* TIEMPO PARA ACTUALIZACIONES */
    			String tiempo = (String) prefs.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES);
    			if(tiempo != null && tiempo.length() > 0)
    				TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = Long.parseLong(tiempo);
    			
    			/* DISTANCIA PARA ACTUALIZACIONES */
    			String distancia = (String) prefs.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES);
    			if(distancia != null && distancia.length() > 0)
    				DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = Float.parseFloat(distancia);
    			
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
    	  Intent intent = new Intent(Intent.ACTION_MAIN);
    	  intent.addCategory(Intent.CATEGORY_HOME);
    	  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	  startActivity(intent);
      }
      
      //para las demas cosas, se reenvia el evento al listener habitual
      return super.onKeyDown(keyCode, event);
    }
    
    
    private void iniciaServicio(){
    	try{
//    		ServicioActualizacion.establecerActividadPrincipal(this);
//    		Intent servicio = new Intent(this, ServicioActualizacion.class);
//    		
//    		// se ejecuta el servicio
//    		startService(servicio);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
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

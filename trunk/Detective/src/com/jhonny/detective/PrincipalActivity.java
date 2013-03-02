package com.jhonny.detective;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import com.google.android.maps.MapActivity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class PrincipalActivity extends FragmentActivity {
	
	private static Integer DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = 5000; // en metros (5 kms)
	private static Integer TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = 30 * (60 * 1000); // en minutos (30 minutos)
	private static final String FICHERO_CONFIGURACION = "config.properties";
	protected LocationManager locationManager;
//	public static ProgressDialog pd;
	
	WifiManager wifi = null;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try{
    		super.onCreate(savedInstanceState);
    		setContentView(R.layout.activity_principal);
    		
    		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    		
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
	        getMenuInflater().inflate(R.menu.activity_principal, menu);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	return true;
    }
    
    
    @Override
    public void onDestroy(){
    	try{
    		guardaDatosConfiguracion();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    /**
     * boton solo wifi
     * @param view
     */
    public void soloWifi(View view){
    	try{
    		informaEstadoActualGPS();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    /**
     * boton solo 3g
     * @param view
     */
    public void solo3g(View view){
    	try{
	    	if(R.id.button2 == view.getId()){
	    		TextView texto2 = (TextView) findViewById(R.id.textView4);
	    		texto2.setText("boton Solo 3G");
	    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    /**
     * boton enviar datos
     * @param view
     */
    public void enviarDatos(View view){
    	try{
	    	if(R.id.button3 == view.getId()){
	    		TextView texto3 = (TextView) findViewById(R.id.textView6);
	    		texto3.setText("boton Enviar Datos");
	    		
	    		Intent intent = new Intent(this, PosicionesActivity.class);
    			startActivity(intent);
	    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    public void muestraMapa(View view){
    	try{
    		if(R.id.button4 == view.getId()){
    			String geoUriString = getResources().getString(R.string.mapa_ubicacion_inicial);  
    			Uri geoUri = Uri.parse(geoUriString);  
    			Intent intent = new Intent(Intent.ACTION_VIEW, geoUri);
    			startActivity(intent);
    			
    			// no funciona
//    			Intent intent = new Intent(getApplicationContext(), MapActivity.class);
//    			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//    			startActivity(intent);
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
    		// obtiene el objeto GPS
    		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    		boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    		if(enabled == false){
    			 Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    			 startActivity(intent);
    		}else{
    			TextView textoStatus = (TextView) findViewById(R.id.textView2);
    			textoStatus.setText("encendido");
    		}
    		
    		// listener del GPS
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
    				TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES,
    				DISTANCIA_MINIMA_PARA_ACTUALIZACIONES, 
    				new MyLocationListener());
    		
    		// localizador del GPS
    		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    		if(location != null){
    			String message = String.format("Current Location \n Longitude: %1$s \n Latitude: %2$s",
    					location.getLongitude(), location.getLatitude());
    			Toast.makeText(PrincipalActivity.this, message, Toast.LENGTH_LONG).show();
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
    			
    			/* NOMBRE DEL USUARIO */
    			String usuario = (String) prefs.get(Constantes.PROP_NOMBRE_USUARIO);
    			if(usuario != null && usuario.length() > 0){
    				TextView textoUsuario = (TextView) findViewById(R.id.textView4);
    				textoUsuario.setText(usuario);
    			}
    			
    			/* TIPO DE CUENTA */
    			String tipoCuenta = (String) prefs.get(Constantes.PROP_TIPO_CUENTA);
    			if(tipoCuenta != null && tipoCuenta.length() > 0){
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
    
    
    private void cargaPosicionesAlmacenadas(){
    	int contador = 0;
    	try{
			List<ObjetoPosicion> lista = FileUtil.getListaAssetPosiciones(PrincipalActivity.this);
			if(lista != null)
				contador = lista.size();
			
			TextView textoPosiciones = (TextView) findViewById(R.id.textView6);
			textoPosiciones.setText(String.valueOf(contador));
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
    }
    
    
    private void almacenaPosicionActualEnFichero(ObjetoPosicion pos){
    	try{
    		OutputStream output = openFileOutput(Constantes.FICHERO_POSICIONES, MODE_APPEND);
	    	OutputStreamWriter out = new OutputStreamWriter(output);
	    	String valor = pos.getFecha().getTime()+"?"+pos.getLatitud()+"?"+pos.getLongitud()+"\r\n";
	    	out.write(valor);
	    	out.close();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
    
    
    private void informaEstadoActualInternet(){
    	try{
    		TextView textoWifi = (TextView) findViewById(R.id.textView8);
    		
    		if(wifi.isWifiEnabled())
    			textoWifi.setText("Encendida");
    		else
    			textoWifi.setText("Apagada");
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    
    
    
    
    public class MyLocationListener implements LocationListener{
    	
    	/* CUANDO LA POSICION GPS CAMBIA SEGUN EL CONSTRUCTOR DE DISTANCIA Y TIEMPO */
    	public void onLocationChanged(Location location) {
    		ObjetoPosicion pos = new ObjetoPosicion();
    		pos.setFecha(new Date());
    		pos.setLatitud(location.getLatitude());
    		pos.setLongitud(location.getLongitude());
    		almacenaPosicionActualEnFichero(pos);
    		cargaPosicionesAlmacenadas();
    	}
    	
    	/* CUANDO EL ESTADO DEL GPS CAMBIA */
    	public void onStatusChanged(String s, int i, Bundle b) {
    	}
    	
    	/* AL APAGAR EL GPS */
    	public void onProviderDisabled(String s) {
    		
    	}
    	
    	/* AL ENCENDER EL GPS */
    	public void onProviderEnabled(String s) {
    		
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

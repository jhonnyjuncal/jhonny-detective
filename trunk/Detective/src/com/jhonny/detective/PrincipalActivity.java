package com.jhonny.detective;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class PrincipalActivity extends SherlockActivity {
	
	private static final long serialVersionUID = -7155207696891363901L;
	protected static String PASS;
	protected static float DISTANCIA_MINIMA_PARA_ACTUALIZACIONES;
	protected static long TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES;
	protected static Integer TIPO_CUENTA;
	protected static final String FICHERO_CONFIGURACION = "config.properties";
	
	public static List<ObjetoPosicion> listaPosiciones = null;
	public static View viewPrincipal = null;
	public static Resources resourcesPrincipal = null;
	private ActionBar actionBar;
	private int contSalida = 0;
	private SlidingMenu menu;
	private View view;
	private Context context;
	
	//Constants for tablet sized ads (728x90)
	private static final int IAB_LEADERBOARD_WIDTH = 728;
	private static final int MED_BANNER_WIDTH = 480;
	//Constants for phone sized ads (320x50)
	private static final int BANNER_AD_WIDTH = 320;
	private static final int BANNER_AD_HEIGHT = 50;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_principal);
		contSalida = 0;
    	
		try{
			this.context = this;
			this.view = getWindow().getDecorView();
			
    		menu = new SlidingMenu(this);
    		menu.setMode(SlidingMenu.LEFT);
	        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	        menu.setShadowWidthRes(R.dimen.shadow_width);
	        menu.setShadowDrawable(R.drawable.ext_sombra);
	        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
	        menu.setFadeDegree(0.35f);
	        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
	        menu.setMenu(R.layout.activity_opciones);
	        
	        actionBar = getSupportActionBar();
	        if(actionBar != null){
	        	actionBar.setDisplayShowCustomEnabled(true);
	        	
	        	// boton < de la action bar
	        	actionBar.setDisplayHomeAsUpEnabled(false);
	        	actionBar.setHomeButtonEnabled(true);
	        }
    		
    		// se inicia el servicio de actualizacion de coordenadas
    		iniciaServicio();
    		
    		FileUtil.setWifiManager((WifiManager)getSystemService(Context.WIFI_SERVICE));
    		
    		// carga los datos de la configuracion
    		cargaDatosConfiguracion();
    		
    		// estado actual de la wifi
    		informaEstadoActualGPS();
    		
    		// posiciones almacenadas
    		FileUtil.cargaPosicionesAlmacenadas((Context)this, getWindow().getDecorView());
			
    		// informa estado de la wifi/3G
    		informaEstadoActualInternet();
    		
    		int placementWidth = BANNER_AD_WIDTH;

			//Finds an ad that best fits a users device.
			if(canFit(IAB_LEADERBOARD_WIDTH)) {
			    placementWidth = IAB_LEADERBOARD_WIDTH;
			}else if(canFit(MED_BANNER_WIDTH)) {
			    placementWidth = MED_BANNER_WIDTH;
			}
			
			MMAdView adView = new MMAdView(this);
			adView.setApid("148574");
			MMRequest request = new MMRequest();
			adView.setMMRequest(request);
			adView.setId(MMSDK.getDefaultAdId());
			adView.setWidth(placementWidth);
			adView.setHeight(BANNER_AD_HEIGHT);

			LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout2);
			//Add the adView to the layout. The layout is assumed to be a RelativeLayout.
			layout.addView(adView);
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_principal, menu);
    	return true;
    }
    
    
    @Override
    public void onDestroy(){
    	try{
    		contSalida = 0;
    		Map<String, String> valores = new HashMap<String, String>();
    		
    		valores.put(Constantes.PROP_PASSWORD, PASS);
    		valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, String.valueOf(TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES));
    		valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, String.valueOf(DISTANCIA_MINIMA_PARA_ACTUALIZACIONES));
    		valores.put(Constantes.PROP_TIPO_CUENTA, String.valueOf(TIPO_CUENTA));
    		
    		FileUtil.guardaDatosConfiguracion(valores, (Context)this);
    		super.onDestroy();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    @Override
    public void onResume(){
    	super.onResume();
    	contSalida = 0;
    	
    	try{
	    	reiniciarFondoOpciones();
	    	cargaConfiguracionGlobal();
			FileUtil.cargaPosicionesAlmacenadas((Context)this, getWindow().getDecorView());
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    public void muestraMapa(View view){
    	try{
    		if(R.id.button1 == view.getId()){
				Intent intent = new Intent(this, MapaActivity.class);
				startActivity(intent);
	    	}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    public void muestraPosiciones(View view){
    	try{
    		if(R.id.button2 == view.getId()){
    			Intent intent = new Intent(this, PosicionesActivity.class);
    			startActivity(intent);
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
    		seleccionarOrigenDeLocalizacion();
    		boolean enabled = false;
    		
    		LocationManager loc = FileUtil.getLocationManagerGps();
    		if(loc != null)
    			enabled = loc.isProviderEnabled(LocationManager.GPS_PROVIDER);
    		
    		TextView textoStatus = (TextView) findViewById(R.id.textView2);
    		if(enabled == false)
    			textoStatus.setText(getResources().getString(R.string.txt_apagada));
    		else
    			textoStatus.setText(getResources().getString(R.string.txt_encendida));
    		
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    private void seleccionarOrigenDeLocalizacion(){
    	Localizador localizador = null;
    	
    	try{
    		// listener del GPS
    		LocationManager locationGps = FileUtil.getLocationManagerGps();
    		LocationManager locationInternet = FileUtil.getLocationManagerInternet();
    		
    		if(locationGps == null){
    			localizador = FileUtil.getLocalizador();
    			if(localizador == null){
    				localizador = new Localizador();
    				localizador.view = getWindow().getDecorView();
    				localizador.contexto = (Context)this;
    				FileUtil.setLocalizador(localizador);
    			}
    			locationGps = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    		}
    		if(locationInternet == null){
    			localizador = FileUtil.getLocalizador();
    			if(localizador == null){
    				localizador = new Localizador();
    				localizador.view = getWindow().getDecorView();
    				localizador.contexto = (Context)this;
    				FileUtil.setLocalizador(localizador);
    			}
    			locationInternet = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    		}
    		
    		if(locationGps != null){
    			if(locationGps.isProviderEnabled(LocationManager.GPS_PROVIDER)){
	    			locationGps.requestLocationUpdates(LocationManager.GPS_PROVIDER,
							TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES, 
							localizador);
    			}
    			FileUtil.setLocationManagerGps(locationGps);
    		}
    		
    		if(locationInternet != null){
    			if(locationInternet.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
	    			locationInternet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES, 
							localizador);
    			}
    			FileUtil.setLocationManagerGps(locationGps);
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
    				TextView textoCuenta = (TextView) findViewById(R.id.textView8);
    				textoCuenta.setText(tipoCuenta);
    			}
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    protected void informaEstadoActualInternet(){
    	try{
    		PrincipalActivity.viewPrincipal = getWindow().getDecorView();
    		PrincipalActivity.resourcesPrincipal = getResources();
    		TextView textoWifi = (TextView)findViewById(R.id.textView6);
    		
    		if(FileUtil.getWifiManager().isWifiEnabled())
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
    
    
    public void muestraHome(View view){
		try{
			this.view = view;
			
			LinearLayout layout_inicio = (LinearLayout)findViewById(R.id.opc_layout_inicio);
			layout_inicio.setBackgroundResource(R.color.gris_oscuro);
			view.buildDrawingCache(true);
			
			Intent intent = new Intent(this, PrincipalActivity.class);
			startActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			menu.toggle();
		}
	}
	
	
	public void muestraConfiguracion(View view){
		try{
			this.view = view;
			
			LinearLayout layout_inicio = (LinearLayout)findViewById(R.id.opc_layout_conf);
			layout_inicio.setBackgroundResource(R.color.gris_oscuro);
			view.buildDrawingCache(true);
			
			Intent intent = new Intent(this, ConfiguracionActivity.class);
			startActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			menu.toggle();
		}
	}
	
	
	public void muestraPassword(View view){
		try{
			this.view = view;
			
			LinearLayout layout_inicio = (LinearLayout)findViewById(R.id.opc_layout_pass);
			layout_inicio.setBackgroundResource(R.color.gris_oscuro);
			view.buildDrawingCache(true);
			
			Intent intent = new Intent(this, ContrasenaActivity.class);
			startActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			menu.toggle();
		}
	}
	
	
	public void muestraBorrar(View view){
		try{
			this.view = view;
			
			LinearLayout layout_inicio = (LinearLayout)findViewById(R.id.opc_layout_borra);
			layout_inicio.setBackgroundResource(R.color.gris_oscuro);
			view.buildDrawingCache(true);
			
			Intent intent = new Intent(this, BorrarPosicionesActivity.class);
			startActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			menu.toggle();
		}
	}


	public void muestraAcerca(View view){
		try{
			this.view = view;
			
			LinearLayout layout_inicio = (LinearLayout)findViewById(R.id.opc_layout_about);
			layout_inicio.setBackgroundResource(R.color.gris_oscuro);
			view.buildDrawingCache(true);
			
			Intent intent = new Intent(this, AcercaActivity.class);
			startActivity(intent);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			menu.toggle();
		}
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
	
	
	private void cargaConfiguracionGlobal(){
		try{
			if(this.view != null){
				String fondo = FileUtil.getFondoPantallaAlmacenado(this.context);
				if(fondo != null){
					String imagen = Constantes.mapaFondo.get(Integer.parseInt(fondo));
					int imageResource1 = this.view.getContext().getApplicationContext().getResources().getIdentifier(
							imagen, "drawable", this.view.getContext().getApplicationContext().getPackageName());
					Drawable image = this.view.getContext().getResources().getDrawable(imageResource1);
					ImageView imageView = (ImageView)findViewById(R.id.fondo_principal);
					imageView.setImageDrawable(image);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			menu.toggle();
		}
		return true;
	}
	
	protected boolean canFit(int adWidth) {
		int adWidthPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, adWidth, getResources().getDisplayMetrics());
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		return metrics.widthPixels >= adWidthPx;
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

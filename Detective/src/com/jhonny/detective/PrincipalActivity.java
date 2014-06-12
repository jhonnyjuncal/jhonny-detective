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
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class PrincipalActivity extends SherlockActivity {
	
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
	private Dialog dialogo;
	private EditText email;
	private CheckBox check;
	private Properties prop;
	
	private static final int IAB_LEADERBOARD_WIDTH = 728;
	private static final int MED_BANNER_WIDTH = 480;
	private static final int BANNER_AD_WIDTH = 320;
	private static final int BANNER_AD_HEIGHT = 50;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_principal);
		contSalida = 0;
    	
		try{
			//MMSDK.setLogLevel(MMSDK.LOG_LEVEL_DEBUG);
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
			cargaPublicidad();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    public void muestraMapa(View view){
		Intent intent = new Intent(this, MapaActivity.class);
		startActivity(intent);
    }
    
    
    public void muestraPosiciones(View view){
    	Intent intent = new Intent(this, PosicionesActivity.class);
		startActivity(intent);
    }
    
    
    public void enviarPosicionesPorMail(View view){
    	try{
    		dialogo = new Dialog(this, R.style.Theme_Dialog_Translucent);
    		dialogo.setTitle(R.string.titulo_envio_email);
    		dialogo.setContentView(R.layout.alert_email);
    		
    		Button boton_enviar = (Button)dialogo.findViewById(R.id.btnEnviar);
    		Button boton_cancelar = (Button)dialogo.findViewById(R.id.btnCancelar);
    		email = (EditText)dialogo.findViewById(R.id.alert_editText1);
    		check = (CheckBox)dialogo.findViewById(R.id.alert_checkBox1);
    		
    		try{
				prop = FileUtil.getFicheroAssetConfiguracion(context);
			}catch(IOException e){
				e.printStackTrace();
			}
			
			if(prop != null){
				String valor_check = (String)prop.get(Constantes.PROP_EMAIL_CHECK);
				if(valor_check != null && valor_check.equals("true")){
					this.check.setChecked(true);
					String email_envio = (String)prop.get(Constantes.PROP_EMAIL_ENVIO);
					if(email_envio != null)
						this.email.setText(email_envio);
				}else
					this.check.setChecked(false);
			}
    		
    		boton_enviar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String direccion = email.getText().toString();
					
					Map<String, String> valores = new HashMap<String, String>();
					
					valores.put(Constantes.PROP_PASSWORD, (String)prop.get(Constantes.PROP_PASSWORD));
					valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, (String)prop.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES));
					valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, (String)prop.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES));
					valores.put(Constantes.PROP_TIPO_CUENTA, (String)prop.get(Constantes.PROP_TIPO_CUENTA));
					valores.put(Constantes.PROP_FONDO_PANTALLA, (String)prop.get(Constantes.PROP_FONDO_PANTALLA));
					valores.put(Constantes.PROP_EMAIL, (String)prop.get(Constantes.PROP_EMAIL));
					
					if(check.isChecked()){
						// guardar la direccion de email en el fichero de propiedades
						valores.put(Constantes.PROP_EMAIL_ENVIO, direccion);
						valores.put(Constantes.PROP_EMAIL_CHECK, "true");
					}else{
						// guardar la direccion de email en el fichero de propiedades
						valores.put(Constantes.PROP_EMAIL_ENVIO, "");
						valores.put(Constantes.PROP_EMAIL_CHECK, "false");
					}
					
					FileUtil.guardaDatosConfiguracion(valores, context);
					
					Email.enviarPosicionesPorMail(PrincipalActivity.this, direccion, context);
					dialogo.dismiss();
				}
			});
			
			boton_cancelar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogo.cancel();
				}
			});
    		
    		dialogo.show();
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
    				//TextView textoCuenta = (TextView) findViewById(R.id.textView8);
    				//textoCuenta.setText(tipoCuenta);
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
	
	private void cargaPublicidad(){
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
		layout.removeAllViews();
		layout.addView(adView);
		adView.getAd();
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

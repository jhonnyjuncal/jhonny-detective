package com.jhonny.detective;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


public class ConfiguracionActivity extends SherlockActivity implements OnItemSelectedListener {
	
	private static final long serialVersionUID = 362472463923801596L;
	private AdView adView1;
	private AdView adView2;
	private Spinner spDistancia;
	private Spinner spTiempo;
	private Spinner spFondo;
	private ActionBar actionBar;
	private SlidingMenu menu;
	private View view;
	
	private static String PASS;
	private static float DISTANCIA_MINIMA_PARA_ACTUALIZACIONES;
	private static long TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES;
	private static String TIPO_CUENTA;
	private int contSalida = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracion);
		
		contSalida = 0;
		int pos1 = 0;
		int pos2 = 0;
		int pos3 = 0;
		
		try{
			menu = new SlidingMenu(this);
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
			
			// PUBLICIDAD 1
			adView1 = new AdView(this, AdSize.BANNER, "a1513f4a3b63be1");
			LinearLayout layout1 = (LinearLayout)findViewById(R.id.linearLayout2);
			layout1.addView(adView1);
			adView1.loadAd(new AdRequest());
		
			// PUBLICIDAD 2
			adView2 = new AdView(this, AdSize.BANNER, "a1513f4a3b63be1");
			LinearLayout layout2 = (LinearLayout)findViewById(R.id.linearLayout3);
			layout2.addView(adView2);
			adView2.loadAd(new AdRequest());
			
			// se cargan los datos de la configuracion almacenada
			spDistancia = (Spinner) findViewById(R.id.spinner1);
			spTiempo = (Spinner) findViewById(R.id.spinner2);
			spFondo = (Spinner) findViewById(R.id.spinner3);
			
			pos1 = FileUtil.getPosicionSpinnerSeleccionada(1, this);
			pos2 = FileUtil.getPosicionSpinnerSeleccionada(2, this);
			pos3 = FileUtil.getPosicionSpinnerSeleccionada(3, this);
			
			spDistancia.setSelection(pos1);
			spTiempo.setSelection(pos2);
			spFondo.setSelection(pos3);
			
			spDistancia.setOnItemSelectedListener(this);
			spTiempo.setOnItemSelectedListener(this);
			spFondo.setOnItemSelectedListener(this);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_configuracion, menu);
		return true;
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
	
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		try{
			Properties prop = FileUtil.getFicheroAssetConfiguracion(this);
			
			if(prop != null){
				PASS = (String)prop.get(Constantes.PROP_PASSWORD);
				TIPO_CUENTA = (String)prop.get(Constantes.PROP_TIPO_CUENTA);
				
				switch((int)spDistancia.getSelectedItemId()){
					case 0:
						// 5000 metros
						DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = 5000;
						break;
					case 1:
						// 10000 metros
						DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = 10000;
						break;
					case 2:
						// 15000 metros
						DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = 15000;
						break;
					case 3:
						// 20000 metros
						DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = 20000;
						break;
				}
				
				switch((int)spTiempo.getSelectedItemId()){
					case 0:
						// 900000 milisegundos
						TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = 900000;
						break;
					case 1:
						// 1800000 milisegundos
						TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = 1800000;
						break;
					case 2:
						// 2700000 milisegundos
						TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = 2700000;
						break;
					case 3:
						// 3600000 milisegundos
						TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = 3600000;
						break;
				}
				
				Map<String, String> valores = new HashMap<String, String>();
				valores.put(Constantes.PROP_PASSWORD, PASS);
				valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, String.valueOf(DISTANCIA_MINIMA_PARA_ACTUALIZACIONES));
				valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, String.valueOf(TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES));
				valores.put(Constantes.PROP_TIPO_CUENTA, TIPO_CUENTA);
				
				LocationManager locationManager = FileUtil.getLocationManager();
				Localizador localizador = FileUtil.getLocalizador();
				
				if(locationManager != null)
					locationManager.removeUpdates(localizador);
				
				FileUtil.getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER,
						TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES, 
						localizador);
				FileUtil.guardaDatosConfiguracion(valores, this);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
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
}

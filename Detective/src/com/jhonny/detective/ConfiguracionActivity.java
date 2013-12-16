package com.jhonny.detective;

import java.util.ArrayList;
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
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;


public class ConfiguracionActivity extends SherlockActivity implements OnItemSelectedListener {
	
	private static final long serialVersionUID = 362472463923801596L;
	private Spinner spDistancia;
	private Spinner spTiempo;
	private Spinner spFondo;
	private ActionBar actionBar;
	private SlidingMenu menu;
	private View view;
	private Context context;
	
	private static String PASS;
	private static float DISTANCIA_MINIMA_PARA_ACTUALIZACIONES;
	private static long TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES;
	private static String TIPO_CUENTA;
	private static String FONDO_PANTALLA;
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
			
			// se cargan los datos de la configuracion almacenada
			spDistancia = (Spinner) findViewById(R.id.spinner1);
			spTiempo = (Spinner) findViewById(R.id.spinner2);
			spFondo = (Spinner) findViewById(R.id.spinner3);
			
			cargarSpinnerFondoPantallas();
			
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
		cargaConfiguracionGlobal();
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
				
				switch((int)spFondo.getSelectedItemId()){
					case 0:
						FONDO_PANTALLA = "1";
						break;
					case 1:
						FONDO_PANTALLA = "2";
						break;
					case 2:
						FONDO_PANTALLA = "3";
						break;
				}
				
				Map<String, String> valores = new HashMap<String, String>();
				valores.put(Constantes.PROP_PASSWORD, PASS);
				valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, String.valueOf(DISTANCIA_MINIMA_PARA_ACTUALIZACIONES));
				valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, String.valueOf(TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES));
				valores.put(Constantes.PROP_TIPO_CUENTA, TIPO_CUENTA);
				valores.put(Constantes.PROP_FONDO_PANTALLA, FONDO_PANTALLA);
				
				LocationManager locationManagerGps = FileUtil.getLocationManagerGps();
				LocationManager locationManagerInternet = FileUtil.getLocationManagerInternet();
				Localizador localizador = FileUtil.getLocalizador();
				
				if(locationManagerGps != null && locationManagerGps.isProviderEnabled(LocationManager.GPS_PROVIDER))
					locationManagerGps.removeUpdates(localizador);
				else if(locationManagerInternet != null && locationManagerInternet.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
					locationManagerInternet.removeUpdates(localizador);
				
				if(locationManagerGps != null && locationManagerGps.isProviderEnabled(LocationManager.GPS_PROVIDER))
					FileUtil.getLocationManagerGps().requestLocationUpdates(LocationManager.GPS_PROVIDER,
							TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES, 
							localizador);
				else if(locationManagerInternet != null && locationManagerInternet.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
					FileUtil.getLocationManagerInternet().requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES, 
							localizador);
				
				
				FileUtil.guardaDatosConfiguracion(valores, this);
				cargaConfiguracionGlobal();
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
	
	
	private void cargarSpinnerFondoPantallas(){
		try{
			List<String> list = new ArrayList<String>();
			list.add("Fondo 1");
			list.add("Fondo 2");
			list.add("Fondo 3");
			
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			spFondo.setAdapter(dataAdapter);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	private void cargaConfiguracionGlobal(){
		try{
			if(this.view != null){
				String imagen = FileUtil.getFondoPantallaAlmacenado(this.context);
				if(imagen != null){
					int imageResource1 = this.view.getContext().getApplicationContext().getResources().getIdentifier(
							imagen, "drawable", this.view.getContext().getApplicationContext().getPackageName());
					Drawable image = this.view.getContext().getResources().getDrawable(imageResource1);
					ImageView imageView = (ImageView)findViewById(R.id.fondo_configuracion);
					imageView.setImageDrawable(image);
					this.view.buildDrawingCache(false);
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
}

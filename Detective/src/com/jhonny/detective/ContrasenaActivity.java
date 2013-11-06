package com.jhonny.detective;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class ContrasenaActivity extends SherlockActivity {
	
	private static final long serialVersionUID = 1770771858269363336L;
	private AdView adView1 = null;
	private AdView adView2 = null;
	private ActionBar actionBar;
	private int contSalida = 0;
	private SlidingMenu menu;
	private View view;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contrasena);
		contSalida = 0;
		
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
	        
			// publicidad 1
			adView1 = new AdView(this, AdSize.BANNER, "a1513f4a3b63be1");
			LinearLayout layout1 = (LinearLayout)findViewById(R.id.linearLayout2);
			layout1.addView(adView1);
			adView1.loadAd(new AdRequest());
		
			// publicidad 2
			adView2 = new AdView(this, AdSize.BANNER, "a1513f4a3b63be1");
			LinearLayout layout2 = (LinearLayout)findViewById(R.id.linearLayout3);
			layout2.addView(adView2);
			adView2.loadAd(new AdRequest());
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_contrasena, menu);
		return true;
	}
	
	
	@Override
    protected void onResume(){
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
	
	
	public void cambiarContrasena(View view){
		String passAlmacenado;
		
		try{
			EditText passViejo = (EditText) findViewById(R.id.editText1);
			EditText passNuevo1 = (EditText) findViewById(R.id.editText2);
			EditText passNuevo2 = (EditText) findViewById(R.id.editText3);
			
			Properties prop = FileUtil.getFicheroAssetConfiguracion(this);
			
			if(passViejo == null || passViejo.getText().toString().equals("")){
				Toast.makeText(this, getResources().getString(R.string.txt_passViejo_incorrecto), Toast.LENGTH_LONG).show();
				return;
			}
			if(passNuevo1 == null || passNuevo1.getText().toString().equals("")){
				Toast.makeText(this, getResources().getString(R.string.txt_passNuevo_incorrecto), Toast.LENGTH_LONG).show();
				return;
			}
			if(passNuevo2 == null || passNuevo2.getText().toString().equals("")){
				Toast.makeText(this, getResources().getString(R.string.txt_passNuevo_incorrecto), Toast.LENGTH_LONG).show();
				return;
			}
			
			if(prop != null){
				passAlmacenado = (String)prop.get(Constantes.PROP_PASSWORD);
				
				if(passAlmacenado.toString().equals(passViejo.getText().toString())){
					if(passNuevo1.getText().toString().equals(passNuevo2.getText().toString())){
						Map<String, String> valores = new HashMap<String, String>();
						
						valores.put(Constantes.PROP_PASSWORD, passNuevo1.getText().toString());
						valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, (String)prop.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES));
						valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, (String)prop.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES));
						valores.put(Constantes.PROP_TIPO_CUENTA, (String)prop.get(Constantes.PROP_TIPO_CUENTA));
						
						FileUtil.guardaDatosConfiguracion(valores, this);
						
						Toast.makeText(this, getResources().getString(R.string.txt_cambio_pass_ok), Toast.LENGTH_LONG).show();
						
						passViejo.setText("");
						passNuevo1.setText("");
						passNuevo2.setText("");
					}else{
						Toast.makeText(this, getResources().getString(R.string.txt_nuevos_pass_incorrectos), Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(this, getResources().getString(R.string.txt_datos_incorrectos), Toast.LENGTH_LONG).show();
				}
			}
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
}

package com.jhonny.detective;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;


public class ContrasenaActivity extends SherlockActivity {
	
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contrasena);
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
		cargaConfiguracionGlobal();
		cargaPublicidad();
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
						valores.put(Constantes.PROP_FONDO_PANTALLA, (String)prop.get(Constantes.PROP_FONDO_PANTALLA));
						valores.put(Constantes.PROP_EMAIL, (String)prop.get(Constantes.PROP_EMAIL));
						valores.put(Constantes.PROP_EMAIL_ENVIO, (String)prop.get(Constantes.PROP_EMAIL_ENVIO));
						valores.put(Constantes.PROP_EMAIL_CHECK, (String)prop.get(Constantes.PROP_EMAIL_CHECK));
						
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
	
	private void cargaConfiguracionGlobal(){
		try{
			if(this.view != null){
				String fondo = FileUtil.getFondoPantallaAlmacenado(this.context);
				if(fondo != null){
					String imagen = Constantes.mapaFondo.get(Integer.parseInt(fondo));
					int imageResource1 = this.view.getContext().getApplicationContext().getResources().getIdentifier(
							imagen, "drawable", this.view.getContext().getApplicationContext().getPackageName());
					Drawable image = this.view.getContext().getResources().getDrawable(imageResource1);
					ImageView imageView = (ImageView)findViewById(R.id.fondo_contrasena);
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
		
		MMAdView adView1 = new MMAdView(this);
		adView1.setApid("148574");
		MMRequest request1 = new MMRequest();
		adView1.setMMRequest(request1);
		adView1.setId(MMSDK.getDefaultAdId());
		adView1.setWidth(placementWidth);
		adView1.setHeight(BANNER_AD_HEIGHT);

		LinearLayout layout2 = (LinearLayout)findViewById(R.id.linearLayout2);
		layout2.removeAllViews();
		layout2.addView(adView1);
		adView1.getAd();
		
		MMAdView adView2 = new MMAdView(this);
		adView2.setApid("148574");
		MMRequest request = new MMRequest();
		adView2.setMMRequest(request);
		adView2.setId(MMSDK.getDefaultAdId());
		adView2.setWidth(placementWidth);
		adView2.setHeight(BANNER_AD_HEIGHT);

		LinearLayout layout3 = (LinearLayout)findViewById(R.id.linearLayout3);
		layout3.removeAllViews();
		layout3.addView(adView2);
		adView2.getAd();
	}
}

package com.jhonny.detective;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class InicioActivity extends SherlockActivity {
	
	private String PASS;
	private String DIST_MIN_ACTUALIZACIONES;
	private String TMP_MIN_ACTUALIZACIONES;
	private String TIPO_CUENTA;
	private String FONDO_PANTALLA;
	private int contSalida = 0;
	private SlidingMenu menu;
	private View view;
	private Context context;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_inicio);
    	contSalida = 0;
    	this.context = this;
    	this.view = getWindow().getDecorView();
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_inicio, menu);
		return true;
    }
    
    
    @Override
    protected void onResume(){
    	super.onResume();
    	contSalida = 0;
    	
    	try{
        	EditText et = (EditText)findViewById(R.id.editText1);
        	et.setText("");
        	
        	reiniciarFondoOpciones();
        	cargaConfiguracionGlobal();
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
    
    
    public void accesoUsuarioRegistrado(View view){
    	Properties prop = new Properties();
    	Context ctx = this;
    	
    	try{
    		EditText passUsuario = (EditText)findViewById(R.id.editText1);
    		prop = FileUtil.getFicheroAssetConfiguracion(ctx);
    		
    		if(prop != null){
    			// carga los datos de la configuracion guardados
    			if(prop.containsKey(Constantes.PROP_PASSWORD))
    				PASS = (String)prop.get(Constantes.PROP_PASSWORD);
    			if(prop.containsKey(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES))
    				DIST_MIN_ACTUALIZACIONES = (String)prop.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES);
    			if(prop.containsKey(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES))
    				TMP_MIN_ACTUALIZACIONES = (String)prop.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES);
    			if(prop.containsKey(Constantes.PROP_TIPO_CUENTA))
    				TIPO_CUENTA = (String)prop.get(Constantes.PROP_TIPO_CUENTA);
    			if(prop.containsKey(Constantes.PROP_FONDO_PANTALLA))
    				FONDO_PANTALLA = (String)prop.get(Constantes.PROP_FONDO_PANTALLA);
    		}
    		
    		if(passUsuario == null || passUsuario.length() <= 0){
    			String text = getResources().getString(R.string.txt_debe_introducir);
    			Toast.makeText(InicioActivity.this, text, Toast.LENGTH_LONG).show();
    		}else{
    			Map <String, String> valores = new HashMap<String, String>();
    			
				if(PASS == null || PASS.toString().equals("null")){
					// contraseña sin establecer
					PASS = passUsuario.getText().toString();
					
					valores.put(Constantes.PROP_PASSWORD, PASS);
					valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, DIST_MIN_ACTUALIZACIONES);
					valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, TMP_MIN_ACTUALIZACIONES);
					valores.put(Constantes.PROP_TIPO_CUENTA, TIPO_CUENTA);
					valores.put(Constantes.PROP_FONDO_PANTALLA, FONDO_PANTALLA);
					
					FileUtil.guardaDatosConfiguracion(valores, InicioActivity.this);
				}
				
				if(passUsuario.getText().toString().equals(PASS)){
					// contraseña corrrecta
					Intent intent = new Intent(this, PrincipalActivity.class);
					startActivity(intent);
				}else{
					// contraseña incorrecta
					String text = getResources().getString(R.string.txt_datos_incorrectos);
					Toast.makeText(InicioActivity.this, text, Toast.LENGTH_LONG).show();
					passUsuario.setText("");
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
			if(layout_inicio != null){
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
			}
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
					ImageView imageView = (ImageView)findViewById(R.id.fondo_inicio);
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
}

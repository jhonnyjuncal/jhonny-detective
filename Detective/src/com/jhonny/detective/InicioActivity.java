package com.jhonny.detective;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class InicioActivity extends FragmentActivity {
	
	private String PASS;
	private String DIST_MIN_ACTUALIZACIONES;
	private String TMP_MIN_ACTUALIZACIONES;
	private String TIPO_CUENTA;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_inicio, menu);
        return true;
    }
    
    
    @Override
    protected void onResume(){
    	super.onResume();
    	
    	try{
        	EditText et = (EditText)findViewById(R.id.editText1);
        	et.setText("");
        }catch(Exception ex){
        	ex.printStackTrace();
        }
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
}

package com.jhonny.detective;

import java.io.InputStream;
import java.util.Properties;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class InicioActivity extends Activity {
	
	public static String PASS = null;
	private static final String FICHERO_CONFIGURACION = "config.properties";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        
		EditText passUsuario = (EditText) findViewById(R.id.editText2);
		passUsuario.setText("");
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_inicio, menu);
        return true;
    }
    
    
    public void accesoUsuarioRegistrado(View view){
    	try{
    		EditText passUsuario = (EditText) findViewById(R.id.editText2);
    		
    		if(passUsuario == null || passUsuario.length() <= 0){
    			String text = getResources().getString(R.string.txt_debe_introducir);
    			Toast.makeText(InicioActivity.this, text, Toast.LENGTH_LONG).show();
    		}else{
    			Resources resources = this.getResources();
    			AssetManager assetManager = resources.getAssets();
    			
    			InputStream inputStream = assetManager.open(FICHERO_CONFIGURACION);
    		    Properties properties = new Properties();
    		    properties.load(inputStream);
    			
    		    if(properties != null){
    		    	String contrasena = (String)properties.get("contrasena");
    		    	if(contrasena != null){
    		    		if(contrasena.length() > 0 && contrasena.equals("null")){
    		    			// contraseña sin establecer
    		    			PASS = passUsuario.getText().toString();
    		    			guardaNuevaContrasena();
    		    		}else{
    		    			PASS = contrasena;
    		    		}
    		    	}
    		    	
    		    	if(passUsuario.getText().toString().equals(PASS)){
        				// usuario y contraseña corrrectos
        				Intent intent = new Intent(this, PrincipalActivity.class);
        				startActivity(intent);
        				this.finish();
        			}else{
        				String text = getResources().getString(R.string.txt_datos_incorrectos);
            			Toast.makeText(InicioActivity.this, text, Toast.LENGTH_LONG).show();
            			passUsuario.setText("");
        			}
    		    }
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    private void guardaNuevaContrasena(){
    	try{
    		// lectura del fichero de configuracion
    		SharedPreferences prefs = getSharedPreferences(FICHERO_CONFIGURACION, Context.MODE_WORLD_WRITEABLE);
    		SharedPreferences.Editor editor = prefs.edit();
    		
    		if(prefs != null){
    			// hay que recoger los datos introducidos por el usuario en la configuracion
    			editor.putString("contrasena", PASS);
    			editor.commit();
    			finish();
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
}

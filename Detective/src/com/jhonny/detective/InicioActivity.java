package com.jhonny.detective;

import java.io.InputStream;
import java.util.Properties;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class InicioActivity extends FragmentActivity {
	
	public static String USUARIO = null;
	public static String CONTRASENA = null;
	private static final String FICHERO_CONFIGURACION = "config.properties";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        
        EditText textoUsuario = (EditText) findViewById(R.id.editText1);
        textoUsuario.setText("");
		EditText passUsuario = (EditText) findViewById(R.id.editText2);
		passUsuario.setText("");
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_inicio, menu);
        return true;
    }
    
    
    public void nuevoUsuarioLink(View view){
    	
    }
    
    
    public void recordarContrasenaLink(View view){
    	
    }
    
    
    public void accesoUsuarioRegistrado(View view){
    	try{
    		EditText textoUsuario = (EditText) findViewById(R.id.editText1);
    		EditText passUsuario = (EditText) findViewById(R.id.editText2);
    		
    		if(textoUsuario == null || passUsuario == null){
    			String text = "debe introducir el usuario y la contraseña";
    			Toast.makeText(InicioActivity.this, text, Toast.LENGTH_LONG).show();
    		}else{
    			Resources resources = this.getResources();
    			AssetManager assetManager = resources.getAssets();
    			
    			InputStream inputStream = assetManager.open(FICHERO_CONFIGURACION);
    		    Properties properties = new Properties();
    		    properties.load(inputStream);
    			
    		    if(properties != null){
    		    	String usuario = (String)properties.get("usuario");
    		    	if(usuario != null && usuario.length() > 0)
        				USUARIO = usuario;
    		    	
    		    	String contrasena = (String)properties.get("contrasena");
    		    	if(contrasena != null && contrasena.length() > 0)
        				CONTRASENA = contrasena;
    		    	
    		    	if(textoUsuario.getText().toString().equals(USUARIO) && passUsuario.getText().toString().equals(CONTRASENA)){
        				// usuario y contraseña corrrectos
        				Intent intent = new Intent(this, PrincipalActivity.class);
        				startActivity(intent);
        			}else{
        				String text = "Los datos introducidos son incorrectos";
            			Toast.makeText(InicioActivity.this, text, Toast.LENGTH_LONG).show();
            			passUsuario.setText("");
        			}
    		    }
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }
    
    
    /* Add a class to handle fragment */
//    public static class SSFFragment extends Fragment {
//       @Override
//       public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//          // Inflate the layout for this fragment
//          View v = inflater.inflate(R.layout.activity_mapa, container, false);
//          return v;
//       }
//    }
}

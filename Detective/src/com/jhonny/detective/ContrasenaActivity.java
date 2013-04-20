package com.jhonny.detective;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


public class ContrasenaActivity extends Activity {
	
	AdView adView1 = null;
	AdView adView2 = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contrasena);
		
		
		// se crea la vista
		adView1 = new AdView(this, AdSize.BANNER, "a1513f4a3b63be1");
		
		// se obtiene el layout para el banner
		LinearLayout layout1 = (LinearLayout)findViewById(R.id.linearLayout2);
	    
		// se añade la vista
		layout1.addView(adView1);
		
		// se inicia la solicitud para cargar el anuncio
		adView1.loadAd(new AdRequest());
		
		
		// se crea la vista
		adView2 = new AdView(this, AdSize.BANNER, "a1513f4a3b63be1");
		
		// se obtiene el layout para el banner
		LinearLayout layout2 = (LinearLayout)findViewById(R.id.linearLayout3);
		
		// se añade la vista
		layout2.addView(adView2);
		
		// se inicia la solicitud para cargar el anuncio
		adView2.loadAd(new AdRequest());
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_contrasena, menu);
		return true;
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
}

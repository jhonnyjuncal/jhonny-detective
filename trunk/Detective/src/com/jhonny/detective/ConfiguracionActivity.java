package com.jhonny.detective;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;


public class ConfiguracionActivity extends Activity implements OnItemSelectedListener {
	
	private AdView adView1;
	private AdView adView2;
	private Spinner spDistancia;
	private Spinner spTiempo;
	private static String PASS;
	private static float DISTANCIA_MINIMA_PARA_ACTUALIZACIONES;
	private static long TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES;
	private static String TIPO_CUENTA;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracion);
		
		int pos1 = 0;
		int pos2 = 0;
		
		try{
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
			
			
			// se cargan los datos de la configuracion almacenada
			spDistancia = (Spinner) findViewById(R.id.spinner1);
			spTiempo = (Spinner) findViewById(R.id.spinner2);
			
			pos1 = FileUtil.getPosicionSpinnerSeleccionada(1, this);
			pos2 = FileUtil.getPosicionSpinnerSeleccionada(2, this);
			
			spDistancia.setSelection(pos1);
			spTiempo.setSelection(pos2);
			
			spDistancia.setOnItemSelectedListener(this);
			spTiempo.setOnItemSelectedListener(this);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_configuracion, menu);
		return true;
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
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
}

package com.jhonny.detective;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class PosicionesActivity extends Activity implements OnItemClickListener {
	
	AdView adView = null;
	List<ObjetoPosicion> listaPosiciones = null;
	ListView listView = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posiciones);
		
		// se crea la vista
		adView = new AdView(this, AdSize.BANNER, "a1513f4a3b63be1");
		// se obtiene el layout para el banner
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout2);
		// se añade la vista
		layout.addView(adView);
		// se inicia la solicitud para cargar el anuncio
		adView.loadAd(new AdRequest());
		
		cargaPosicionesAlmacenadas();
		
		listView = (ListView)findViewById(R.id.listView1);
		
		if(listaPosiciones != null && listaPosiciones.size() > 0){
			List<String> lista = new ArrayList<String>();
			
			for(ObjetoPosicion op : listaPosiciones){
				DateFormat dateFormatter1 = DateFormat.getDateInstance(DateFormat.LONG, getResources().getConfiguration().locale);
				DateFormat dateFormatter2 = DateFormat.getTimeInstance(DateFormat.MEDIUM, getResources().getConfiguration().locale);
				lista.add(dateFormatter1.format(op.getFecha()) + " - " + dateFormatter2.format(op.getFecha()));
			}
			
			StableArrayAdapter adapter = new StableArrayAdapter(this, R.layout.listview_personalizado, lista);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_posiciones, menu);
		return true;
	}
	
	
	public void cargaPosicionesAlmacenadas(){
    	try{
    		// lectura del fichero de configuracion
    		Context ctx = this;
    		Properties prefs = FileUtil.getFicheroAssetConfiguracion(ctx);
    		
    		String tipoCuenta = (String)prefs.get(Constantes.PROP_TIPO_CUENTA);
			listaPosiciones = FileUtil.getListaAssetPosiciones(ctx, Integer.parseInt(tipoCuenta));
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
    }
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		try{
			ObjetoPosicion op = listaPosiciones.get(pos);
			Locale locale = getResources().getConfiguration().locale;
			
			EditText editLatitud = (EditText)findViewById(R.id.editText1);
			EditText editLongitud = (EditText)findViewById(R.id.editText2);
			EditText editFecha = (EditText)findViewById(R.id.editText3);
			EditText editHora = (EditText)findViewById(R.id.editText4);
			
			editLatitud.setText(String.valueOf(op.getLatitud()));
			editLongitud.setText(String.valueOf(op.getLongitud()));
			editFecha.setText(FileUtil.getFechaFormateada(op.getFecha(), locale));
			editHora.setText(FileUtil.getHoraFormateada(op.getFecha(), locale));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	
	private class StableArrayAdapter extends ArrayAdapter<String> {
		
		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
		
		
		public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			
			for(int i=0; i<objects.size(); i++){
				mIdMap.put(objects.get(i), i);
			}
		}
		
		
		@Override
	    public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}
		
		
		@Override
		public boolean hasStableIds() {
			return true;
		}
	}
}

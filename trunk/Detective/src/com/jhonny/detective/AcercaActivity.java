package com.jhonny.detective;

import java.util.Locale;
import org.joda.time.DateTime;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.view.Menu;
import android.widget.TextView;


public class AcercaActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acerca);
		
		try{
			// version de la aplicacion
			PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
			TextView textoVersion = (TextView)findViewById(R.id.textView3);
			textoVersion.setText(pInfo.versionName);
			
			// fecha de creacion
			TextView textoFecha = (TextView)findViewById(R.id.textView5);
			DateTime fecha = new DateTime("2013-11-04");
			Locale locale = getResources().getConfiguration().locale;
			textoFecha.setText(FileUtil.getFechaFormateada(fecha.toDate(), locale));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_acerca, menu);
		return true;
	}

}

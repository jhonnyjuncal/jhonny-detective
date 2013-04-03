package com.jhonny.detective;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;


public class PosicionesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posiciones);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_posiciones, menu);
		return true;
	}

}

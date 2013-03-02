package com.jhonny.detective;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;


public class PosicionesActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posiciones);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_posiciones, menu);
        return true;
    }
}

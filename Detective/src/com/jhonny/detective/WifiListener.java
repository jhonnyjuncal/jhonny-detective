package com.jhonny.detective;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;


public class WifiListener extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			if(PrincipalActivity.viewPrincipal != null && PrincipalActivity.resourcesPrincipal != null){
				TextView textoWifi = (TextView)PrincipalActivity.viewPrincipal.findViewById(R.id.textView6);
	    		
	    		if(FileUtil.getWifiManager().isWifiEnabled())
	    			textoWifi.setText(PrincipalActivity.resourcesPrincipal.getString(R.string.txt_encendida));
	    		else
	    			textoWifi.setText(PrincipalActivity.resourcesPrincipal.getString(R.string.txt_apagada));
	    		
	    		PrincipalActivity.viewPrincipal.buildDrawingCache(true);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}

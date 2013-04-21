package com.jhonny.detective;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;


public class ServicioActualizacion extends Service{
	
	private Timer timer = null;
	public static Activity ACTIVIDAD;
	private boolean primeraEjecucion = true;
	
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
	
	
	public static void establecerActividadPrincipal(Activity actividad){
		ServicioActualizacion.ACTIVIDAD = actividad;
	}
	
	
	public void onCreate(){
        super.onCreate();
        
        // se inicia el servicio
        this.iniciarServicio();
    }
	
	
	public void iniciarServicio(){
		try{
			if(this.timer == null){
				// Creamos el timer
				this.timer = new Timer();
				
				// Configuramos lo que tiene que hacer
				this.timer.scheduleAtFixedRate(new TimerTask(){
						public void run(){
							ejecutarTarea();
						}}, 0, 2 * (60 * 1000)); // minutos
			}
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
	
	
	public void finalizarServicio(){
		try{
			// Detenemos el timer
			this.timer.cancel();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	private void ejecutarTarea(){
		try{
			if(!primeraEjecucion){
				ServicioActualizacion.ACTIVIDAD.runOnUiThread(new Runnable(){
					public void run(){
						if(!FileUtil.getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)){
							for(String prov : FileUtil.getLocationManager().getAllProviders()){
								Location location = FileUtil.getLocationManager().getLastKnownLocation(prov);
								
								if(location != null){
									ObjetoPosicion pos = new ObjetoPosicion();
									
						    		pos.setFecha(new Date());
						    		pos.setLatitud(location.getLatitude());
						    		pos.setLongitud(location.getLongitude());
						    		
						    		PrincipalActivity pa = new PrincipalActivity();
						    		FileUtil.almacenaPosicionesAlFinalEnFichero(pos, pa.getApplicationContext());
						    		FileUtil.cargaPosicionesAlmacenadas(FileUtil.getLocalizador().contexto, 
						    				FileUtil.getLocalizador().view);
								}
							}
						}
					}
				});
			}else
				primeraEjecucion = false;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}

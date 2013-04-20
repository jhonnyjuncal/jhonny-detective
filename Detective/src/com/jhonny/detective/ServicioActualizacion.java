package com.jhonny.detective;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.joda.time.DateTime;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;


public class ServicioActualizacion extends Service{
	
	private Timer timer = null;
	public static Activity ACTIVIDAD;
	
	
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
			// Creamos el timer
			this.timer = new Timer();
 
			// Configuramos lo que tiene que hacer
			this.timer.scheduleAtFixedRate(new TimerTask(){
					public void run(){
					ejecutarTarea();
					}}, 0, 10 * (60 * 1000)); // minutos
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
			ServicioActualizacion.ACTIVIDAD.runOnUiThread(new Runnable(){
				public void run(){
					System.out.println(new DateTime().toString());
					
					if(!FileUtil.getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER)){
						for(String prov : FileUtil.getLocationManager().getAllProviders()){
							System.out.println("- provider: " + prov);
							Location location = FileUtil.getLocationManager().getLastKnownLocation(prov);
							
							if(location != null){
								System.out.println("- latitud: " + location.getLatitude() + 
											" - longitud: " + location.getLongitude());
								
								ObjetoPosicion pos = new ObjetoPosicion();
					    		pos.setFecha(new Date());
					    		pos.setLatitud(location.getLatitude());
					    		pos.setLongitud(location.getLongitude());
					    		
					    		System.out.println("-- " + pos.toString());
					    		PrincipalActivity pa = new PrincipalActivity();
					    		FileUtil.almacenaPosicionesAlFinalEnFichero(pos, pa.getApplicationContext());
					    		pa.cargaPosicionesAlmacenadas();
							}else{
								System.out.println("location es nulo");
							}
						}
					}
				}
			});
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}

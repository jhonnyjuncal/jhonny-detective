package com.jhonny.detective;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.joda.time.DateTime;
import org.joda.time.Days;
import android.content.Context;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.TextView;


public class FileUtil implements Serializable{
	
	private static final long serialVersionUID = -3721769765641235234L;
	private static LocationManager locationManagerGps;
	private static LocationManager locationManagerInternet;
	private static Localizador localizador;
	private static WifiManager wifi = null;
	
	
	public static LocationManager getLocationManagerGps(){
		return locationManagerGps;
	}
	
	
	public static void setLocationManagerGps(LocationManager locationManagerGps){
		FileUtil.locationManagerGps = locationManagerGps;
	}
	
	
	public static LocationManager getLocationManagerInternet(){
		return locationManagerInternet;
	}
	
	
	public static void setLocationManagerInternet(LocationManager locationManagerInternet){
		FileUtil.locationManagerInternet = locationManagerInternet;
	}
	
	
	public static Localizador getLocalizador(){
		return localizador;
	}
	
	
	public static void setLocalizador(Localizador localizador){
		FileUtil.localizador = localizador;
	}
	
	
	public static WifiManager getWifiManager(){
		return FileUtil.wifi;
	}
	
	
	public static void setWifiManager(WifiManager wifi){
		FileUtil.wifi = wifi;
	}
	
	
	/**
	 * Metodo que se le pasa un objeto de tipo Resources (clase que extiende a Activity) y devuelve
	 * el fichero de propiedades "config.properties"
	 * @param Resources
	 * @return Properties
	 */
	public static Properties getFicheroAssetConfiguracion(Context ctx) throws IOException{
		Properties properties = new Properties();
		
		try{
			InputStream instream = ctx.openFileInput(Constantes.FICHERO_CONFIGURACION);
			
			if(instream != null){
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				int contador = 1;
				
				String linea = buffreader.readLine();
				while(linea != null){
					if(linea.equals(""))
						linea = buffreader.readLine();
					if(linea != null){
						switch(contador){
							case 1:
								// linea de la contraseña
								properties.put(Constantes.PROP_PASSWORD, linea);
								break;
							case 2:
								// distancia minima de la actualizacion
								properties.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, linea);
								break;
							case 3:
								// tiempo maximo de actualizacion
								properties.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, linea);
								break;
							case 4:
								// tipo de cuenta
								properties.put(Constantes.PROP_TIPO_CUENTA, linea);
								break;
							case 5:
								// fondo de pantalla
								properties.put(Constantes.PROP_FONDO_PANTALLA, linea);
								break;
						}
						contador++;
						linea = buffreader.readLine();
					}
				}
			}
		}catch(FileNotFoundException fnf){
			FileOutputStream out = ctx.openFileOutput(Constantes.FICHERO_CONFIGURACION, Context.MODE_PRIVATE);
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return properties;
	}
	
	
	
	/**
	 * Devuelve las posiciones almacenadas en el fichero
	 * @param recurso
	 * @return listaPosiciones
	 * @throws IOException
	 */
	public static List<ObjetoPosicion> getListaAssetPosiciones(Context ctx, int tipoCuenta) throws IOException{
		List<ObjetoPosicion> listaPosiciones = null;
		InputStream instream = null;
		
		try{
			File f = new File(ctx.getFilesDir().toString() + System.getProperty("file.separator") + Constantes.FICHERO_POSICIONES);
			if(!f.exists()){
				f.createNewFile();
				return new ArrayList<ObjetoPosicion>();
			}
			
			instream = ctx.openFileInput(Constantes.FICHERO_POSICIONES);
			if(instream != null){
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				
				String linea = buffreader.readLine();
				while(linea != null){
					if(linea.equals(""))
						linea = buffreader.readLine();
					if(listaPosiciones == null)
						listaPosiciones = new ArrayList<ObjetoPosicion>();
					if(linea != null){
						ObjetoPosicion op = FileUtil.conviertePosicionLeida(linea);
						if(op != null){
							int dias = cantidadDeDiasDiferencia(op.getFecha());
							switch(tipoCuenta){
								case 1:
									// cuenta gratuita
									if(dias <= 1){
										listaPosiciones.add(op);
									}
									break;
								case 2:
									// cuenta medium (5 dias de almacenamiento)
									if(dias <= 5){
										listaPosiciones.add(op);
									}
									break;
								case 3:
									// cuenta premium (15 dias de almacenamiento)
									if(dias <= 15){
										listaPosiciones.add(op);
									}
									break;
							}
						}
					}
					if(linea != null && linea.length() > 0)
						linea = "";
				}
		    }
			
			borraFicheroActualDePosiciones(ctx);
			almacenaPosicionesActualesEnFichero(listaPosiciones, ctx);
		}catch(FileNotFoundException e){
			FileOutputStream out = ctx.openFileOutput(Constantes.FICHERO_POSICIONES, Context.MODE_PRIVATE);
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(instream != null)
					instream.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return listaPosiciones;
	}
	
	
	/**
	 * borra el fichero de posiciones
	 * @param ctx
	 */
	public static void borraFicheroActualDePosiciones(Context ctx){
		OutputStreamWriter out = null;
		try{
			OutputStream output = ctx.openFileOutput(Constantes.FICHERO_POSICIONES, Context.MODE_PRIVATE);
			out = new OutputStreamWriter(output);
			out.write("");
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try {
				out.close();
			}catch(IOException e){
				e.printStackTrace();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Convierte las posiciones leidas del fichero en ObjetoPosiciones
	 * @param linea
	 * @return ObjetoPosicion
	 */
	public static ObjetoPosicion conviertePosicionLeida(String linea){
		ObjetoPosicion pos = null;
		
		try{
			if(linea != null){
				pos = new ObjetoPosicion();
				
				Integer indice1 = linea.indexOf("?");
				String fecha = linea.substring(0, indice1);
				pos.setFecha(new Date(Long.parseLong(fecha)));
				
				Integer indice2 = linea.indexOf("?", ++indice1);
				String latitud = linea.substring(indice1, indice2);
				pos.setLatitud(Double.parseDouble(latitud));
				
				String longitud = linea.substring(++indice2, linea.length());
				pos.setLongitud(Double.parseDouble(longitud));
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return pos;
	}
	
	
	/**
	 * Calkcula la cantidad de dias entre 2 fechas
	 * @param fecha
	 * @return int dias
	 */
	public static int cantidadDeDiasDiferencia(Date fecha){
		int dias = 0;
		try{
			DateTime fechaActual = new DateTime();
			DateTime fechaCoordenada = new DateTime(fecha);
			
			dias = Days.daysBetween(fechaCoordenada, fechaActual).getDays();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return dias;
	}
	
	
	/**
	 * Añade una posicion al final del fichero
	 * @param pos
	 * @param ctx
	 */
	public static void almacenaPosicionesAlFinalEnFichero(ObjetoPosicion pos, Context ctx){
		OutputStreamWriter out = null;
    	try{
    		if(pos != null){
    			OutputStream output = ctx.openFileOutput(Constantes.FICHERO_POSICIONES, Context.MODE_APPEND);
    			out = new OutputStreamWriter(output);
    			
    			String valor = pos.getFecha().getTime()+"?"+pos.getLatitud()+"?"+pos.getLongitud()+"\r\n";
		    	out.write(valor);
    		}
    	}catch(IOException e){
    		e.printStackTrace();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}finally{
    		try{
    			if(out != null)
    				out.close();
    		}catch(Exception ex){
    			ex.printStackTrace();
    		}
    	}
	}
	
	
	/**
	 * Añade las posiciones de lista al fichero de posiciones
	 * @param posiciones
	 * @param ctx
	 */
	public static void almacenaPosicionesActualesEnFichero(List<ObjetoPosicion> posiciones, Context ctx){
		OutputStreamWriter out = null;
		
    	try{
    		if(posiciones != null && posiciones.size() > 0){
    			OutputStream output = ctx.openFileOutput(Constantes.FICHERO_POSICIONES, Context.MODE_PRIVATE);
    			out = new OutputStreamWriter(output);
    			
	    		for(ObjetoPosicion pos : posiciones){
			    	String valor = pos.getFecha().getTime() + "?" + pos.getLatitud() + "?" + pos.getLongitud() + "\r\n";
			    	out.write(valor);
	    		}
    		}
    	}catch(IOException e){
    		e.printStackTrace();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}finally{
    		try{
    			if(out != null)
    				out.close();
    		}catch(Exception ex){
    			ex.printStackTrace();
    		}
    	}
    }
	
	
	/**
	 * Devuelve la fecha formateada dependiendo de la configuracion del telefono
	 * @param fecha
	 * @param locale
	 * @return fecha formateada
	 */
	public static String getFechaFormateada(Date fecha, Locale locale){
		String resultado = "";
		
		try{
			DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			resultado = dateFormatter.format(fecha);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resultado;
	}
	
	
	/**
	 * Devuelve la hora formateada dependiendo de la configuracion del telefono
	 * @param fecha
	 * @param locale
	 * @return hora formateada
	 */
	public static String getHoraFormateada(Date fecha, Locale locale){
		String resultado = "";
		
		try{
			DateFormat dateFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
			resultado = dateFormatter.format(fecha);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resultado;
	}
	
	
	/**
	 * Guarda los valores de la configuracion actual
	 * @param valores
	 * @param ctx
	 */
	public static void guardaDatosConfiguracion(Map<String, String> valores, Context ctx){
		OutputStreamWriter out = null;
		String distMinActualizaciones = null;
		String tmpoMinActualizaciones = null;
		String tipoCuenta = null;
		String fondoPan = null;
		
		try{
			if(valores != null){
				OutputStream output = ctx.openFileOutput(Constantes.FICHERO_CONFIGURACION, Context.MODE_PRIVATE);
    			out = new OutputStreamWriter(output);
    			
				if(valores.containsKey(Constantes.PROP_PASSWORD)){
					out.write(valores.get(Constantes.PROP_PASSWORD) + "\r\n");
				}
				if(valores.containsKey(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES)){
					distMinActualizaciones = valores.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES);
					if(distMinActualizaciones == null)
						distMinActualizaciones = Constantes.DEFECTO_DISTANCIA_MINIMA_ACTUALIZACIONES;
					out.write(distMinActualizaciones + "\r\n");
				}
				if(valores.containsKey(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES)){
					tmpoMinActualizaciones = valores.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES);
					if(tmpoMinActualizaciones == null)
						tmpoMinActualizaciones = Constantes.DEFECTO_TIEMPO_MINIMO_ACTUALIZACIONES;
					out.write(tmpoMinActualizaciones + "\r\n");
				}
				if(valores.containsKey(Constantes.PROP_TIPO_CUENTA)){
					tipoCuenta = valores.get(Constantes.PROP_TIPO_CUENTA);
					if(tipoCuenta == null)
						tipoCuenta = Constantes.DEFECTO_TIPO_CUENTA;
					out.write(tipoCuenta + "\r\n");
				}
				if(valores.containsKey(Constantes.PROP_FONDO_PANTALLA)){
					fondoPan = valores.get(Constantes.PROP_FONDO_PANTALLA);
					if(fondoPan == null)
						fondoPan = Constantes.DEFECTO_FONDO;
					out.write(fondoPan + "\r\n");
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				if(out != null)
					out.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	
	public static int getPosicionSpinnerSeleccionada(int variable, Context ctx){
		int resultado = 0;
		String key = null;
		
		try{
			if(variable > 0){
				Properties prop = getFicheroAssetConfiguracion(ctx);
				
				switch(variable){
					case 1:
						// Distancia minima de actualizacion
						if(!Constantes.mapaDist.isEmpty()){
							key = (String)prop.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES);
							resultado = Constantes.mapaDist.get(key).intValue();
						}
						break;
					case 2:
						// Tiempo minimo de actualizacion
						if(!Constantes.mapaTmpo.isEmpty()){
							key = (String)prop.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES);
							resultado = Constantes.mapaTmpo.get(key).intValue();
						}
						break;
					case 3:
						// Fondo de pantalla de aplicacion
						if(!Constantes.mapaFondo.isEmpty()){
							String fondo = getFondoPantallaAlmacenado(ctx);
							if(fondo == null){
								resultado = 0;
							}else{
								int cont = 0;
								for(String valor : Constantes.mapaFondo.values()){
									if(valor.equals(fondo.toString())){
										resultado = cont;
										break;
									}
									cont++;
								}
							}
						}
						break;
					default:
						return 0;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return resultado;
	}
	
	
	public static String getFondoPantallaAlmacenado(Context context){
		try{
			Properties prop = null;
			if(!Constantes.mapaFondo.isEmpty()){
				prop = FileUtil.getFicheroAssetConfiguracion(context);
				if(prop != null && prop.containsKey(Constantes.PROP_FONDO_PANTALLA.toString())){
					return (String)prop.get(Constantes.PROP_FONDO_PANTALLA);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	
	public static void cargaPosicionesAlmacenadas(Context ctx, View view){
    	int contador = 0;
    	try{
    		// lectura del fichero de configuracion
    		Properties prefs = new Properties();
    		prefs = FileUtil.getFicheroAssetConfiguracion(ctx);
    		
    		String tipoCuenta = (String) prefs.get(Constantes.PROP_TIPO_CUENTA);
    		
    		List<ObjetoPosicion> lista = FileUtil.getListaAssetPosiciones(ctx, Integer.parseInt(tipoCuenta));
    		
			if(lista != null){
				contador = lista.size();
			}else{
				contador = 0;
			}
			
			TextView textoPosiciones = (TextView)view.findViewById(R.id.textView4);
			if(textoPosiciones != null){
				textoPosiciones.setText(String.valueOf(contador));
			}
			
			if(lista != null){
				PrincipalActivity.listaPosiciones = lista;
			}else{
				PrincipalActivity.listaPosiciones = new ArrayList<ObjetoPosicion>();
			}
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
    }
}

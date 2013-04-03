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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.joda.time.DateTime;
import org.joda.time.Days;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;


public class FileUtil implements Serializable{
	
	private static final long serialVersionUID = -3721769765641235234L;
	
	
	/**
	 * Metodo que se le pasa un objeto de tipo Resources (clase que extiende a Activity) y devuelve
	 * el fichero de propiedades "config.properties"
	 * @param Resources
	 * @return Properties
	 */
	public static Properties getFicheroAssetConfiguracion(Resources recurso) throws IOException{
		Properties properties = new Properties();
		try{
			Resources resources = recurso;
			AssetManager assetManager = resources.getAssets();
			
			InputStream inputStream = assetManager.open(Constantes.FICHERO_CONFIGURACION);
			properties.load(inputStream);
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
		
		try{
			File f = new File(ctx.getFilesDir().toString() + System.getProperty("file.separator") + Constantes.FICHERO_POSICIONES);
			if(!f.exists()){
				f.createNewFile();
				return new ArrayList<ObjetoPosicion>();
			}
			
			InputStream instream = ctx.openFileInput(Constantes.FICHERO_POSICIONES);
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
			
			instream.close();
		}catch(FileNotFoundException e){
			FileOutputStream out = ctx.openFileOutput(Constantes.FICHERO_POSICIONES, 0);
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return listaPosiciones;
	}
	
	
	/**
	 * borra el fichero de posiciones
	 * @param ctx
	 */
	private static void borraFicheroActualDePosiciones(Context ctx){
		OutputStreamWriter out = null;
		try{
			OutputStream output = ctx.openFileOutput(Constantes.FICHERO_POSICIONES, ctx.MODE_PRIVATE);
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
			
			dias = Days.daysBetween(fechaActual, fechaCoordenada).getDays();
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
    			OutputStream output = ctx.openFileOutput(Constantes.FICHERO_POSICIONES, ctx.MODE_APPEND);
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
    			OutputStream output = ctx.openFileOutput(Constantes.FICHERO_POSICIONES, ctx.MODE_PRIVATE);
    			out = new OutputStreamWriter(output);
    			
	    		for(ObjetoPosicion pos : posiciones){
			    	String valor = pos.getFecha().getTime()+"?"+pos.getLatitud()+"?"+pos.getLongitud()+"\r\n";
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
}

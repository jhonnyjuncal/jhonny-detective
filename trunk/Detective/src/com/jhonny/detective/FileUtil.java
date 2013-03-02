package com.jhonny.detective;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
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
		Resources resources = recurso;
		AssetManager assetManager = resources.getAssets();
		
		InputStream inputStream = assetManager.open(Constantes.FICHERO_CONFIGURACION);
		properties.load(inputStream);
		
		return properties;
	}
	
	
	
	/**
	 * 
	 * @param recurso
	 * @return
	 * @throws IOException
	 */
	public static List<ObjetoPosicion> getListaAssetPosiciones(Context ctx) throws IOException{
		List<ObjetoPosicion> listaPosiciones = null;
		
		try{
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
						listaPosiciones.add(op);
					}
					if(linea != null && linea.length() > 0)
						linea = "";
				}
		    }
			
			instream.close();
		}catch(FileNotFoundException e){
			FileOutputStream out = ctx.openFileOutput(Constantes.FICHERO_POSICIONES, 0);
			out.close();
		}
		return listaPosiciones;
	}
	
	
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
}

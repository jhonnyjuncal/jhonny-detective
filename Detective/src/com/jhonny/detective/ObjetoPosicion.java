package com.jhonny.detective;

import java.io.Serializable;
import java.util.Date;


public class ObjetoPosicion implements Serializable{
	
	private static final long serialVersionUID = 999430421804375555L;
	
	
	private Date fecha;
	private double latitud;
	private double longitud;
	
	
	public ObjetoPosicion() {
		super();
	}
	
	public ObjetoPosicion(Date fecha, double latitud, double longitud){
		super();
		this.fecha = fecha;
		this.latitud = latitud;
		this.longitud = longitud;
	}
	
	
	public Date getFecha() {
		return fecha;
	}
	
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	public double getLatitud() {
		return latitud;
	}
	
	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}
	
	public double getLongitud() {
		return longitud;
	}
	
	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}
	
	public String toString(){
		return "Fecha: " + this.fecha + " - latitud: " + this.latitud + " - longitud: " + this.longitud;
	}
}

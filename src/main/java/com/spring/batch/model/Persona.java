package com.spring.batch.model;

public class Persona {
	
	private String primerNombre;
	private String apellido;
	private String dni;
	
	public static String[] fields() {
		return new String[] {"primerNombre","apellido","dni"};
	}
	
	
	public Persona(String primerNombre, String apellido, String dni) {
		super();
		this.primerNombre = primerNombre;
		this.apellido = apellido;
		this.dni = dni;
	}
	public Persona() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getPrimerNombre() {
		return primerNombre;
	}
	public void setPrimerNombre(String primerNombre) {
		this.primerNombre = primerNombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	@Override
	public String toString() {
		return "Persona [primerNombre=" + primerNombre + ", apellido=" + apellido + ", dni=" + dni + "]";
	}
	
	
	
	
	
	

}

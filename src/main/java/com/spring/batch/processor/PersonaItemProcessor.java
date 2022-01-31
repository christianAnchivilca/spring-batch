package com.spring.batch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.spring.batch.model.Persona;

public class PersonaItemProcessor implements ItemProcessor<Persona, Persona> {
	
	private static final Logger LOG= LoggerFactory.getLogger(PersonaItemProcessor.class);

	@Override
	public Persona process(Persona item) throws Exception {
		//tomamaos los datos  para poder hacerle un cambio
		String primerNombre = item.getPrimerNombre().toUpperCase();
		String apellido = item.getApellido().toUpperCase();
		String dni = item.getDni();
		
		Persona persona = new Persona(primerNombre,apellido,dni);
		
		LOG.info("CONVIRTIENDO ("+item+")  A "+persona);
		return persona;
	}

}

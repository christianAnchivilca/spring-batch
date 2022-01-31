package com.spring.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import com.spring.batch.listener.JobListener;
import com.spring.batch.model.Persona;
import com.spring.batch.processor.PersonaItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	//inyectamos 2 dependencias
	@Autowired
	public JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	
	@Bean
	public FlatFileItemReader<Persona> reader(){
		return new FlatFileItemReaderBuilder<Persona>()
				.name("personaItemReader")//primero es darle un nombre a este itemReader
				.resource(new ClassPathResource("personas-data.csv"))//segundo  es indicarle el recurso que vamos a importar
				.delimited()//le decimos a spring que nuestro archivo esta delimitado por comas
				.names(new String[] {"primerNombre","apellido","dni"})//asignando las propiedads con name y fieldSetMapper
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Persona>() {{
					setTargetType(Persona.class);
				}}).build();
	}
	
	@Bean
	public PersonaItemProcessor processor() {
		return new PersonaItemProcessor();
	}
	
	/*@Bean
	public JdbcBatchItemWriter<Persona> writer(DataSource dataSource){
		return new JdbcBatchItemWriterBuilder<Persona>()// ya que sera las propiedades de la clase Persona que guardaremos en nuestra bd sql, lo usaremos a la hora de realizar un insert
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO persona (primerNombre,apellido,dni) VALUES (:primerNombre, :apellido, :dni)")
				.dataSource(dataSource)
				.build();
		
	}*/
	@Bean
	public FlatFileItemWriter<Persona> writerCsv(){
		
		FlatFileItemWriter<Persona> writer = new FlatFileItemWriter<Persona>();
		writer.setResource(new FileSystemResource("C://dev//bbva//export.csv"));
		DelimitedLineAggregator<Persona> aggregator = new DelimitedLineAggregator<>();
		BeanWrapperFieldExtractor<Persona> fieldExtractor =  new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(Persona.fields());
		aggregator.setFieldExtractor(fieldExtractor);
		writer.setLineAggregator(aggregator);
		return writer;
		
	}
	
	
	@Bean
	public Job importPersonaJob(JobListener listener,Step step1) {//agregamos como parametros un listener para escuchar el estado de nuestro job,tambien tendra un step
		
		return jobBuilderFactory.get("importPersonaJob")
				.incrementer(new RunIdIncrementer())//debemos indicar un incrementar a pesar de que sea uno solo para que la estructura de spring framework en los batch pueda funcionar de manera correcta
		         .listener(listener)
		         .flow(step1)
		         .end()
		         .build();
	}
	@Bean
	public Step step1() {//recibe solo nuestro writer como parametro
		return stepBuilderFactory.get("step1")
				.<Persona,Persona> chunk(10)// definimos nuestro lote , definimos 10 registros que seran guardados en nuestra bdd, si tuvieramos mas se dividen en 10 lotes que es la cantidad que estamos indicando en el chunk
				.reader(reader())
				.processor(processor())
				.writer(writerCsv())
				.build();
	}

}

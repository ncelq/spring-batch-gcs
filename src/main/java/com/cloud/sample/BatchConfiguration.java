package com.cloud.sample;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.storage.GoogleStorageProtocolResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Configuration
@EnableBatchProcessing
@PropertySource("classpath:batch.properties")
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
	
	@Value("gs://test-24203634/output.csv")
	private Resource outputResource;

    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<String> reader() {
        FlatFileItemReader<String> reader = new FlatFileItemReader<String>();
        reader.setResource(new ClassPathResource("sample-data.csv"));
        reader.setLineMapper(new PassThroughLineMapper());
        return reader;
    }

    @Bean
    public TextItemProcessor processor() {
        return new TextItemProcessor();
    }

    @Bean
    public ItemWriter<String> writer() throws Exception {
    	GSFileWriter<String> writer = new GSFileWriter<String>();
    	
    	writer.setStorage(StorageApplication.storage());
    	writer.setResource(outputResource);

    	//writer.setResource(new FileSystemResource(new File("target/output.txt")));
    	writer.setLineAggregator(new PassThroughLineAggregator<String>());


    	return writer;
 
    }
    
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob() throws Exception {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .start(step1()).build();
                //.start(step1()).next(step2()).build();
    }

    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory.get("step1")
                .<String, String> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    /*
    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
        		.tasklet(taskletStep)
                .build();
    }*/
    // end::jobstep[]
    

	@Configuration
	@Import(GoogleStorageProtocolResolver.class)
	static class StorageApplication {

		@Bean
		public static Storage storage() throws Exception {
			InputStream keyStream = BatchConfiguration.class.getClassLoader().getResourceAsStream("key.json");

			// Define the Google cloud storage
			Storage storage = StorageOptions.newBuilder()
				    .setCredentials(ServiceAccountCredentials.fromStream(keyStream))
				    .build()
				    .getService();

			return storage;
		}

	}
}




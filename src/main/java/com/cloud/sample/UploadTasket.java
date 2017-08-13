package com.cloud.sample;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class UploadTasket implements Tasklet{

    private static final Logger log = LoggerFactory.getLogger(UploadTasket.class);
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("Upload file");
		/*
		try{
    		File file = new File("C:\\readfile\\1.txt");
    		if(file.delete()){
    			System.out.println("### TaskletStep:" + file.getName() + " is deleted!");
    		}else{
    			System.out.println("Delete operation is failed.");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	*/
		return RepeatStatus.FINISHED;
	}
}

package com.cloud.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class TextItemProcessor implements ItemProcessor<String, String> {

    private static final Logger log = LoggerFactory.getLogger(TextItemProcessor.class);

    @Override
    public String process(final String txt) throws Exception {


        log.info("Converting (" + txt + ") into (>" + txt + ")");

        return ">"+txt;
    }

}

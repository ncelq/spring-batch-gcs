
package com.cloud.sample;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.WriteFailedException;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

public class GSFileWriter<T> extends FlatFileItemWriter<T> {
	private Storage storage;
	private Resource resource;
	private static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");
	private OutputStream os;

	private String lineSeparator = DEFAULT_LINE_SEPARATOR;
	@Override
	public void write(List<? extends T> items) throws Exception {

		StringBuilder lines = new StringBuilder();
		for (T item : items) {
			lines.append(item).append(lineSeparator);
		}
		byte[] bytes = lines.toString().getBytes();
		try {
			os.write(bytes);
		}
		catch (IOException e) {
			throw new WriteFailedException("Could not write data.  The file may be corrupt.", e);
		}

		os.flush();
	}
	
	@Override
	public void open(ExecutionContext executionContext) {
		try {
			os = ((WritableResource)resource).getOutputStream();
			String bucket = resource.getURI().getHost();
	    	String fileName = resource.getURI().getPath().substring(1);

	    	BlobInfo info = BlobInfo.newBuilder(bucket, fileName).build();
	    	storage.create(info);
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(ExecutionContext executionContext) {	
	}
	
	@Override
	public void close() {
		super.close();

		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	@Override
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	/*
	Writer writer = new BufferedWriter(resource.getInputStream());

	public GSFileWriter() {
		this.setExecutionContextName(ClassUtils.getShortName(GSFileWriter.class));
	}
	
	@Override
	public void write(List<? extends T> items) throws Exception {

		if (!getOutputState().isInitialized()) {
			throw new WriterNotOpenException("Writer must be open before it can be written to");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Writing to flat file with " + items.size() + " items.");
		}

		OutputState state = getOutputState();

		StringBuilder lines = new StringBuilder();
		int lineCount = 0;
		for (T item : items) {
			lines.append(lineAggregator.aggregate(item) + lineSeparator);
			lineCount++;
		}
		try {
			state.write(lines.toString());
		}
		catch (IOException e) {
			throw new WriteFailedException("Could not write data.  The file may be corrupt.", e);
		}
		state.linesWritten += lineCount;
	}
*/
}

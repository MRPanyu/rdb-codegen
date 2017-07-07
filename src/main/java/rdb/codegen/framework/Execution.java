package rdb.codegen.framework;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rdb.codegen.framework.api.ModelProcessor;
import rdb.codegen.framework.api.ModelReader;
import rdb.codegen.framework.model.Database;

/**
 * This class combines a <code>ModelReader</code>, <code>Processor</code>s and
 * <code>Generator</code>s into a full code generation process.
 * <p>
 * You can build an <code>Execution</code> by code or by
 * <code>Configuration</code> class. Use the <code>execute</code> method to
 * start code generation.
 * </p>
 * 
 * @author Panyu
 *
 */
public class Execution {

	protected Log logger = LogFactory.getLog(this.getClass());

	private ModelReader reader;
	private List<ModelProcessor> processors = new ArrayList<ModelProcessor>();

	public Execution() {
	}

	public Execution(ModelReader reader, List<ModelProcessor> processors) {
		this.reader = reader;
		this.processors.addAll(processors);
	}

	/** Main entry point for code generation */
	public void execute() throws Exception {
		logger.info("Execution start.");
		try {
			logger.info("Start reader, class: " + reader.getClass().getName());
			Database model = reader.read();
			logger.info("Finished reader");
			int processorIndex = 1;
			for (ModelProcessor processor : processors) {
				logger.info("Start processor #" + processorIndex + ", class: " + processor.getClass().getName());
				model = processor.process(model);
				logger.info("Finished processor #" + processorIndex);
				processorIndex++;
			}
		} catch (Exception e) {
			logger.error("Execution error.", e);
			throw e;
		}
	}

	public ModelReader getReader() {
		return reader;
	}

	public void setReader(ModelReader reader) {
		this.reader = reader;
	}

	public List<ModelProcessor> getProcessors() {
		return processors;
	}

}

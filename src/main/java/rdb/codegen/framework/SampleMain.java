package rdb.codegen.framework;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class SampleMain {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.configure("classpath:config-sample.xml");

		File outputPath = new File(conf.getProperty("outputPath"));
		if (outputPath.isDirectory()) {
			FileUtils.cleanDirectory(outputPath);
		}

		Execution exec = conf.buildExecution();
		exec.execute();
	}

}

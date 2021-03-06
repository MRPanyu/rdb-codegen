package rdb.codegen.framework;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class SampleMain {

	public static void main(String[] args) throws Exception {
		// YmlConfiguration conf = new YmlConfiguration();
		// conf.configure("classpath:configs/config-sample.yml");
		// XmlConfiguration conf = new XmlConfiguration();
		// conf.configure("classpath:configs/config-sample.xml");

		YmlConfiguration conf = new YmlConfiguration();
		conf.configure("classpath:configs/config-one-page-html-doc.yml");
		
		File outputPath = new File(conf.getProperty("outputPath"));
		if (outputPath.isDirectory()) {
			FileUtils.cleanDirectory(outputPath);
		}

		Execution exec = conf.buildExecution();
		exec.execute();
	}

}

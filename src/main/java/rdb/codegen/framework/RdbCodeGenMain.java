package rdb.codegen.framework;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class RdbCodeGenMain {

	public static void main(String[] args) throws Exception {
		
		YmlConfiguration conf = new YmlConfiguration();
		conf.configure("classpath:configs/config-tk-mybatis-entity.yml");
		
		File outputPath = new File(conf.getProperty("outputPath"));
		if (outputPath.isDirectory()) {
			FileUtils.cleanDirectory(outputPath);
		}

		Execution exec = conf.buildExecution();
		exec.execute();
	}

}

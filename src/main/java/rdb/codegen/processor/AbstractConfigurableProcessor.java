package rdb.codegen.processor;

import java.util.Properties;

import rdb.codegen.framework.api.Configurable;
import rdb.codegen.framework.api.ModelProcessor;

public abstract class AbstractConfigurableProcessor implements ModelProcessor, Configurable {

	protected Properties configProperties;

	public Properties getConfigProperties() {
		return configProperties;
	}

	public void setConfigProperties(Properties configProperties) {
		this.configProperties = configProperties;
	}

}

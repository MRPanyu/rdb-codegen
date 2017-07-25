package rdb.codegen.framework;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

import rdb.codegen.framework.api.Configurable;
import rdb.codegen.framework.api.ModelProcessor;
import rdb.codegen.framework.api.ModelReader;
import rdb.codegen.framework.utils.Utils;

/**
 * This class builds an <code>Execution</code> from configuration yml file.
 * <p>
 * For sample of configuation file, see config-sample.yml in resources folder.
 * </p>
 * 
 * @author Panyu
 *
 */
public class YmlConfiguration {

	private Map<String, Object> config;
	private Properties properties;

	/**
	 * Read the configuration from file.
	 * 
	 * @param file
	 *            absolute or relative file path, or if prefixed by "classpath:"
	 *            then load the file from classpath.
	 */
	public void configure(String file) throws Exception {
		String content = Utils.loadResource(file, "UTF-8");
		configure(new StringReader(content));
	}

	/**
	 * Read the configuration from <code>InputStream</code>, note that the stream is
	 * not closed after read.
	 */
	public void configure(InputStream ins) throws Exception {
		configure(new InputStreamReader(ins, "UTF-8"));
	}

	/**
	 * Read the configuration from <code>Reader</code>, note that the reader is not
	 * closed after read.
	 */
	@SuppressWarnings("unchecked")
	public void configure(Reader reader) throws Exception {
		config = (Map<String, Object>) new Yaml().load(reader);
		properties = readProperties();
	}

	/**
	 * Builds a new <code>Execution</code> from current config.
	 */
	public Execution buildExecution() throws Exception {
		Properties props = readProperties();
		List<ModelReader> readers = readObjects("reader", ModelReader.class, props);
		List<ModelProcessor> processors = readObjects("processors", ModelProcessor.class, props);
		Execution execution = new Execution(readers.get(0), processors);
		return execution;
	}

	/** Gets a property after reading config file */
	public String getProperty(String key) throws Exception {
		return this.properties.getProperty(key);
	}

	@SuppressWarnings("unchecked")
	private Properties readProperties() throws Exception {
		Properties props = new Properties();
		Map<String, Object> p = (Map<String, Object>) config.get("properties");
		if (p != null) {
			props.putAll(p);
		}
		return props;
	}

	private String fillPropertyPlaceHolder(Properties props, String value) throws Exception {
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("[$][{]([^}]+)[}]");
		Matcher matcher = pattern.matcher(value);
		while (matcher.find()) {
			String propertyName = matcher.group(1);
			String propertyValue = props.getProperty(propertyName);
			if (propertyValue == null) {
				throw new RuntimeException("Cannot find property value for expression ${" + propertyName + "}");
			}
			matcher.appendReplacement(sb, propertyValue);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> readObjects(String propName, Class<T> baseType, Properties props) throws Exception {
		List<T> list = new ArrayList<T>();
		Object o = config.get(propName);
		List<Map<String, Object>> ls = new ArrayList<Map<String, Object>>();
		if (o instanceof List) {
			ls = (List<Map<String, Object>>) o;
		} else {
			ls.add((Map<String, Object>) o);
		}
		for (Map<String, Object> el : ls) {
			String className = (String) el.get("class");
			if (StringUtils.isBlank(className)) {
				throw new RuntimeException("No class attribute defined");
			}
			Class<?> cls = Class.forName(className);
			Object obj = cls.newInstance();
			for (Map.Entry<String, Object> entry : el.entrySet()) {
				String name = entry.getKey();
				String value = StringUtils.trimToEmpty((String) entry.getValue());
				value = fillPropertyPlaceHolder(props, value);
				BeanUtils.setProperty(obj, name, value);
			}
			if (obj instanceof Configurable) {
				((Configurable) obj).setConfigProperties(props);
			}
			list.add((T) obj);
		}
		return list;
	}
}

package rdb.codegen.framework;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import rdb.codegen.framework.api.Configurable;
import rdb.codegen.framework.api.ModelProcessor;
import rdb.codegen.framework.api.ModelReader;
import rdb.codegen.framework.utils.Utils;

/**
 * <code>Configuration</code> class builds an <code>Execution</code> from
 * configuration xml file.
 * <p>
 * For sample of configuation file, see config-sample.xml in resources folder.
 * </p>
 * 
 * @author Panyu
 *
 */
public class Configuration {

	private Document document;
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
	public void configure(Reader reader) throws Exception {
		SAXReader r = new SAXReader();
		document = r.read(reader);
		properties = readProperties();
	}

	/**
	 * Builds a new <code>Execution</code> from current config.
	 */
	public Execution buildExecution() throws Exception {
		Properties props = readProperties();
		List<ModelReader> readers = readObjects("reader", ModelReader.class, props);
		List<ModelProcessor> processors = readObjects("processor", ModelProcessor.class, props);
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
		Element elProperties = document.getRootElement().element("properties");
		if (elProperties != null) {
			List<Element> els = elProperties.elements();
			for (Element el : els) {
				String name = el.getName();
				String value = el.getTextTrim();
				props.setProperty(name, value);
			}
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
	private <T> List<T> readObjects(String elementName, Class<T> baseType, Properties props) throws Exception {
		List<T> list = new ArrayList<T>();
		List<Element> elements = document.getRootElement().elements(elementName);
		for (Element element : elements) {
			String className = element.attributeValue("class");
			if (StringUtils.isBlank(className)) {
				throw new RuntimeException("No class attribute defined for element <" + elementName + ">");
			}
			Class<?> cls = Class.forName(className);
			Object obj = cls.newInstance();
			List<Element> propertyElements = element.elements();
			for (Element propertyElement : propertyElements) {
				String name = propertyElement.getName();
				String value = propertyElement.getTextTrim();
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

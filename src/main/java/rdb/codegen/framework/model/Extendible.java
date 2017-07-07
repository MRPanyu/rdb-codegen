package rdb.codegen.framework.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base class for all models. Contains a <code>Map</code> property 'ext' for
 * storing arbitrary contents.
 * 
 * @author Panyu
 *
 */
@SuppressWarnings("serial")
public class Extendible implements Serializable {

	private Map<String, Object> ext = new LinkedHashMap<String, Object>();

	public Map<String, Object> getExt() {
		return this.ext;
	}

	public Object getExtProperty(String name) {
		return ext.get(name);
	}

	public void setExtProperty(String name, Object value) {
		ext.put(name, value);
	}

}

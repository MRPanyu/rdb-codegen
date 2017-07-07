package rdb.codegen.reader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import rdb.codegen.framework.api.Configurable;
import rdb.codegen.framework.api.ModelReader;

public abstract class AbstractJDBCModelReader implements ModelReader, Configurable {

	protected Properties configProperties;
	protected String driverClassName;
	protected String url;
	protected String user;
	protected String password;
	protected String schema;
	protected String tableNames;

	public Properties getConfigProperties() {
		return configProperties;
	}

	public void setConfigProperties(Properties configProperties) {
		this.configProperties = configProperties;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * Table names to generate code, split by ",". May use sql like expression
	 * (example: "T_USER_%,T_SYS_%")
	 */
	public String getTableNames() {
		return tableNames;
	}

	public void setTableNames(String tableNames) {
		this.tableNames = tableNames;
	}

	public String[] getTableNamesAsArray() {
		String[] array = null;
		if (StringUtils.isBlank(tableNames)) {
			array = new String[] { "%" };
		} else {
			array = tableNames.split(",");
			for (int i = 0; i < array.length; i++) {
				array[i] = StringUtils.trimToEmpty(array[i]);
			}
		}
		return array;
	}

	protected Connection getConnection() throws Exception {
		Class.forName(driverClassName);
		Connection conn = DriverManager.getConnection(url, user, password);
		return conn;
	}

}

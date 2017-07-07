package rdb.codegen.processor;

import org.apache.commons.lang.StringUtils;

import rdb.codegen.framework.model.Database;
import rdb.codegen.framework.model.Table;

/**
 * Set ext property in <code>Table</code>:
 * <ul>
 * <li>className</li>
 * <li>classNameCamel</li>
 * </ul>
 * <p>
 * Can optionally removes a table name prefix.
 * <p>
 * Example:
 * <ul>
 * <li>this.tableNamePrefix="T_"</li>
 * <li>table.tableName="T_USER_INFO"</li>
 * </ul>
 * <ul>
 * <li>table.ext.className="UserInfo"</li>
 * <li>table.ext.classNameCamel="userInfo"</li>
 * </ul>
 * 
 * @author Panyu
 *
 */
public class DefaultClassNameSetter extends AbstractConfigurableProcessor {

	private String tableNamePrefix = "";

	public String getTableNamePrefix() {
		return tableNamePrefix;
	}

	public void setTableNamePrefix(String tableNamePrefix) {
		this.tableNamePrefix = tableNamePrefix;
	}

	@Override
	public Database process(Database model) throws Exception {
		for (Table table : model.getTables()) {
			String tableName = table.getTableName();
			tableName = tableName.toLowerCase();
			if (StringUtils.isNotBlank(tableNamePrefix) && tableName.startsWith(tableNamePrefix.toLowerCase())) {
				tableName = tableName.substring(tableNamePrefix.length());
			}
			String[] arrTableName = tableName.split("_");
			StringBuilder sb = new StringBuilder();
			for (String part : arrTableName) {
				if (StringUtils.isNotBlank(part)) {
					sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
				}
			}
			table.setExtProperty("className", sb.toString());
			table.setExtProperty("classNameCamel", sb.substring(0, 1).toLowerCase() + sb.substring(1));
		}
		return model;
	}

}

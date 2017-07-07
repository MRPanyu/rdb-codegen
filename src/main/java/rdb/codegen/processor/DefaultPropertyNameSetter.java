package rdb.codegen.processor;

import org.apache.commons.lang.StringUtils;

import rdb.codegen.framework.model.Column;
import rdb.codegen.framework.model.Database;
import rdb.codegen.framework.model.Table;

/**
 * Set ext property in <code>Column</code>:
 * <ul>
 * <li>propertyName</li>
 * <li>propertyNamePascal</li>
 * </ul>
 * Example:
 * <ul>
 * <li>column.columnName="user_name"</li>
 * </ul>
 * <ul>
 * <li>column.ext.propertyName="userName"</li>
 * <li>column.ext.propertyNamePascal="UserName"</li>
 * </ul>
 * 
 * @author Panyu
 *
 */
public class DefaultPropertyNameSetter extends AbstractConfigurableProcessor {

	@Override
	public Database process(Database model) throws Exception {
		for (Table table : model.getTables()) {
			for (Column column : table.getColumns()) {
				String columnName = column.getColumnName();
				String[] arrColumnName = columnName.split("_");
				StringBuilder sb = new StringBuilder();
				for (String part : arrColumnName) {
					if (StringUtils.isNotBlank(part)) {
						sb.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
					}
				}
				String propertyNamePascal = sb.toString();
				String propertyName = sb.substring(0, 1).toLowerCase() + sb.substring(1);
				column.setExtProperty("propertyName", propertyName);
				column.setExtProperty("propertyNamePascal", propertyNamePascal);
			}
		}
		return model;
	}

}

package rdb.codegen.processor;

import rdb.codegen.framework.model.Column;
import rdb.codegen.framework.model.Database;
import rdb.codegen.framework.model.Table;

/**
 * Sets ext property "propertyType" by <code>dbType</code> or
 * <code>jdbcType</code>.
 * 
 * @author Panyu
 *
 */
public abstract class AbstractPropertyTypeSetter extends AbstractConfigurableProcessor {

	@Override
	public Database process(Database model) throws Exception {
		for (Table table : model.getTables()) {
			for (Column column : table.getColumns()) {
				String dbType = column.getDbType();
				int jdbcType = column.getJdbcType();
				String propertyType = getPropertyType(dbType, jdbcType);
				column.setExtProperty("propertyType", propertyType);
			}
		}
		return null;
	}

	protected abstract String getPropertyType(String dbType, int jdbcType);

}

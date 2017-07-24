package rdb.codegen.processor;

import java.sql.Types;

/**
 * Basic property type setter, sets ext property "propertyType" based on jdbc
 * type.
 * 
 * @see AbstractPropertyTypeSetter
 * @author Panyu
 *
 */
public class DefaultPropertyTypeSetter extends AbstractPropertyTypeSetter {

	@Override
	protected String getPropertyType(String dbType, int jdbcType) {
		String type = "java.lang.Object";
		if (Types.BIGINT == jdbcType || Types.DECIMAL == jdbcType || Types.FLOAT == jdbcType || Types.DOUBLE == jdbcType
				|| Types.NUMERIC == jdbcType) {
			type = "java.math.BigDecimal";
		} else if (Types.INTEGER == jdbcType) {
			type = "java.lang.Long";
		} else if (Types.CHAR == jdbcType || Types.NCHAR == jdbcType || Types.VARCHAR == jdbcType
				|| Types.NVARCHAR == jdbcType || Types.LONGVARCHAR == jdbcType || Types.LONGNVARCHAR == jdbcType
				|| Types.CLOB == jdbcType) {
			type = "java.lang.String";
		} else if (Types.DATE == jdbcType || Types.TIME == jdbcType || Types.TIMESTAMP == jdbcType) {
			type = "java.util.Date";
		}
		return type;
	}

}

package rdb.codegen.framework.model;

/**
 * Model for database column.
 * 
 * @author Panyu
 *
 */
@SuppressWarnings("serial")
public class Column extends Extendible {

	private String columnName;
	private String dbType;
	private int jdbcType;
	private String jdbcTypeName;
	private int length;
	private int precision;
	private int scale;
	private boolean nullable;
	private boolean primaryKey;
	private boolean foreignKey;
	private boolean unique;
	private boolean autoIncrement;
	private boolean indexed;
	private String defaultValue;
	private String comment;

	/** Column name as in database */
	public String getColumnName() {
		return columnName;
	}

	/** Column name as in database */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/** Column type as in database */
	public String getDbType() {
		return dbType;
	}

	/** Column type as in database */
	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	/** Column type as <code>java.sql.Types</code> */
	public int getJdbcType() {
		return jdbcType;
	}

	/** Column type as <code>java.sql.Types</code> */
	public void setJdbcType(int jdbcType) {
		this.jdbcType = jdbcType;
	}

	/** Column type static property name in <code>java.sql.Types</code> */
	public String getJdbcTypeName() {
		return jdbcTypeName;
	}

	/** Column type static property name in <code>java.sql.Types</code> */
	public void setJdbcTypeName(String jdbcTypeName) {
		this.jdbcTypeName = jdbcTypeName;
	}

	/** Length for text type */
	public int getLength() {
		return length;
	}

	/** Length for text type */
	public void setLength(int length) {
		this.length = length;
	}

	/** Numeric precision */
	public int getPrecision() {
		return precision;
	}

	/** Numeric precision */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/** Numeric scale */
	public int getScale() {
		return scale;
	}

	/** Numeric scale */
	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public boolean isForeignKey() {
		return foreignKey;
	}

	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}

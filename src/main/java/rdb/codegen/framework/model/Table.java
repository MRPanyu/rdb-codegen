package rdb.codegen.framework.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for database table.
 * 
 * @author Panyu
 * 
 */
@SuppressWarnings("serial")
public class Table extends Extendible {

	private String tableName;
	private String comment;
	private List<Column> columns = new ArrayList<Column>();
	private PrimaryKey primaryKey;
	private List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
	private List<Unique> uniques = new ArrayList<Unique>();

	/** Table name as in database */
	public String getTableName() {
		return tableName;
	}

	/** Table name as in database */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}

	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(List<ForeignKey> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public List<Unique> getUniques() {
		return uniques;
	}

	public void setUniques(List<Unique> uniques) {
		this.uniques = uniques;
	}

}

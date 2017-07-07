package rdb.codegen.framework.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base model for database constraints(primary key, unique, foreign key).
 * 
 * @author Panyu
 * 
 */
@SuppressWarnings("serial")
public class Constraint extends Extendible {

	private String name;
	private String indexName;
	private List<String> columnNames = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

}

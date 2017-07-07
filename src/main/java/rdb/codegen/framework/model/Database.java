package rdb.codegen.framework.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for database. It's just a group of tables with extendible property, not
 * necessarily contains all tables in the real database.
 * 
 * @author Panyu
 *
 */
@SuppressWarnings("serial")
public class Database extends Extendible {

	private List<Table> tables = new ArrayList<Table>();

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

}

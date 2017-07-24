package rdb.codegen.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import rdb.codegen.framework.model.Database;
import rdb.codegen.framework.model.ForeignKey;
import rdb.codegen.framework.model.Table;

/**
 * Sets ext property "referanceTable" in <code>ForeignKey</code>.
 * <p>
 * Set ext property "referers" in <code>Table</code>.
 * <p>
 * <b>This processor should be used after processors that sets
 * className/propertyName/propertyType.
 * 
 * @author Panyu
 */
public class ForeignKeyReferenceSetter extends AbstractConfigurableProcessor {

	@SuppressWarnings("unchecked")
	@Override
	public Database process(Database model) throws Exception {
		for (Table table : model.getTables()) {
			table.setExtProperty("referers", new ArrayList<Table>());
		}
		for (Table table : model.getTables()) {
			for (ForeignKey fk : table.getForeignKeys()) {
				String refTableName = fk.getReferenceTableName();
				Table refTable = findTable(model, refTableName);
				fk.setExtProperty("referenceTable", refTable);
				List<Table> referers = (List<Table>) refTable.getExtProperty("referers");
				referers.add(table);
			}
		}
		return null;
	}

	private Table findTable(Database model, String tableName) throws Exception {
		Table table = null;
		for (Table t : model.getTables()) {
			if (StringUtils.equals(tableName, t.getTableName())) {
				table = t;
				break;
			}
		}
		return table;
	}

}

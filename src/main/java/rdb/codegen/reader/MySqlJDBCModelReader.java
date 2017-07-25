package rdb.codegen.reader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

import rdb.codegen.framework.model.Column;
import rdb.codegen.framework.model.Database;
import rdb.codegen.framework.model.ForeignKey;
import rdb.codegen.framework.model.PrimaryKey;
import rdb.codegen.framework.model.Table;
import rdb.codegen.framework.model.Unique;
import rdb.codegen.framework.utils.Utils;

public class MySqlJDBCModelReader extends AbstractJDBCModelReader {

	@Override
	public Database read() throws Exception {
		Database model = new Database();
		Connection conn = getConnection();
		try {
			readTableNames(conn, model);
			for (Table table : model.getTables()) {
				readTableColumns(conn, table);
			}
			for (Table table : model.getTables()) {
				readTableConstraints(conn, table);
			}
		} finally {
			conn.close();
		}
		return model;
	}

	private void readTableNames(Connection conn, Database model) throws Exception {
		String[] array = getTableNamesAsArray();
		for (String tableNameLike : array) {
			PreparedStatement ps = conn.prepareStatement(
					"select table_name, table_comment from information_schema.tables where table_schema=? and upper(table_name) like ?;");
			ps.setString(1, getSchema());
			ps.setString(2, tableNameLike.toUpperCase());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Table table = new Table();
				table.setTableName(rs.getString(1));
				table.setComment(rs.getString(2));
				model.getTables().add(table);
			}
			rs.close();
			ps.close();
		}
	}

	private void readTableColumns(Connection conn, Table table) throws Exception {
		PreparedStatement ps = conn.prepareStatement(
				"select column_name, data_type, character_maximum_length, numeric_precision, numeric_scale, is_nullable, column_default, column_comment, ordinal_position from information_schema.COLUMNS where table_schema=? and table_name=? order by ordinal_position");
		ps.setString(1, getSchema());
		ps.setString(2, table.getTableName());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Column column = new Column();
			column.setColumnName(rs.getString(1));
			column.setDbType(rs.getString(2));
			column.setLength(rs.getInt(3));
			column.setPrecision(rs.getInt(4));
			column.setScale(rs.getInt(5));
			column.setNullable("YES".equalsIgnoreCase(rs.getString(6)));
			column.setDefaultValue(rs.getString(7));
			column.setComment(rs.getString(8));
			table.getColumns().add(column);
		}
		rs.close();
		ps.close();
		// get column meta data
		ps = conn.prepareStatement("select * from " + getSchema() + "." + table.getTableName() + " where 1=0");
		rs = ps.executeQuery();
		ResultSetMetaData meta = rs.getMetaData();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			int jdbcType = meta.getColumnType(i);
			String jdbcTypeName = Utils.getJdbcTypeName(jdbcType);
			for (Column column : table.getColumns()) {
				if (columnName.equalsIgnoreCase(column.getColumnName())) {
					column.setJdbcType(jdbcType);
					column.setJdbcTypeName(jdbcTypeName);
				}
			}
		}
	}

	private void readTableConstraints(Connection conn, Table table) throws Exception {
		PreparedStatement ps = conn.prepareStatement(
				"select constraint_name, constraint_type from information_schema.table_constraints where table_schema=? and table_name=?");
		ps.setString(1, getSchema());
		ps.setString(2, table.getTableName());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String type = rs.getString(2);
			if ("PRIMARY KEY".equalsIgnoreCase(type)) {
				PrimaryKey primaryKey = new PrimaryKey();
				primaryKey.setName(rs.getString(1));
				primaryKey.setIndexName(primaryKey.getName());
				table.setPrimaryKey(primaryKey);
			} else if ("UNIQUE".equalsIgnoreCase(type)) {
				Unique unique = new Unique();
				unique.setName(rs.getString(1));
				unique.setIndexName(unique.getName());
				table.getUniques().add(unique);
			} else if ("FOREIGN KEY".equalsIgnoreCase(type)) {
				ForeignKey foreignKey = new ForeignKey();
				foreignKey.setName(rs.getString(1));
				foreignKey.setIndexName(foreignKey.getName());
				table.getForeignKeys().add(foreignKey);
			}
		}
		rs.close();
		ps.close();
		// handle columns
		Map<String, Column> columnMap = new HashMap<String, Column>();
		for (Column column : table.getColumns()) {
			columnMap.put(column.getColumnName(), column);
		}
		String sql = "select column_name, referenced_table_name, referenced_column_name, ordinal_position from information_schema.key_column_usage where table_schema=? and table_name=? and constraint_name=? order by ordinal_position";
		PrimaryKey primaryKey = table.getPrimaryKey();
		if (primaryKey != null) {
			ps = conn.prepareStatement(sql);
			ps.setString(1, getSchema());
			ps.setString(2, table.getTableName());
			ps.setString(3, primaryKey.getName());
			rs = ps.executeQuery();
			while (rs.next()) {
				String columnName = rs.getString(1);
				primaryKey.getColumnNames().add(columnName);
				columnMap.get(columnName).setPrimaryKey(true);
			}
			rs.close();
		}
		for (Unique unique : table.getUniques()) {
			ps = conn.prepareStatement(sql);
			ps.setString(1, getSchema());
			ps.setString(2, table.getTableName());
			ps.setString(3, unique.getName());
			rs = ps.executeQuery();
			while (rs.next()) {
				String columnName = rs.getString(1);
				unique.getColumnNames().add(columnName);
				columnMap.get(columnName).setUnique(true);
			}
			rs.close();
		}
		for (ForeignKey foreignKey : table.getForeignKeys()) {
			ps = conn.prepareStatement(sql);
			ps.setString(1, getSchema());
			ps.setString(2, table.getTableName());
			ps.setString(3, foreignKey.getName());
			rs = ps.executeQuery();
			while (rs.next()) {
				String columnName = rs.getString(1);
				foreignKey.getColumnNames().add(columnName);
				columnMap.get(columnName).setForeignKey(true);
				foreignKey.setReferenceTableName(rs.getString(2));
				foreignKey.getReferenceColumnNames().add(rs.getString(3));
			}
			rs.close();
		}
		ps.close();
	}

}

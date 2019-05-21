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

public class OracleJDBCModelReader extends AbstractJDBCModelReader {

	@Override
	public Database read() throws Exception {
		Database model = new Database();
		Connection conn = getConnection();
		try {
			readTableNames(conn, model);
			for (Table table : model.getTables()) {
				readTableComment(conn, table);
			}
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
			PreparedStatement ps = conn
					.prepareStatement("select table_name from user_tables where upper(table_name) like ?");
			ps.setString(1, tableNameLike.toUpperCase());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String tableName = rs.getString(1);
				Table table = new Table();
				table.setTableName(tableName);
				model.getTables().add(table);
			}
			rs.close();
			ps.close();
		}
	}

	private void readTableComment(Connection conn, Table table) throws Exception {
		PreparedStatement ps = conn.prepareStatement("select comments from user_tab_comments where table_name=?");
		ps.setString(1, table.getTableName());
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			table.setComment(rs.getString(1));
		}
		rs.close();
		ps.close();
	}

	private void readTableColumns(Connection conn, Table table) throws Exception {
		PreparedStatement ps = conn.prepareStatement(
				"select column_name, data_type, data_length, data_precision, data_scale, nullable, data_default, column_id from user_tab_columns where table_name=? order by column_id asc");
		ps.setString(1, table.getTableName());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			Column column = new Column();
			column.setColumnName(rs.getString(1));
			column.setDbType(rs.getString(2));
			column.setLength(rs.getInt(3));
			column.setPrecision(rs.getInt(4));
			column.setScale(rs.getInt(5));
			column.setNullable("Y".equalsIgnoreCase(rs.getString(6)));
			column.setDefaultValue(rs.getString(7));
			table.getColumns().add(column);
		}
		rs.close();
		ps.close();
		// get column comments
		ps = conn.prepareStatement("select comments from user_col_comments where table_name=? and column_name=?");
		for (Column column : table.getColumns()) {
			ps.setString(1, table.getTableName());
			ps.setString(2, column.getColumnName());
			rs = ps.executeQuery();
			if (rs.next()) {
				column.setComment(rs.getString(1));
			}
			rs.close();
		}
		ps.close();
		// get column meta data
		ps = conn.prepareStatement("select * from " + table.getTableName() + " where 1=0");
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
		rs.close();
		ps.close();
	}

	private void readTableConstraints(Connection conn, Table table) throws Exception {
		Map<String, String> foreignKeyRefConstraints = new HashMap<String, String>();
		PreparedStatement ps = conn.prepareStatement(
				"select constraint_name, constraint_type, r_constraint_name, index_name from user_constraints where table_name=? and constraint_type in ('P', 'U', 'R')");
		ps.setString(1, table.getTableName());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String type = rs.getString(2);
			if ("P".equals(type)) {
				PrimaryKey primaryKey = new PrimaryKey();
				primaryKey.setName(rs.getString(1));
				primaryKey.setIndexName(rs.getString(4));
				table.setPrimaryKey(primaryKey);
			} else if ("U".equals(type)) {
				Unique unique = new Unique();
				unique.setName(rs.getString(1));
				unique.setIndexName(rs.getString(4));
				table.getUniques().add(unique);
			} else {
				ForeignKey foreignKey = new ForeignKey();
				foreignKey.setName(rs.getString(1));
				foreignKey.setIndexName(rs.getString(4));
				table.getForeignKeys().add(foreignKey);
				foreignKeyRefConstraints.put(foreignKey.getName(), rs.getString(3));
			}
		}
		rs.close();
		ps.close();
		// handle columns
		Map<String, Column> columnMap = new HashMap<String, Column>();
		for (Column column : table.getColumns()) {
			columnMap.put(column.getColumnName(), column);
		}
		ps = conn.prepareStatement(
				"select column_name, position from user_cons_columns where constraint_name=? order by position asc");
		PrimaryKey primaryKey = table.getPrimaryKey();
		if (primaryKey != null) {
			ps.setString(1, primaryKey.getName());
			rs = ps.executeQuery();
			while (rs.next()) {
				String columnName = rs.getString(1);
				primaryKey.getColumnNames().add(columnName);
				columnMap.get(columnName).setPrimaryKey(true);
			}
			rs.close();
		}
		for (Unique unique : table.getUniques()) {
			ps.setString(1, unique.getName());
			rs = ps.executeQuery();
			while (rs.next()) {
				String columnName = rs.getString(1);
				unique.getColumnNames().add(columnName);
				columnMap.get(columnName).setUnique(true);
			}
			rs.close();
		}
		for (ForeignKey foreignKey : table.getForeignKeys()) {
			ps.setString(1, foreignKey.getName());
			rs = ps.executeQuery();
			while (rs.next()) {
				String columnName = rs.getString(1);
				foreignKey.getColumnNames().add(columnName);
				columnMap.get(columnName).setForeignKey(true);
			}
			rs.close();
		}
		ps.close();
		// get foreign key reference table/columns
		ps = conn.prepareStatement("select table_name from user_constraints where constraint_name=?");
		for (ForeignKey foreignKey : table.getForeignKeys()) {
			String refConstraintName = foreignKeyRefConstraints.get(foreignKey.getName());
			ps.setString(1, refConstraintName);
			rs = ps.executeQuery();
			if (rs.next()) {
				foreignKey.setReferenceTableName(rs.getString(1));
			}
			rs.close();
		}
		ps.close();
		ps = conn.prepareStatement(
				"select column_name, position from user_cons_columns where constraint_name=? order by position asc");
		for (ForeignKey foreignKey : table.getForeignKeys()) {
			String refConstraintName = foreignKeyRefConstraints.get(foreignKey.getName());
			ps.setString(1, refConstraintName);
			rs = ps.executeQuery();
			while (rs.next()) {
				foreignKey.getReferenceColumnNames().add(rs.getString(1));
			}
			rs.close();
		}
		ps.close();
	}

}

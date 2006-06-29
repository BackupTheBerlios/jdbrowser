package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class DatabaseModel {
	String databasename;

	ArrayList tables;

	Collection schemas;

	public String getDatabasename() {
		return databasename;
	}

	public void setDatabasename(String databasename) {
		this.databasename = databasename;
	}

	public Collection getSchemas() {
		return schemas;
	}

	public void setSchemas(Collection schemas) {
		this.schemas = schemas;
	}

	public Collection getTables() {
		return tables;
	}

	public void addTable(Table tablename) {
		if (tables == null)
			tables = new ArrayList();
		tables.add(tablename);
	}

	public void addTable(String tablename) {
		Table table = new Table();
		table.setName(tablename);
		if (tables == null)
			tables = new ArrayList();
		tables.add(table);
	}

	public void addTable(String tablename, String schema, Collection columns) {
		Table table = new Table();
		table.setName(tablename);
		table.setSchema(schema);
		table.setColumns(columns);
		if (tables == null)
			tables = new ArrayList();
		tables.add(table);
	}

	public void setTables(Collection newTables) {
		if (tables == null)
			tables = new ArrayList();
		tables.addAll(newTables);
	}

	public void dumpTables() {
		if (tables != null) {
			for (int i = 0; i < tables.size(); i++) {
				Table tmp = (Table) tables.get(i);
				System.out.println("Database model has table: " + tmp.getName());
			}
		}
	}

	/**
	 * Get tables from a schema
	 * 
	 * @param name
	 * @return
	 */
	public Collection getTables(String name) {
		ArrayList result = new ArrayList();
		Iterator ite = tables.iterator();
		while (ite.hasNext()) {
			Table table = (Table) ite.next();
			if (table.getSchema().equals(name))
				result.add(table);
		}
		return result;
	}

	/**
	 * Get a table
	 * 
	 * @param name
	 * @return
	 */
	public Table getTable(String tablename) {
		ArrayList result = new ArrayList();
		Iterator ite = tables.iterator();
		while (ite.hasNext()) {
			Table table = (Table) ite.next();
			if (table.getName().equals(tablename))
				return table;
		}
		return null;
	}

}

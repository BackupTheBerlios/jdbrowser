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
	public void addTable(String tablename) {
		Table table = new Table();
		table.setName(tablename);
		if(tables == null)
			tables = new ArrayList();
		tables.add(table);
	}
	public void addTable(String tablename, String schema) {
		Table table = new Table();
		table.setName(tablename);
		table.setSchema(schema);
		if(tables == null)
			tables = new ArrayList();
		tables.add(table);
	}
	public void setTables(Collection newTables) {
		if(tables == null)
			tables = new ArrayList();
		tables.addAll(newTables);
	}
	public void dumpTables() {
		if(tables != null) {
		for(int i=0; i < tables.size(); i++) {
			Table tmp = (Table) tables.get(i);
			System.out.println("Database model has table: "+tmp.getName());
		}
	}
	}
	public Collection getTables(String name) {
		ArrayList result = new ArrayList();
		Iterator ite = tables.iterator();
		while(ite.hasNext()) {
			Table table = (Table)ite.next();
			if(table.getSchema().equals(name))
				result.add(table);
		}
		return result;
	}
}

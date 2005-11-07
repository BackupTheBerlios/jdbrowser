package model;

import java.util.Collection;

public class Table {
	String name;

	Collection Columns;

	public Collection getColumns() {
		return Columns;
	}

	public void addColumn(String columnName) {
		Columns.add(columnName);
	}
	
	public void setColumns(Collection columns) {
		Columns = columns;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

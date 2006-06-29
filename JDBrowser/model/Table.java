package model;

import java.util.ArrayList;
import java.util.Collection;

public class Table implements TableAndColumnInterface {
	private String name;

	private String schema;

	private Collection Columns;

	public Collection getColumns() {
		return Columns;
	}

	public void addColumn(String columnName) {
		Column tcif = new Column();
		tcif.setName(columnName);
		if (Columns == null)
			Columns = new ArrayList();
		Columns.add(tcif);
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

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

}

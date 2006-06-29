package model;

public class Column implements TableAndColumnInterface {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

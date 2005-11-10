package helpers;

import model.DatabaseModel;

public class DataModelHelper {
	private static DatabaseModel model;

	public static DatabaseModel getModel() {
		return model;
	}

	public static void setModel(DatabaseModel newmodel) {
		model = newmodel;
	}
}

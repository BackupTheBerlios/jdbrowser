package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import config.DataSourceConfig;

public class ConfigurationHandler {
	public static ArrayList getConfiguredDatabases() {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("database", DataSourceConfig.class);
		try {
			return (ArrayList) xstream.fromXML(new FileReader("config.xml"));
			// DataSourceConfig dsc = (DataSourceConfig )databases.get(0);
			// System.out.println("Config says: "+dsc.getDriver());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DataSourceConfig getConfiguredDatabases(String alias) {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("database", DataSourceConfig.class);
		try {
			ArrayList databases = (ArrayList) xstream.fromXML(new FileReader("config.xml"));
			for (int i = 0; i < databases.size(); i++) {
				DataSourceConfig dsc = (DataSourceConfig) databases.get(i);
				if (dsc.getAlias().equals(alias))
					return dsc;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}

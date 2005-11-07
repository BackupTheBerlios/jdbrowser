package helpers;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import config.DataSourceConfig;

public class ConfigReaderWriter {
	public static ArrayList getConfiguredDatabases() {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("database", DataSourceConfig.class);
		try {
			return (ArrayList) xstream.fromXML(new FileReader("config.xml"));
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
				if(dsc.getAlias().equals(alias))
					return dsc;
				}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void addNewConfigToFile(DataSourceConfig newdsc) {
		ArrayList databases = getConfiguredDatabases();
		databases.add(newdsc);
		WriteConfig(databases);
	}
	
	private static void WriteConfig(ArrayList databases) {
		XStream xstream = new XStream();
		xstream.alias("database", DataSourceConfig.class);

		String xml = xstream.toXML(databases);
		System.out.println(xml);
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("config.xml"));
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(xml);
			bw.flush();
			bw.close();
			osw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private void WriteConfig() {
		List databases = new ArrayList();

		DataSourceConfig dsc = new DataSourceConfig("oracle", "oracle.jdbc.OracleDriver", "secret", "url", "username");
		DataSourceConfig dsc1 = new DataSourceConfig("mysql", "oracle.jdbc.OracleDriver", "secret", "url", "username");
		databases.add(dsc);
		databases.add(dsc1);

		XStream xstream = new XStream();
		xstream.alias("database", DataSourceConfig.class);

		String xml = xstream.toXML(databases);
		System.out.println(xml);
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("config.xml"));
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(xml);
			bw.flush();
			bw.close();
			osw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}

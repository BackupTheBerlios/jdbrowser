package config;


import org.gnu.glade.LibGlade;
import org.gnu.gtk.Entry;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.Window;


import main.DatabaseList;
import main.JDBMain;

public class DataSourceConfigUI {
	Entry driver;
	Entry url;
	Entry userid;
	Entry password;
	public DataSourceConfigUI()  {
		Window mainwindow = (Window)JDBMain.getGladeApp().getWidget("addDatasource");
		mainwindow.show();
		driver = (Entry)JDBMain.getGladeApp().getWidget("entryDriver");
		url = (Entry)JDBMain.getGladeApp().getWidget("entryURL");
		userid = (Entry)JDBMain.getGladeApp().getWidget("entryUser");
		password = (Entry)JDBMain.getGladeApp().getWidget("entryPassword");
	}
	public void setValues(DataSourceConfig dsc) {
		driver.setText(dsc.getDriver());
		url.setText(dsc.getUrl());
		userid.setText(dsc.getUserid());
		password.setText(dsc.getPassword());
	}
}

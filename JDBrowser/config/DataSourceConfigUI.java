package config;


import helpers.ConfigReaderWriter;
import main.JDBMain;

import org.gnu.gtk.Button;
import org.gnu.gtk.Entry;
import org.gnu.gtk.Window;
import org.gnu.gtk.event.ButtonEvent;
import org.gnu.gtk.event.ButtonListener;

public class DataSourceConfigUI implements ButtonListener {
	Entry driver;
	Entry url;
	Entry userid;
	Entry password;
	Entry alias;
	
	Button apply;
	Button cancel;
	Window mainwindow;
	public DataSourceConfigUI()  {
		mainwindow = (Window)JDBMain.getGladeApp().getWidget("addDatasource");
		mainwindow.show();
		driver = (Entry)JDBMain.getGladeApp().getWidget("entryDriver");
		url = (Entry)JDBMain.getGladeApp().getWidget("entryURL");
		userid = (Entry)JDBMain.getGladeApp().getWidget("entryUser");
		password = (Entry)JDBMain.getGladeApp().getWidget("entryPassword");
		alias = (Entry)JDBMain.getGladeApp().getWidget("entryAlias");
		
		apply = (Button) JDBMain.getGladeApp().getWidget("button_apply");
		apply.addListener(this);
		cancel = (Button) JDBMain.getGladeApp().getWidget("button_cancel");
		cancel.addListener(this);
	}
	public void setValues(DataSourceConfig dsc) {
		driver.setText(dsc.getDriver());
		url.setText(dsc.getUrl());
		userid.setText(dsc.getUserid());
		password.setText(dsc.getPassword());
	}
	public void buttonEvent(ButtonEvent event) {
		if (event.isOfType(ButtonEvent.Type.CLICK)) {
			if(event.getSource().equals(apply)) {
				System.out.println("save config");
				DataSourceConfig dsc = new DataSourceConfig();
				dsc.setAlias(alias.getText());
				dsc.setDriver(driver.getText());
				dsc.setPassword(password.getText());
				dsc.setUrl(url.getText());
				dsc.setUserid(userid.getText());
				ConfigReaderWriter.addNewConfigToFile(dsc);
				System.out.println("Config saved..");
				mainwindow.destroy();
			}
			if(event.getSource().equals(cancel)) {
				System.out.println("cancels config");
				mainwindow.destroy();
			}
		}
		
	}
}

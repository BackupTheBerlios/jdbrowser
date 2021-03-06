package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.gnu.gdk.Pixbuf;
import org.gnu.glade.GladeXMLException;
import org.gnu.glade.LibGlade;
import org.gnu.gnome.App;
import org.gnu.gnome.AppBar;
import org.gnu.gnome.Program;
import org.gnu.gtk.ButtonsType;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.DataColumnPixbuf;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.DialogFlags;
import org.gnu.gtk.Entry;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.MenuItem;
import org.gnu.gtk.MessageDialog;
import org.gnu.gtk.MessageType;
import org.gnu.gtk.ResponseType;
import org.gnu.gtk.ToolButton;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Viewport;
import org.gnu.gtk.Widget;
import org.gnu.gtk.Window;
import org.gnu.gtk.event.LifeCycleEvent;
import org.gnu.gtk.event.LifeCycleListener;
import org.gnu.gtk.event.MenuItemEvent;
import org.gnu.gtk.event.MenuItemListener;
import org.gnu.gtk.event.ToolButtonEvent;
import org.gnu.gtk.event.ToolButtonListener;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import config.DataSourceConfig;
import config.DataSourceConfigUI;

public class JDBMain implements ToolButtonListener, MenuItemListener {
	Logger log = Logger.getLogger(JDBMain.class);
	public static String DB_DRIVER = null;

	public static LibGlade gladeApp;

	Viewport viewport;

	VBox contentpane = null;

	// Tree view
	TreeView resulttable = null;

	ListStore ls = null;

	DataColumnPixbuf ColThumbImage;

	DataColumnString ColData;

	ComboBox searchtypecombo;

	public static String DBURL = "";

	public static String DBUSER = "";

	public static String DBPASSWORD = "";

	static AppBar statusbar;
	private static Window window;
	SqlView sqlview;
	
	public JDBMain() throws FileNotFoundException, GladeXMLException, IOException {
		BasicConfigurator.configure();
		readConfig();

		gladeApp = new LibGlade("glade/jdbmain.glade", this);
		window = (Window) gladeApp.getWidget("mainwindow");
		addWindowCloser();

//		TextView sqlviewer = (TextView) gladeApp.getWidget("sqltextview");
		sqlview = new SqlView();
		DatabaseList dblist = new DatabaseList((TreeView) gladeApp.getWidget("databaselist"));

		TreeView sqltreeview = (TreeView) gladeApp.getWidget("sqltreeview");
		statusbar = (AppBar) gladeApp.getWidget("appbar1");
		statusbar.setStatusText("Appliction loaded");

		MenuItem aboutmenu = (MenuItem) gladeApp.getWidget("about1");
		aboutmenu.addListener(this);

		ToolButton newbutton = (ToolButton) gladeApp.getWidget("new_button");
		newbutton.addListener(this);
		
		dblist.createDatbaseListInView();
		dblist.initTable(sqltreeview);
		
	}

	private void readConfig() {
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("database", DataSourceConfig.class);
		try {
			List databases = (ArrayList) xstream.fromXML(new FileReader("config.xml"));
			DataSourceConfig dsc = (DataSourceConfig) databases.get(0);
			System.out.println("Config says: " + dsc.getDriver());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		// new ReadConfig();
		try {
			Program.initGnomeUI("First", "0.1", args);
			Gtk.init(args);
			new JDBMain();
			Gtk.main();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addWindowCloser() {
		
		window.addListener(new LifeCycleListener() {
			public void lifeCycleEvent(LifeCycleEvent event) {
			}

			public boolean lifeCycleQuery(LifeCycleEvent event) {
				Gtk.mainQuit();
				return false;
			}
		});
		window.setDefaultSize(600, 600);
		window.setMinimumSize(800, 600);
	}

//	public void initTable() {
//		ColThumbImage = new DataColumnPixbuf();
//		ColData = new DataColumnString();
//		ls = new ListStore(new DataColumn[] { ColThumbImage, ColData });
//
//		resulttable.setEnableSearch(true); /*
//											 * allows to use keyboard to search
//											 * items matching the pressed keys
//											 */
//
//		resulttable.setAlternateRowColor(true); /* no comments smile */
//		resulttable.setModel(ls);
//
//		TreeViewColumn col0 = new TreeViewColumn();
//		CellRendererPixbuf render1 = new CellRendererPixbuf();
//		col0.packStart(render1, true);
//		col0.addAttributeMapping(render1, CellRendererPixbuf.Attribute.PIXBUF, ColThumbImage);
//
//		TreeViewColumn col2 = new TreeViewColumn();
//		CellRendererText render2 = new CellRendererText();
//		col2.packStart(render2, true);
//		col2.addAttributeMapping(render2, CellRendererText.Attribute.MARKUP, ColData);
//
//		resulttable.setSearchDataColumn(ColData);
//		/* append columns */
//		resulttable.appendColumn(col0);
//		resulttable.appendColumn(col2);
//	}

	public void addToTable(byte[] image, String data) {
		TreeIter row = ls.appendRow();
		if (!(image == null))
			ls.setValue(row, ColThumbImage, new Pixbuf(image));

		ls.setValue(row, ColData, data);

		resulttable.showAll();

	}

	public static int getMaxRows() {
		Entry max = (Entry) JDBMain.gladeApp.getWidget("max_number_of_rows");
		String text = max.getText();
		return Integer.parseInt(text);
	}

	public static LibGlade getGladeApp() {
		return gladeApp;
	}

	public boolean toolButtonEvent(ToolButtonEvent event) {
		Widget source = (Widget) event.getSource();
		if (source.getName().equals("new_button")) {
			System.out.println("new button");
			new DataSourceConfigUI();
		}
		return true;
	}

	public static void setStatusbarMessage(String msg) {
		statusbar.setStatusText(msg);
	}

	public static void showErrorDialog(String text) {
		final MessageDialog dialog = new MessageDialog(window, DialogFlags.MODAL,MessageType.ERROR, ButtonsType.OK, text, true);
		dialog.setTitle("Error");
		int status = dialog.run();
		if(status == ResponseType.OK.getValue()) 
			dialog.destroy();
	}

	public static void getInforDialog(String text) {
		final MessageDialog dialog = new MessageDialog(window, DialogFlags.MODAL,MessageType.ERROR, ButtonsType.OK, text, true);
		dialog.setTitle("Error");
		int status = dialog.run();
		if(status == ResponseType.OK.getValue()) 
			dialog.destroy();

	}

	public void menuItemEvent(MenuItemEvent event) {
		System.out.println(event.getType());
		if ("about1".equals(((Widget) event.getSource()).getName())) {
			main.AboutDialog dialog = new main.AboutDialog();
			int status = dialog.run();
			if(status == ResponseType.CANCEL.getValue()  ) 
				dialog.destroy();
		}

	}
}
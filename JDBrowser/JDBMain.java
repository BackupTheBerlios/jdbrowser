import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.gnu.gdk.Pixbuf;
import org.gnu.glade.GladeXMLException;
import org.gnu.glade.LibGlade;
import org.gnu.gnome.AppBar;
import org.gnu.gnome.Program;
import org.gnu.gtk.CellRendererPixbuf;
import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnPixbuf;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.Entry;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Viewport;
import org.gnu.gtk.Window;
import org.gnu.gtk.event.LifeCycleEvent;
import org.gnu.gtk.event.LifeCycleListener;

import com.thoughtworks.xstream.XStream;

import config.DataSourceConfig;

public class JDBMain {
	public static String DB_DRIVER = null;

	public static LibGlade firstApp;

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
	public static String DBPASSWORD =""; 
/*
 * 
 *  for configuration parsing see
 *  http://jakarta.apache.org/commons/betwixt/guide/examples#simple-example
 */
	public JDBMain() throws FileNotFoundException, GladeXMLException, IOException {
		ReadConfig();
		ReadProperties();
		firstApp = new LibGlade("glade/jdbmain.glade", this);
		addWindowCloser();
		DatabaseList dblist = new DatabaseList((TreeView) firstApp.getWidget("databaselist"));
		TreeView sqltreeview = (TreeView) firstApp.getWidget("sqltreeview");
		AppBar statusbar = (AppBar) firstApp.getWidget("statusbar");
		statusbar.setStatusText("Appliction loaded");
		//TextView sqlviewer = (TextView) firstApp.getWidget("sqltextview");
		//SqlView sqlview = new SqlView(sqlviewer,firstApp, dblist);
		
		dblist.initTable(sqltreeview);
		dblist.addToTable(null,null);
	}

	private void ReadConfig() {
		List databases = new ArrayList();
		 
		DataSourceConfig dsc = new DataSourceConfig("oracle", "oracle.jdbc.OracleDriver","secret", "url", "username");
		DataSourceConfig dsc1 = new DataSourceConfig("mysql", "oracle.jdbc.OracleDriver","secret", "url", "username");
		databases.add(dsc);
		databases.add(dsc1);
		
		XStream xstream = new XStream();
		xstream.alias("database", DataSourceConfig.class);
		
		String xml = xstream.toXML(databases);
		System.out.println(xml);
	
}

	private void ReadProperties() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("jdbrowser.properties"));
			DBURL = properties.getProperty("dburl");
			DBUSER = properties.getProperty("dbuser");
			DBPASSWORD = properties.getProperty("dbpassword");
			DB_DRIVER = properties.getProperty("dbdriver");
			
		} catch (IOException e) {
			System.out.println("Can't find or load properties file jdbrowser.properties");
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
		Window window = (Window) firstApp.getWidget("mainwindow");
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

	public void initTable() {
		ColThumbImage = new DataColumnPixbuf();
		ColData = new DataColumnString();
		ls = new ListStore(new DataColumn[] { ColThumbImage, ColData });

		resulttable.setEnableSearch(true); /*
											 * allows to use keyboard to search
											 * items matching the pressed keys
											 */

		resulttable.setAlternateRowColor(true); /* no comments smile */
		resulttable.setModel(ls);

		TreeViewColumn col0 = new TreeViewColumn();
		CellRendererPixbuf render1 = new CellRendererPixbuf();
		col0.packStart(render1, true);
		col0.addAttributeMapping(render1, CellRendererPixbuf.Attribute.PIXBUF, ColThumbImage);

		TreeViewColumn col2 = new TreeViewColumn();
		CellRendererText render2 = new CellRendererText();
		col2.packStart(render2, true);
		col2.addAttributeMapping(render2, CellRendererText.Attribute.MARKUP, ColData);

		resulttable.setSearchDataColumn(ColData);
		/* append columns */
		resulttable.appendColumn(col0);
		resulttable.appendColumn(col2);
	}


	public void addToTable(byte[] image, String data)  {
		TreeIter row = ls.appendRow();
		if (!(image == null))
			ls.setValue(row, ColThumbImage, new Pixbuf(image));

		ls.setValue(row, ColData, data);

		resulttable.showAll();

	}
	public static  int getMaxRows() {
		Entry max = (Entry)JDBMain.firstApp.getWidget("max_number_of_rows");
		String text = max.getText();
		return Integer.parseInt(text);
	}
}

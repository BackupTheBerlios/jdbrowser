package main;

import helpers.ComboBoxHelper;

import org.gnu.gdk.Atom;
import org.gnu.gdk.ModifierType;
import org.gnu.glib.EventType;
import org.gnu.glib.Type;
import org.gnu.gtk.Button;
import org.gnu.gtk.Clipboard;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.TextBuffer;
import org.gnu.gtk.TextView;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.event.ButtonEvent;
import org.gnu.gtk.event.ButtonListener;
import org.gnu.gtk.event.KeyEvent;
import org.gnu.gtk.event.KeyListener;

import config.DataSourceConfig;

public class SqlView implements ButtonListener, KeyListener {
	TextView view;

	TreeView sqltreeview;

	DatabaseList dblist;

	private boolean controlDown;

	static ComboBoxHelper cb_database;

	static ComboBox cb_table;

	// Clipboard cb;
	public SqlView() {
		Button execute = (Button) JDBMain.getGladeApp().getWidget("execute_sql_button");
		view = (TextView) JDBMain.getGladeApp().getWidget("sqltextview");
		view.setCursorVisible(true);
		view.setEditable(true);
		view.addListener((KeyListener) this);
		execute.addListener((ButtonListener) this);
		// cb = Clipboard.get(new Atom(view.getHandle()));

	}

	public void buttonEvent(ButtonEvent event) {
		if (event.isOfType(ButtonEvent.Type.CLICK)) {
			System.out.println("click");
			TextBuffer textbuffer = view.getBuffer();
			String test = textbuffer.getText(textbuffer.getStartIter(), textbuffer.getEndIter(), true);
			System.out.println(test);
			System.out.println(getCombobox_database().getActiveText());
			DataSourceConfig dsc = (DataSourceConfig) getCombobox_database().getData(getCombobox_database().getActiveText());
			if (!(SqlView.cb_database.getActiveText() == null || SqlView.cb_database.getActiveText().equals("")))
				DatabaseList.getSqltreeview().addDataToTable(test, SqlView.cb_database.getActiveText(), dsc);
			else
				JDBMain.showErrorDialog("Please select a database from the dropdown menu");

			// System.out.println("---- > "+cb.getText());
		}
	}

	public static ComboBoxHelper getCombobox_database() {
		if (cb_database == null) {
			cb_database = new ComboBoxHelper(JDBMain.getGladeApp().getWidget("databasecombobox").getHandle());
			cb_database.initColumn();
		}
		return cb_database;
	}

	public static ComboBox getCombobox_table() {
		if (cb_table == null)
			cb_table = (ComboBox) JDBMain.getGladeApp().getWidget("comboboxtable");
		return cb_table;
	}

	public boolean keyEvent(KeyEvent event) {
		if (event.getType().getName().equals("KEY_PRESSED")) {
			if (event.getModifierKey().getValue() == 20) {
				controlDown = true;
			}
			if(controlDown && event.getKeyval()== 32) {
				System.out.println("Show table compleation list..");
			}
		}
		if (event.getType().getName().equals("KEY_RELEASED")) {
			if (event.getModifierKey().getValue() == 20) {
				controlDown = false;
			}
		}
		return false;
	}

}

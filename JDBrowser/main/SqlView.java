package main;

import helpers.ComboBoxHelper;
import helpers.CompletionHelper;
import helpers.DataModelHelper;

import java.util.StringTokenizer;

import model.DatabaseModel;

import org.apache.log4j.Logger;
import org.gnu.gdk.ModifierType;
import org.gnu.gtk.Button;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.TextBuffer;
import org.gnu.gtk.TextView;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.event.ButtonEvent;
import org.gnu.gtk.event.ButtonListener;
import org.gnu.gtk.event.KeyEvent;
import org.gnu.gtk.event.KeyListener;

import config.DataSourceConfig;

public class SqlView implements KeyListener {
	Logger log = Logger.getLogger(SqlView.class);

	TextView view;

	TreeView sqltreeview;

	static DatabaseList dblist;

	static ComboBoxHelper cb_database;

	static ComboBox cb_table;

	// Clipboard cb;
	public SqlView() {
		Button execute = (Button) JDBMain.getGladeApp().getWidget("execute_sql_button");
		view = (TextView) JDBMain.getGladeApp().getWidget("sqltextview");
		view.setCursorVisible(true);
		view.setEditable(true);
		view.addListener(this);
		ExecuteButtonListener bl = new ExecuteButtonListener();
		execute.addListener(bl);
		// cb = Clipboard.get(new Atom(view.getHandle()));

	}

	private class ExecuteButtonListener implements ButtonListener {
		public void buttonEvent(ButtonEvent event) {
			if (event.isOfType(ButtonEvent.Type.CLICK)) {
				TextBuffer textbuffer = view.getBuffer();
				String test = textbuffer.getText(textbuffer.getStartIter(), textbuffer.getEndIter(), true);
				log.debug("-------->" + getCombobox_database().getActiveText());
				DataSourceConfig dsc = (DataSourceConfig) getCombobox_database().getData(getCombobox_database().getActiveText());
				if (!(SqlView.cb_database.getActiveText() == null || SqlView.cb_database.getActiveText().equals("")))
					DatabaseList.getSqltreeview().addDataToTable(test, SqlView.cb_database.getActiveText(), dsc);
				else
					JDBMain.showErrorDialog("Please select a database from the dropdown menu");
			}
		}
	}

	/**
	 * Sets the value of the combobox to the parameter alias.
	 * 
	 * @param alias
	 */
	public static void setCombobox(String alias) {
		ListStore store = cb_database.getListStore();
		String databasename = "";

		int count = 0;
		cb_database.setActive(0);
		TreeIter iter = cb_database.getActiveIter();
		while (iter != null) {
			cb_database.setActive(count++);
			iter = cb_database.getActiveIter();
			databasename = store.getValue(iter, cb_database.getDataColumn());
			if (databasename.equals(alias)) {
				break;
			}
			iter = cb_database.getActiveIter().getNextIter();
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
		log.debug(event.getString());
		log.debug("-->"+event.getKeyval());
		log.debug("-->"+event.getModifierKey().getValue());
		if (event.getType().getName().equals("KEY_PRESSED")) {
			if (event.getKeyval() == 32 && (event.getModifierKey().getValue() == 20 || event.getModifierKey().getValue() == 4)) {
				System.out.println("Show table compleation list..");
				DatabaseModel model = DataModelHelper.getModel();

				TextBuffer textbuffer = view.getBuffer();
				String sql = textbuffer.getText(textbuffer.getStartIter(), textbuffer.getEndIter(), true);
				StringTokenizer st = new StringTokenizer(sql, " ");
				String command = "";
				while (st.hasMoreTokens()) {
					command = st.nextToken();
				}
				System.out.println("Command: " + command);
				if (command.equalsIgnoreCase("FROM")) {
					System.out.println("Running completion");
					CompletionHelper comp = new CompletionHelper(model.getTables(getCombobox_database().getActiveText()));
					comp.showAll();
					comp.run();
					// Table table = model.getTable(comp.getUserselection());
					view.getBuffer().insertText(" " + comp.getUserselection());
				} else if (command.equalsIgnoreCase("WHERE")) {
					System.out.println("Running completion");
					CompletionHelper comp = new CompletionHelper(model.getTable(getTableNameFromSql()).getColumns());
					comp.showAll();
					comp.run();
					view.getBuffer().insertText(" " + comp.getUserselection());

				}
			}
		}

		return false;
	}

	private String getTableNameFromSql() {
		TextBuffer buf = view.getBuffer();
		String sql = buf.getText(buf.getStartIter(), buf.getEndIter(), true);
		sql = sql.toUpperCase();
		StringTokenizer st = new StringTokenizer(sql, " ");
		while (st.hasMoreTokens()) {
			String tmp = st.nextToken();
			if ("FROM".equalsIgnoreCase(tmp)) {
				String table = st.nextToken();
				System.out.println("Found table :" + table);
				return table.trim();
			}
		}
		return null;
	}

}

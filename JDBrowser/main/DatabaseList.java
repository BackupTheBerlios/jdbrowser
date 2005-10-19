package main;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreePath;
import org.gnu.gtk.TreeStore;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
import org.gnu.gtk.event.TreeSelectionEvent;
import org.gnu.gtk.event.TreeSelectionListener;
import org.gnu.gtk.event.TreeViewEvent;
import org.gnu.gtk.event.TreeViewListener;

import config.DataSourceConfig;
import config.DataSourceConfigUI;

public class DatabaseList implements TreeSelectionListener, TreeViewListener {
	TreeView list;

	static TableColumnList sqltreeview;

	TreeStore ls;

	DataColumnString ColData;

	public DatabaseList(TreeView list) {
		this.list = list;
		this.list.getSelection().addListener((TreeSelectionListener) this);
		this.list.addListener((TreeViewListener) this);
	}

	public void initTable(TreeView sqltreeview) {
		this.sqltreeview = new TableColumnList(sqltreeview);
		ColData = new DataColumnString();
		ls = new TreeStore(new DataColumn[] { ColData });

		list.setEnableSearch(true); /*
									 * allows to use keyboard to search items
									 * matching the pressed keys
									 */

		list.setAlternateRowColor(true); /* no comments smile */
		list.setModel(ls);

		TreeViewColumn col2 = new TreeViewColumn();
		CellRendererText render2 = new CellRendererText();
		col2.packStart(render2, true);
		col2.addAttributeMapping(render2, CellRendererText.Attribute.MARKUP, ColData);

		list.setSearchDataColumn(ColData);
		/* append columns */
		list.appendColumn(col2);
	}

	public void addToTable(byte[] image, String data) {
		DatabaseMetaData dbmeta = null;
		ResultSet dbs= null;
		Connection conn = null;
		try {
			
			ArrayList databases = JDBMain.getConfiguredDatabases();
			for (int i = 0; i < databases.size(); i++) {
				DataSourceConfig dsc = (DataSourceConfig) databases.get(i);

				Class.forName(dsc.getDriver());
				// Connection mysql = DriverManager.getConnection(JDBMain.DBURL,
				// JDBMain.DBUSER, JDBMain.DBPASSWORD);
				try {
				System.out.println(dsc.getUrl() + "\t" + dsc.getUserid() + "\t" + dsc.getPassword());
				conn = DriverManager.getConnection(dsc.getUrl(), dsc.getUserid(), dsc.getPassword());
				dbmeta = conn.getMetaData();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (dbmeta.supportsSchemasInTableDefinitions()) { // mainly
																	// oracle
					System.out.println("Runnig on : " + dbmeta.getDatabaseProductName());
					dbs = dbmeta.getSchemas();

					TreeIter databaseconfig = ls.appendRow(null);
					ls.setData("databaseinfo_"+dsc.getAlias(), dsc);
					ls.setValue(databaseconfig, ColData, dsc.getAlias());
					while (dbs.next()) {
						TreeIter dbrow = ls.appendRow(databaseconfig);
						ls.setValue(dbrow, ColData, dbs.getString(1));
//						SqlView.getCombobox_database().appendText(""+dbs.getString(1));
						SqlView.getCombobox_database().addData(dbs.getString(1), ""+dbs.getString(1), dsc);
						
						TreeIter tablesrow;
						String[] types = { "TABLE" };
						ResultSet tables = dbmeta.getTables("", dbs.getString(1), "%", types);
						while (tables.next()) {
							tablesrow = ls.appendRow(dbrow);
						//	System.out.println(tables.getString(3));
							ls.setValue(tablesrow, ColData, tables.getString(3));
						}
						tables.close();
					}
				} else {
					System.out.println("Runnig on witn no schemas : " + dbmeta.getDatabaseProductName());
					String[] types = { "TABLE" };
					dbs = dbmeta.getCatalogs();

					TreeIter databaseconfig = ls.appendRow(null);

					ls.setValue(databaseconfig, ColData, dsc.getAlias());
					ls.setData("databaseinfo_"+dsc.getAlias(), dsc);
					while (dbs.next()) {
						TreeIter dbrow = ls.appendRow(databaseconfig);
						ls.setValue(dbrow, ColData, dbs.getString(1));
						SqlView.getCombobox_database().addData(dbs.getString(1), ""+dbs.getString(1), dsc);

						TreeIter tablesrow;
						//System.out.println(dbs.getString(1));
						ResultSet tables = dbmeta.getTables(dbs.getString(1), "", "%", types);
						while (tables.next()) {
							tablesrow = ls.appendRow(dbrow);
						//	System.out.println(tables.getString(3));
							ls.setValue(tablesrow, ColData, tables.getString(3));
						}
						tables.close();
					}
				}
			}

			System.out.println(SqlView.getCombobox_database().getData("smatest"));
			list.showAll();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				dbs.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void selectionChangedEvent(TreeSelectionEvent event) {
		System.out.println("OneClick " + event);
	}

	public void treeViewEvent(TreeViewEvent event) {
		if (event.isOfType(TreeViewEvent.Type.ROW_ACTIVATED)) {
			TreePath[] tp = list.getSelection().getSelectedRows();
			if (tp.length == 1) {
				TreePath tp1 = event.getTreePath();
				
				
				TreeIter item1 = ls.getIter(tp1.toString());
				String table = ls.getValue(item1, ColData);
				tp1.up();
				item1 = ls.getIter(tp1.toString());
				String database = ls.getValue(item1, ColData);

				tp1.up();
				item1 = ls.getIter(tp1.toString());
				String dbalias = ls.getValue(item1, ColData);

				System.out.println(table + "\t\t" + database+"\t\t"+dbalias);
				DataSourceConfig dsc = (DataSourceConfig)ls.getData("databaseinfo_"+dbalias);
				System.out.println(dsc.getUrl());
				sqltreeview.addToTable(table, database, dsc);
			}
		}

	}

	public static TableColumnList getSqltreeview() {
		return sqltreeview;
	}
}

package main;

import helpers.DataModelHelper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import model.Column;
import model.DatabaseModel;
import model.Table;
import model.TableAndColumnInterface;

import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreePath;
import org.gnu.gtk.TreeSelection;
import org.gnu.gtk.TreeStore;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
import org.gnu.gtk.event.TreeSelectionEvent;
import org.gnu.gtk.event.TreeSelectionListener;
import org.gnu.gtk.event.TreeViewEvent;
import org.gnu.gtk.event.TreeViewListener;

import config.DataSourceConfig;

public class DatabaseList implements TreeSelectionListener, TreeViewListener {
	TreeView list;

	static TableColumnList sqltreeview;

	TreeStore ls;

	DataColumnString ColData;

	private int DB_LEVEL = 2;

	public DatabaseList(TreeView list) {
		this.list = list;
		this.list.getSelection().addListener(this);
		this.list.addListener(this);
	}

	public void initTable(TreeView sqltreeview) {
		this.sqltreeview = new TableColumnList(sqltreeview);
		ColData = new DataColumnString();
		ls = new TreeStore(new DataColumn[] { ColData });

		list.setEnableSearch(true); 
		list.setAlternateRowColor(true); 
		
		TreeViewColumn col2 = new TreeViewColumn();
		CellRendererText render2 = new CellRendererText();
		col2.packStart(render2, true);
		col2.addAttributeMapping(render2, CellRendererText.Attribute.MARKUP, ColData);

		list.setSearchDataColumn(ColData);
		/* append columns */
		list.appendColumn(col2);
		list.setModel(ls);

	}

	public void addToTable(DataSourceConfig dsc, TreePath tp) {
		DatabaseMetaData dbmeta = null;
		ResultSet dbs = null;
		Connection conn = null;
		try {
			System.out.println(dsc.getDriver());
			Class.forName(dsc.getDriver());
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

				TreeIter databaseconfig = ls.getIter(tp.toString());

				DatabaseModel dbmodel = new DatabaseModel();
				dbmodel.setDatabasename(tp.toString());

				while (dbs.next()) {
					TreeIter dbrow = ls.appendRow(databaseconfig);
					ls.setValue(dbrow, ColData, dbs.getString(1));
					// SqlView.getCombobox_database().appendText(""+dbs.getString(1));
					SqlView.getCombobox_database().addData(dbs.getString(1), "" + dbs.getString(1), dsc);

					TreeIter tablesrow;
					String[] types = { "TABLE" };
					ResultSet tables = dbmeta.getTables("", dbs.getString(1), "%", types);

					while (tables.next()) {
						Table table  = new Table();
						
						tablesrow = ls.appendRow(dbrow);
						ls.setValue(tablesrow, ColData, tables.getString(3));
						
						ResultSet columns = dbmeta.getColumns("", dbs.getString(1),tables.getString(3), "%");
						while(columns.next()) {
							table.addColumn(columns.getString(4));
						}
						table.setSchema(dbs.getString(1));
						table.setName(tables.getString(3));
						
						dbmodel.addTable(table);
					}
					dbmodel.dumpTables();
					DataModelHelper.setModel(dbmodel);
					tables.close();
				}
			} else {
				System.out.println("Runnig on witn no schemas : " + dbmeta.getDatabaseProductName());
				String[] types = { "TABLE" };
				dbs = dbmeta.getCatalogs();

				// TreeIter databaseconfig = ls.appendRow(null);
				TreeIter databaseconfig = ls.getIter(tp.toString());

				ls.setValue(databaseconfig, ColData, dsc.getAlias());
				ls.setData("databaseinfo_" + dsc.getAlias(), dsc);
				while (dbs.next()) {
					TreeIter dbrow = ls.appendRow(databaseconfig);
					ls.setValue(dbrow, ColData, dbs.getString(1));
					SqlView.getCombobox_database().addData(dbs.getString(1), "" + dbs.getString(1), dsc);

					TreeIter tablesrow;
					// System.out.println(dbs.getString(1));
					ResultSet tables = dbmeta.getTables(dbs.getString(1), "", "%", types);
					while (tables.next()) {
						tablesrow = ls.appendRow(dbrow);
						// System.out.println(tables.getString(3));
						ls.setValue(tablesrow, ColData, tables.getString(3));
					}
					tables.close();
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
				if (dbs != null)
					dbs.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void createDatbaseListInView() {
		ArrayList databases = JDBMain.getConfiguredDatabases();
		if (databases != null) {
			for (int i = 0; i < databases.size(); i++) {
				DataSourceConfig dsc = (DataSourceConfig) databases.get(i);
				TreeIter databaseconfig = ls.appendRow(null);
				ls.setData("databaseinfo_" + dsc.getAlias(), dsc);
				ls.setData("isSchemaRead", new Boolean(true));
				ls.setValue(databaseconfig, ColData, dsc.getAlias());
			}
		}
		list.showAll();
	}

	public void selectionChangedEvent(TreeSelectionEvent event) {
		TreeSelection treeSelection = (TreeSelection) event.getSource();
		// Only run on top level databases
		if (treeSelection.getSelectedRows()[0].getDepth() == 1) {
			if (event.getType() == TreeSelectionEvent.Type.CHANGED) {
				Boolean isSchemaRead = (Boolean) treeSelection.getData("isSchemaRead");
				if (isSchemaRead == null || isSchemaRead.booleanValue()) {
					TreePath[] tp = treeSelection.getSelectedRows();
					TreePath tp1 = tp[0];
					TreeIter item1 = ls.getIter(tp1.toString());
					String table = ls.getValue(item1, ColData);
					DataSourceConfig dsc = JDBMain.getConfiguredDatabases(table);
					ls.setData("databaseinfo_" + dsc.getAlias(), dsc);
					ls.setData("isSchemaRead", new Boolean(true));
					addToTable(dsc, tp[0]);
					list.expandRow(tp1, true);
				}
			}

		}

		if (treeSelection.getSelectedRows()[0].getDepth() == DB_LEVEL) {
			System.out.println("Yehaa..");
			TreePath[] tp = treeSelection.getSelectedRows();
			TreePath tp1 = tp[0];
			TreeIter item1 = ls.getIter(tp1.toString());
			String table = ls.getValue(item1, ColData);
			SqlView.setCombobox(table);
		}

	}

	public void treeViewEvent(TreeViewEvent event) {
		if (event.isOfType(TreeViewEvent.Type.ROW_ACTIVATED)) {
			TreePath[] tp = list.getSelection().getSelectedRows();
			if (tp.length == 1) {
				if (event.getTreePath().getDepth() >= 3) {
					TreePath tp1 = event.getTreePath();

					TreeIter item1 = ls.getIter(tp1.toString());
					String table = ls.getValue(item1, ColData);
					tp1.up();
					item1 = ls.getIter(tp1.toString());
					String database = ls.getValue(item1, ColData);

					tp1.up();
					item1 = ls.getIter(tp1.toString());
					String dbalias = ls.getValue(item1, ColData);

					System.out.println(table + "\t\t" + database + "\t\t" + dbalias);
					DataSourceConfig dsc = (DataSourceConfig) ls.getData("databaseinfo_" + dbalias);
					System.out.println(dsc.getUrl());
					sqltreeview.addToTable(table, database, dsc);
				}

			}

		}

	}

	public static TableColumnList getSqltreeview() {
		return sqltreeview;
	}
}

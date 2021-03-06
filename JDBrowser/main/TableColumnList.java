package main;

import helpers.SqlRunner;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
import org.gnu.gtk.event.TreeSelectionEvent;
import org.gnu.gtk.event.TreeSelectionListener;
import org.gnu.gtk.event.TreeViewColumnEvent;
import org.gnu.gtk.event.TreeViewColumnListener;
import org.gnu.gtk.event.TreeViewEvent;
import org.gnu.gtk.event.TreeViewListener;

import config.DataSourceConfig;

public class TableColumnList implements TreeSelectionListener, TreeViewListener, TreeViewColumnListener {
	Logger log = Logger.getLogger(TableColumnList.class);

	TreeView list;

	ListStore ls;

	List columnnames;

	DataColumnString ColData;

	DataColumn[] columns;

	boolean init = false;

	private String tablename;

	public TableColumnList(TreeView list) {
		this.list = list;
		this.list.getSelection().addListener((TreeSelectionListener) this);
		this.list.addListener((TreeViewListener) this);
	}

	public void initTable() {
		ColData = new DataColumnString();
		ls = new ListStore(new DataColumn[] { ColData });
		list.setHeadersVisible(true);
		list.setEnableSearch(true);
		list.setHeadersClickable(true);
		/*
		 * allows to use keyboard to search items matching the pressed keys
		 */

		list.setAlternateRowColor(true); /* no comments smile */
		list.setModel(ls);

		TreeViewColumn col2 = new TreeViewColumn();
		CellRendererText render2 = new CellRendererText();
		col2.packStart(render2, true);
		col2.addAttributeMapping(render2, CellRendererText.Attribute.TEXT, ColData);

		list.setSearchDataColumn(ColData);
		/* append columns */
		list.appendColumn(col2);
		init = true;
	}

	public void addColumns(List titles) {
		if (!init)
			initTable();
		TreeViewColumn[] tmpcolumns = list.getColumns();
		for (int j = 0; j < tmpcolumns.length; j++) {
			list.removeColumn(tmpcolumns[j]);
		}

		columns = new DataColumn[titles.size()];
		for (int i = 0; i < titles.size(); i++) {
			columns[i] = new DataColumnString();
		}
		ls = new ListStore(columns);
		for (int i = 0; i < titles.size(); i++) {
			DataColumnString thiscol = (DataColumnString) columns[i];
			TreeViewColumn col2 = new TreeViewColumn();
			CellRendererText render2 = new CellRendererText();
			col2.packStart(render2, true);
			col2.addAttributeMapping(render2, CellRendererText.Attribute.TEXT, thiscol);

			col2.setTitle(titles.get(i).toString());
			// col2.setSortIndicator(true);
			// col2.setSortOrder(SortType.ASCENDING);
			col2.addListener(this);
			col2.setData("DataColumn", thiscol);
			list.appendColumn(col2);
		}
		list.setModel(ls);
		list.setHeadersVisible(true);
		list.setEnableSearch(true);
		list.setHeadersClickable(true);
	}

	public void addToTable(String tablename, String db, DataSourceConfig database) {
		this.tablename = tablename;
		if (!init)
			initTable();
		while (true) {
			TreeIter item = ls.getIter("0");
			if (item == null)
				break;
			ls.removeRow(item);
		}
		try {
			Class.forName(database.getDriver());
			Connection mysql = DriverManager.getConnection(database.getUrl(), database.getUserid(), database.getPassword());
			DatabaseMetaData dbmeta = mysql.getMetaData();

			System.out.println("Getting columns for table: " + tablename + " in database " + db);
			ResultSet rs = dbmeta.getColumns(db, null, tablename, null);

			columnnames = new LinkedList();
			while (rs.next()) {
				String columnname = rs.getString("COLUMN_NAME");
				columnnames.add(columnname);
				System.out.println("Adding column to array: " + columnname);
			}
			addColumns(columnnames);
			addDataToTable(null, db, database);
			rs.close();
			mysql.close();
			list.showAll();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void addDataToTable(String sql, String db, DataSourceConfig database) {
		if (!init)
			initTable();
		while (true) {
			TreeIter item = ls.getIter("0");
			if (item == null)
				break;
			ls.removeRow(item);
		}
		try {
			long start = System.currentTimeMillis();
			log.debug(database.getDriver());
			SqlRunner sqlrun = new SqlRunner(database);
			System.out.println("Getting columns for table: " + tablename);
			if (sql == null)
				sql = "select * from " + tablename;
			sqlrun.setCatalog(db);

			ResultSet rs = sqlrun.executeQuery(sql);
			addColumnsFromResultSet(rs);
			int counter = 0;
			while (rs.next()) {
				counter++;
				TreeIter dbrow = ls.appendRow();
				for (int i = 0; i < columnnames.size(); i++) {
					String columnvalue = rs.getString(columnnames.get(i).toString());
					ls.setValue(dbrow, (DataColumnString) columns[i], columnvalue);
				}
				if (counter >= JDBMain.getMaxRows())
					break;
			}
			long end = System.currentTimeMillis();
			JDBMain.setStatusbarMessage("Got " + counter + " rows in " + (end - start) + " milliseconds");
			rs.close();
			sqlrun.close();
			list.showAll();
		} catch (SQLException e) {
			JDBMain.showErrorDialog(e.getMessage());
			e.printStackTrace();
		}

	}

	public void addColumnsFromResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData metadata = rs.getMetaData();
		if (columnnames == null)
			columnnames = new LinkedList();
		else
			columnnames.clear();
		for (int i = 1; i <= metadata.getColumnCount(); i++) {
			columnnames.add(metadata.getColumnLabel(i));
		}
		addColumns(columnnames);
	}

	public void selectionChangedEvent(TreeSelectionEvent event) {
		// System.out.println("OneClick " + event);
	}

	public void treeViewEvent(TreeViewEvent event) {
		// if (event.isOfType(TreeViewEvent.Type.ROW_ACTIVATED)) {
		// TreePath[] tp = list.getSelection().getSelectedRows();
		// if (tp.length == 1) {
		// TreeIter item = ls.getIter(tp[0].toString());
		// System.out.println("string selected: " + ls.getValue(item, ColData));
		// }
		// }

	}

	public void columnClickedEvent(TreeViewColumnEvent event) {
		TreeViewColumn col1 = (TreeViewColumn) event.getSource();
		DataColumn col = (DataColumn) col1.getData("DataColumn");
		list.setSearchDataColumn(col);
		col1.setSortColumn(col);
		list.showAll();
	}
}

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gnu.gtk.CellRendererText;
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

public class DatabaseList implements TreeSelectionListener, TreeViewListener {
	TreeView list;
	TableColumnList sqltreeview;
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

		try {
			boolean ran = false;
			Class.forName(JDBMain.DB_DRIVER);
			System.out.println(JDBMain.DBURL+"\n"+JDBMain.DBUSER+"\n"+ JDBMain.DBPASSWORD);
			Connection mysql = DriverManager.getConnection(JDBMain.DBURL,  JDBMain.DBUSER, JDBMain.DBPASSWORD);
			System.out.println(mysql.getCatalog());
			DatabaseMetaData dbmeta = mysql.getMetaData();
			System.out.println("Runnig on : "+dbmeta.getDatabaseProductName());
			
			ResultSet dbs = dbmeta.getSchemas();
			while (dbs.next()) {
				ran = true;
				TreeIter dbrow = ls.appendRow(null);
				ls.setValue(dbrow, ColData, dbs.getString(1));

				TreeIter tablesrow;

				String[] types = { "TABLE" };
				ResultSet tables = dbmeta.getTables("", dbs.getString(1), "%", types);
				while (tables.next()) {
					tablesrow = ls.appendRow(dbrow);
					System.out.println(tables.getString(3));
					ls.setValue(tablesrow, ColData, tables.getString(3));
				}
				tables.close();
			}
			if(!ran) {
				TreeIter dbrow = ls.appendRow(null);
				TreeIter tablesrow;

				String[] types = { "TABLE" };
				ResultSet tables = dbmeta.getTables("", "ECLUB2", "%", types);
				while (tables.next()) {
					tablesrow = ls.appendRow(dbrow);
					System.out.println(tables.getString(3));
					ls.setValue(tablesrow, ColData, tables.getString(3));
				}
				tables.close();
			}
			dbs.close();
			mysql.close();
			list.showAll();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void selectionChangedEvent(TreeSelectionEvent event) {
		System.out.println("OneClick " + event);
	}

	public void treeViewEvent(TreeViewEvent event) {
		if (event.isOfType(TreeViewEvent.Type.ROW_ACTIVATED)) {
			TreePath[] tp = list.getSelection().getSelectedRows();
			if (tp.length == 1)
			{
				TreePath tp1 = event.getTreePath();
				TreeIter item1 = ls.getIter( tp1.toString() );
				String table = ls.getValue( item1, ColData );
				tp1.up();
				item1 = ls.getIter( tp1.toString() );
				String database = ls.getValue( item1, ColData );

				System.out.println(table+"\t"+database);
				sqltreeview.addToTable(table, database);
			}
		}

	}

	public TableColumnList getSqltreeview() {
		return sqltreeview;
	}
}

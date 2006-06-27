package helpers;

import java.util.Collection;
import java.util.Iterator;

import model.TableAndColumnInterface;

import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.Dialog;
import org.gnu.gtk.HBox;
import org.gnu.gtk.PolicyType;
import org.gnu.gtk.ScrolledWindow;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreePath;
import org.gnu.gtk.TreeSelection;
import org.gnu.gtk.TreeStore;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
import org.gnu.gtk.event.KeyEvent;
import org.gnu.gtk.event.KeyListener;

public class CompletionHelper extends Dialog implements KeyListener {
	private static TreeView completionlist;

	private static TreeStore ls;

	private static DataColumnString ColData;

	private static String userselection = "";

	public static Boolean isShown;

	public CompletionHelper(Collection collection) {
		// this.addButton(GtkStockItem.YES, 1);
		// this.addButton(GtkStockItem.NO, 2);
		this.setTitle("Select table");
		this.setDefaultSize(400, 200);
		init(collection);
		showAll();
	}

	public void init(Collection collection) {
		HBox mainBox = new HBox(false, 0);
		this.getDialogLayout().add(mainBox);
		this.setDefaultSize(400, 200);

		ScrolledWindow scrollwindow = new ScrolledWindow(null, null);
		scrollwindow.setCanFocus(true);
		completionlist = new TreeView();
		initTable();
		Iterator ite = collection.iterator();
		while (ite.hasNext()) {
			TableAndColumnInterface item = (TableAndColumnInterface) ite.next();
			addData(item.getName());
		}
		scrollwindow.setPolicy(PolicyType.ALWAYS, PolicyType.ALWAYS);
		scrollwindow.addWithViewport(completionlist);
		mainBox.packStart(scrollwindow, true, true, 0);

		completionlist.addListener(this);

		// this.getDialogLayout().add(completionlist);
		// completionWindow.setDefaultSize(200, 400);
		// completionWindow.show();
	}

	public static void initTable() {
		ColData = new DataColumnString();
		ls = new TreeStore(new DataColumn[] { ColData });
		TreeViewColumn col2 = new TreeViewColumn();
		CellRendererText render2 = new CellRendererText();
		col2.packStart(render2, true);
		col2.addAttributeMapping(render2, CellRendererText.Attribute.TEXT, ColData);

		completionlist.appendColumn(col2);
		
		completionlist.setEnableSearch(true);
		completionlist.setAlternateRowColor(true);
		completionlist.setModel(ls);
		completionlist.setSearchDataColumn(ColData);
		
		completionlist.setCanFocus(true);
		completionlist.setHeadersVisible(true);
		completionlist.setReorderable(false);
		completionlist.setFixedHeightMode(false);
		completionlist.setHoverExpand(false);
		completionlist.setHoverSelection(false);
	}

	public static void addData(String lable) {
		TreeIter databaseconfig = ls.appendRow(null);
		ls.setValue(databaseconfig, ColData, lable);
	}

	public boolean keyEvent(KeyEvent event) {
		if (event.getType().getName().equals("KEY_PRESSED")) {
			if (event.getKeyval() == 65293) {
				TreeSelection selection = completionlist.getSelection();
				TreePath[] tp = selection.getSelectedRows();
				TreePath tp1 = tp[0];
				TreeIter item1 = ls.getIter(tp1.toString());
				userselection = ls.getValue(item1, ColData);
				hide();
			}

			if (event.getKeyval() == 65307) {
				hide();
			}
		}
		return false;
	}

	public String getUserselection() {
		return userselection;
	}

}

package helpers;

import java.util.Collection;
import java.util.Iterator;

import main.JDBMain;
import model.TableAndColumnInterface;

import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.TextBuffer;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreePath;
import org.gnu.gtk.TreeSelection;
import org.gnu.gtk.TreeStore;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
import org.gnu.gtk.Window;
import org.gnu.gtk.event.KeyEvent;
import org.gnu.gtk.event.KeyListener;

public class CompletionHelper implements KeyListener {
	static TreeView completionlist;

	static TreeStore ls;

	static CompletionHelper INSTANCE;

	private static TextBuffer textbuffer;
	Window completionWindow;
	public static void addToWindow(Collection collection, TextBuffer textbuf) {
		if (INSTANCE == null)
			INSTANCE = new CompletionHelper();
		textbuffer = textbuf;
		INSTANCE.init(collection);
		
	}

	public CompletionHelper() {
	}

	public void init(Collection collection) {
		completionWindow = (Window) JDBMain.getGladeApp().getWidget("completionWindow");
		completionlist = (TreeView) JDBMain.getGladeApp().getWidget("completionview");

		initTable();
		Iterator ite = collection.iterator();
		while (ite.hasNext()) {
			TableAndColumnInterface item = (TableAndColumnInterface) ite.next();
			addData(item.getName());
		}
		completionlist.addListener(this);
		completionWindow.setDefaultSize(200, 400);
		completionWindow.show();

	}

	private static DataColumnString ColData;

	public static void initTable() {
		ColData = new DataColumnString();
		ls = new TreeStore(new DataColumn[] { ColData });

		completionlist.setEnableSearch(true);
		completionlist.setAlternateRowColor(true); /* no comments smile */
		completionlist.setModel(ls);
		completionlist.setSearchDataColumn(ColData);

		TreeViewColumn col2 = new TreeViewColumn();
		CellRendererText render2 = new CellRendererText();
		col2.packStart(render2, true);
		col2.addAttributeMapping(render2, CellRendererText.Attribute.TEXT, ColData);

		/* append columns */
		completionlist.appendColumn(col2);
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
				String table = ls.getValue(item1, ColData);

				textbuffer.insertText(table+" ");
				completionWindow.hide();
			}
			
			if(event.getKeyval() == 65307) {
				completionWindow.hide();
			}
		}
		return false;
	}

}

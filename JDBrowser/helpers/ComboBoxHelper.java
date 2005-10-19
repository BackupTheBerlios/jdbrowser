package helpers;

import org.gnu.glib.Handle;
import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.Combo;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreeModel;
import org.gnu.gtk.TreeStore;
import org.gnu.gtk.TreeViewColumn;

public class ComboBoxHelper extends ComboBox {
	ListStore ls;

	DataColumnString ColData;

	public ComboBoxHelper() {
		super();
	}

	public ComboBoxHelper(Handle arg0) {
		super(arg0);
	}

	public ComboBoxHelper(TreeModel arg0) {
		super(arg0);
	}

	public void initColumn() {
		ColData = new DataColumnString();
		ls = new ListStore(new DataColumn[] { ColData });
		this.setModel(ls);

		TreeViewColumn col2 = new TreeViewColumn();
		CellRendererText render2 = new CellRendererText();
		col2.packStart(render2, true);
		col2.addAttributeMapping(render2, CellRendererText.Attribute.TEXT, ColData);
	}

	public void addData(String lable, String dataname, Object data) {
		TreeIter databaseconfig = ls.appendRow();

		ls.setValue(databaseconfig, ColData, lable);
		ls.setData(dataname, data);
	}

	public Object getData(String searchvalue) {
		return ls.getData(searchvalue);
	}
}

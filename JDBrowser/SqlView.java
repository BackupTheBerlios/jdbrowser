import org.gnu.gdk.EventType;
import org.gnu.glade.LibGlade;
import org.gnu.gtk.Button;
import org.gnu.gtk.TextBuffer;
import org.gnu.gtk.TextView;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.event.ButtonEvent;
import org.gnu.gtk.event.ButtonListener;

public class SqlView implements ButtonListener {
	TextView view;
	TreeView sqltreeview;
	DatabaseList dblist;
	public SqlView(TextView sqlviewer, LibGlade firstApp, DatabaseList dblist) {
		view = sqlviewer;
		this.sqltreeview = sqltreeview;
		this.dblist = dblist;
		Button execute = (Button) firstApp.getWidget("execute_sql_button");
		execute.addListener(this);
	}

	public void buttonEvent(ButtonEvent event) {
		if (event.isOfType(ButtonEvent.Type.CLICK)) {
			System.out.println("click");
			TextBuffer textbuffer = view.getBuffer();
			String test = textbuffer.getText(textbuffer.getStartIter(), textbuffer.getEndIter(), true);
			System.out.println(test);
			dblist.getSqltreeview().addDataToTable(test);
		}
	}
}

package helpers;

import org.gnu.gtk.Dialog;
import org.gnu.gtk.HBox;
import org.gnu.gtk.ProgressBar;
import org.gnu.gtk.TextBuffer;
import org.gnu.gtk.TextView;

public class InfoWindow {
	public static Dialog getInformationDialog(String text) {
		Dialog win = new Dialog();
		win.setTitle("Information");
		HBox box = new HBox(true, 0);
		TextView textview = new TextView();

		box.packStart(textview);
		win.getDialogLayout().packStart(box);

		TextBuffer buf = new TextBuffer();
		buf.setText(text);
		textview.setBuffer(buf);
		textview.setEditable(false);

		ProgressBar bar = new ProgressBar();
		bar.pulse();
		box.packStart(bar);
		win.showAll();

		// win.setDefaultResponse(0);
		// win.run();
		return win;
	}

}

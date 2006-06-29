package main;

public class AboutDialog extends org.gnu.gtk.AboutDialog {

	/**
	 * Default about dialog.. TODO: Doesn't seem to close
	 */
	public AboutDialog() {
		super();
		setAuthors(new String[] { "S?ren Mathiasen" });
		setTitle("About JDBrowser");
		setCopyright("Soren Mathiasen 2006");
		setComments("This is very untested :)");
		setName("JDBrowser");
		setWebsite("http://jdbrowser.berlios.de");
	}
}

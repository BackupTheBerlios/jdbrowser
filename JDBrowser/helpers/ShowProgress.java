package helpers;

import org.gnu.glib.Fireable;
import org.gnu.glib.Timer;
import org.gnu.gtk.ProgressBar;
import org.gnu.gtk.Window;
import org.gnu.gtk.WindowType;

public class ShowProgress implements Fireable {
	private volatile ProgressBar bar;

	private SqlRunner job;

	public ShowProgress(SqlRunner job) {
		this.job = job;
		Window window = new Window(WindowType.TOPLEVEL);

		bar = new ProgressBar();
		window.add(bar);
		window.showAll();

		doSomeTask(job);
	}

	private void doSomeTask(Thread job) {

		// start some heavy task...

		job.start();

		// start updating the progress bar...
		Timer timer = new Timer(50, this);
		timer.start();
	}

	/*
	 * This method will be called every second, to update the progressbar
	 */
	public boolean fire() {

		boolean shouldContinue = true;

		bar.pulse();

		if (job.isDone()) {

			System.out.println("Task is done");
			shouldContinue = false;
		}

		return shouldContinue;
	}

}

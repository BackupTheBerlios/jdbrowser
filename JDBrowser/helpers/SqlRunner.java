package helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import config.DataSourceConfig;

public class SqlRunner extends Thread {
	Connection conn;

	private boolean done = false;

	public SqlRunner(DataSourceConfig database) {
		try {
			Class.forName(database.getDriver());
			conn = DriverManager.getConnection(database.getUrl(), database.getUserid(), database.getPassword());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void setCatalog(String db) {
		try {
			conn.setCatalog(db);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(final String sql) {
		ResultSet rs;
		try {
			rs = conn.createStatement().executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean isDone() {
		return done;
	}

	public void run() {
		while (!isDone())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void setDone(boolean done) {
		this.done = done;
	}

}

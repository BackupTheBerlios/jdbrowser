package config;

public class DataSourceConfig {

	String alias;

	String url;

	String driver;

	String userid;

	String password;
	public DataSourceConfig() {
		
	}
	public DataSourceConfig(String alias, String driver, String password, String url, String userid) {
		super();
		this.alias = alias;
		this.driver = driver;
		this.password = password;
		this.url = url;
		this.userid = userid;
	}
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String toString() {
		return alias;
	}
}

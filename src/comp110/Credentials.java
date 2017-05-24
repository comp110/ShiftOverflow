package comp110;

public class Credentials {

	// variables
	private String m_username;
	private String m_password;
	
	
	// functions
	public Credentials(String user, String pass) {
		this.m_username = user;
		this.m_password = pass;
	}
	
	public String getUsername() {
		return this.m_username;
	}
	
	public String getPassword() {
		return this.m_password;
	}
}

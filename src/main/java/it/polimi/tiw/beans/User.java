package it.polimi.tiw.beans;

public class User {
	private int id;
	private String username;
	private String password;
	private String email;
	private String role;

	public int getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setId(int i) {
		id = i;
	}

	public void setUsername(String un) {
		username = un;
	}
	
	public void setEmail(String em) {
		email = em;
	}
	
	public void setPassword(String psw) {
		password = psw;
	}
	
	public void setRole(String r) {
		role = r;
	}


}


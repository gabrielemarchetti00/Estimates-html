package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  id, role, username FROM user  WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setRole(result.getString("role"));
					user.setUsername(result.getString("username"));
					return user;
				}
			}
		}
	}
	
	public void signUpUser(String usrn, String email, String pwd, String role) throws SQLException {
		String query = "INSERT into user (username, email, password, role) VALUES(?, ?, ?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, email);
			pstatement.setString(3, pwd);
			pstatement.setString(4, role);
			pstatement.executeUpdate();
		}
	}
	
	public User findClientByEstimate(int estid) throws SQLException {
		User client = null;
		String query = "SELECT U.username FROM user U JOIN estimate E ON U.id = E.client WHERE E.id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, estid);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					client = new User();
					client.setUsername(result.getString("username"));
				}
			}
		}
		return client;
	}
	
	public User findWorkerByEstimate(int estid) throws SQLException {
		User worker = null;
		String query = "SELECT U.username FROM user U JOIN estimate E ON U.id = E.worker WHERE E.id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, estid);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					worker = new User();
					worker.setUsername(result.getString("username"));
				}
			}
		}
		return worker;
	}
	
	public List<String> findAllUsernames() throws SQLException {
		List<String> usernames = new ArrayList<String>();
		String query = "SELECT username FROM user";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					String usrn = null;
					usrn = result.getString("username");
					usernames.add(usrn);
				}
			}
		}
		return usernames;
	}
}

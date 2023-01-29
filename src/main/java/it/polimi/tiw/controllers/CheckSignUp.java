package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CheckSignUp")
public class CheckSignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection connection = null;

	public CheckSignUp() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// get and check params
		String role = request.getParameter("role");
		String usrn = request.getParameter("username");
		String email = request.getParameter("email");
		String pwd = request.getParameter("pwd");
		String rpwd = request.getParameter("rpwd");
		if (role == null || role.isEmpty() || usrn == null || usrn.isEmpty() || email == null || email.isEmpty()
				|| pwd == null || pwd.isEmpty() || rpwd == null || rpwd.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}

		// sign up after checking if the data is valid
		UserDAO userDAO = new UserDAO(connection);
		List<String> usernames = new ArrayList<String>();
		try {
			usernames = userDAO.findAllUsernames();
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Cant get all usernames");
		}
		
		if (usernames.contains(usrn)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Username already used");
			return;
		}
		
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		if (!pat.matcher(email).matches()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect email format");
			return;
		}
		
		if (!rpwd.equals(pwd)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Passwords do not match");
			return;
		}

		UserDAO usr = new UserDAO(connection);
		try {
			usr.signUpUser(usrn, email, pwd, role);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in signing up");
			return;
		}

		String path = getServletContext().getContextPath() + "/index.html";
		response.sendRedirect(path);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

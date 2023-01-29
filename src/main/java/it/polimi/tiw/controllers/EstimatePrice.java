package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EstimateDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/EstimatePrice")
public class EstimatePrice extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public EstimatePrice() {
        super();
    }
    
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//get and check params
		String price = request.getParameter("price");
		float pricef = 0;
		try {
			pricef = Float.parseFloat(price);
			if(pricef <= 0 || price.isEmpty()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Price must be greater then zero");
				return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad assignment data");
			return;
		}
		
		//insert price into estimate
		int estid = Integer.parseInt(request.getParameter("estid"));
		EstimateDAO estimateDAO = new EstimateDAO(connection);
		try {
			estimateDAO.insertPrice(pricef, estid);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to insert price");
			return;
		}
		
		//set the estimate to assigned so the list in the homeWorker is updated
		try {
			estimateDAO.setAssigned(estid);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to assign estimate");
			return;
		}
		
		//update list of estimates handled by the worker
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		try {
			estimateDAO.assignWorker(user.getId(), estid);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to assign estimate");
			return;
		}

		//return view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToHomeWorker";
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

package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Estimate;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EstimateDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GoToHomeWorker")
public class GoToHomeWorker extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

    public GoToHomeWorker() {
        super();
    }
    
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext); //ERRORE
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//the list naestimates contains the estimates not assigned yet
		EstimateDAO estimatesDAO = new EstimateDAO(connection);
		List<Estimate> naestimates = new ArrayList<Estimate>();
		try {
			naestimates = estimatesDAO.findNotAssignedEstimates();
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not possible to recover not assigned estimates");
			return;
		}
		
		//find all the estimates handled by the worker
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		List<Estimate> westimates = new ArrayList<Estimate>();
		try {
			westimates = estimatesDAO.findEstimatesByWorker(user.getId());
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not possible to recover not assigned estimates");
			return;
		}
		
		String path = "/WEB-INF/HomeWorker.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("naestimates", naestimates);
		ctx.setVariable("westimates", westimates);
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
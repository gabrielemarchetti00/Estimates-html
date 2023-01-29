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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Estimate;
import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EstimateDAO;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.ProductDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GoToEstimatePrice")
public class GoToEstimatePrice extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

    public GoToEstimatePrice() {
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
		//get and check params
		String chosenEstimate = request.getParameter("estimateid");
		Integer chosenEstimateId = null;
		try {
			chosenEstimateId = Integer.parseInt(chosenEstimate);
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		EstimateDAO estimateDAO = new EstimateDAO(connection);
		Estimate estimate = null;
		try {
			estimate = estimateDAO.findEstimatesById(chosenEstimateId);
			if (estimate == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		
		OptionDAO optionsDAO = new OptionDAO(connection);
		List<Option> options = new ArrayList<Option>();
		try {
			options = optionsDAO.findOptionsByEstimate(chosenEstimateId);
			if (options == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover options names");
			return;
		}
		
		//find client by estimate so i can display his username
		UserDAO userDAO = new UserDAO(connection);
		User client = null;
		try {
			client = userDAO.findClientByEstimate(chosenEstimateId);
			if (client == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover client");
			return;
		}
		
		//get product name
		ProductDAO productDAO = new ProductDAO(connection);
		Product product = null;
		try {
			product = productDAO.findProductByEstimate(chosenEstimateId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not possible to recover product");
			return;
		}
		
		//redirect to estimate price
		String path = "/WEB-INF/EstimatePrice.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("estimate", estimate);
		ctx.setVariable("chosenEstimateId", chosenEstimateId);
		ctx.setVariable("options", options);
		ctx.setVariable("client", client);
		ctx.setVariable("product", product);
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
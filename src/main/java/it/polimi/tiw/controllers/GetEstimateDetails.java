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

@WebServlet("/GetEstimateDetails")
public class GetEstimateDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

    public GetEstimateDetails() {
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
		
		//check if an estimate with that id exists
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
		
		//get options of the selected estimate
		OptionDAO optionsDAO = new OptionDAO(connection);
		List<Option> options = new ArrayList<Option>();
		try {
			options = optionsDAO.findOptionsByEstimate(chosenEstimateId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not possible to recover options names");
			return;
		}
		
		//get client username
		UserDAO userDAO = new UserDAO(connection);
		User client = null;
		String usrnc = null;
		try {
			client = userDAO.findClientByEstimate(chosenEstimateId);
			usrnc = client.getUsername();
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not possible to recover options names");
			return;
		}
	
		//get product name
		ProductDAO productDAO = new ProductDAO(connection);
		Product product = null;
		String prodname = null;
		try {
			product = productDAO.findProductByEstimate(chosenEstimateId);
			prodname = product.getName();
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not possible to recover product name");
			return;
		}
		
		//get worker username
		User worker = null;
		String usrnw = null;
		boolean b = false; //used to check if the estimates has been assigned yet
		try {
			worker = userDAO.findWorkerByEstimate(chosenEstimateId);
			if(worker == null) {
				b = true;
			}else usrnw = worker.getUsername();
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not possible to recover worker username");
			return;
		}
		
		//redirect to home client and add estimate details
		String path = "/WEB-INF/EstimateDetails.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("estimate", estimate);
		ctx.setVariable("chosenEstimateId", chosenEstimateId);
		ctx.setVariable("options", options);
		ctx.setVariable("usrnc", usrnc);
		ctx.setVariable("usrnw", usrnw);
		ctx.setVariable("prodname", prodname);
		ctx.setVariable("b", b);
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

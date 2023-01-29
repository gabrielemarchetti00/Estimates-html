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
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.EstimateDAO;
import it.polimi.tiw.dao.ProductDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GoToHomeClient")
public class GoToHomeClient extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

    public GoToHomeClient() {
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
		//show list of estimates created by the client
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		EstimateDAO estimatesDAO = new EstimateDAO(connection);
		List<Estimate> estimates = new ArrayList<Estimate>();
		try {
			estimates = estimatesDAO.findEstimatesByClient(user.getId());
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not possible to recover estimates");
			return;
		}
		
		//show list of products for createEstimate form
		ProductDAO productsDAO = new ProductDAO(connection);
		List<Product> products = new ArrayList<Product>();
		try {
			products = productsDAO.findAllProducts();
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not possible to recover products");
			return;
		}
		
		//redirect to home client and add estimates and products
		String path = "/WEB-INF/HomeClient.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("estimates", estimates);
		ctx.setVariable("products", products);
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

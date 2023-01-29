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

import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.ProductDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GetProductOptions")
public class GetProductOptions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

    public GetProductOptions() {
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
		
		//get and check parameters
		Integer productId = null;
		try {
			productId = Integer.parseInt(request.getParameter("productid"));
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		
		//if a product with that id exists, obtain his options
		ProductDAO productDAO = new ProductDAO(connection);
		Product product = null;
		String prodname = null;
		OptionDAO optionsDAO = new OptionDAO(connection);
		List<Option> options = new ArrayList<Option>();
		try {
			product = productDAO.findProductById(productId);
			if (product == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
			prodname = product.getName();
			options = optionsDAO.findOptionsByProduct(productId);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Not possible to recover product");
			return;
		}
		
		//redirect to home page client and add productid and options 
		String path = "/WEB-INF/ProductOptions.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("productid", productId);
		ctx.setVariable("options", options);
		ctx.setVariable("prodname", prodname);
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


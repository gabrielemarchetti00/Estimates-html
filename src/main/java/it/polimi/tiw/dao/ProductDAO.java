package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import it.polimi.tiw.beans.Product;

public class ProductDAO {
	private Connection con;

	public ProductDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Product> findAllProducts() throws SQLException {
		List<Product> products = new ArrayList<Product>();
		
		String query = "SELECT * FROM product";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Product product = new Product();
					product.setId(result.getInt("id"));
					product.setName(result.getString("name"));
					
					byte[] imgData = result.getBytes("image");
					String encodedImg=Base64.getEncoder().encodeToString(imgData);
					product.setImage(encodedImg);
					products.add(product);
				}
			}
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
		}
		return products;
	}
	
	public Product findProductById(int productId) throws SQLException {
		Product product = null;
		
		String query = "SELECT * FROM product WHERE id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, productId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					product = new Product();
					product.setId(result.getInt("id"));
					product.setName(result.getString("name"));
					product.setImage(result.getString("image"));;
				}
			}
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
		}
		return product;
	}
	
	public Product findProductByEstimate(int estid) throws SQLException {
		Product product = null;
		String query = "SELECT P.name FROM product P JOIN estimate E ON P.id = E.product WHERE E.id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, estid);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					product = new Product();
					product.setName(result.getString("name"));
				}
			}
		}
		return product;
	}
}
	

package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Estimate;

public class EstimateDAO {
	private Connection con;

	public EstimateDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Estimate> findEstimatesByClient(int userid) throws SQLException {
		List<Estimate> estimates = new ArrayList<Estimate>();
		
		String query = "SELECT * FROM estimate WHERE client = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, userid);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Estimate estimate = new Estimate();
					estimate.setClient(userid);
					estimate.setId(result.getInt("id"));
					estimate.setPrice(result.getFloat("price"));
					estimate.setProduct(result.getInt("product"));
					estimate.setWorker(result.getInt("worker"));
					estimates.add(estimate);
				}
			}
		}
		return estimates;
	}
	
	public void createEstimate(int userid, int prodid, int id) throws SQLException {
		String query = "INSERT into estimate (client, product, price, worker, id) VALUES(?, ?, ?, ?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, userid);
			pstatement.setInt(2, prodid);
			pstatement.setFloat(3, 0);
			pstatement.setInt(4, 0);
			pstatement.setInt(5, id);
			pstatement.executeUpdate();
		}
	}
	
	public Estimate findEstimatesById(int estid) throws SQLException {
		Estimate estimate = null;
		
		String query = "SELECT * FROM estimate WHERE id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, estid);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					estimate = new Estimate();
					estimate.setClient(result.getInt("client"));
					estimate.setPrice(result.getFloat("price"));
					estimate.setProduct(result.getInt("product"));
					estimate.setWorker(result.getInt("worker"));
				}
			}
		}
		return estimate;
	}
	
	public List<Estimate> findAllEstimates() throws SQLException {
		List<Estimate> allEstimates = new ArrayList<Estimate>();
		
		String query = "SELECT * FROM estimate";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Estimate estimate = new Estimate();
					estimate.setId(result.getInt("id"));
					estimate.setClient(result.getInt("client"));
					estimate.setPrice(result.getFloat("price"));
					estimate.setProduct(result.getInt("product"));
					estimate.setWorker(result.getInt("worker"));
					allEstimates.add(estimate);
				}
			}
		}
		return allEstimates;
	}
	
	public List<Estimate> findNotAssignedEstimates() throws SQLException {
		List<Estimate> naest = new ArrayList<Estimate>();
		
		String query = "SELECT * FROM estimate WHERE assigned = 0";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Estimate estimate = new Estimate();
					estimate.setId(result.getInt("id"));
					estimate.setClient(result.getInt("client"));
					estimate.setPrice(result.getFloat("price"));
					estimate.setProduct(result.getInt("product"));
					estimate.setWorker(result.getInt("worker"));
					naest.add(estimate);
				}
			}
		}
		return naest;
	}
	
	public List<Estimate> findEstimatesByWorker(int userid) throws SQLException {
		List<Estimate> estimates = new ArrayList<Estimate>();
		
		String query = "SELECT * FROM estimate WHERE worker = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, userid);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Estimate estimate = new Estimate();
					estimate.setClient(result.getInt("client"));
					estimate.setId(result.getInt("id"));
					estimate.setPrice(result.getFloat("price"));
					estimate.setProduct(result.getInt("product"));
					estimate.setWorker(userid);
					estimates.add(estimate);
				}
			}
		}
		return estimates;
	}
	
	public void insertPrice(float price, int estid) throws SQLException {
		String query = "UPDATE estimate SET price = ? WHERE id = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setFloat(1, price);
			pstatement.setInt(2, estid);
			pstatement.executeUpdate();
		}
	}
	
	public void setAssigned(int estid) throws SQLException {
		String query = "UPDATE estimate SET assigned = ? WHERE id = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, 1);
			pstatement.setInt(2, estid);
			pstatement.executeUpdate();
		}
	}
	
	public void assignWorker(int workerid, int estid) throws SQLException {
		String query = "UPDATE estimate SET worker = ? WHERE id = ? ";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, workerid);
			pstatement.setInt(2, estid);
			pstatement.executeUpdate();
		}
	}

}

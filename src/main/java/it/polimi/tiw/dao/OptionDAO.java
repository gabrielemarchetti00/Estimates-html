package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Option;

public class OptionDAO {
	private Connection con;

	public OptionDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Option> findOptionsByProduct(int productId) throws SQLException {
		List<Option> options = new ArrayList<Option>();
		String query = "SELECT * FROM optionn WHERE prodid = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, productId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Option opt = new Option();
				opt.setId(result.getInt("id"));
				opt.setName(result.getString("name"));
				opt.setType(result.getString("type"));
				options.add(opt);
			}
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
		}
		return options;
	}
	
	public List<Option> findOptionsByEstimate(int estimateid) throws SQLException {
		List<Option> options = new ArrayList<Option>();
		String query = "SELECT O.id, O.name, O.type FROM optionn O JOIN estopts E ON E.optid = O.id WHERE E.estid = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, estimateid);
			result = pstatement.executeQuery();
			while (result.next()) {
				Option opt = new Option();
				opt.setId(result.getInt("id"));
				opt.setName(result.getString("name"));
				opt.setType(result.getString("type"));
				options.add(opt);
			}
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
		}
		return options;
	}
	
}

package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Estopts;

public class EstoptsDAO {
	private Connection con;

	public EstoptsDAO(Connection connection) {
		this.con = connection;
	}
	
	public void createEstopts(int estid, int optid) throws SQLException {
		String query = "INSERT INTO estopts (estid, optid) VALUES(?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, estid);
			pstatement.setInt(2, optid);
			pstatement.executeUpdate();
		}
	}
	
	public List<Estopts> findEstoptsByEstimate(int estimateid) throws SQLException {
		List<Estopts> estopts = new ArrayList<Estopts>();
		String query = "SELECT * FROM estopts WHERE estid = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, estimateid);
			result = pstatement.executeQuery();
			while (result.next()) {
				Estopts estopt = new Estopts();
				estopt.setId(result.getInt("id"));
				estopt.setOptId(result.getInt("optid"));
				estopts.add(estopt);
			}
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
		}
		return estopts;
	}
}
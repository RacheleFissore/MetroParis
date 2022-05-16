package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.Connessione;
import it.polito.tdp.metroparis.model.CoppiaId;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}

	public boolean isFermataConnesse(Fermata partenza, Fermata arrivo) {
		String sqlString = "SELECT COUNT(*) AS cnt "
				+ "FROM connessione "
				+ "WHERE id_stazP = ? "
				+ "AND id_stazA = ?";
		
		Connection connection = DBConnect.getConnection();
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlString);
			statement.setInt(1, partenza.getIdFermata());
			statement.setInt(2, arrivo.getIdFermata());
			ResultSet resultSet = statement.executeQuery();
			resultSet.first(); // Ho solo un elemento, quindi mi posiziono sul primo
			int count = resultSet.getInt("cnt");
			connection.close();
			return count > 0; // Ritorna true solo le due partenze sono connesse tra loro, cioè se il cnt > 0 vuol dire che c'è una connessione
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Errore db", e);
		}
	}

	public List<Integer> getIdFermateConnesse(Fermata partenza) {
		String sqlString = "SELECT id_stazA "
				+ "FROM connessione "
				+ "WHERE id_stazP = ? "
				+ "GROUP BY id_stazA";
		
		Connection connection = DBConnect.getConnection();
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlString);
			statement.setInt(1, partenza.getIdFermata());
			ResultSet resultSet = statement.executeQuery();
			List<Integer> results = new ArrayList<Integer>();
			
			while (resultSet.next()) {
				results.add(resultSet.getInt("id_stazA"));				
			}
			connection.close();
			return results; 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public List<Fermata> getFermateConnesse(Fermata partenza) { // Mi restitiuisce una lista di oggetti della classe Fermata
		final String sqlString = "SELECT id_fermata, nome, coordx, coordy "
				+ "FROM fermata "
				+ "WHERE id_fermata IN ( SELECT id_stazA "
				+ "							FROM connessione "
				+ "							WHERE id_stazP = ? "
				+ "							GROUP BY id_stazA ) "
				+ "ORDER BY nome ASC";
		Connection connection = DBConnect.getConnection();
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlString);
			statement.setInt(1, partenza.getIdFermata());
			ResultSet resultSet = statement.executeQuery();
			List<Fermata> results = new ArrayList<Fermata>();
			
			while (resultSet.next()) {
				Fermata f = new Fermata(resultSet.getInt("id_Fermata"), resultSet.getString("nome"),
						new LatLng(resultSet.getDouble("coordx"), resultSet.getDouble("coordy")));
				results.add(f);
				
			}
			connection.close();
			return results; 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<CoppiaId> getAllFermateConnesse() {
		String sqlString = "SELECT DISTINCT id_stazP, id_stazA "
				+ "FROM connessione";
		
		Connection connection = DBConnect.getConnection();
		
		try {
			PreparedStatement statement = connection.prepareStatement(sqlString);
			ResultSet resultSet = statement.executeQuery();
			List<CoppiaId> results = new ArrayList<CoppiaId>();
			
			while (resultSet.next()) {
				CoppiaId coppiaId = new CoppiaId(resultSet.getInt("id_stazP"), resultSet.getInt("id_stazA"));
				results.add(coppiaId);
				
			}
			connection.close();
			return results; 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

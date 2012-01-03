package com.java.phondeux.team;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class TeamHandler {

	private final ConnectionManager cm;
	private HashMap<String, Integer> idbind;
	
	public TeamHandler(Team parent) throws SQLException, ClassNotFoundException {
		  cm = new ConnectionManager("team.db");
		  idbind = new HashMap<String, Integer>();
		  initTables();
		  initStatements();
		  populateTable();
	}
	
	public ConnectionManager ConnectionManager() {
	   return cm;
	}
	
	private void initTables() throws SQLException {
		cm.executeUpdate("create table if not exists teams (id INTEGER PRIMARY KEY, name CHAR(8), namelc CHAR(8), desc TEXT, motd TEXT)");
		cm.executeUpdate("create table if not exists players (id INTEGER PRIMARY KEY, name TEXT, teamid INTEGER);");
	}
	
	private void initStatements() throws SQLException {
		cm.prepareStatement("createTeam", "insert into teams (name, namelc) values (?, ?);");
		cm.prepareStatement("getLatestTeam", "select * from teams order by id desc limit 0, 1;");
		cm.prepareStatement("getTeam", "select * from teams where namelc=?;");
		cm.prepareStatement("deleteTeam", "delete from teams where namelc=?;");
	}
	
	private void populateTable() throws SQLException {
		ResultSet rs = cm.executeQuery("select * from teams");
		String name;
		int id;
		
		do {
			name = rs.getString("namelc");
			id = rs.getInt("id");
			idbind.put(name, id);
		} while (rs.next());
		
		rs.close();
	}
	
	public void close(boolean graceful) throws SQLException {
	      if (graceful) {
          cm.conn.setAutoCommit(false);
          cm.conn.commit();
       }
       cm.prep.clear();
       cm.conn.close();
    }
	
	//---------------------Team methods
	
	/**
	 * Create a team
	 * @param name the name of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamCreate(String name) throws SQLException {
		if (teamExists(name)) return false;
		
		cm.getPreparedStatement("createTeam").setString(1, name);
		cm.getPreparedStatement("createTeam").setString(2, name.toLowerCase());
		cm.executePreparedUpdate("createTeam");
		
		ResultSet rs = cm.executePreparedQuery("getLatestTeam");
		int id = rs.getInt("id");
		rs.close();
		
		idbind.put(name.toLowerCase(), id);
		
		return true;
	}
	
	/**
	 * Delete a team
	 * @param name the name of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamDelete(String name) throws SQLException {
		if (!teamExists(name)) return false;
		
		cm.getPreparedStatement("deleteTeam").setString(1, name.toLowerCase());
		cm.executePreparedUpdate("deleteTeam");
		
		idbind.remove(name.toLowerCase());
		
		return true;
	}
	
	/**
	 * Get the id of a team
	 * @param name the name of the team
	 * @return the id of the team, or null if the team doesn't exist
	 */
	public Integer teamGetID(String name) {
		return idbind.get(name);
	}
	
	/**
	 * Check if a team exists
	 * @param name the name of the team
	 * @return true if exists
	 */
	public boolean teamExists(String name) throws SQLException {
		return idbind.containsKey(name.toLowerCase());
	}
	
	/**
	 * Set the description for a team
	 * @param name the name of the team
	 * @param description the description of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamSetDescription(String name, String description) throws SQLException {
		return false;
	}
	
	/**
	 * Get the description of a team
	 * @param name the name of the team
	 * @return the description, or null if unsuccessful
	 * @throws SQLException
	 */
	public String teamGetDescription(String name) throws SQLException {
		return null;
	}
	
	/**
	 * Set the message of the day for a team
	 * @param name the name of the team
	 * @param motd the motd of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamSetMotd(String name, String motd) throws SQLException {
		return false;
	}
	
	/**
	 * Get the message of the day for a team
	 * @param name the name of the team
	 * @return the motd, or null if unsuccessful
	 */
	public String teamGetMotd(String name) {
		return null;
	}
	
	//---------------------Player methods
	
	/**
	 * Create a player
	 * @param name the name of the player
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean playerCreate(String name) throws SQLException {
		return false;
	}
	
	/**
	 * Delete a player
	 * @param name the name of the player
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean playerDelete(String name) throws SQLException {
		return false;
	}
	
	/**
	 * Check if a player exists
	 * @param name the name of the player
	 * @return true if exists
	 * @throws SQLException
	 */
	public boolean playerExists(String name) throws SQLException {
		return false;
	}
}

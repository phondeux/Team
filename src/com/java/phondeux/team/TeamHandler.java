package com.java.phondeux.team;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TeamHandler {
	private final ConnectionManager cm;
	private HashMap<String, Integer> idbindteam;
	private HashMap<String, Integer> idbindplayer;
	
	public TeamHandler(Team parent, String database, String user, String password) throws SQLException, ClassNotFoundException {
		  cm = new ConnectionManager(database, user, password);
		  idbindteam = new HashMap<String, Integer>();
		  idbindplayer = new HashMap<String, Integer>();
		  initTables();
		  initStatements();
		  populateMap();
	}
	
	public ConnectionManager ConnectionManager() {
	   return cm;
	}
	
	private void initTables() throws SQLException {
		cm.executeUpdate("create table if not exists teams (id INT UNSIGNED NOT NULL AUTO_INCREMENT, PRIMARY KEY (id), name CHAR(8), descr TEXT, motd TEXT);");
		cm.executeUpdate("create table if not exists players (id INT UNSIGNED NOT NULL AUTO_INCREMENT, PRIMARY KEY (id), name TEXT, teamid INT UNSIGNED, teamstatus TINYINT UNSIGNED);");
	}
	
	private void initStatements() throws SQLException {
		cm.prepareStatement("createTeam", "insert into teams (name, descr, motd) values (?, 'Description is unset', 'Motd is unset');");
		cm.prepareStatement("getLatestTeam", "select * from teams order by id desc limit 0, 1;");
		cm.prepareStatement("getTeam", "select * from teams where id=?;");
		cm.prepareStatement("getTeamAll", "select * from teams;");
		cm.prepareStatement("deleteTeam", "delete from teams where id=?;");
		cm.prepareStatement("setTeamDescription", "update teams set desc=? where id=?;");
		cm.prepareStatement("getTeamDescription", "select descr from teams where id=?;");
		cm.prepareStatement("setTeamMotd", "update teams set motd=? where id=?;");
		cm.prepareStatement("getTeamMotd", "select motd from teams where id=?;");
		cm.prepareStatement("getTeamList", "select name from teams;");
		
		cm.prepareStatement("createPlayer", "insert into players (name, teamid, teamstatus) values (?, 0, 0);");
		cm.prepareStatement("getLatestPlayer", "select * from players order by id desc limit 0, 1;");
		cm.prepareStatement("getPlayer", "select * from players where id=?;");
		cm.prepareStatement("getPlayerAll", "select * from players;");
		cm.prepareStatement("setPlayerTeam", "update players set teamid=? where id=?;");
		cm.prepareStatement("getPlayerTeam", "select teamid from players where id=?;");
		//Statuses - 0:Not on a team, 1:Member, 2:Mod, 3:Owner
		cm.prepareStatement("setPlayerStatus", "update players set teamstatus=? where id=?;");
		cm.prepareStatement("getPlayerStatus", "select teamstatus from players where id=?;");
	}
	
	private void populateMap() throws SQLException {
		ResultSet rs;
		String name;
		int id;
		
		rs = cm.executePreparedQuery("getTeamAll");
		if (rs.first()) {
			do {
				name = rs.getString("name").toLowerCase();
				id = rs.getInt("id");
				idbindteam.put(name, id);
			} while (rs.next());
		}
		rs.close();
		
		rs = cm.executePreparedQuery("getPlayerAll");
		if (rs.first()) {
			do {
				name = rs.getString("name").toLowerCase();
				id = rs.getInt("id");
				idbindplayer.put(name, id);
			} while (rs.next());
		}
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
	 * Gets a team
	 * @param name the name of the team
	 * @return a resultset containing all columns
	 * @throws SQLException
	 */
	public ResultSet teamGet(String name) throws SQLException {
		return (teamGet(teamGetID(name)));
	}
	
	/**
	 * Gets a team
	 * @param id the id of the team
	 * @return a resultset containing all columns
	 * @throws SQLException
	 */
	public ResultSet teamGet(Integer id) throws SQLException {
		cm.getPreparedStatement("getTeam").setInt(1, id);
		return cm.executePreparedQuery("getTeam");
	}
	
	/**
	 * Create a team
	 * @param name the name of the team
	 * @return the team id if successful, otherwise -1
	 * @throws SQLException
	 */
	public int teamCreate(String name) throws SQLException {
		if (teamExists(name)) return -1;
		
		cm.getPreparedStatement("createTeam").setString(1, name);
		cm.executePreparedUpdate("createTeam");
		
		ResultSet rs = cm.executePreparedQuery("getLatestTeam");
		rs.first();
		int id = rs.getInt("id");
		rs.close();
		
		idbindteam.put(name.toLowerCase(), id);
		
		return id;
	}
	
	/**
	 * Delete a team
	 * @param name the name of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamDelete(String name) throws SQLException {
		return teamDelete(teamGetID(name));
	}
	
	/**
	 * Delete a team
	 * @param id the id of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamDelete(Integer id) throws SQLException {
		if (!teamExists(id)) return false;
		
		cm.getPreparedStatement("deleteTeam").setInt(1, id);
		cm.executePreparedUpdate("deleteTeam");
		
		idbindteam.keySet().remove(id);
		
		return true;
	}
	
	/**
	 * Get the id of a team
	 * @param name the name of the team
	 * @return the id of the team, or null if the team doesn't exist
	 */
	public Integer teamGetID(String name) {
		return idbindteam.get(name.toLowerCase());
	}
	
	/**
	 * Check if a team exists
	 * @param name the name of the team
	 * @return true if exists
	 */
	public boolean teamExists(String name) {
		if (name == null) return false;
		return idbindteam.containsKey(name.toLowerCase());
	}
	
	/**
	 * Check if a team exists
	 * @param id the id of the team
	 * @return true if exists
	 */
	public boolean teamExists(Integer id) {
		if (id == null) return false;
		return idbindteam.containsValue(id);
	}
	
	/**
	 * Set the description for a team
	 * @param name the name of the team
	 * @param description the description of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamSetDescription(String name, String description) throws SQLException {
		return teamSetDescription(teamGetID(name), description);
	}
	
	/**
	 * Set the description for a team
	 * @param id the id of the team
	 * @param description the description of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamSetDescription(Integer id, String description) throws SQLException {
		if (!teamExists(id)) return false;
		
		cm.getPreparedStatement("setTeamDescription").setString(1, description);
		cm.getPreparedStatement("setTeamDescription").setInt(2, id);
		cm.executePreparedUpdate("setTeamDescription");
		
		return true;
	}
	
	/**
	 * Get the description of a team
	 * @param name the name of the team
	 * @return the description, or null if unsuccessful
	 * @throws SQLException
	 */
	public String teamGetDescription(String name) throws SQLException {
		return teamGetDescription(teamGetID(name));
	}
	
	/**
	 * Get the description of a team
	 * @param id the id of the team
	 * @return the description, or null if unsuccessful
	 * @throws SQLException
	 */
	public String teamGetDescription(Integer id) throws SQLException {
		if (!teamExists(id)) return null;
		cm.getPreparedStatement("getTeamDescription").setInt(1, id);
		ResultSet rs = cm.executePreparedQuery("getTeamDescription");
		rs.first();
		String desc = rs.getString("descr");
		rs.close();
		return desc;
	}
	
	/**
	 * Set the message of the day for a team
	 * @param name the name of the team
	 * @param motd the motd of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamSetMotd(String name, String motd) throws SQLException {
		return teamSetMotd(teamGetID(name), motd);
	}
	
	/**
	 * Set the message of the day for a team
	 * @param id the id of the team
	 * @param motd the motd of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamSetMotd(Integer id, String motd) throws SQLException {
		if (!teamExists(id)) return false;

		cm.getPreparedStatement("setTeamMotd").setString(1, motd);
		cm.getPreparedStatement("setTeamMotd").setInt(2, id);
		cm.executePreparedUpdate("setTeamMotd");
		
		return true;
	}
	
	/**
	 * Get the message of the day for a team
	 * @param name the name of the team
	 * @return the motd, or null if unsuccessful
	 */
	public String teamGetMotd(String name) throws SQLException {
		return teamGetMotd(teamGetID(name));
	}
	
	/**
	 * Get the message of the day for a team
	 * @param id the id of the team
	 * @return the motd, or null if unsuccessful
	 */
	public String teamGetMotd(Integer id) throws SQLException {
		if (!teamExists(id)) return null;
		
		cm.getPreparedStatement("getTeamMotd").setInt(1, id);
		ResultSet rs = cm.executePreparedQuery("getTeamMotd");
		rs.first();
		String motd = rs.getString("motd");
		rs.close();
		return motd;
	}
	
	/**
	 * Get a list of all the teams
	 * @return an arraylist containing the name of every team
	 * @throws SQLException
	 */
	public ArrayList<String> teamGetList() throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		
		ResultSet rs = cm.executePreparedQuery("getTeamList");
		if (rs.first()) {
			do {
				list.add(rs.getString("name"));
			} while (rs.next());
		}
		
		return list;
	}
	
	//---------------------Player methods
	
	/**
	 * Get the id of a player
	 * @param name the name of the player
	 * @return the id of the player, or null if the player doesn't exist
	 */
	public Integer playerGetID(String name) {
		if (!idbindplayer.containsKey(name.toLowerCase())) return null;
		return idbindplayer.get(name.toLowerCase());
	}
	
	/**
	 * Check if a player exists
	 * @param name the name of the player
	 * @return true if exists
	 */
	public boolean playerExists(String name) {
		if (name == null) return false;
		return (idbindplayer.containsKey(name.toLowerCase()));
	}
	
	/**
	 * Check if a player exists
	 * @param id the id of the player
	 * @return true if exists
	 */
	public boolean playerExists(Integer id) {
		if (id == null) return false;
		return (idbindplayer.containsValue(id));
	}
	
	/**
	 * Create a player
	 * @param name the name of the player
	 * @return the player id if successful, otherwise -1
	 * @throws SQLException
	 */
	public Integer playerCreate(String name) throws SQLException {
		if (playerExists(name)) return -1;
		
		cm.getPreparedStatement("createPlayer").setString(1, name);
		cm.executePreparedUpdate("createPlayer");
		
		ResultSet rs = cm.executePreparedQuery("getLatestPlayer");
		rs.first();
		int id = rs.getInt("id");
		rs.close();
		
		idbindplayer.put(name.toLowerCase(), id);
		
		return id;
	}
	
	/**
	 * Gets a player
	 * @param name the name of the player
	 * @return a resultset containing all columns
	 * @throws SQLException
	 */
	public ResultSet playerGet(String name) throws SQLException {
		return playerGet(playerGetID(name));
	}
	
	/**
	 * Gets a player
	 * @param id the id of the player
	 * @return a resultset containing all columns
	 * @throws SQLException
	 */
	public ResultSet playerGet(Integer id) throws SQLException {
		cm.getPreparedStatement("playerGet").setInt(1, id);
		return cm.executePreparedQuery("playerGet");
	}
	
	/**
	 * Gets the teamid of a player
	 * @param name the name of the player
	 * @return the id of the team the player is on
	 * @throws SQLException
	 */
	public Integer playerGetTeam(String name) throws SQLException {
		return playerGetTeam(playerGetID(name));
	}
	
	/**
	 * Gets the teamid of a player
	 * @param id the id of the player
	 * @return the id of the team the player is on
	 * @throws SQLException
	 */
	public Integer playerGetTeam(Integer id) throws SQLException {
		cm.getPreparedStatement("getPlayerTeam").setInt(1, id);
		ResultSet rs = cm.executePreparedQuery("getPlayerTeam");
		rs.first();
		int teamid = rs.getInt("teamid");
		rs.close();
		return teamid;
	}
	
	/**
	 * Set the team of a player
	 * @param name the name of the player
	 * @param teamid the id of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean playerSetTeam(String name, Integer teamid) throws SQLException {
		return playerSetTeam(playerGetID(name), teamid);
	}
	
	/**
	 * Set the team of a player
	 * @param id the id of the player
	 * @param teamid the id of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean playerSetTeam(Integer id, Integer teamid) throws SQLException {
		if (!playerExists(id)) return false;
		cm.getPreparedStatement("setPlayerTeam").setInt(1, teamid);
		cm.getPreparedStatement("setPlayerTeam").setInt(2, id);
		cm.executePreparedUpdate("setPlayerTeam");
		return true;
	}
	
	public Integer playerGetStatus(String name) throws SQLException {
		return playerGetStatus(playerGetID(name));
	}
	
	public Integer playerGetStatus(Integer id) throws SQLException {
		cm.getPreparedStatement("getPlayerStatus").setInt(1, id);
		ResultSet rs = cm.executePreparedQuery("getPlayerStatus");
		rs.first();
		int status = rs.getInt("teamstatus");
		rs.close();
		return status;
	}
	
	public boolean playerSetStatus(String name, Integer status) throws SQLException {
		return playerSetStatus(playerGetID(name), status);
	}
	
	public boolean playerSetStatus(Integer id, Integer status) throws SQLException {
		if (!playerExists(id)) return false;
		cm.getPreparedStatement("setPlayerStatus").setInt(1, status);
		cm.getPreparedStatement("setPlayerStatus").setInt(2, id);
		cm.executePreparedUpdate("setPlayerStatus");
		return true;
	}
}

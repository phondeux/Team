package com.java.phondeux.team;

//import java.sql.ResultSet;
import java.sql.SQLException;


public class TeamHandler {

	private final ConnectionManager cm;
	
	public TeamHandler(Team parent) 
	   throws SQLException, ClassNotFoundException {
		  cm = new ConnectionManager("team.db");
		  cm.executeUpdate("create table if not exists teams (key INTEGER PRIMARY KEY, name TEXT, description TEXT)");
		  initStatements();
	}
	
	public ConnectionManager ConnectionManager(){
	   return this.cm;
	}
	
	private void initStatements() throws SQLException {
		cm.prepareStatement("createTeam", "insert into teams (name) values (?);");
		cm.prepareStatement("deleteTeam", "delete from teams where name=?;");
	}
	
	public void close(boolean graceful)
       throws SQLException {
	      if (graceful) {
          this.cm.conn.setAutoCommit(false);
          this.cm.conn.commit();
       }
       this.cm.prep.clear();
       this.cm.conn.close();
    }
	
	//---------------------Team methods
	
	/**
	 * Create a team
	 * @param name the name of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamCreate(String name) throws SQLException {
		return false;
	}
	
	/**
	 * Delete a team
	 * @param name the name of the team
	 * @return true if successful
	 * @throws SQLException
	 */
	public boolean teamDelete(String name) throws SQLException {
		return false;
	}
	
	/**
	 * Check if a team exists
	 * @param name the name of the team
	 * @return true if exists
	 * @throws SQLException
	 */
	public boolean teamExists(String name) throws SQLException {
		return false;
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

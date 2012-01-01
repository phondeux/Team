package com.java.phondeux.team;

//import java.sql.ResultSet;
import java.sql.SQLException;


public class TeamDBHandler {

	private final ConnectionManager cm;
	
	public TeamDBHandler(Team parent) 
	   throws SQLException, ClassNotFoundException {
		  cm = new ConnectionManager("team.db");
		  cm.executeUpdate("create table if not exists teams (key INTEGER PRIMARY KEY, name TEXT)");
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
	
	/*
	 *  Team routines
	 *    createTeam
	 *    deleteTeam
	 *    existsTeam
	 */
	/**
	 * Create a new home entry
	 * @param Teamname
	 * @throws SQLException
	 */
	public void createTeam(String teamName) throws SQLException {
	}

	/**
	 * Sees if a player is subscribed to a particular list
	 * @param Teamname
	 * @return True if the team exists, otherwise false
	 * @throws SQLException
	 */
}

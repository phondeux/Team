package com.java.phondeux.team;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class ConnectionManager {
	
	public Connection conn;
	protected final int pageSize = 4;
	protected final HashMap<String,PreparedStatement> prep = new HashMap<String,PreparedStatement>();
	
	/**
	 * SQLite: Initializes the database and connection to it, keeps track of prepared statements
	 * @param database The database file to use, should end in .db
	 * @throws SQLException If there is an error connecting to the database
	 * @throws ClassNotFoundException If the driver for SQLite is not found
	 */
	public ConnectionManager(String database) throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + database);
	}
	
	/**
	 * MySQL: Establishes the connection to the database, keeps track of prepared statements
	 * @param database The url of the database
	 * @param user The user to the database
	 * @param password The password, if applicable
	 * @throws SQLException If there is an error connecting to the database
	 * @throws ClassNotFoundException If the driver for MySQL is not found
	 */
	public ConnectionManager(String database, String user, String password) throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://" + database, user, password);
	}
	
	/**
	 * Gets the database connection for direct manipulation
	 * @return The raw connection
	 */
	public Connection getConnection() {
		return conn;
	}
	
	/**
	 * Prepares a statement for later usage, this can be much faster if you use a particular statement
	 * often
	 * @param handle The handle with which you reference the statement
	 * @param sql The SQL string used in the statement
	 * @throws SQLException
	 */
	public void prepareStatement(String handle, String sql) throws SQLException {
		prep.put(handle, conn.prepareStatement(sql));
	}
	
	/**
	 * Gets the prepared statement tied to the given handle
	 * @param handle The handle of the statement to get
	 * @return The statement or null if none exists with that handle
	 */
	public PreparedStatement getPreparedStatement(String handle) {
		return prep.get(handle);
	}
	
	/**
	 * Executes a prepared statement
	 * @param handle The handle of the statement to execute
	 * @return ResultSet if the first result is a ResultSet object;
	 *         Update count if the first result is an update count or there is no result;
	 *         null if the statement wasn't found
	 * @throws SQLException
	 */
	public Object executePreparedStatement(String handle) throws SQLException {
		if (prep.containsKey(handle)) {
			if (prep.get(handle).execute()) {
				return prep.get(handle).getResultSet();
			} else {
				return prep.get(handle).getUpdateCount();
			}
		} else {
			return null;
		}
	}
	
	/**
	 * Executes a prepared update
	 * @param handle The handle of the update to execute
	 * @return either (1) the row count for SQL Data Manipulation Language 
	 *         (DML) statements, (2) 0 for SQL statements that return nothing
	 *         or (3) -1 if the handle doesn't exist
	 * @throws SQLException
	 */
	public int executePreparedUpdate(String handle) throws SQLException {
		if (prep.containsKey(handle)) {
			return prep.get(handle).executeUpdate();
		} else {
			return -1;
		}
	}
	
	/**
	 * Executes a prepared query
	 * @param handle The handle of the query to execute
	 * @return The ResultSet or null if the handle doesn't exist
	 * @throws SQLException
	 */
	public ResultSet executePreparedQuery(String handle) throws SQLException {
		if (prep.containsKey(handle)) {
			return prep.get(handle).executeQuery();
		} else {
			return null;
		}
	}
	
	/**
	 * Executes a raw statement
	 * @param sql Any SQL statement
	 * @return ResultSet if the first result is a ResultSet;
	 *         Update count if the first result is an update count or there is no result;
	 * @throws SQLException
	 */
	public Object executeStatement(String sql) throws SQLException {
		Statement stat = conn.createStatement();
		Object out = null;
		if (stat.execute(sql)) {
			out = stat.getResultSet();
		} else {
			out = stat.getUpdateCount();
		}
		stat.close();
		return out;
	}
	
	/**
	 * Executes the given SQL statement, which may be an INSERT, UPDATE, or DELETE statement 
	 * or an SQL statement that returns nothing, such as an SQL DDL statement.
	 * @param sql SQL Data Manipulation Language (DML) statement, 
	 *        such as INSERT, UPDATE or DELETE; or an SQL statement 
	 *        that returns nothing, such as a DDL statement. 
	 * @return either (1) the row count for SQL Data Manipulation Language 
	 *         (DML) statements or (2) 0 for SQL statements that return nothing 
	 * @throws SQLException
	 */
	public int executeUpdate(String sql) throws SQLException {
		Statement stat = conn.createStatement();
		int out = stat.executeUpdate(sql);
		stat.close();
		return out;
	}
	
	/**
	 * Executes the given SQL statement, which returns a single ResultSet object. 
	 * @param sql an SQL statement to be sent to the database, typically a static SQL SELECT statement
	 * @return a ResultSet object that contains the data produced by the given query; never null
	 * @throws SQLException
	 */
	public ResultSet executeQuery(String sql) throws SQLException {
		Statement stat = conn.createStatement();
		return stat.executeQuery(sql);
	}
}
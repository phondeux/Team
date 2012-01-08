package com.java.phondeux.team;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class StatsHandler {
	protected final Team parent;
	protected final ConnectionManager cm;
	
	public StatsHandler(Team parent, ConnectionManager cm) throws SQLException {
		this.parent = parent;
		this.cm = cm;
		
		initStatements();
	}
	
	private void initStatements() throws SQLException {
		cm.prepareStatement("statsPlayerNumKills", "select count(*) as 'count' from events where type=6 and parent=?;");
		cm.prepareStatement("statsPlayerNumDeaths", "select count(*) as 'count' from events where type=6 and child=?;");
		cm.prepareStatement("statsPlayerNumPvpDeaths", "select count(*) as 'count' from events where type=6 and parent!=0 and child=?;");
		cm.prepareStatement("statsPlayerDeath", "select * from events where type=6 and child=? order by id desc limit ?, 1;");
		cm.prepareStatement("statsPlayerPvpDeath", "select * from events where type=6 and parent!=0 and child=? order by id desc limit ?, 1;");
		cm.prepareStatement("statsPlayerKill", "select * from events where type=6 and parent=? order by id desc limit ?, 1;");
		cm.prepareStatement("statsPlayerLastLogin", "select * from events where type=11 and parent=? order by id desc limit 0, 1;");
	}
	
	public PlayerStats GetPlayerStats(Integer id) {
		return new PlayerStats(id);
	}
	
	public TeamStats GetTeamStats(Integer id) {
		return new TeamStats(id);
	}
	
	protected class PlayerStats {
		private final int id;
		
		public PlayerStats(Integer id) {
			this.id = id;
		}
		
		/**
		 * Get the number of murders this player has committed
		 */
		public Integer NumKills() {
			try {
				cm.getPreparedStatement("statsPlayerNumKills").setInt(1, id);
				ResultSet rs = cm.executePreparedQuery("statsPlayerNumKills");
				if (!rs.first()) return 0;
				int kills = rs.getInt("count");
				rs.close();
				return kills;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * Get the number of times this player has died in total
		 */
		public Integer NumDeaths() {
			try {
				cm.getPreparedStatement("statsPlayerNumDeaths").setInt(1, id);
				ResultSet rs = cm.executePreparedQuery("statsPlayerNumDeaths");
				if (!rs.first()) return 0;
				int deaths = rs.getInt("count");
				rs.close();
				return deaths;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * Get the number of times this player has died as a result of someone stabbing him
		 */
		public Integer NumPvpDeaths() {
			try {
				cm.getPreparedStatement("statsPlayerNumPvpDeaths").setInt(1, id);
				ResultSet rs = cm.executePreparedQuery("statsPlayerNumPvpDeaths");
				if (!rs.first()) return 0;
				int deaths = rs.getInt("count");
				rs.close();
				return deaths;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * Get the last time this player logged in
		 * @return the Date of the last login, or null if the player is online
		 */
		public Date LastLogin() {
			if (parent.getServer().getPlayer(parent.th.playerGetName(id)).isOnline()) return null;
			Date date = null;
			
			try {
				cm.getPreparedStatement("statsPlayerLastLogin").setInt(1, id);
				ResultSet rs = cm.executePreparedQuery("statsPlayerLastLogin");
				if (!rs.first()) return null;
				date = rs.getDate("timestamp");
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return date;
		}
		
		/**
		 * Get detailed information on a death caused by anything
		 * @param num the deaths number, where 0 is the most recent
		 * @return a PlayerDeath object describing the event
		 */
		public PlayerDeath Death(Integer num) {
			PlayerDeath ret = new PlayerDeath(num, false);
			return ret.data == null ? null : ret;
		}
		
		/**
		 * Get detailed information on a death caused by another player
		 * @param num the deaths number, where 0 is the most recent
		 * @return a PlayerDeath object describing the event
		 */
		public PlayerDeath PvpDeath(Integer num) {
			PlayerDeath ret = new PlayerDeath(num, true);
			return ret.data == null ? null : ret;
		}
		
		/**
		 * Get detailed information on a kill
		 * @param num the kills number, where 0 is the most recent
		 * @return a PlayerKill object describing the event
		 */
		public PlayerKill Kill(Integer num) {
			PlayerKill ret = new PlayerKill(num);
			return ret.data == null ? null : ret;
		}
		
		protected class PlayerDeath {
			public Integer killerid = null;
			public String data = null;
			public Date timestamp = null;
			
			public PlayerDeath(Integer num, boolean pvp) {
				try {
					String query = pvp ? "statsPlayerPvpDeath" : "statsPlayerDeath";
					cm.getPreparedStatement(query).setInt(1, id);
					cm.getPreparedStatement(query).setInt(2, num);
					ResultSet rs = cm.executePreparedQuery(query);
					if (!rs.first()) return;
					killerid = rs.getInt("parent");
					data = rs.getString("data");
					timestamp = rs.getTimestamp("timestamp");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		protected class PlayerKill {
			public Integer victimid = null;
			public String data = null;
			public Date timestamp = null;
			
			public PlayerKill(Integer num) {
				try {
					cm.getPreparedStatement("statsPlayerKill").setInt(1, id);
					cm.getPreparedStatement("statsPlayerKill").setInt(2, num);
					ResultSet rs = cm.executePreparedQuery("statsPlayerKill");
					if (!rs.first()) return;
					victimid = rs.getInt("child");
					data = rs.getString("data");
					timestamp = rs.getTimestamp("timestamp");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected class TeamStats {
		private final int id;
		private ArrayList<PlayerStats> members = null;
		
		public TeamStats(Integer teamid) {
			id = teamid;
			members = new ArrayList<PlayerStats>();
			try {
				ArrayList<Integer> memberid = parent.th.playersGetIdOnTeam(id);
				for (Integer i : memberid) {
					members.add(GetPlayerStats(i));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Get the number of members on the given team
		 */
		public Integer NumMembers() {
			return members.size();
		}
		
		/**
		 * Get the total number of kills of everyone on the team
		 */
		public Integer NumKills() {
			Integer ret = 0;
			
			for (PlayerStats stats : members) {
				ret += stats.NumKills();
			}
			
			return ret;
		}
		
		/**
		 * Get the total number of deaths of everyone on the team
		 */
		public Integer NumDeaths() {
			Integer ret = 0;
			
			for (PlayerStats stats : members) {
				ret += stats.NumDeaths();
			}
			
			return ret;
		}
		
		/**
		 * Get the total number of deaths caused by another player of everyone on the team
		 */
		public Integer NumPvpDeaths() {
			Integer ret = 0;
			
			for (PlayerStats stats : members) {
				ret += stats.NumPvpDeaths();
			}
			
			return ret;
		}
	}
}

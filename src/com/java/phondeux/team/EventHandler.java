package com.java.phondeux.team;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;

public class EventHandler {
	protected final Team parent;
	protected final ConnectionManager cm;
	private final EventFactory ef;
	private EnumMap<Type, ArrayList<EventCallback>> callbackmap;
	
	public enum Type {TeamCreate, TeamDisband, TeamOpen, TeamClose,
					  PlayerJoin, PlayerLeave, PlayerDeath, PlayerInvite,
					  PlayerDeinvite, TeamMotd, PlayerKicked};
	
	public EventHandler(Team parent, ConnectionManager cm) throws SQLException {
		this.parent = parent;
		this.cm = cm;
		callbackmap = new EnumMap<Type, ArrayList<EventCallback>>(Type.class);
		ef = new EventFactory();
		initTables();
		initStatements();
	}
	
	private void initTables() throws SQLException {
		cm.executeUpdate("create table if not exists events (id INT UNSIGNED NOT NULL AUTO_INCREMENT, PRIMARY KEY (id), type TINYINT UNSIGNED, parent INT UNSIGNED, child INT UNSIGNED, data TEXT, timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");
	}
	
	private void initStatements() throws SQLException {
		//Parent: player id, child: team id
		cm.prepareStatement("newEventTeamCreate", "insert into events (type, parent, child) values (0, ?, ?);");
		//Parent: player id, child: team id
		cm.prepareStatement("newEventTeamDisband", "insert into events (type, parent, child) values (1, ?, ?);");
		//Parent: player id, child: team id
		cm.prepareStatement("newEventTeamOpen", "insert into events (type, parent, child) values (2, ?, ?);");
		//Parent: player id, child: team id
		cm.prepareStatement("newEventTeamClose", "insert into events (type, parent, child) values (3, ?, ?);");
		//Parent: player id, child: team id
		cm.prepareStatement("newEventPlayerJoin", "insert into events (type, parent, child) values (4, ?, ?);");
		//Parent: player id, child: team id
		cm.prepareStatement("newEventPlayerLeave", "insert into events (type, parent, child) values (5, ?, ?);");
		//Parent: killer id, child: victim id, data: cause
		cm.prepareStatement("newEventPlayerDeath", "insert into events (type, parent, child, data) values (6, ?, ?, ?);");
		//Parent: player id, child: team id, data: inviter id
		cm.prepareStatement("newEventPlayerInvite", "insert into events (type, parent, child, data) values (7, ?, ?, ?);");
		//Parent: player id, child: team id, data: deinviter id
		cm.prepareStatement("newEventPlayerDeinvite", "insert into events (type, parent, child, data) values (8, ?, ?, ?);");
		//Parent: player id, child: team id, data: motd
		cm.prepareStatement("newEventTeamMotd", "insert into events (type, parent, child, data) values (9, ?, ?, ?);");
		//Parent: player id, child: team id, data: kicker id
		cm.prepareStatement("newEventPlayerKicked", "insert into events (type, parent, child, data) values (10, ?, ?, ?);");
	}
	
	public interface EventCallback {
		void run(int parent, int child, String data);
	}
	
	public void RegisterCallback(EventCallback callback, Type type) {
		if (callbackmap.get(type) == null) {
			callbackmap.put(type, new ArrayList<EventCallback>());
		}
		
		callbackmap.get(type).add(callback);
	}
	
	private void DoCallback(Type type, int parent, int child, String data) {
		if (callbackmap.get(type) == null) return;
		for (EventCallback callback : callbackmap.get(type)) {
			callback.run(parent, child, data);
		}
	}
	
	public EventFactory CreateEvent() {
		return ef;
	}
	
	protected class EventFactory {
		public void TeamCreate(int playerid, int teamid) throws SQLException {
			DoCallback(Type.TeamCreate, playerid, teamid, null);
			cm.getPreparedStatement("newEventTeamCreate").setInt(1, playerid);
			cm.getPreparedStatement("newEventTeamCreate").setInt(2, teamid);
			cm.executePreparedUpdate("newEventTeamCreate");
		}
		
		public void TeamDisband(int playerid, int teamid) throws SQLException {
			DoCallback(Type.TeamDisband, playerid, teamid, null);
			cm.getPreparedStatement("newEventTeamDisband").setInt(1, playerid);
			cm.getPreparedStatement("newEventTeamDisband").setInt(2, teamid);
			cm.executePreparedUpdate("newEventTeamDisband");
		}
		
		public void TeamOpen(int playerid, int teamid) throws SQLException {
			DoCallback(Type.TeamOpen, playerid, teamid, null);
			cm.getPreparedStatement("newEventTeamOpen").setInt(1, playerid);
			cm.getPreparedStatement("newEventTeamOpen").setInt(2, teamid);
			cm.executePreparedUpdate("newEventTeamOpen");
		}
		
		public void TeamClose(int playerid, int teamid) throws SQLException {
			DoCallback(Type.TeamClose, playerid, teamid, null);
			cm.getPreparedStatement("newEventTeamClose").setInt(1, playerid);
			cm.getPreparedStatement("newEventTeamClose").setInt(2, teamid);
			cm.executePreparedUpdate("newEventTeamClose");
		}
		
		public void PlayerJoin(int playerid, int teamid) throws SQLException {
			DoCallback(Type.PlayerJoin, playerid, teamid, null);
			cm.getPreparedStatement("newEventPlayerJoin").setInt(1, playerid);
			cm.getPreparedStatement("newEventPlayerJoin").setInt(2, teamid);
			cm.executePreparedUpdate("newEventPlayerJoin");
		}
		
		public void PlayerLeave(int playerid, int teamid) throws SQLException {
			DoCallback(Type.PlayerLeave, playerid, teamid, null);
			cm.getPreparedStatement("newEventPlayerLeave").setInt(1, playerid);
			cm.getPreparedStatement("newEventPlayerLeave").setInt(2, teamid);
			cm.executePreparedUpdate("newEventPlayerLeave");
		}
		
		public void PlayerDeath(int killerid, int victimid, String data) throws SQLException {
			DoCallback(Type.PlayerDeath, killerid, victimid, data);
			cm.getPreparedStatement("newEventPlayerDeath").setInt(1, killerid);
			cm.getPreparedStatement("newEventPlayerDeath").setInt(2, victimid);
			cm.getPreparedStatement("newEventPlayerDeath").setString(3, data);
			cm.executePreparedUpdate("newEventPlayerDeath");
		}
		
		public void PlayerInvite(int playerid, int teamid, Integer inviterid) throws SQLException {
			DoCallback(Type.PlayerInvite, playerid, teamid, inviterid.toString());
			cm.getPreparedStatement("newEventPlayerInvite").setInt(1, playerid);
			cm.getPreparedStatement("newEventPlayerInvite").setInt(2, teamid);
			cm.getPreparedStatement("newEventPlayerInvite").setString(3, inviterid.toString());
			cm.executePreparedUpdate("newEventPlayerInvite");
		}
		
		public void PlayerDeinvite(int playerid, int teamid, Integer deinviterid) throws SQLException {
			DoCallback(Type.PlayerDeinvite, playerid, teamid, deinviterid.toString());
			cm.getPreparedStatement("newEventPlayerDeinvite").setInt(1, playerid);
			cm.getPreparedStatement("newEventPlayerDeinvite").setInt(2, teamid);
			cm.getPreparedStatement("newEventPlayerDeinvite").setString(3, deinviterid.toString());
			cm.executePreparedUpdate("newEventPlayerDeinvite");
		}
		
		public void TeamMotd(int playerid, int teamid, String motd) throws SQLException {
			DoCallback(Type.TeamMotd, playerid, teamid, motd);
			cm.getPreparedStatement("newEventTeamMotd").setInt(1, playerid);
			cm.getPreparedStatement("newEventTeamMotd").setInt(2, teamid);
			cm.getPreparedStatement("newEventTeamMotd").setString(3, motd);
			cm.executePreparedUpdate("newEventTeamMotd");
		}
		
		public void PlayerKicked(int playerid, int teamid, Integer kicker) throws SQLException {
			DoCallback(Type.PlayerKicked, playerid, teamid, kicker.toString());
			cm.getPreparedStatement("newEventPlayerKicked").setInt(1, playerid);
			cm.getPreparedStatement("newEventPlayerKicked").setInt(2, teamid);
			cm.getPreparedStatement("newEventPlayerKicked").setString(3, kicker.toString());
			cm.executePreparedUpdate("newEventPlayerKicked");
		}
	}
}

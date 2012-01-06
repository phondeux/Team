package com.java.phondeux.team;

import java.sql.SQLException;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class TeamPlayerListener extends PlayerListener {
	public Team team;

	public TeamPlayerListener(Team team) {
		this.team = team;
	}
	
	public void onPlayerJoin(final PlayerJoinEvent event) {
		
		if (!team.tdbh.playerExists(event.getPlayer().getName())) {
			try {
				team.tdbh.playerCreate(event.getPlayer().getName());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

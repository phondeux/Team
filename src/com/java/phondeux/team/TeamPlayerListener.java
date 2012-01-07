package com.java.phondeux.team;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class TeamPlayerListener extends PlayerListener {
	public Team team;

	public TeamPlayerListener(Team team) {
		this.team = team;
	}
	
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Integer pID = team.th.playerGetID(player.getName());
		String tMOTD = "";
		
		if (!team.th.playerExists(player.getName())) {
			try {
				team.th.playerCreate(player.getName());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		// Fetch team motd
		//  - Don't display motd if it's empty
		try {
			tMOTD = team.th.teamGetMotd(team.th.playerGetTeam(pID));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (tMOTD.length() != 0) {
			player.sendMessage(tMOTD);
		}
		// check if player has invites and display them as well as a short 'how to join a team' message
	}
}

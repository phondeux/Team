package com.java.phondeux.team;

import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeamPlayerListener implements Listener {
	public Team team;

	public TeamPlayerListener(Team team) {
		this.team = team;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!team.th.playerExists(player.getName())) {
			try {
				team.th.playerCreate(player.getName());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		Integer pID = team.th.playerGetID(player.getName());
		String tMOTD = "";
		
		// Fetch team motd
		//  - Don't display motd if it's empty
		try {
			team.eh.CreateEvent().PlayerConnect(pID);
			tMOTD = team.th.teamGetMotd(team.th.playerGetTeam(pID));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (tMOTD != null && tMOTD.length() != 0) {
			player.sendMessage(tMOTD);
		}
		// check if player has invites and display them as well as a short 'how to join a team' message
		// pre-pend players team name to their chat name
		// Also, pre-pend the team tag (up to eight characters) to their display name
		try {
			if (team.th.playerGetTeam(pID) != 0){
				player.setDisplayName(team.th.teamGetName(team.th.playerGetTeam(pID)) + " " + player.getName());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChat(final PlayerChatEvent event) {
		Integer pID = team.th.playerGetID(event.getPlayer().getName());
		Integer teamID = 0;
		try {
			teamID = team.th.playerGetTeam(pID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (team.th.teamChatter.contains(pID) && teamID != 0) {
			String teamName = team.th.teamGetName(teamID);
			team.th.teamSendToMembers(teamID, event.getPlayer().getName() + ": " + event.getMessage());
			team.log.info("TC : " + teamName + " : " + event.getPlayer().getName() + " " + event.getMessage());
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		try {
			team.eh.CreateEvent().PlayerDisonnect(team.th.playerGetID(event.getPlayer().getName()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

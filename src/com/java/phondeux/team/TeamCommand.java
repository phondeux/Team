package com.java.phondeux.team;

import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {
	private final Team plugin;
	
	public TeamCommand(Team plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Team commands
		//      create		- create a team
		//		disband		- disband a team
		//		invite		- invite a player to a team (admin or mod only)
		//		deinvite	- de-invite a player
		//		description - set team description
		//		join		- join a team
		//		leave		- leave a team
		//		kick		- kick a player from a team
		//		open		- toggle team open enrollment
		//		close		- toggle team open enrollment
		//		promote		- team member -> mod -> owner
		//		demote		- mod -> team member
		//		chat		- toggle all chat to be team-only
		//					  /tc will also be used to team chat for convenience
		//		help		- a list of the commands and how to use them
		Player player = (Player)sender;
		Integer pID = plugin.tdbh.playerGetID(player.getName());
		Integer pStatus = 0, pTeamID = 0;
		try {
			pStatus = plugin.tdbh.playerGetStatus(pID);
			pTeamID = plugin.tdbh.playerGetTeam(pID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (args.length > 0) {
			if (args[0].matches("create")) {
				if (pStatus != 0) {
					player.sendMessage("You are already on a team.");
					return true;
				}
				if (args[1].length() > 8) {
					player.sendMessage("Team names are limited to 8 characters.");
					return true;
				}
				if (plugin.tdbh.teamExists(args[1])) {
					player.sendMessage("A team with that name already exists.");
					return true;
				}
				
				try {
					int teamid = plugin.tdbh.teamCreate(args[1]);
					plugin.tdbh.playerSetTeam(pID, teamid);
					plugin.tdbh.playerSetStatus(pID, 3);
					player.sendMessage("Team " + args[1] + " created successfully!");
				} catch (SQLException e) {
					player.sendMessage("Database error.");
					e.printStackTrace();
				}
				
				return true;
			}
			if (args[0].matches("disband")) {
				if (pStatus != 3) {
					player.sendMessage("Either you aren't on a team, or you are not the owner.");
					return true;
				}
				try {
					plugin.tdbh.teamDelete(pTeamID);
					plugin.tdbh.playerSetStatus(pID, 0);
					player.sendMessage("Your team has been disbanded.");
					//TODO: Remove all players on the team
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return true;
			}
			if (args[0].matches("invite")) {
				return true;
			}
			if (args[0].matches("deinvite")) {
				return true;
			}
			if (args[0].matches("description")) {
				return true;
			}
			if (args[0].matches("join")) {
				return true;
			}
			if (args[0].matches("leave")) {
				return true;
			}
			if (args[0].matches("kick")) {
				return true;
			}
			if (args[0].matches("open")) {
				return true;
			}
			if (args[0].matches("close")) {
				return true;
			}
			if (args[0].matches("promote")) {
				return true;
			}
			if (args[0].matches("demote")) {
				return true;
			}
			if (args[0].matches("chat")) {
				return true;
			}
			if (args[0].matches("help")) {
				try {
					ArrayList<String> tmp = plugin.tdbh.teamGetList();
					for (String s : tmp) {
						player.sendMessage(s);
					}
					if (tmp.size() == 0) {
						player.sendMessage("No teams");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return true;
			}
		} else {
			// Return a simple two/three column list of commands and how to get a full list
			//    ie /team help #
			player.sendMessage("Using /team");
			player.sendMessage(ChatColor.RED + "/team create" + ChatColor.WHITE + " - Creates a team");
			return true;
		}
		return true;
	}
}

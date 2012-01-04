package com.java.phondeux.team;

import java.sql.SQLException;

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
		Player thePlayer = (Player)sender;
		if (args.length > 0) {
			if (args[0].matches("create")) {
				try {
					plugin.tdbh.teamCreate(args[1]);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return true;
			}
			if (args[0].matches("disband")) {
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
				return true;
			}
		} else {
			// Return a simple two/three column list of commands and how to get a full list
			//    ie /team help #
			thePlayer.sendMessage("Using /team");
			thePlayer.sendMessage(ChatColor.RED + "/team create" + ChatColor.WHITE + " - Creates a team");
			return true;
		}
		return true;
	}
}

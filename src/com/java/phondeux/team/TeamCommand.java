package com.java.phondeux.team;

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
		String chatString = " ";
		for(int i=0; i<args.length; i++) {
		    chatString = chatString + args[i] + " ";
		}
		chatString = chatString.trim();
		Player thePlayer = (Player)sender;

		if (thePlayer.hasPermission("adminchat")) {
			ArrayList<Player> onlineAdmins = getOnlineAdmins();
			for (Player player : onlineAdmins) {
				player.sendMessage("<" + ChatColor.RED + thePlayer.getName() + ChatColor.WHITE + "> " + ChatColor.GOLD + chatString);
			}
			plugin.log.info("[ADMIN] " + thePlayer.getDisplayName() + ": " + chatString);
		} else {
			thePlayer.sendMessage("This command isn't available to you.");
			return true;
		}
		return true;
	}

	private ArrayList<Player> getOnlineAdmins() {
		Player[] Players = plugin.getServer().getOnlinePlayers();
		ArrayList<Player> onlineAdmins = new ArrayList<Player>(); 
		
		for (Player player : Players){
			if (player.hasPermission("adminchat")) {
				onlineAdmins.add(player);
			}
		}
		return onlineAdmins;
	}
}

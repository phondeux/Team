package com.java.phondeux.team;

import org.bukkit.ChatColor;

import com.java.phondeux.team.StatsHandler.TeamStats;


public class TeamUtils {
	public static String colorize(String string) {
		for (ChatColor cc : ChatColor.values()) {
			string = string.replaceAll("%" + cc.name(), cc.toString());
		}
		return string;
	}
	
	public static String formatTeam(TeamStats stats, String string) {
		string = string.replaceAll("%members", stats.NumMembers().toString());
		string = string.replaceAll("%kills", stats.NumKills().toString());
		string = string.replaceAll("%deaths", stats.NumDeaths().toString());
		string = string.replaceAll("%pvpdeaths", stats.NumPvpDeaths().toString());
		return colorize(string);
	}
}

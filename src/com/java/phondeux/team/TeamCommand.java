package com.java.phondeux.team;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.java.phondeux.team.StatsHandler.PlayerStats;
import com.java.phondeux.team.StatsHandler.PlayerStats.PlayerDeath;
import com.java.phondeux.team.StatsHandler.PlayerStats.PlayerKill;

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
		Integer pID = plugin.th.playerGetID(player.getName());
		Integer pStatus = 0, pTeamID = 0;
		try {
			pStatus = plugin.th.playerGetStatus(pID);
			pTeamID = plugin.th.playerGetTeam(pID);
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		}
		if (command.getName().equals("team")) {
			if (args.length > 0) {
				if (args[0].matches("create")) {
					if (args.length < 2) {
						player.sendMessage("No team specified.");
						return true;
					}
					if (pStatus != 0) {
						player.sendMessage("You are already on a team.");
						return true;
					}
					if (args[1].length() > 8) {
						player.sendMessage("Team names are limited to 8 characters.");
						return true;
					}
					if (!(args[1].matches("\\w+"))) {
						player.sendMessage("Team names may be made up of only a-z, A-Z, or 0-9");
						return true;
					}
					if (plugin.th.teamExists(args[1])) {
						player.sendMessage("A team with that name already exists.");
						return true;
					}
					
					try {
						int teamid = plugin.th.teamCreate(args[1]);
						plugin.th.playerSetTeam(pID, teamid);
						plugin.th.playerSetStatus(pID, 3);
						plugin.eh.CreateEvent().TeamCreate(pID, teamid);
						player.setDisplayName(args[1] + " " + player.getName());
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					
					return true;
				}
				else if (args[0].matches("disband")) {
					if (pStatus != 3) {
						player.sendMessage("Either you aren't on a team, or you aren't the owner.");
						return true;
					}
					try {
						plugin.eh.CreateEvent().TeamDisband(pID, pTeamID);
						plugin.th.teamDelete(pTeamID);
						ArrayList<String> members = plugin.th.playersGetNameOnTeam(pTeamID);
						for (String m : members) {
							plugin.th.playerSetStatus(plugin.th.playerGetID(m), 0);
							plugin.th.playerSetTeam(plugin.th.playerGetID(m), 0);
							plugin.getServer().getPlayer(m).setDisplayName(plugin.getServer().getPlayer(m).getPlayerListName());
						}
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].matches("invite")) {
					if (args.length < 2) {
						player.sendMessage("No player specified.");
						return true;
					}
					if (pStatus != 3 && pStatus != 2) {
						player.sendMessage("Either you aren't on a team, or you aren't a mod/owner.");
						return true;
					}
					Integer playerid = plugin.th.playerGetID(args[1]);
					if (playerid == null) {
						player.sendMessage("The player " + args[1] + " doesn't exist.");
						return true;
					}
					try {
						plugin.eh.CreateEvent().PlayerInvite(playerid, pTeamID, pID);
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].matches("deinvite")) {
					if (args.length < 2) {
						player.sendMessage("No player specified.");
						return true;
					}
					if (pStatus != 3 && pStatus != 2) {
						player.sendMessage("Either you aren't on a team, or you aren't a mod/owner.");
						return true;
					}
					Integer playerid = plugin.th.playerGetID(args[1]);
					if (playerid == null) {
						player.sendMessage("The player " + args[1] + " doesn't exist.");
						return true;
					}
					try {
						if (!plugin.th.playerIsInvited(playerid, pTeamID)) {
							player.sendMessage("The player " + args[1] + " isn't invited.");
							return true;
						}
						plugin.eh.CreateEvent().PlayerDeinvite(playerid, pTeamID, pID);
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].matches("setmotd")) {
					if (args.length < 2) {
						player.sendMessage("No motd specified.");
						return true;
					}
					if (pStatus != 3 && pStatus != 2) {
						player.sendMessage("Either you aren't on a team, or you aren't a mod/owner.");
						return true;
					}
					String motd = "";
					for (int i = 1; i < args.length; i++) {
						motd += args[i];
						if (i != args.length - 1) motd += " ";
					}
					try {
						plugin.eh.CreateEvent().TeamMotd(pID, pTeamID, motd);
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].matches("description")) {
					if (args.length < 2) {
						player.sendMessage("No description specified.");
						return true;
					}
					if (pStatus != 3 && pStatus != 2) {
						player.sendMessage("Either you aren't on a team, or you aren't the owner.");
						return true;
					}
					String descr = "";
					for (int i = 1; i < args.length; i++) {
						descr += args[i];
						if (i != args.length - 1) descr += " ";
					}
					try {
						plugin.th.teamSetDescription(pTeamID, descr);
						plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getName() + " updated their team description:");
						plugin.getServer().broadcastMessage(ChatColor.WHITE + descr);
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].matches("join")) {
					if (args.length < 2) {
						player.sendMessage("No team specified.");
						return true;
					}
					if (pStatus != 0) {
						player.sendMessage("You're already on a team.");
						return true;
					}
					Integer teamid = plugin.th.teamGetID(args[1]);
					if (!plugin.th.teamExists(teamid)) {
						player.sendMessage("The team " + args[1] + " doesn't exist.");
						return true;
					}
					String teamname = plugin.th.teamGetName(teamid);
					try {
						if (plugin.th.teamGetStatus(teamid) == 1 && !plugin.th.playerIsInvited(pID, teamid)) {
							player.sendMessage(teamname + " is closed.");
							return true;
						}
						plugin.th.playerSetStatus(pID, 1);
						plugin.th.playerSetTeam(pID, teamid);
						plugin.eh.CreateEvent().PlayerJoin(pID, teamid);
						player.setDisplayName(teamname + " " + player.getName());
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].matches("leave")) {
					if (pStatus == 0) {
						player.sendMessage("Either you aren't on a team, or you aren't the owner.");
						return true;
					}
					String teamname = plugin.th.teamGetName(pTeamID);
					if (pStatus == 3) {
						player.sendMessage("You own " + teamname + ", you must disband it.");
						return true;
					}
					try {
						plugin.eh.CreateEvent().PlayerLeave(pID, pTeamID);
						plugin.th.playerSetStatus(pID, 0);
						plugin.th.playerSetTeam(pID, 0);
	
						player.setDisplayName(player.getPlayerListName());
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].matches("kick")) {
					if (args.length < 2) {
						player.sendMessage("No player specified.");
						return true;
					}
					if (pStatus != 3 && pStatus != 2) {
						player.sendMessage("Either you aren't on a team, or you aren't a mod/owner.");
						return true;
					}
					Integer playerid = plugin.th.playerGetID(args[1]);
					if (playerid == null) {
						player.sendMessage("The player " + args[1] + " doesn't exist.");
						return true;
					}
					try {
						if (plugin.th.playerGetTeam(playerid) != pTeamID) {
							player.sendMessage("The player " + args[1] + " isn't on your team.");
							return true;
						}
						if (plugin.th.playerGetStatus(playerid) >= pStatus) {
							player.sendMessage("You can't kick " + args[1] + ".");
						}
						plugin.eh.CreateEvent().PlayerKicked(playerid, pTeamID, pID);
						plugin.th.playerSetStatus(playerid, 0);
						plugin.th.playerSetTeam(playerid, 0);
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].matches("open")) {
					if (pStatus != 3) {
						player.sendMessage("Either you aren't on a team, or you aren't the owner.");
						return true;
					}
					try {
						if (plugin.th.teamGetStatus(pTeamID) == 0) {
							player.sendMessage("Your team is already open.");
							return true;
						}
						plugin.th.teamSetStatus(pTeamID, 0);
						plugin.eh.CreateEvent().TeamOpen(pID, pTeamID);
						player.sendMessage("Your team is now open.");
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].matches("close")) {
					if (pStatus != 3) {
						player.sendMessage("Either you aren't on a team, or you aren't the owner.");
						return true;
					}
					try {
						if (plugin.th.teamGetStatus(pTeamID) == 1) {
							player.sendMessage("Your team is already closed.");
							return true;
						}
						plugin.th.teamSetStatus(pTeamID, 1);
						plugin.eh.CreateEvent().TeamClose(pID, pTeamID);
						player.sendMessage("Your team is now closed.");
					} catch (SQLException e) {
						player.sendMessage("Database error.");
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].matches("promote")) {
					return true;
				}
				else if (args[0].matches("demote")) {
					return true;
				}
				else if (args[0].matches("chat")) {
					if (pStatus == 0) {
						player.sendMessage("You aren't on a team, chat is defaulted to global.");
						return true;
					} else {
						if (plugin.th.teamChatter.contains(pID)) {
							plugin.th.teamChatter.remove(pID);
							player.sendMessage("Team chat disabled");
						} else {
							plugin.th.teamChatter.add(pID);
							player.sendMessage("Team chat enabled");
						}
					}
					return true;
				}
				else if (args[0].matches("who")) {
					try {
						ArrayList<String> tmp = plugin.th.teamGetList();
						for (String s : tmp) {
							String msg = s + ": ";
							int teamid = plugin.th.teamGetID(s);
							ArrayList<String> tmp2 = plugin.th.playersGetNameOnTeam(teamid);
							for (String s2 : tmp2) {
								msg += s2 + ", ";
							}
							msg = msg.substring(0, msg.length() - 2);
							player.sendMessage(msg);
						}
						if (tmp.size() == 0) {
							player.sendMessage("No teams");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return true;
				}
				else if (args[0].equals("playerinfo")) {
					if (args.length != 2) {
						player.sendMessage("No player specified.");
						return true;
					}
					if (!plugin.th.playerExists(args[1])) {
						player.sendMessage("The player '" + args[1] + "' doesn't exist.");
						return true;
					}
					int infopid = plugin.th.playerGetID(args[1]);
					int infotid = 0;
					PlayerStats infop_stats = plugin.sh.GetPlayerStats(infopid);
					try {
						infotid = plugin.th.playerGetTeam(infopid);
					} catch (SQLException e) {
						e.printStackTrace();
						return true;
					}
					
					String infop_kdratio = "0.00";
					if (infop_stats.NumPvpDeaths() > 0) {
						infop_kdratio = String.format("%1$,.2f", (double) infop_stats.NumKills() / (double) infop_stats.NumPvpDeaths());
					} else if (infop_stats.NumKills() > 0) {
						infop_kdratio = "Infinite";
					}
					
					player.sendMessage("--- " + plugin.th.playerGetName(infopid) + " ---");
					if (infotid != 0) player.sendMessage("Team: " + plugin.th.teamGetName(infotid));
					player.sendMessage("[PvP] Kills: " + infop_stats.NumKills() + ", deaths: " + infop_stats.NumPvpDeaths()
							+ ", K/D: " + infop_kdratio);
					
					if (plugin.getServer().getPlayer(args[1]) == null) {
						SimpleDateFormat formatter = new SimpleDateFormat("kk:mm:ss dd MMM yyyy");
						player.sendMessage("Last online: " + formatter.format(infop_stats.LastLogin()));
					}
					
					player.sendMessage("--- Most recent events ---");
					for (int line = 0; line < 4; line++) {
						Object event = infop_stats.Event(line);
						if (event instanceof PlayerKill) {
							PlayerKill pk = (PlayerKill) event;
							if (pk.victimid == null) break;
							
							player.sendMessage("#PvpKill: Killed " + plugin.th.playerGetName(pk.victimid) + " with " + pk.data);
						} else if (event instanceof PlayerDeath) {
							PlayerDeath pd = (PlayerDeath) event;
							if (pd.killerid == null) break;
							
							if (pd.pvp) player.sendMessage("#PvpDeath: Killed by " + plugin.th.playerGetName(pd.killerid) + " with " + pd.data);
							else player.sendMessage("#Death: " + pd.data);
						}
					}
				}
				else if (args[0].matches("help")) {
					if (args.length != 2) {
						player.sendMessage("Please use /team help # where the number is 1 or 2.");
						return true;
					}
					if (args.length < 2) {
						player.sendMessage("Usage: /team [command]");
						player.sendMessage(ChatColor.RED + "create, disband, kick, invite, deinvite, open, close");
						player.sendMessage(ChatColor.RED + "playerinfo, promote, demote, join, leave, chat");
						player.sendMessage(ChatColor.RED + "setmotd, who, help");
						return true;
					}
					if (args[1].matches("1")) {
						player.sendMessage(ChatColor.RED + "create [teamname] - " + ChatColor.WHITE + "Create a team, max 8 character name");
						player.sendMessage(ChatColor.RED + "disband - " + ChatColor.WHITE + "Disbands a team.");
						player.sendMessage(ChatColor.RED + "kick [playername] - " + ChatColor.WHITE + "Kicks a player from a team");
						player.sendMessage(ChatColor.RED + "invite [playername] - " + ChatColor.WHITE + "Invites a player to join a team");
						player.sendMessage(ChatColor.RED + "deinvite [playername] - " + ChatColor.WHITE + "Removes team invitation");
						player.sendMessage(ChatColor.RED + "open - " + ChatColor.WHITE + "Sets team to allow anyone to join");
						player.sendMessage(ChatColor.RED + "close - " + ChatColor.WHITE + "Sets team to invite-only");
						player.sendMessage(ChatColor.RED + "chat - " + ChatColor.WHITE + "Toggles chat between team-only and global.");
					}
					if (args[1].matches("2")) {
						player.sendMessage(ChatColor.RED + "playerinfo [playername] - " + ChatColor.WHITE + "Provides stats on playername");
						player.sendMessage(ChatColor.RED + "promote [playername] - " + ChatColor.WHITE + "Raises a player in rank");
						player.sendMessage(ChatColor.RED + "demote [playername] - " + ChatColor.WHITE + "Lowers a player in rank");
						player.sendMessage(ChatColor.RED + "join [teamname] - " + ChatColor.WHITE + "Join a team");
						player.sendMessage(ChatColor.RED + "leave - " + ChatColor.WHITE + "Removes you from a team");
						player.sendMessage(ChatColor.RED + "setmotd - " + ChatColor.WHITE + "Read the team message of the day");
						player.sendMessage(ChatColor.RED + "who - " + ChatColor.WHITE + "Lists all teams and members");
						player.sendMessage(ChatColor.RED + "help [#]- " + ChatColor.WHITE + "This help which you're reading");
					}
					return true;
				}

			} else {
				// Return a simple two/three column list of commands and how to get a full list
				//    ie /team help #
				player.sendMessage("Usage: /team [command]");
				player.sendMessage(ChatColor.RED + "create, disband, kick, invite, deinvite, open, close");
				player.sendMessage(ChatColor.RED + "playerinfo, promote, demote, join, leave, chat");
				player.sendMessage(ChatColor.RED + "setmotd, who, help");
				return true;
			}
			return true;
		} else if (command.getName().equals("tc")) {
			if (pTeamID == 0) {
				player.sendMessage("You must be on a team to use teamchat.");
			} else if (args.length == 0) {
				player.sendMessage("/tc message");
			} else {
				String msg = "";
				for (String part : args) {
					msg += part + " ";
				}
				plugin.th.teamSendToMembers(pTeamID, player.getName() + ": " + msg.trim());
				String teamName = plugin.th.teamGetName(pTeamID);
				plugin.log.info("TC : " + teamName + " : " + player.getName() + " " + msg.trim());
			}
			return true;
		}
		return true;
	}
}

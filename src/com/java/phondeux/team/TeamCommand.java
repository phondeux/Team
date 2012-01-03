package com.java.phondeux.team;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {
	public TeamCommand(Team plugin) {
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
		
		Player thePlayer = (Player)sender;

		
		return true;
	}
}

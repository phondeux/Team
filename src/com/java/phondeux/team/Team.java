//The Package
package com.java.phondeux.team;

import java.util.logging.Logger;

//import org.bukkit.Bukkit;
//import org.bukkit.GameMode;
//import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Team for Bukkit
 *
 * @author Phondeux
 */
//Starts the class
public class Team extends JavaPlugin{
	protected Logger log;
	public Team ac;
	
	private final TeamPlayerListener playerListener = new TeamPlayerListener(this);

	@Override
	//When the plugin is disabled this method is called.
	public void onDisable() {
		//Print "Team Disabled" on the log.
		System.out.println("Team Disabled");
	}

	@Override
	//When the plugin is enabled this method is called.
	public void onEnable() {
		log = Logger.getLogger("Minecraft");
		getCommand("ac").setExecutor(new TeamCommand(this));

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_PORTAL, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, this.playerListener, Event.Priority.Normal, this);

		//Get the infomation from the yml file.
        PluginDescriptionFile pdfFile = this.getDescription();
        //Print that the plugin has been enabled!
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}
}

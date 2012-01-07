package com.java.phondeux.team;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Team for Bukkit
 *
 * @author Phondeux
 */
public class Team extends JavaPlugin{
	protected Logger log;
	protected ConnectionManager cm;
	protected TeamHandler th;
	public EventHandler eh;
	
	private final TeamPlayerListener playerListener = new TeamPlayerListener(this);

	@Override
	public void onDisable() {
		System.out.println("[team] disabled");
	}

	@Override
	public void onEnable() {
		log = Logger.getLogger("Minecraft");
		getCommand("team").setExecutor(new TeamCommand(this));

		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal, this);

		initialize();
		
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}

	private void initialize() {
        try {
        	log.info("[Team] Connecting to database..");
        	cm = new ConnectionManager("localhost/teamdata", "teamuser", "teampass");
        	log.info("[Team] Initializing TeamHandler..");
        	th = new TeamHandler(this, cm);
        	log.info("[Team] Initializing EventHandler..");
        	eh = new EventHandler(this, cm);        	
        } catch (SQLException e) {
        	e.printStackTrace();
        	log.severe("[Team] Initialization failed due to SQLException!");
        	getPluginLoader().disablePlugin(this);
        	return;
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();
        	log.severe("[Team] Initialization failed due to the driver not being found!");
        	getPluginLoader().disablePlugin(this);
        	return;
        }
	}
}

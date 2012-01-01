package com.java.phondeux.team;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeamPlayerListener extends PlayerListener {
	public Team ac;

	public TeamPlayerListener(Team team) {
		this.ac = team;
	}
	
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if (event.getPlayer().isOp()) {
			Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(ac, new Runnable() {
		        public void run() {
		                event.getPlayer().setGameMode(GameMode.CREATIVE);
		        }}, 20l);
		}
	}
	
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		if (event.getPlayer().isOp()) {
			Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(ac, new Runnable() {
		        public void run() {
		                event.getPlayer().setGameMode(GameMode.CREATIVE);
		        }}, 20l);
		}
	}
	
	public void onPlayerPortal(final PlayerPortalEvent event) {
		if (event.getPlayer().isOp()) {
			Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(ac, new Runnable() {
		        public void run() {
		                event.getPlayer().setGameMode(GameMode.CREATIVE);
		        }}, 20l);
		}
	}
}

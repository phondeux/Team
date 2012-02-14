package com.java.phondeux.team;

import java.sql.SQLException;

import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

@SuppressWarnings("deprecation")
public class TeamEntityListener extends EntityListener {
	public Team parent;

	public TeamEntityListener(Team team) {
		this.parent = team;
	}
	
	//Mostly taken from https://github.com/gominecraft/DeathNotifier
	public String getDeathMessage(Entity entity) {
		String msg = "";
		
		if (entity == null) {
			msg = "UNKNOWN";
		} else if (entity instanceof Blaze) {
			msg = "BLAZE";
		} else if (entity instanceof CaveSpider) {
			msg = "CAVESPIDER";
		} else if (entity instanceof Creeper) {
			msg = "CREEPER";
		} else if (entity instanceof EnderDragon) {
			msg = "ENDERDRAGON";
		} else if (entity instanceof Enderman) {
			msg = "ENDERMAN";
		} else if (entity instanceof Ghast) {
			msg = "GHAST";
		} else if (entity instanceof Giant) {
			msg = "GIANT";
		} else if (entity instanceof MagmaCube) {
			msg = "MAGMACUBE";
		} else if (entity instanceof PigZombie) {
			msg = "PIGZOMBIE";
		} else if (entity instanceof Player) {
			if (((Player) entity).getItemInHand().getAmount() == 0) {
				msg = "FIST";
			} else {
				msg = ((Player) entity).getItemInHand().getType().toString();
			}
		} else if (entity instanceof Projectile) {
			if (entity instanceof Arrow) {
				if (((Arrow) entity).getShooter() == null) {
					msg = "DISPENSER";
				} else if (((Arrow) entity).getShooter() instanceof Skeleton) {
					msg = "SKELETON";
				} else if (((Arrow) entity).getShooter() instanceof Player) {
					msg = "SHOT";
				}
			} else if (entity instanceof Fireball) {
				if (((Fireball) entity).getShooter() instanceof Blaze) {
					msg = "BLAZE";
				} else if (((Fireball) entity).getShooter() instanceof Ghast) {
					msg = "GHAST";
				}
			}
		} else if (entity instanceof Silverfish) {
			msg = "SILVERFISH";
		} else if (entity instanceof Skeleton) {
			msg = "SKELETON";
		} else if (entity instanceof Slime) {
			msg = "SLIME";
		} else if (entity instanceof Spider) {
			msg = "SPIDER";
		} else if (entity instanceof TNTPrimed) {
			msg = "TNT";
		} else if (entity instanceof Wolf) {
			if (((Wolf) entity).isTamed()) {
				msg = "TAMEWOLF";
			} else {
				msg = "WOLF";
			}
		} else if (entity instanceof Zombie) {
			msg = "ZOMBIE";
		}
		return msg;
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			EntityDamageEvent cause = event.getEntity().getLastDamageCause();
			String victim = ((Player) event.getEntity()).getName();
			String causeStr = "UNKNOWN";
			Integer killerid = 0;
			
			if (cause instanceof EntityDamageByEntityEvent) {
				Entity killer = ((EntityDamageByEntityEvent) cause).getDamager();
				if (killer instanceof Player) {
					killerid = parent.th.playerGetID(((Player) killer).getName());
				} else if (killer instanceof Projectile) {
					if (killer instanceof Arrow) {
						if (((Arrow) killer).getShooter() instanceof Player) {
							killerid = parent.th.playerGetID(((Player) ((Arrow) killer).getShooter()).getName());
						}
					}
				}
				causeStr = getDeathMessage(killer);
			} else if (cause == null) {
				causeStr = "UNKNOWN";
			} else {
				switch (cause.getCause()) {
					case CONTACT:
						causeStr = "CACTUS";
						break;
					case DROWNING:
						causeStr = "DROWNING";
						break;
					case FALL:
						causeStr = "FALL";
						break;
					case FIRE:
					case FIRE_TICK:
						causeStr = "FIRE";
						break;
					case LAVA:
						causeStr = "LAVA";
						break;
					case LIGHTNING:
						causeStr = "LIGHTNING";
						break;
					case STARVATION:
						causeStr = "STARVATION";
						break;
					case SUFFOCATION:
						causeStr = "SUFFOCATION";
						break;
					case SUICIDE:
						causeStr = "SUICIDE";
						break;
					case VOID:
						causeStr = "VOID";
						break;
					}
			}
			
			try {
				parent.eh.CreateEvent().PlayerDeath(killerid, parent.th.playerGetID(victim), causeStr);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

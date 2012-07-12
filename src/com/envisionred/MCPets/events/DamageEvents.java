package com.envisionred.MCPets.events;

import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.envisionred.MCPets.MCPets;

public class DamageEvents implements Listener{
	private MCPets plugin = MCPets.plugin;
	@EventHandler
	public void negateOtherDamage(EntityDamageEvent event){
		Entity e = event.getEntity();
		UUID id = e.getUniqueId();
		boolean doNotTakeDamage = plugin.getConfig().getBoolean("pets-invincible", true);
		//only cancel if it's set so they don't take damage
		if (doNotTakeDamage) {
			//check if the section in the Pets.yml isn't null
			if (plugin.getPets().getConfigurationSection("pets." + id.toString()) != null){
				if (event.getCause() != DamageCause.ENTITY_ATTACK){
					//cancel event if the cause isn't from an entity
					event.setCancelled(true);
				}
			}
		}
	}
	@EventHandler
	public void negateEntityDamage(EntityDamageByEntityEvent event){
		Entity victim = event.getEntity();
		UUID id = victim.getUniqueId();
		boolean doNotTakeDamage = plugin.getConfig().getBoolean("pets-invincible", true);
		if (doNotTakeDamage){
			FileConfiguration pets = plugin.getPets();
			if (pets.getConfigurationSection("pets." + id.toString()) != null){
				Entity damager = event.getDamager();
				if (!(damager instanceof Player)){
					event.setCancelled(true);
					return;
				}
				Player player = (Player) damager;
				String playerName = player.getName();
				if (playerName.equalsIgnoreCase(pets.getString("pets." + id.toString() + ".owner"))){
					return;
				}else{
					event.setCancelled(true);
				}
			}
		}
	}

}

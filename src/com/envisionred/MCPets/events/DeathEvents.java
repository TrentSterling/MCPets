package com.envisionred.MCPets.events;

import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.envisionred.MCPets.MCPets;

public class DeathEvents implements Listener{
public static Map<UUID, String>ownedPets;
	@EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
	public void deathEvents(EntityDeathEvent event){
		Entity entity = event.getEntity();
		UUID id = entity.getUniqueId();
		FileConfiguration pets = MCPets.plugin.getPets();
		if (pets.getConfigurationSection("pets." + id.toString()) == null){
			return;
		}else{
			String ownerName = pets.getString("pets." + id.toString() + ".owner");
			Player owner = MCPets.plugin.getServer().getPlayer(ownerName);
			String petName = pets.getString("pets." + id.toString() + ".name");
			if (owner != null){
				owner.sendMessage(ChatColor.RED + "Your pet (" + petName +") has just died!");
			}
			pets.set("pets." + id.toString(), null);
			MCPets.plugin.savePetsFile();
		}
		}
	
}

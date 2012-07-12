package com.envisionred.MCPets.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.envisionred.MCPets.MCPets;

public class ClickEvents implements Listener{
	public static Map<String, UUID> selectedMobs = new HashMap<String, UUID>();
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void getRightClickOnBaby(PlayerInteractEntityEvent event){		
		Entity e = event.getRightClicked();
		if (e instanceof LivingEntity){
			if (e instanceof Animals){
				Animals mob = (Animals) e;
				int age = mob.getAge();
				if (age < 0){
					UUID id = mob.getUniqueId();
					String idString = id.toString();
					Player player = event.getPlayer();
					FileConfiguration pets = MCPets.plugin.getPets();
					if (pets.getConfigurationSection("pets." + idString) == null){
						selectMob(player.getName(), id);
						player.sendMessage(ChatColor.BLUE + "You have selected an untamed animal. Do /pet tame to tame it.");
						return;
					}else{						
						String owner  = MCPets.plugin.getPets().getString("pets." + idString + ".owner");
						String petName = MCPets.plugin.getPets().getString("pets." + idString + ".name");
						if (!owner.equalsIgnoreCase(player.getName())){
						player.sendMessage(ChatColor.RED + "This pet (named " + ChatColor.GREEN + petName + ChatColor.RED + ") is owned by "
						+ ChatColor.GREEN + owner+ ChatColor.RED + " and cannot be selected by others");
						return;
						}else{							
							if (player.isSneaking()){
								mob.setPassenger(player);
								return;
							}
							player.sendMessage(ChatColor.GREEN + "You have selected one of your pets named " + ChatColor.AQUA + petName + ChatColor.GREEN + ". Do /pet help to view commands!");
							selectMob(player.getName(), id);
						}
					}
				}else{
					return;
				}
			}else{
				return;
			}
		}else{
			return;
		}
	}
	public void selectMob(String name , UUID id){
		if (selectedMobs.containsKey(name)){
			selectedMobs.remove(name);
		}
		selectedMobs.put(name, id);
	}
}

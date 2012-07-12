package com.envisionred.MCPets;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;


public class Utils {
	public Entity getEntityByUUID(UUID id){
		List<World> worlds = Bukkit.getServer().getWorlds();
		for (World world : worlds){
			List<Entity> entities = world.getEntities();
			for (Entity entity : entities){
				UUID idCandidate = entity.getUniqueId();
				if (idCandidate.equals(id)){
					return entity;
				}
			}
		}
		return null;
	}
	public int getNumberOfPets(Player player){
		int petNo = 0;
		FileConfiguration pets = MCPets.plugin.getPets();
		for (String idString : pets.getConfigurationSection("pets").getKeys(false)){
			String ownerName = pets.getString("pets." + idString + ".owner");
			if (ownerName.equalsIgnoreCase(player.getName())){
				petNo++;
			}
		}
		return petNo;
	}
}

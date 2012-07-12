package com.envisionred.MCPets;

import java.util.Random;
import java.util.UUID;

import net.minecraft.server.EntityCreature;
import net.minecraft.server.Navigation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.entity.CraftCreature;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;


public class setPetsToFollow implements Runnable{
Utils utils = new Utils();
	@Override
	public void run() {
		FileConfiguration pets = MCPets.plugin.getPets();
		if (pets.getConfigurationSection("pets").getKeys(false) == null){
			return;
		}
		for (String idString : pets.getConfigurationSection("pets").getKeys(false)){
			UUID id = UUID.fromString(idString);
			Animals pet = (Animals) utils.getEntityByUUID(id);
			String ownerName = pets.getString("pets." + idString +".owner");
			Player player = MCPets.plugin.getServer().getPlayerExact(ownerName);
			if (player == null){
				continue;
			}
			if (pet == null){
				Bukkit.getServer().broadcastMessage("pet is null");
				continue;
			}
			Location playerLoc = player.getLocation();
			Location petLoc = pet.getLocation();
			boolean sitting = pets.getBoolean("pets." + id.toString() + ".sitting");
			if (sitting == true){
				Double x = pets.getDouble("pets." + idString + ".sitX");
				Double y = pets.getDouble("pets." + idString + ".sitY");
				Double z = pets.getDouble("pets." + idString + ".sitZ");
				Float yaw = (float) pets.getDouble("pets." + idString + ".yaw");
				Float pitch = (float) pets.getDouble("pets." + idString + ".pitch");
				Location sitLocation = new Location(pet.getWorld(), x, y, z, yaw, pitch);
				if (!petLoc.equals(sitLocation)){
					pet.teleport(sitLocation);
					continue;
				}
				continue;
			}
			if (pet.getPassenger() != null){
				continue;
			
			}		
			if (playerLoc.distance(petLoc) >= 20){
				Location tpLoc = playerLoc;
				while (tpLoc.getBlock().getType() == Material.AIR){
					tpLoc.setY(tpLoc.getY() - 1);
				}
				tpLoc.setY(tpLoc.getY() + 1);
				pet.teleport(tpLoc);
				continue;
			}
			EntityCreature ec = ((CraftCreature)pet).getHandle();
			Navigation nav = ec.al();
			double offsetX = new Random().nextGaussian();
			double offsetY = new Random().nextGaussian();
			nav.a(playerLoc.getX() + offsetX, playerLoc.getY(), playerLoc.getZ() + offsetY, 0.3F);
		}
	}

}
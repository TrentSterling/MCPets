package com.envisionred.MCPets;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ClickEvents implements Listener{
public static Map<String, UUID> selectedMobs = new HashMap<String, UUID>();
public static Map<UUID, String> ownedPets;
	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void getRightClickOnBaby(PlayerInteractEntityEvent event){
    ownedPets = MCPets.ownedPets;
	Entity e = event.getRightClicked();
	if (e instanceof LivingEntity){
		if (e instanceof Animals){
			Animals mob = (Animals) e;
			int age = mob.getAge();
			if (age < 0){
			Player player = event.getPlayer();
			if (!ownedPets.containsKey(mob.getUniqueId())){
				player.sendMessage(ChatColor.BLUE +"You have selected an unowned baby animal. Type /pet tame to have it as your pet");
			selectMob(player.getName(), mob.getUniqueId());
			return;
		}else{
			UUID id = mob.getUniqueId();
			if (ownedPets.get(id).matches(player.getName())){
				player.sendMessage(ChatColor.AQUA + "You have selected one of your pets! Here are some commands to use:");
				player.sendMessage(ChatColor.AQUA + "/pet setname <name> - Sets the selected pet's name to the specified name.");
				player.sendMessage(ChatColor.AQUA + "/pet sit - Toggles the pet's \"sitting\" mode, if toggled on, the pet won't move, if toggled on, it will follow you.");
				player.sendMessage(ChatColor.AQUA + "/pet release - Releases your pet into the wild.");
				selectMob(player.getName(), id);
				return;
			}
		String ownerName = ownedPets.get(mob.getUniqueId());
		player.sendMessage(ChatColor.RED + "That baby animal is already owned by " + ownerName + " and cannot be selected by others.");
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

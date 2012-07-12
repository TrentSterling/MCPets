package com.envisionred.MCPets;

import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.envisionred.MCPets.events.ClickEvents;
public class PetCommand implements CommandExecutor{
	public static Map<String, UUID>selectedMobs;
	public PetCommand(MCPets instance){
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pet")){
			if (!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "The pet command cannot be used by console.");
				return true;
			}
			Player player = (Player) sender;
			if (args.length == 0){
				sender.sendMessage(ChatColor.GREEN + "MCPets by " + ChatColor.RED + "EnvisionRed");
				sender.sendMessage(ChatColor.GREEN + "Do /pet help to view help.");
				return true;
			}
			if (args.length >= 1){
				if(args[0].equalsIgnoreCase("Tame")){
					if (player.hasPermission("MCPets.tame")){
						return Tame(player, cmd);	
					} else{
						player.sendMessage(ChatColor.RED + "You do not have permission for that command!");
						return true;
					}
				}
				if(args[0].equalsIgnoreCase("Sit")){
					if (player.hasPermission("MCPets.sit")){
						return Sit(player, args, cmd);
					}else{
						player.sendMessage(ChatColor.RED + "You do not have permission for that command!");
						return true;
					}
				}				
				if (args[0].equalsIgnoreCase("Help")){
					return Help(player, cmd);
				}
				if (args[0].equalsIgnoreCase("Release")){
					if (player.hasPermission("MCPets.release")){
					return Release(player, cmd);
					} else{
						player.sendMessage(ChatColor.RED + "You do not have permission for that command!");
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("SetName")){
					if (player.hasPermission("MCPets.name")){
						return Name(player, cmd, args);
					} else{
						player.sendMessage(ChatColor.RED + "You do not have permission for that command!");
						return true;
					}
				}
			}
		}
		return false;
	}
	public boolean Tame(Player player, Command cmd){
		String playerName = player.getName();
		if (ClickEvents.selectedMobs.containsKey(playerName)){
			UUID id = ClickEvents.selectedMobs.get(playerName);
			Entity e = new Utils().getEntityByUUID(id);
			Animals pet = (Animals) e;
			if (!(pet.getAge() < 0)){
				player.sendMessage(ChatColor.GREEN + "Sorry, you waited too long to tame your pet, now it is an adult.");
				return true;
			}
			String name = e.getType().toString();
			FileConfiguration pets = MCPets.plugin.getPets();
			if (pets.getConfigurationSection("pets." + id.toString()) != null){
				String ownerName = pets.getString("pets." + id.toString() + ".owner");
				if (ownerName.equalsIgnoreCase(playerName)){
					player.sendMessage(ChatColor.GREEN + "You can't tame your pet again.");
					return true;
				}
				String petName = pets.getString("pets." + id.toString() + ".name");
				player.sendMessage(ChatColor.GREEN + "That pet (named " + ChatColor.AQUA + petName + ChatColor.GREEN + ") is owned by " + ChatColor.AQUA + ownerName + ChatColor.GREEN +" and cannot be tamed by you.");
				return true;
			}
			int maxPets = MCPets.plugin.getConfig().getInt("max-pets");
			int playerPets = new Utils().getNumberOfPets(player);
			if (playerPets >= maxPets){
				player.sendMessage(ChatColor.GREEN + "You already have the max amount of pets.");
				player.sendMessage(ChatColor.GREEN + "To tame another you will have to release one of your current ones.");
				return true;
			}
			String idString = id.toString();
			pets.createSection("pets." +idString);
			pets.set("pets." + idString + ".owner", playerName);
			pets.set("pets." +idString + ".name", name);
			pets.set("pets." +idString + ".sitting", false);
			pets.set("pets." + idString + ".type", name);
			pet.setAgeLock(true);
			player.sendMessage(ChatColor.AQUA + "Congratulations! You have tamed your pet. Do /pet help to view commands.");
			MCPets.plugin.savePetsFile();
		}else{
			player.sendMessage(ChatColor.RED + "You do not have a selected animal. Right click a baby animal to select it.");
		}
		return true;
	}
	public boolean Sit(Player player, String [] args, Command cmd){
		String playerName = player.getName();
		if (ClickEvents.selectedMobs.containsKey(playerName)){
			UUID id = ClickEvents.selectedMobs.get(playerName);
			FileConfiguration pets = MCPets.plugin.getPets();
			if(pets.getConfigurationSection("pets." + id.toString()) != null){
				String ownerName = pets.getString("pets." + id.toString() + ".owner");
				if (!ownerName.equalsIgnoreCase(playerName)){
					String petName = pets.getString("pets." + id.toString() + ".name");
					player.sendMessage(ChatColor.GREEN + "That pet (named " +petName+ ") is owned by " + ownerName +"and cannot be told to sit by you.");
					return true;
				}
				boolean sit = pets.getBoolean("pets." +id.toString() + ".sitting");
				if (sit){
					pets.set("pets." + id.toString() + ".sitting", false);
					String petName = pets.getString("pets." + id.toString() + ".name");
					player.sendMessage(ChatColor.GREEN + "Your pet (named " + petName + ") is no longer sitting.");
					return true;
				}
				if (!sit){
					Entity e = new Utils().getEntityByUUID(id);
					Location loc = e.getLocation();
					pets.set("pets." + id.toString() + ".sitting", true);
					pets.set("pets." + id.toString() + ".sitX", loc.getX());
					pets.set("pets." + id.toString() + ".sitY", loc.getY());
					pets.set("pets." + id.toString() + ".sitZ", loc.getZ());
					pets.set("pets." + id.toString() + ".yaw", loc.getYaw());
					pets.set("pets." + id.toString() + ".pitch", loc.getPitch());
					String petName = pets.getString("pets." + id.toString() + ".name");
					MCPets.plugin.savePetsFile();
					player.sendMessage(ChatColor.GREEN + "Your pet (named " + petName + ") is now sitting.");
					return true;
				}
			}else{
				player.sendMessage(ChatColor.GREEN + "That pet has not been tamed. Do /pet tame to tame it.");
			}
				
		}else{
			player.sendMessage(ChatColor.GREEN + "You do not have a selected animal. Right click a baby animal to select it.");
		}
		return true;
	}
	public boolean Help(Player player, Command cmd){
		int k = 0;
		if (player.hasPermission("MCPets.tame")){
		player.sendMessage(ChatColor.AQUA + "/pet tame: Tames a selected baby animal.");
		k++;
		}if (player.hasPermission("MCPets.release")){
		player.sendMessage(ChatColor.AQUA + "/pet release: releases a selected pet of yours.");
		k++;
		}if (player.hasPermission("MCPets.sit")){
		player.sendMessage(ChatColor.AQUA + "/pet sit: Toggles a selected pet's \"sitting\" mode. When on, the pet will not move.");
		k++;
		}if (player.hasPermission("MCPets.name")){
		player.sendMessage(ChatColor.AQUA + "/pet setname: Set's your selected pet's name.");
		k++;
		} if (k == 0){
			player.sendMessage(ChatColor.RED + "You do not have any MCPets permissions, so no help will be shown :(");
		}
		return true;
	}
	public boolean Release(Player player, Command cmd){
		if (ClickEvents.selectedMobs.containsKey(player.getName())){
			UUID id = ClickEvents.selectedMobs.get(player.getName());
			FileConfiguration pets = MCPets.plugin.getPets();
			String idString = id.toString();
			if (pets.getConfigurationSection("pets." + idString) == null){
				player.sendMessage(ChatColor.GREEN + "That pet does not have an owner. Do /pet tame to tame it.");
				return true;
			}
			String ownerName = pets.getString("pets." +id.toString() + ".owner");
			if (!ownerName.equalsIgnoreCase(player.getName())){
				String petName = pets.getString("pets." + id.toString() + ".name");
				player.sendMessage(ChatColor.RED + "That pet belongs to " + ownerName + " and is named " + petName);
				player.sendMessage(ChatColor.RED + "It cannot be released by you.");
			}else{
				pets.set("pets." + id.toString(), null);
				MCPets.plugin.savePetsFile();
				player.sendMessage(ChatColor.GREEN + "You have released your pet.");
				Animals mob = (Animals) new Utils().getEntityByUUID(id);
				mob.setAgeLock(false);
				return true;
			}
			return true;
		}else{
			player.sendMessage(ChatColor.RED + "You do not have a selected animal. Right click a baby animal to select it.");
			return true;
		}
		
	}
	public boolean Name(Player player, Command cmd, String[] args){
		String playerName = player.getName();
		if (args.length < 2){
			player.sendMessage(ChatColor.RED + "Proper usage: /pet setname name");
			return true;
		}
		if (ClickEvents.selectedMobs.containsKey(playerName)){
			UUID id = ClickEvents.selectedMobs.get(playerName);
			FileConfiguration pets = MCPets.plugin.getPets();
			String idString = id.toString();
			if (pets.getConfigurationSection("pets." + idString) == null){
				player.sendMessage(ChatColor.GREEN + "That pet does not have an owner. Do /pet tame to tame it.");
				return true;
			}
			String ownerName = pets.getString("pets." + idString + ".owner");
			String petName = pets.getString("pets." + idString + ".name");
			if (!playerName.equalsIgnoreCase(ownerName)){
				player.sendMessage(ChatColor.RED + "That pet belongs to " + ownerName + " and is named " + petName);
				player.sendMessage(ChatColor.RED + "Its name cannot be changed by you.");
				return true;
			}
			String newName = args[1];
			char[]chars = newName.toCharArray();
			if (chars.length > 10){
				player.sendMessage(ChatColor.RED + "The maximum length for pet names is 10.");
				return true;
			}
			pets.set("pets." + id.toString() + ".name", newName);
			MCPets.plugin.savePetsFile();
			player.sendMessage(ChatColor.GREEN + "Your pet (formerly named " +ChatColor.AQUA + petName+ChatColor.GREEN +  ") is now named " + ChatColor.AQUA + newName);
			return true;
		}else{
			player.sendMessage(ChatColor.RED + "You do not have a selected animal. Right click a baby animal to select it.");
			return true;
		}
	}
}

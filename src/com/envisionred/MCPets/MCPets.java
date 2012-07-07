package com.envisionred.MCPets;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MCPets extends JavaPlugin{
	public static Map<UUID, String> ownedPets;
	public static Logger log;
	public static MCPets plugin;
public void onDisable(){
	log.info("EnvisionRed's MCPets disabled.");
	saveOwnedMobs(ClickEvents.ownedPets);
}
public void onEnable(){
	plugin = this;
	log = this.getLogger();
	log.info("EnvisionRed's MCPets enabled");
	this.getServer().getPluginManager().registerEvents(new ClickEvents(), this);
	ownedPets = loadOwnedMobs();
}
public void saveOwnedMobs(Map<UUID, String> ownedMobs){
	try{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.getDataFolder() + "/ownedMobs"));
		oos.writeObject(ownedMobs);
		oos.flush();
		oos.close();
	}catch(Exception e){
		log.severe("Failed to save owned pets.");
	}
}
public Map<UUID, String>loadOwnedMobs(){
	try{
		ObjectInputStream oos = new ObjectInputStream(new FileInputStream(this.getDataFolder() + "/ownedMobs"));
		Object result = oos.readObject();
		Map<UUID, String>map = (Map<UUID, String>) result;
		return map;
	}catch(Exception e){
		log.severe("Failed to load owned pets.");
	}
	return null;
}
}

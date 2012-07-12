package com.envisionred.MCPets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.envisionred.MCPets.Metrics.Graph;
import com.envisionred.MCPets.events.ClickEvents;
import com.envisionred.MCPets.events.DamageEvents;
import com.envisionred.MCPets.events.DeathEvents;

public class MCPets extends JavaPlugin{
	private PetCommand cmdExecutor;
	public static Map<String, UUID> selectedMobs;
	public static Logger log;
	public static MCPets plugin;
	private FileConfiguration petsConfig = null;
	private File petsFile = null;
	@Override
	public void onDisable(){
		log.info("EnvisionRed's MCPets disabled.");
	}
	@Override
	public void onEnable(){
		enableConfig();
		registerEvents();
		plugin = this;
		log = this.getLogger();
		log.info("EnvisionRed's MCPets enabled");
		cmdExecutor = new PetCommand(this);
		getCommand("pet").setExecutor(cmdExecutor);
		if (!this.getConfig().getBoolean("opt-out-metrics", false)){
			startMetrics();
		}
	}
	public void enableConfig(){
		File cfgFile = new File(this.getDataFolder(), "config.yml");
		if (!cfgFile.exists()){
			this.getConfig().options().copyHeader(true);
			this.saveDefaultConfig();
		}
		petsFile = new File(this.getDataFolder(), "Pets.yml");
		if (!petsFile.exists()){
			this.getPets().options().copyDefaults(true);
			this.getPets().options().copyHeader(true);
			this.savePetsFile();
			
		}
	}
	public void registerEvents(){
		Bukkit.getServer().getPluginManager().registerEvents(new ClickEvents(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new DeathEvents(), this);
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new setPetsToFollow(), 100L, 10L);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			public void run(){
				Bukkit.getServer().getPluginManager().registerEvents(new DamageEvents(), MCPets.plugin);
			}
		}, 200L);
	}
	public void startMetrics(){
		boolean noMetrics = this.getConfig().getBoolean("opt-out-metrics", false);
		if (noMetrics){
			return;
		}
		try {
			Metrics metrics = new Metrics(this);
			Graph petGraph = metrics.createGraph("Total number of pets");
			petGraph.addPlotter(new Metrics.Plotter("No. of pets"){

				@Override
				public int getValue() {
					int petCount = 0;
					for (@SuppressWarnings("unused") String idString : MCPets.plugin.getPets().getConfigurationSection("pets").getKeys(false)){
						petCount++;					
					}
					return petCount;
				}
				
			});
			metrics.start();
			log.info("Metrics started");
		} catch (IOException e) {
			log.info("Metrics failed to start.");
			return;
		}
	}
	public FileConfiguration getPets(){
		if (petsConfig == null){
			this.reloadPetsFile();
		}
		return petsConfig;
	}
	public void reloadPetsFile(){
		if (petsFile == null){
			petsFile = new File(this.getDataFolder(),
					"Pets.yml");
		}
		petsConfig = YamlConfiguration.loadConfiguration(petsFile);
		InputStream petStream = this.getResource("Pets.yml");
		if (petStream != null){
			YamlConfiguration petDefault = YamlConfiguration.loadConfiguration(petStream);
			petsConfig.setDefaults(petDefault);
		}
	}
	public void savePetsFile(){
		Logger log = this.getLogger();
		if (petsFile == null || petsConfig == null){
			return;
		}
		try {
			petsConfig.save(petsFile);
		}catch(IOException e){
			log.severe("Failed to save the pets file.");
		}
	}
}

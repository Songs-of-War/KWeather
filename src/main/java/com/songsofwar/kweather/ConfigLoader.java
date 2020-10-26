package com.songsofwar.kweather;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigLoader 
{	
	public FileConfiguration loadConfig()
	{
		KWeather.getPlugin().saveDefaultConfig();

		if(isMissing()) {
			newreplaceOldConfig();
		}
		return KWeather.getPlugin().getConfig();
	}
	
	private boolean isMissing()
	{
		YamlConfiguration defconfig = YamlConfiguration.loadConfiguration(new InputStreamReader(KWeather.getPlugin().getResource("config.yml")));
        for (Entry<String, Object> entry : defconfig.getValues(true).entrySet()) {
            if(!KWeather.getPlugin().getConfig().isSet(entry.getKey())) {
            	return true;
            }
        }
		return false;
	}
	
	public void newreplaceOldConfig()
	{
		replaceOldConfig();
		Bukkit.getServer().getLogger().info(KWeather.getPrefix() + "Saving your old options to the new config file.");
		Bukkit.getServer().getLogger().info(KWeather.getPrefix() + "WARNING: This may not save all previous options if paths have been changed from a previous version.");
		Bukkit.getServer().getLogger().info(KWeather.getPrefix() + "To help the missing paths will be printed to help you check");
		Bukkit.getServer().getLogger().info(KWeather.getPrefix() + "If you need comments the default config has been saved to defaultconfig.yml");
		File newcf = new File(KWeather.getPlugin().getDataFolder(), "config.yml");
		File oldcf = new File(KWeather.getPlugin().getDataFolder(), "oldconfig.yml");
		YamlConfiguration newconfig = YamlConfiguration.loadConfiguration(newcf);
		YamlConfiguration oldconfig = YamlConfiguration.loadConfiguration(oldcf);
		
		Bukkit.getServer().getLogger().info(KWeather.getPrefix() + "Missing config paths:");
		for(Entry<String, Object> entry : newconfig.getValues(true).entrySet()) {
			if(!oldconfig.isSet(entry.getKey())) {
				newconfig.set(entry.getKey(), entry.getValue());
				Bukkit.getServer().getLogger().info(KWeather.getPrefix() + "\t- " + entry.getKey());
			} else {
				newconfig.set(entry.getKey(), oldconfig.get(entry.getKey()));
			}
		}
		
		try {
			newconfig.save(newcf);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		try {
			saveDefConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		try {
			addComments();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	private void saveDefConfig() throws Exception
	{
		File defaultconfig = new File(KWeather.getPlugin().getDataFolder(), "defaultconfig.yml");
		if(defaultconfig.exists()) {
			defaultconfig.delete();
			defaultconfig.createNewFile();
		} else {
			defaultconfig.createNewFile();
		}
		
		BufferedReader resourcereader = new BufferedReader(new InputStreamReader(KWeather.getPlugin().getResource("config.yml")));
		BufferedWriter defaultwriter = new BufferedWriter(new FileWriter(defaultconfig));
		
		String line;
		while((line = resourcereader.readLine()) != null) {
			defaultwriter.write(line + "\r\n");
		}
		defaultwriter.close();
		resourcereader.close();
	}
	
	public void replaceOldConfig()
	{
		Bukkit.getServer().getLogger().info(KWeather.getPrefix() + "There has been some updates to the config.yml");
		Bukkit.getServer().getLogger().info(KWeather.getPrefix() + "Saving the old config to oldconfig.yml and making a new one.");
		File oldcf = new File(KWeather.getPlugin().getDataFolder(), "oldconfig.yml");
		if(!oldcf.exists()) {
			try {
				oldcf.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileConfiguration oldc = KWeather.getPlugin().getConfig();
		
		try {
			oldc.save(oldcf);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		File cf = new File(KWeather.getPlugin().getDataFolder(), "config.yml");
		if(cf.exists()) {
			cf.delete();
		}
		
		KWeather.getPlugin().saveDefaultConfig();
	}
	
	public FileConfiguration reloadCon() 
	{
		KWeather.getPlugin().saveDefaultConfig();
		KWeather.getPlugin().reloadConfig();
		return KWeather.getPlugin().getConfig();
	}
}

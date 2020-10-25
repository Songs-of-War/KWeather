package com.songsofwar.xweather.xweather;

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
		XWeather.getPlugin().saveDefaultConfig();

		if(isMissing()) {
			newreplaceOldConfig();
		}
		return XWeather.getPlugin().getConfig();
	}
	
	private boolean isMissing()
	{
		YamlConfiguration defconfig = YamlConfiguration.loadConfiguration(new InputStreamReader(XWeather.getPlugin().getResource("config.yml")));
        for (Entry<String, Object> entry : defconfig.getValues(true).entrySet()) {
            if(!XWeather.getPlugin().getConfig().isSet(entry.getKey())) {
            	return true;
            }
        }
		return false;
	}
	
	public void newreplaceOldConfig()
	{
		replaceOldConfig();
		Bukkit.getServer().getLogger().info(XWeather.getPrefix() + "Saving your old options to the new config file.");
		Bukkit.getServer().getLogger().info(XWeather.getPrefix() + "WARNING: This may not save all previous options if paths have been changed from a previous version.");
		Bukkit.getServer().getLogger().info(XWeather.getPrefix() + "To help the missing paths will be printed to help you check");
		Bukkit.getServer().getLogger().info(XWeather.getPrefix() + "If you need comments the default config has been saved to defaultconfig.yml");
		File newcf = new File(XWeather.getPlugin().getDataFolder(), "config.yml");
		File oldcf = new File(XWeather.getPlugin().getDataFolder(), "oldconfig.yml");
		YamlConfiguration newconfig = YamlConfiguration.loadConfiguration(newcf);
		YamlConfiguration oldconfig = YamlConfiguration.loadConfiguration(oldcf);
		
		Bukkit.getServer().getLogger().info(XWeather.getPrefix() + "Missing config paths:");
		for(Entry<String, Object> entry : newconfig.getValues(true).entrySet()) {
			if(!oldconfig.isSet(entry.getKey())) {
				newconfig.set(entry.getKey(), entry.getValue());
				Bukkit.getServer().getLogger().info(XWeather.getPrefix() + "\t- " + entry.getKey());
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
		File defaultconfig = new File(XWeather.getPlugin().getDataFolder(), "defaultconfig.yml");
		if(defaultconfig.exists()) {
			defaultconfig.delete();
			defaultconfig.createNewFile();
		} else {
			defaultconfig.createNewFile();
		}
		
		BufferedReader resourcereader = new BufferedReader(new InputStreamReader(XWeather.getPlugin().getResource("config.yml")));
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
		Bukkit.getServer().getLogger().info(XWeather.getPrefix() + "There has been some updates to the config.yml");
		Bukkit.getServer().getLogger().info(XWeather.getPrefix() + "Saving the old config to oldconfig.yml and making a new one.");
		File oldcf = new File(XWeather.getPlugin().getDataFolder(), "oldconfig.yml");
		if(!oldcf.exists()) {
			try {
				oldcf.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileConfiguration oldc = XWeather.getPlugin().getConfig();
		
		try {
			oldc.save(oldcf);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		File cf = new File(XWeather.getPlugin().getDataFolder(), "config.yml");
		if(cf.exists()) {
			cf.delete();
		}
		
		XWeather.getPlugin().saveDefaultConfig();
	}
	
	public FileConfiguration reloadCon() 
	{
		XWeather.getPlugin().saveDefaultConfig();
		XWeather.getPlugin().reloadConfig();
		return XWeather.getPlugin().getConfig();
	}
}

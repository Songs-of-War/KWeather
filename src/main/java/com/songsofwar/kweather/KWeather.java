package com.songsofwar.kweather;

import com.songsofwar.kweather.Weather.Point.PointWeather;
import com.songsofwar.kweather.Weather.World.Puddle;
import com.songsofwar.kweather.Weather.World.WorldWeather;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.songsofwar.kweather.Weather.WeatherHandler;

public class KWeather extends JavaPlugin
{
	private static Plugin plugin;
	private static String pluginprefix = "[KWeather] ";
	private static Updater update;
	private static ConfigLoader configloader;
	private static LanguageLoader languageloader;
	private static boolean debugmode;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		configloader = new ConfigLoader();
		FileConfiguration config = configloader.loadConfig();
		debugmode = config.getBoolean("Debug");
		languageloader = LanguageLoader.setupLoader();
		WeatherHandler.setUpHandler(config);
		this.getCommand("kweather").setExecutor(new Commands());
		/*Add after first upload of the pro version to do update checks
		 * Remember that there is a section in Events.java too
		update = new Updater(this, 62733, config);
		
		update.checkForUpdates(null);*/
		
		Bukkit.getServer().getPluginManager().registerEvents(new Events(), this);
		Bukkit.getServer().getLogger().info(pluginprefix + "Finished loading plugin.");
	}
	
	@Override
	public void onLoad()
	{
		try {
			WorldGuardManager.setUp();
		} catch(NoClassDefFoundError e) {}
	}
	
	@Override
	public void onDisable()
	{
		Bukkit.getServer().getLogger().info(pluginprefix + "Stopping all current World weather events");
		WorldWeather.stopAll();
		Bukkit.getServer().getLogger().info(pluginprefix + "Stopping all current Point weather events");
		PointWeather.stopAll();
		for(World world : Bukkit.getServer().getWorlds()) {
			Bukkit.getServer().getLogger().info(pluginprefix + "Clearing puddles for " + world.getName());
			Puddle.dryUpPuddles(world);
		}
	}
	
	public static FileConfiguration reloadCon() 
	{
		WeatherHandler.stopAll();
		languageloader = LanguageLoader.setupLoader();
		return configloader.reloadCon();
	}
	
	public static Plugin getPlugin() {return plugin;}
	public static String getPrefix() {return pluginprefix;}
	public static Updater getUpdater() {return update;}
	public static LanguageLoader getLanguageLoader() {return languageloader;}
	public static boolean isDebug() {return debugmode;}
}

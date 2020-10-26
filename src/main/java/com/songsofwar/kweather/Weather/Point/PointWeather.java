package com.songsofwar.kweather.Weather.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.songsofwar.kweather.KWeather;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import com.songsofwar.kweather.Weather.Point.Types.Meteor;
import com.songsofwar.kweather.Weather.Point.Types.Tornado;

public class PointWeather implements PointWeatherType
{
	protected static HashSet<PointWeatherType> weatherevents = new HashSet<PointWeatherType>();
	
	protected Location location;
	protected int currentduration;
	protected boolean currentlyrunning;
	protected int totaldur;
	private int tickrate;
	
	protected PointWeather(Location location, int tickrate, int dur)
	{
		this.totaldur = dur;
		this.location = location;
		this.tickrate = tickrate;
		this.currentlyrunning = true;
		this.currentduration = 0;
	}

	public void start() 
	{
		weatherStart();
		
		new BukkitRunnable() {
			@Override
			public void run()
			{
				if(currentlyrunning == false || currentduration > totaldur || weatherEffect()) {
					stopEffect();
					cancel();
				}
			}
		}.runTaskTimer(KWeather.getPlugin(), 0, tickrate);
	}

	public void stop() 
	{
		currentlyrunning = false;
	}
	
	public static void setUp(FileConfiguration config)
	{
		Tornado.loadConfig(config);
	}
	
	public static boolean checkCommand(CommandSender sender, String[] args)
	{
		if(args[0].equalsIgnoreCase(KWeather.getLanguageLoader().getLanguage().getString("ChatCommands.set"))) {
			if(Tornado.onCommandSet(sender, args)) {
				return true;
			} else if(Meteor.onCommandSet(sender, args)) {
				return true;
			}
		} else if(args[0].equalsIgnoreCase(KWeather.getLanguageLoader().getLanguage().getString("ChatCommands.stop"))) {
			if(Tornado.onCommandStop(sender, args)) {
				return true;
			}
		}
		return false;
	}
	
	public static void stopAll()
	{
		for(PointWeatherType pwt : weatherevents) {
			pwt.stop();
		}
		weatherevents.clear();
	}
	
	public static void stopWeather(Class<?> pwtclass)
	{
		List<PointWeatherType> remove = new ArrayList<PointWeatherType>();
		for(PointWeatherType pwt : weatherevents) {
			if(pwtclass.isInstance(pwt)) {
				pwt.stop();
				remove.add(pwt);
			}
		}
		
		for(PointWeatherType pwt : remove) {
			weatherevents.remove(pwt);
		}
	}
	
	public static void naturalStart(World world)
	{
		Tornado.naturalStart(world);
	}
	
	protected String locationString()
	{
		return location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
	}
	
	protected void weatherStart() {}
	protected void stopEffect() {}
	protected boolean weatherEffect() {return false;}
	protected void weatherEnd() {}
}

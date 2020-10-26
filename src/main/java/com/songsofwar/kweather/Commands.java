package com.songsofwar.kweather;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.songsofwar.kweather.Weather.WeatherHandler;
import com.songsofwar.kweather.Weather.Point.PointWeather;
import com.songsofwar.kweather.Weather.World.WorldWeather;
import com.songsofwar.kweather.Weather.World.WorldWeatherType;

public class Commands implements CommandExecutor
{
	private static FileConfiguration language;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		language = KWeather.getLanguageLoader().getLanguage();
		if(!sender.hasPermission("kweather.admin") && !sender.isOp()) {
			String msg = KWeather.getLanguageLoader().lineInterpreter(language.getString("ChatMessages.noperm"), "");
			sendMessage(msg, sender);
			return true;
		}
		
		if(args.length == 0) {
			getUsage(sender);
			return true;
		}
		
		if(WorldWeather.checkCommands(sender, args)) {return true;}
		
		if(PointWeather.checkCommand(sender, args)) {return true;}
		
		if(args[0].equalsIgnoreCase(language.getString("ChatCommands.set"))) {
			getSetUsage(sender);
			return true;
		}
		
		if(args[0].equalsIgnoreCase(language.getString("ChatCommands.stop"))) {
			if(args.length == 1) {
				String msg = KWeather.getLanguageLoader().lineInterpreter(
						KWeather.getLanguageLoader().getLanguage().getString("ChatMessages.stoppingallweather"), "");
				Commands.sendMessage(msg, sender);
				WeatherHandler.stopAll();
				return true;
			} 
			getStopUsage(sender);
			return true;
		}
		
		if(args[0].equalsIgnoreCase(language.getString("ChatCommands.reload"))) {
			String msg = KWeather.getLanguageLoader().lineInterpreter(language.getString("ChatMessages.reload"), "");
			sendMessage(msg, sender);
			WeatherHandler.loadConfig(KWeather.reloadCon());
			return true;
		}
		getUsage(sender);
		return true;
	}
	
	public static void getUsage(CommandSender sender)
	{
		printUsages("usage", sender);
	}
	
	public static void getSetUsage(CommandSender sender) 
	{
		printUsages("set_usage", sender);
	}
	
	public void getStopUsage(CommandSender sender)
	{
		printUsages("stop_usage", sender);
	}
	
	private static void printUsages(String local, CommandSender sender)
	{
		List<String> usage = language.getStringList("ChatMessages." + local);
		String line;
		for(String s : usage) {
			line = KWeather.getLanguageLoader().lineInterpreter(s, "");
			if(KWeather.getLanguageLoader().doesPrintAllWeather(line)) {
				getWeatherTypes(line, sender);
			} else {
				sendMessage(line, sender);
			}
		}
	}
	
	private static void getWeatherTypes(String line, CommandSender sender)
	{
		sendMessage(line.replaceAll("<print_all_weathertypes>", language.getString("WeatherTypes.Tornado")), sender);
		sendMessage(line.replaceAll("<print_all_weathertypes>", language.getString("WeatherTypes.FireTornado")), sender);
		sendMessage(line.replaceAll("<print_all_weathertypes>", language.getString("WeatherTypes.Meteor")), sender);
		for(WorldWeatherType w : WorldWeather.getWeathers()) {
			sendMessage(line.replaceAll("<print_all_weathertypes>", w.getName()), sender);
		}
	}
	
	public static void sendMessage(String msg, CommandSender sender)
	{
		sender.sendMessage((sender instanceof Player) ? msg : ChatColor.stripColor(msg));
	}
}

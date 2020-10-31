package com.songsofwar.kweather.Weather.World;

import java.util.*;

import com.songsofwar.kweather.KWeather;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.scheduler.BukkitRunnable;

import com.songsofwar.kweather.Weather.WeatherHandler;

public class Puddle 
{
	private static HashSet<WorldWeatherType> currentpdlweather = new HashSet<WorldWeatherType>();
	private static ArrayList<World> wetworlds = new ArrayList<World>();
	private static ArrayList<Puddle> puddles = new ArrayList<Puddle>();
	private static double sizeincrease = 0.75;

	private static LinkedList<Location> puddlelocations = new LinkedList<Location>();
	private Location location;


	public LinkedList<Location> getPuddlelocations() {
		return puddlelocations;
	}

	public void clearPuddlelocations() {
		puddlelocations.clear();
	}

	public Puddle(Location location, WorldWeatherType wwt)
	{
		this.location = location;

		createPuddle();
		if(!currentpdlweather.contains(wwt)) {
			currentpdlweather.add(wwt);
		}
	}

	public static List<Block> getNearbyBlocks(Location location, int radius) {
		List<Block> blocks = new ArrayList<Block>();
		for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}


	private boolean setLocToPuddle(Location loc)
	{
		if(!WeatherHandler.checkValidWaterLocation(loc)) {return false;}
		Block b = loc.getBlock();
		if(!b.getType().equals(Material.AIR)) {return false;}
		b.setType(Material.WATER);
		Levelled waterlvl = (Levelled) b.getBlockData();
		waterlvl.setLevel(7);
		loc.getBlock().setBlockData(waterlvl);
		return true;
	}
	
	private void createPuddle()
	{
		if(!setLocToPuddle(location)) {return;}
		puddles.add(this);
		Random r = new Random();
		Location loc = location.clone();
		for(int x = -1; x <= 1; x++) {
			for(int z = -1; z <= 1; z++) {
				if(x == 0 && z == 0) {continue;}
				if(r.nextDouble() <= sizeincrease) {
					loc.setX(location.getX() + x);
					loc.setZ(location.getZ() + z);
					puddlelocations.add(loc.clone());
				}
			}
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(puddlelocations.isEmpty()) {
					cancel();
				} else {
					Location loc = puddlelocations.removeFirst();
					setLocToPuddle(loc);
				}
			}
		}.runTaskTimer(KWeather.getPlugin(), 20, 20);
	}
	
	public static boolean shouldPuddleStay(Location loc)
	{
		if(WeatherHandler.isDryBiome(loc) || WeatherHandler.isSnowBiome(loc)) {
			return false;
		}
		World world = loc.getWorld();
		updateWetWorlds();
		if(wetworlds.contains(world)) {
			return true;
		}
		return false;
	}
	
	public static void dryUpPuddles(World world)
	{
		ArrayList<Puddle> remove = new ArrayList<Puddle>();
		for(Puddle puddle : puddles) {
			if(puddle.getWorld().equals(world)) {
				if(puddle.getLocation().getBlock().getType().equals(Material.WATER)) {
					puddle.getLocation().getBlock().setType(Material.AIR);
					for(Block b : getNearbyBlocks(puddle.getLocation(), 2)) {
						// This is hacky I know, but meh
						BlockData bd = b.getBlockData();
						b.setType(Material.AIR, true);
						b.setBlockData(bd, true);
					}
				}
				remove.add(puddle);
			}
		}

		puddles.removeAll(remove);
	}
	
	private static void updateWetWorlds()
	{
		wetworlds.clear();
		for(WorldWeatherType wwt: currentpdlweather) {
			wetworlds.addAll(wwt.getRunningWorlds().keySet());
		}
	}
	
	public Location getLocation() {return location;}
	public World getWorld() {return location.getWorld();}
}

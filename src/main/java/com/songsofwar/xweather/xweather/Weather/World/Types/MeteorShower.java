package com.songsofwar.xweather.xweather.Weather.World.Types;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.songsofwar.xweather.xweather.Weather.WeatherHandler;
import com.songsofwar.xweather.xweather.Weather.Point.Types.Meteor;
import com.songsofwar.xweather.xweather.Weather.World.WorldWeather;

public class MeteorShower extends WorldWeather 
{
	private static MeteorShower meteorshower = null;
	
	private static int playerradius;
	private static double chancemeteor;
	private static float explosionsize;
	private static boolean firemeteor;
	
	private MeteorShower(FileConfiguration config)
	{
		super("MeteorShower", config, config.getInt("MeteorShower.TimeBetweenMeteor")*20);
		WorldWeather.addWeatherType(this);
	}
	
	public static MeteorShower setUpMeteorShower(FileConfiguration config)
	{
		if(meteorshower == null) {
			meteorshower = new MeteorShower(config);
		} else {
			meteorshower.loadConfig(config);
			meteorshower.reloadName();
		}
		return meteorshower;
	}
	
	@Override
	protected void loadMoreOptions(FileConfiguration config)
	{
		playerradius = config.getInt("MeteorShower.RadiusAroundPlayer");
		chancemeteor = config.getDouble("MeteorShower.ChanceOfMeteor");
		explosionsize = (float) config.getDouble("MeteorShower.ExplosionSize");
		firemeteor = config.getBoolean("MeteorShower.Fire");
	}
	
	@Override
	protected void weatherEffect(World w)
	{
		Random r = new Random();
		if(r.nextDouble() <= chancemeteor
				&& !w.getPlayers().isEmpty()) {
			Location loc = w.getPlayers().iterator().next().getLocation().clone();
			double dx, dz;
			dx = playerradius*r.nextDouble()*(r.nextBoolean() ? 1:-1);
			dz = playerradius*r.nextDouble()*(r.nextBoolean() ? 1:-1);
			loc.add(dx,0,dz);
			loc.setY(w.getMaxHeight());
			if(WeatherHandler.isLocationLoaded(loc) && !WeatherHandler.locationIsProtected(loc)) {
				new Meteor(loc, explosionsize, firemeteor);
			}
		}
	}
	
	public static float getExplosionSize() {return explosionsize;}
	public static boolean isFire() {return firemeteor;}
	public static int getSpawnRadius() {return playerradius;}
}

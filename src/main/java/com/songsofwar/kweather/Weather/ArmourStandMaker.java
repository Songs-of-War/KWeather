package com.songsofwar.kweather.Weather;

import com.songsofwar.kweather.KWeather;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class ArmourStandMaker 
{
	private ArmorStand as;
	
	public ArmourStandMaker(Location location,
			Material head,
			boolean visible, 
			boolean small,
			boolean gravity,
			String metadata)
	{
		as = location.getWorld().spawn(location, ArmorStand.class);
		
		as.setBasePlate(false);
		as.setArms(true);
		as.setVisible(visible);
		as.setInvulnerable(true);
		as.setCanPickupItems(false);
		as.setGravity(gravity);
		as.setSmall(small);
		as.setHelmet(new ItemStack(head));
		if(metadata != null) {
			as.setMetadata(metadata, new FixedMetadataValue(KWeather.getPlugin(), true));
		}
	}
	
	public void setVelocity(Vector v)
	{
		as.setVelocity(v);
	}
	
	public void delete()
	{
		as.remove();
	}
	
	public Location getLocation() {
		return as.getLocation();
	}
}

package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

public class EntityAltitudeNotEquals extends EntityConditionalSpawnCalculation 
{
	final int altitude;
	public EntityAltitudeNotEquals(boolean inverted, int altitude, List<SpawnCalculation> calculations)
	{ 
		this.altitude = altitude;
		this.inverted = inverted;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return eventInfo.entity.getLocation().getBlockY() != altitude;}
}

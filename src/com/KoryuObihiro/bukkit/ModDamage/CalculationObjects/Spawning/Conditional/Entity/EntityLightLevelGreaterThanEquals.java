package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Spawning.Conditional.Entity;

import java.util.List;

import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.SpawnCalculation;

public class EntityLightLevelGreaterThanEquals extends EntityConditionalSpawnCalculation 
{
	final byte lightLevel;
	public EntityLightLevelGreaterThanEquals(boolean inverted, byte lightLevel, List<SpawnCalculation> calculations)
	{ 
		this.lightLevel = lightLevel;
		this.inverted = inverted;
		this.calculations = calculations;
	}
	@Override
	public boolean condition(SpawnEventInfo eventInfo){ return eventInfo.entity.getLocation().getBlock().getLightLevel() >= lightLevel;}
}

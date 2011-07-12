package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Nestable.Conditional;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.block.Biome;

import com.KoryuObihiro.bukkit.ModDamage.Backend.DamageEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.Backend.SpawnEventInfo;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.CalculationUtility;
import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.ModDamageCalculation;

public class EntityBiome extends EntityConditionalCalculation<Biome>
{
	public EntityBiome(boolean inverted, boolean forAttacker, Biome biome, List<ModDamageCalculation> calculations)
	{ 
		super(inverted, forAttacker, biome, calculations);
	}
	
	@Override
	protected Biome getRelevantInfo(DamageEventInfo eventInfo){ return (forAttacker?eventInfo.entity_attacker:eventInfo.entity_target).getLocation().getBlock().getBiome();}
	@Override
	protected Biome getRelevantInfo(SpawnEventInfo eventInfo){ return eventInfo.entity.getLocation().getBlock().getBiome();}
	
	public static void register()
	{
		CalculationUtility.register(EntityBiome.class, Pattern.compile(CalculationUtility.ifPart + CalculationUtility.entityPart + "biome\\." + CalculationUtility.biomeRegex, Pattern.CASE_INSENSITIVE));
	}
}
package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;

import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.ModDamageElement;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class EntitySpawn extends EntityCalculationRoutine<Entity>
{
	final CreatureType creatureType;
	public EntitySpawn(String configString, EntityReference entityReference, CreatureType creatureType, IntegerMatch match)
	{
		super(configString, entityReference, match);
		this.creatureType = creatureType;
	}

	@Override
	protected void applyEffect(Entity entity, int input) 
	{
		if(input > 0)
			for(int i = 0; i < input; i++)
				entity.getLocation().getWorld().spawnCreature(entity.getLocation(), creatureType);//FIXME 0.9.7 - What if I try to spawn a Wolf_Angry? :<
	}
	
	public static void register()
	{
		CalculationRoutine.registerCalculation(EntitySpawn.class, Pattern.compile("(\\w+)effect\\.spawn\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntitySpawn getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null)
		{
			ModDamageElement element = ModDamageElement.matchElement(matcher.group(2));
			CreatureType creatureType = (element != null)?element.getCreatureType():null;
			if(element != null && creatureType != null && EntityReference.isValid(matcher.group(1)))
				return new EntitySpawn(matcher.group(), EntityReference.match(matcher.group(1)), creatureType, match);
		}
		return null;
	}
}

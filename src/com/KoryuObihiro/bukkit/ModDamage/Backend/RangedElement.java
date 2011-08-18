package com.KoryuObihiro.bukkit.ModDamage.Backend;

import org.bukkit.craftbukkit.entity.CraftArrow;
import org.bukkit.craftbukkit.entity.CraftEgg;
import org.bukkit.craftbukkit.entity.CraftFireball;
import org.bukkit.craftbukkit.entity.CraftFish;
import org.bukkit.craftbukkit.entity.CraftSnowball;
import org.bukkit.entity.Entity;

public enum RangedElement 
{
	BOW, EGG, FIREBALL, FISHINGROD, SNOWBALL;
	
	public static RangedElement matchElement(Entity entity)
	{
		if(entity instanceof CraftArrow)	return BOW;
		if(entity instanceof CraftEgg)		return EGG;
		if(entity instanceof CraftFireball)	return FIREBALL;
		if(entity instanceof CraftFish)		return FISHINGROD; 
		if(entity instanceof CraftSnowball)	return SNOWBALL;
		return null;
	}
	public static RangedElement matchElement(String key)
	{
		for(RangedElement element : RangedElement.values())
			if(element.name().equalsIgnoreCase(key))
				return element;
		return null;
	}
}

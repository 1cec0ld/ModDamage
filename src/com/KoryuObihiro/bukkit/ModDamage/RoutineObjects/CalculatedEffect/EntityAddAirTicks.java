package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculatedEffect;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Routine;

public class EntityAddAirTicks extends EntityCalculatedEffectRoutine 
{
	public EntityAddAirTicks(boolean forAttacker, List<Routine> routines) 
	{
		super(forAttacker, routines);
	}

	@Override
	protected void applyEffect(LivingEntity affectedObject, int input) 
	{
		affectedObject.setRemainingAir(affectedObject.getRemainingAir() + input);
	}

	public static void register(ModDamage routineUtility)
	{
		ModDamage.registerEffect(EntityAddAirTicks.class, Pattern.compile(ModDamage.entityRegex + "effect\\.addAirTicks", Pattern.CASE_INSENSITIVE));
	}
	
	public static EntityAddAirTicks getNew(Matcher matcher, List<Routine> routines)
	{
		if(matcher != null && routines != null)
		{
			List<Material> materials = ModDamage.matchItemAlias(matcher.group(2));
			if(!materials.isEmpty())
				return new EntityAddAirTicks(matcher.group(1).equalsIgnoreCase("attacker"), routines);
		}
		return null;
	}

}

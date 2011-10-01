package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Calculation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.Backend.EntityReference;
import com.KoryuObihiro.bukkit.ModDamage.Backend.IntegerMatching.IntegerMatch;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.CalculationRoutine;

public class PlayerAddItem extends PlayerCalculationRoutine
{
	protected final List<Material> materials;
	public PlayerAddItem(String configString, EntityReference entityReference, List<Material> materials, IntegerMatch match)
	{
		super(configString, entityReference, match);
		this.materials = materials;
	}
	
	@Override
	protected void applyEffect(Player affectedObject, int input) 
	{
		for(Material material : materials)
			affectedObject.getInventory().addItem(new ItemStack(material, input));
	}

	public static void register()
	{
		CalculationRoutine.registerCalculation(PlayerAddItem.class, Pattern.compile("(\\w+)effect\\.addItem\\.(\\w+)", Pattern.CASE_INSENSITIVE));
	}
	
	public static PlayerAddItem getNew(Matcher matcher, IntegerMatch match)
	{
		if(matcher != null && match != null)
		{
			List<Material> materials = ModDamage.matchItemAlias(matcher.group(2));
			if(!materials.isEmpty() && EntityReference.isValid(matcher.group(1)))
				return new PlayerAddItem(matcher.group(), EntityReference.match(matcher.group(1)), materials, match);
		}
		return null;
	}
}

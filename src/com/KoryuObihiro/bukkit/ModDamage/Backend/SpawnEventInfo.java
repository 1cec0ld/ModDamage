package com.KoryuObihiro.bukkit.ModDamage.Backend;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;

public class SpawnEventInfo
{
	Logger log = Logger.getLogger("Minecraft");
	
	//public final Server server; //TODO Implement me soon. :P
	
	public int eventHealth;
	public final World world;
	public final DamageElement damageElement;
	public final LivingEntity entity;
	public final String[] groups;
	
//CONSTRUCTORS
	public SpawnEventInfo(Player player) 
	{
		eventHealth = player.getHealth();
		entity = player;
		damageElement = DamageElement.GENERIC_HUMAN;
		groups = (ModDamage.multigroupPermissions
				?ModDamage.Permissions.getGroups(player.getWorld().getName(), player.getName())
				:ModDamage.Permissions.getGroup(player.getWorld().getName(), player.getName()).split(" "));
		
		world = entity.getWorld();
	}
	
	public SpawnEventInfo(LivingEntity entity) 
	{
		eventHealth = entity.getHealth();
		this.entity = entity;
		damageElement= DamageElement.matchLivingElement(entity);
		groups = null;
		
		world = entity.getWorld();	
	}
}
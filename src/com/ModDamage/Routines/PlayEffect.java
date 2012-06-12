package com.ModDamage.Routines;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Effect;
import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Variables.Ints.Constant;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.DataRef;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class PlayEffect extends Routine
{
	enum EffectType {
		BOW_FIRE(Effect.BOW_FIRE),
		CLICK1(Effect.CLICK1),
		CLICK2(Effect.CLICK2),
		DOOR_TOGGLE(Effect.DOOR_TOGGLE),
		EXTINGUISH(Effect.EXTINGUISH),
		RECORD_PLAY(Effect.RECORD_PLAY),
		GHAST_SHRIEK(Effect.GHAST_SHRIEK),
		GHAST_SHOOT(Effect.GHAST_SHOOT),
		BLAZE_SHOOT(Effect.BLAZE_SHOOT),
		SMOKE(Effect.SMOKE),
		BLOCK_BREAK(Effect.STEP_SOUND),
		POTION_BREAK(Effect.POTION_BREAK),
		ENDER_SIGNAL(Effect.ENDER_SIGNAL),
		MOBSPAWNER_FLAMES(Effect.MOBSPAWNER_FLAMES);
		
		private final Effect effect;
		private EffectType(Effect effect) { this.effect = effect; }
		public Integer dataForExtra(String extra) { return null; }
	}
	
	private final DataRef<Entity> entityRef;
	private final EffectType effectType;
	private final IntegerExp effectData;
	private final IntegerExp radius;
	protected PlayEffect(String configString, DataRef<Entity> entityRef, EffectType effectType, IntegerExp effectData, IntegerExp radius)
	{
		super(configString);
		this.entityRef = entityRef;
		this.effectType = effectType;
		this.effectData = effectData;
		this.radius = radius;
	}

	@Override
	public void run(EventData data) throws BailException
	{
		Entity entity = entityRef.get(data);
		if (entity == null) return;
		
		if (radius == null)
			entity.getWorld().playEffect(entity.getLocation(), effectType.effect, effectData.getValue(data));
		else
			entity.getWorld().playEffect(entity.getLocation(), effectType.effect, effectData.getValue(data), radius.getValue(data));
	}

	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("([a-z]+).playeffect.(\\w+)(?:\\.([^.]+))?(?:\\.radius\\.(.+))?", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}

	protected static class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public PlayEffect getNew(Matcher matcher, EventInfo info)
		{ 
			DataRef<Entity> entityRef = info.get(Entity.class, matcher.group(1).toLowerCase());
			if (entityRef == null) return null;
			
			EffectType effectType;
			try
			{
				effectType = EffectType.valueOf(matcher.group(2).toUpperCase());
			}
			catch (IllegalArgumentException e)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad effect type: \""+matcher.group(2)+"\"");
				return null;
			}
			IntegerExp data;
			if (matcher.group(3) == null)
				data = new Constant(0);
			else {
				Integer ndata = effectType.dataForExtra(matcher.group(3));
				if (ndata == null)
				{
					data = IntegerExp.getNew(matcher.group(3), info);
					
					if (data == null)
					{
						ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad extra data: \""+matcher.group(3)+"\" for " + effectType + " effect.");
						return null;
					}
				}
				else
					data = new Constant(ndata);
			}
			
			IntegerExp radius = null;
			if (matcher.group(4) != null)
			{
				radius = IntegerExp.getNew(matcher.group(4), info);
				if (radius == null)
				{
					ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \""+matcher.group(4)+"\"");
					return null;
				}
			}
			
			ModDamage.addToLogRecord(OutputPreset.INFO, "PlayEffect: " + entityRef + " " + effectType + " " + data + (radius != null? " " + radius : ""));
			return new PlayEffect(matcher.group(), entityRef, effectType, data, radius);
		}
	}
}
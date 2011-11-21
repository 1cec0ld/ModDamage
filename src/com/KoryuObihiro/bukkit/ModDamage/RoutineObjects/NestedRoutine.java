package com.KoryuObihiro.bukkit.ModDamage.RoutineObjects;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.KoryuObihiro.bukkit.ModDamage.ModDamage;
import com.KoryuObihiro.bukkit.ModDamage.PluginConfiguration.OutputPreset;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Parameterized.Delay;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Parameterized.Knockback;
import com.KoryuObihiro.bukkit.ModDamage.RoutineObjects.Parameterized.Message;

public abstract class NestedRoutine extends Routine
{
	private static RoutineBuilder builder = new RoutineBuilder();
	private static HashMap<Pattern, RoutineBuilder> registeredNestedRoutines = new HashMap<Pattern, RoutineBuilder>();

	protected NestedRoutine(String configString){ super(configString);}
	
	public static void registerVanillaRoutines()
	{
		registeredNestedRoutines.clear();
		Delay.register();
		Message.register();
		Knockback.register();
		CalculationRoutine.register();
		ConditionalRoutine.register();
		SwitchRoutine.register();
	}
	
	protected static void registerRoutine(Pattern pattern, RoutineBuilder builder)
	{
		Routine.registerRoutine(registeredNestedRoutines, pattern, builder);
	}
	
	public static NestedRoutine getNew(String key, Object nestedContent)
	{
		Matcher matcher = anyPattern.matcher(key);
		matcher.matches();
		return builder.getNew(matcher, nestedContent);
	}
	
	protected static class RoutineBuilder
	{
		public NestedRoutine getNew(Matcher anyMatcher, Object nestedContent)
		{
			for(Pattern pattern : registeredNestedRoutines.keySet())
			{
				Matcher matcher = pattern.matcher(anyMatcher.group());
				if(matcher.matches())
					return registeredNestedRoutines.get(pattern).getNew(matcher, nestedContent);
			}
			ModDamage.addToLogRecord(OutputPreset.FAILURE, " No match found for nested routine \"" + anyMatcher.group() + "\"");		
			return null;
		}
	}
	
	public static void paddedLogRecord(OutputPreset preset, String message)
	{		
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
		ModDamage.addToLogRecord(preset, message);
		ModDamage.addToLogRecord(OutputPreset.CONSOLE_ONLY, "");
	}
}

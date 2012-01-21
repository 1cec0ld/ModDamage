package com.ModDamage.Routines;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.Backend.TargetEventInfo;
import com.ModDamage.Backend.Matching.DynamicInteger;
import com.ModDamage.PluginConfiguration.OutputPreset;

public class ValueChangeRoutine extends Routine 
{
	private static final LinkedHashMap<Pattern, ValueBuilder> builders = new LinkedHashMap<Pattern, ValueBuilder>();
	protected enum ValueChangeType
	{
		Add
		{
			@Override
			void changeValue(TargetEventInfo eventInfo, int value){ eventInfo.eventValue += value;}
		},
		Set
		{
			@Override
			void changeValue(TargetEventInfo eventInfo, int value){ eventInfo.eventValue = value;}
		},
		Subtract
		{
			@Override
			void changeValue(TargetEventInfo eventInfo, int value){ eventInfo.eventValue -= value;}
		};
		abstract void changeValue(TargetEventInfo eventInfo, int value);

		public String getStringAppend(){ return " (" + this.name().toLowerCase() + ")";}

	}
	
	final protected ValueChangeType changeType;
	final protected DynamicInteger number;
	protected ValueChangeRoutine(String configString, ValueChangeType changeType, DynamicInteger number)
	{
		super(configString);
		this.changeType = changeType;
		this.number = number;
	}
	
	@Override
	public final void run(final TargetEventInfo eventInfo){ changeType.changeValue(eventInfo, getValue(eventInfo));}
	
	protected int getValue(TargetEventInfo eventInfo){ return number.getValue(eventInfo);}
	
	public static void register()
	{
		Routine.registerRoutine(Pattern.compile("(\\+|\\-|set\\.|add\\.|)(.+)", Pattern.CASE_INSENSITIVE), new RoutineBuilder());
	}
	
	private static final class RoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public ValueChangeRoutine getNew(final Matcher matcher)
		{
			ValueChangeType changeType = null;
			if(matcher.group(1).equalsIgnoreCase("-"))
				changeType = ValueChangeType.Subtract;
			else if(matcher.group(1).equalsIgnoreCase("+") || matcher.group(1).equalsIgnoreCase("add."))
				changeType = ValueChangeType.Add;
			else if(matcher.group(1).equalsIgnoreCase("") || matcher.group(1).equalsIgnoreCase("set."))
				changeType = ValueChangeType.Set;
			assert(changeType != null);
			
			for(Entry<Pattern, ValueBuilder> entry : builders.entrySet())
			{
				Matcher anotherMatcher = entry.getKey().matcher(matcher.group(2));
				if(anotherMatcher.matches())
					return entry.getValue().getNew(anotherMatcher, changeType);
			}
			DynamicInteger integer = DynamicInteger.getNew(matcher.group(2), false);
			if(integer != null)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, changeType.name() + ": " + matcher.group(2));
				return new ValueChangeRoutine(matcher.group(), changeType, integer);
			}
			return null;
		}
	}
	
	public static void registerRoutine(Pattern pattern, ValueBuilder builder){ builders.put(pattern, builder);}
	public static abstract class ValueBuilder
	{
		public abstract ValueChangeRoutine getNew(final Matcher matcher, final ValueChangeType changeType);
	}
}
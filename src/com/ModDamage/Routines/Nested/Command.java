package com.ModDamage.Routines.Nested;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import com.ModDamage.ModDamage;
import com.ModDamage.Parsing.DataProvider;
import com.ModDamage.Parsing.IDataProvider;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.Alias.AliasManager;
import com.ModDamage.Alias.CommandAliaser;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.InterpolatedString;
import com.ModDamage.Routines.Routine;

public class Command extends NestedRoutine 
{
	private final Collection<InterpolatedString> commands;
	private final CommandTarget commandTarget;
	
	private Command(String configString, CommandTarget commandTarget, Collection<InterpolatedString> commands)
	{
		super(configString);
		this.commandTarget = commandTarget;
		this.commands = commands;
	}
	
	@Override
	public void run(EventData data) throws BailException
	{
		CommandSender cmdsender = commandTarget.getCommandSender(data);
		for(InterpolatedString command : commands)
		{				
			Bukkit.dispatchCommand(cmdsender, command.toString(data));
		}
	}
	
	private abstract static class CommandTarget
	{
		protected static CommandTarget match(String key, EventInfo info)
		{
			if (key.equalsIgnoreCase("console"))
				return new CommandTarget()
					{
						@Override
						public CommandSender getCommandSender(EventData data)
						{
							CommandSender cmdsender = (CommandSender) Bukkit.getConsoleSender();
							return cmdsender;
						}

						@Override public String toString() { return "console"; }
					};
			
			{
				final IDataProvider<Entity> entityDP = DataProvider.parse(info, Entity.class, key);
				if(entityDP == null) return null;
				return new CommandTarget()
				{
					@Override
					public CommandSender getCommandSender(EventData data) throws BailException
					{
						Entity entity = entityDP.get(data);
						if (entity instanceof CommandSender)
						{
							CommandSender cmdsender = (CommandSender) entity;
							return cmdsender;
						}
						return null;
					}

					@Override public String toString() { return entityDP.toString(); }
				};
			}
		}
		
		abstract public CommandSender getCommandSender(EventData data) throws BailException;
		abstract public String toString();
	}
	
	public static void registerRoutine()
	{
		Routine.registerRoutine(Pattern.compile("command\\.(\\w+)\\.(.+)", Pattern.CASE_INSENSITIVE), new BaseRoutineBuilder());//"debug\\.(server|(?:world(\\.[a-z0-9]+))|(?:player\\.[a-z0-9]+))\\.(_[a-z0-9]+)"
	}
	public static void registerNested()
	{
		NestedRoutine.registerRoutine(Pattern.compile("command.(\\w+)", Pattern.CASE_INSENSITIVE), new NestedRoutineBuilder());
	}
	
	protected static class BaseRoutineBuilder extends Routine.RoutineBuilder
	{
		@Override
		public Command getNew(Matcher matcher, EventInfo info)
		{
			CommandTarget commandTarget = CommandTarget.match(matcher.group(1), info);
			if(commandTarget == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Bad command target: "+matcher.group(1));
				return null;
			}
			
			Collection<InterpolatedString> commands = CommandAliaser.match(matcher.group(2), info);
			if (commands == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "This command form can only be used for command aliases. Please use the following instead.");
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "    - 'command."+matcher.group(1)+"': '" + matcher.group(2) + "'");
				return null;
			}
			
			
			Command routine = new Command(matcher.group(), commandTarget, commands);
			routine.reportContents();
			return routine;
		}
	}
	
	protected static class NestedRoutineBuilder extends NestedRoutine.RoutineBuilder
	{
		@SuppressWarnings("unchecked")
		@Override
		public Command getNew(Matcher matcher, Object nestedContent, EventInfo info)
		{
			if(matcher == null || nestedContent == null)
				return null;
			
			List<String> strings = new ArrayList<String>();
			CommandTarget commandTarget = CommandTarget.match(matcher.group(1), info);
			if(commandTarget == null) return null;
			
			if (nestedContent instanceof String)
				strings.add((String)nestedContent);
			else if(nestedContent instanceof List)
				strings.addAll((List<String>) nestedContent);
			else
				return null;
			

			List<InterpolatedString> commands = new ArrayList<InterpolatedString>();
			for(String string : strings)
			{
				if (AliasManager.aliasPattern.matcher(string).matches())
				{
					Collection<InterpolatedString> istrs = CommandAliaser.match(string, info);
					if (istrs != null) 
					{
						commands.addAll(istrs);
						continue;
					}
					
					ModDamage.addToLogRecord(OutputPreset.WARNING, "Unknown command alias: "+string);
				}
				
				commands.add(new InterpolatedString(string, info, false));
			}
			
			
			Command routine = new Command(matcher.group(), commandTarget, commands);
			routine.reportContents();
			return routine;
		}
	}
	
	private void reportContents()
	{
		if(commands instanceof List)
		{
			String routineString = "Command (" + commandTarget + ")";
			List<InterpolatedString> commandList = (List<InterpolatedString>)commands;
			if(commands.size() > 1)
			{
				ModDamage.addToLogRecord(OutputPreset.INFO, routineString + ":" );
				ModDamage.changeIndentation(true);
				for(int i = 0; i < commands.size(); i++)
					ModDamage.addToLogRecord(OutputPreset.INFO, "- \"" + commandList.get(i).toString() + "\"" );
				ModDamage.changeIndentation(false);
			}
			else ModDamage.addToLogRecord(OutputPreset.INFO, routineString + ": \"" + commandList.get(0).toString() + "\"" );
		}
		else ModDamage.addToLogRecord(OutputPreset.FAILURE, "Fatal: commands are not in a linked data structure!");//shouldn't happen
	}
}
package com.ModDamage.Conditionals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ModDamage.ModDamage;
import com.ModDamage.PluginConfiguration.OutputPreset;
import com.ModDamage.StringMatcher;
import com.ModDamage.Backend.BailException;
import com.ModDamage.EventInfo.EventData;
import com.ModDamage.EventInfo.EventInfo;
import com.ModDamage.Expressions.IntegerExp;

public class Comparison extends Conditional
{
	private enum ComparisonType
	{ 
		EQUALS("==?")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 == operand2; }
		},
		NOTEQUALS("!=")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 != operand2; }
		},
		LESSTHAN("<")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 < operand2; }
		},
		LESSTHANEQUALS("<=")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 <= operand2; }
		},
		GREATERTHAN(">")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 > operand2; }
		},
		GREATERTHANEQUALS(">=")
		{
			@Override
			public boolean compare(int operand1, int operand2){ return operand1 >= operand2; }
		};
		
		public final String operator;
		
		private ComparisonType(String op)
		{
			operator = op;
		}
		
		abstract public boolean compare(int operand1, int operand2);
		
		protected static String comparisonPart;
		protected static String operatorPart;
		protected static Map<String, ComparisonType> nameMap;
		static
		{
			nameMap = new HashMap<String, ComparisonType>();
			
			comparisonPart = "";
			operatorPart = "";
			for(ComparisonType comparisonType : ComparisonType.values())
			{
				comparisonPart += comparisonType.name() + "|";
				nameMap.put(comparisonType.name(), comparisonType);
				operatorPart += comparisonType.operator + "|";
				nameMap.put(comparisonType.operator, comparisonType);
			}
			comparisonPart = comparisonPart.substring(0, comparisonPart.length() - 1);
			operatorPart = operatorPart.substring(0, operatorPart.length() - 1);
			
			nameMap = Collections.unmodifiableMap(nameMap);
		}
	}
	
	public static final Pattern namePattern = Pattern.compile("\\.(" + ComparisonType.comparisonPart + ")\\.", Pattern.CASE_INSENSITIVE);
	public static final Pattern operatorPattern = Pattern.compile("\\s*(>=?|<=?|==?|!=)\\s*"); // doing it like above won't match >= correctly
	protected final IntegerExp operand1;
	protected final IntegerExp operand2;
	protected final ComparisonType comparisonType;
	
	protected Comparison(String configString, IntegerExp operand1, ComparisonType comparisonType, IntegerExp operand2)
	{
		super(configString);
		this.operand1 = operand1;
		this.comparisonType = comparisonType;
		this.operand2 = operand2;
	}

	@Override
	protected boolean myEvaluate(EventData data) throws BailException
	{
		return comparisonType.compare(operand1.getValue(data), operand2.getValue(data));
	}

	
	public static void register()
	{
		Conditional.register(new ConditionalBuilder());
	}
	
	protected static class ConditionalBuilder extends Conditional.ConditionalBuilder
	{
		@Override
		public Conditional getNewFromFront(StringMatcher sm, EventInfo info)
		{
			IntegerExp left = IntegerExp.getIntegerFromFront(sm.spawn(), info);
			if (left == null) return null;
			
			ComparisonType comparisonType;
			
			Matcher matcher = sm.matchFront(operatorPattern);
			if (matcher != null)
			{
				comparisonType = ComparisonType.nameMap.get(matcher.group(1));
			}
			else
			{
				matcher = sm.matchFront(namePattern);
				if (matcher != null)
				{
					comparisonType = ComparisonType.nameMap.get(matcher.group(1).toUpperCase());
					
					ModDamage.addToLogRecord(OutputPreset.WARNING, "The named operator syntax is deprecated. Please use " + comparisonType.operator + "instead of ." + comparisonType.name() + ".");
				}
				else
					return null;
			}
			
			IntegerExp right = IntegerExp.getIntegerFromFront(sm.spawn(), info);
			if (right == null)
			{
				ModDamage.addToLogRecord(OutputPreset.FAILURE, "Unable to match expression: \"" + sm.string + "\"");
				return null;
			}
			
			
			if(comparisonType != null)
			{
				sm.accept();
				return new Comparison(matcher.group(), left, comparisonType, right);
			}
			return null;
		}
	}
}

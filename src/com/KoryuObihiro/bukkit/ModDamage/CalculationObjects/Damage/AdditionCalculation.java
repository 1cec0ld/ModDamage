package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

public class AdditionCalculation extends MathDamageCalculation 
{
	private int addValue;
	public AdditionCalculation(int value){ addValue = value;}
	@Override
	public int calculate(int eventDamage){ return eventDamage + addValue;}
}
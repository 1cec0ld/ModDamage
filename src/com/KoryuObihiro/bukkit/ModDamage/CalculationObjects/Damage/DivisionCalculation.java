package com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.Damage;

import org.bukkit.entity.LivingEntity;

import com.KoryuObihiro.bukkit.ModDamage.CalculationObjects.DamageCalculation;

public class DivisionCalculation extends DamageCalculation 
{
	private int divideValue;
	public DivisionCalculation(int value){ divideValue = value;}
	@Override
	public int calculate(LivingEntity target, LivingEntity attacker, int eventDamage){ return eventDamage/divideValue;}
}

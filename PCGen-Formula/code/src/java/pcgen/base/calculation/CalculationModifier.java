/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.calculation;

import pcgen.base.formula.dependency.DependencyManager;
import pcgen.base.formula.manager.ScopeInformation;

/**
 * A CalculationModifier is a Modifier that is a wrapper around a
 * NEPCalculation. A CalculationModifier also contains the user priority of the
 * Modifier.
 * 
 * @see pcgen.base.calculation.NEPCalculation
 * 
 * @param <T>
 *            The format that this CalculationModifier acts upon
 */
public final class CalculationModifier<T> implements Modifier<T>
{

	/**
	 * The user priority for this CalculationModifier.
	 */
	private final int userPriority;

	/**
	 * The NEPCalculation to be performed by this CalculationModifier.
	 */
	private final NEPCalculation<T> toDo;

	/**
	 * Constructs a new CalculationModifier from the given NEPCalculation and
	 * user priority.
	 * 
	 * The intent is that a solver would process the Modifier with the lowest
	 * user priority first.
	 * 
	 * @param calc
	 *            The NEPCalculation to be performed by this CalculationModifier
	 *            when it is processed
	 * @param userPriority
	 *            The user priority of this CalculationModifier.
	 * @throws IllegalArgumentException
	 *             if the given NEPCalculation is null
	 */
	public CalculationModifier(NEPCalculation<T> calc, int userPriority)
	{
		if (calc == null)
		{
			throw new IllegalArgumentException("Calculation cannot be null");
		}
		toDo = calc;
		this.userPriority = userPriority;
	}

	/**
	 * @see pcgen.base.modifier.Modifier#getUserPriority()
	 */
	@Override
	public int getUserPriority()
	{
		return userPriority;
	}

	/**
	 * @see pcgen.base.modifier.Modifier#process(java.lang.Object,
	 *      pcgen.base.formula.manager.ScopeInformation)
	 */
	@Override
	public T process(T input, ScopeInformation scopeInfo)
	{
		return toDo.process(input, scopeInfo);
	}

	/**
	 * @see pcgen.base.modifier.Modifier#getInherentPriority()
	 */
	@Override
	public int getInherentPriority()
	{
		return toDo.getInherentPriority();
	}

	/**
	 * @see pcgen.base.modifier.Modifier#getDependencies(pcgen.base.formula.manager.ScopeInformation,
	 *      pcgen.base.formula.dependency.DependencyManager)
	 */
	@Override
	public void getDependencies(ScopeInformation scopeInfo,
		DependencyManager fdm)
	{
		toDo.getDependencies(scopeInfo, fdm);
	}

	/**
	 * @see pcgen.base.modifier.Modifier#getInstructions()
	 */
	@Override
	public String getInstructions()
	{
		return toDo.toString();
	}

	/**
	 * @see pcgen.base.modifier.Modifier#getVariableFormat()
	 */
	@Override
	public Class<T> getVariableFormat()
	{
		return toDo.getVariableFormat();
	}

	/**
	 * @see pcgen.base.modifier.Modifier#getIdentification()
	 */
	@Override
	public String getIdentification()
	{
		return toDo.getIdentification();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return userPriority ^ toDo.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof CalculationModifier)
		{
			CalculationModifier<?> other = (CalculationModifier<?>) o;
			return (other.userPriority == userPriority)
				&& other.toDo.equals(toDo);
		}
		return false;
	}
}

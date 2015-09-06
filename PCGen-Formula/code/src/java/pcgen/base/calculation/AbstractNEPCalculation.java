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
 * An AbstractNEPCalculationis a basic template for creating a NEPCalculation.
 * No attempt is made to implement the process method (since a calculation with
 * nothing processed is meaningless), while an empty getDependencies method is
 * provided for convenience.
 * 
 * @param <T>
 *            The format of object upon which this AbstractNEPCalculation
 *            operates
 */
public abstract class AbstractNEPCalculation<T> implements NEPCalculation<T>
{
	/**
	 * The BasicCalculation underlying this AbstractNEPCalculation
	 */
	private final BasicCalculation<T> basicCalc;

	/**
	 * Constructs a new AbstractNEPCalculation with the given BasicCalculation
	 * as the underlying calculation to be performed when this
	 * AbstractNEPCalculation is processed.
	 * 
	 * @param calc
	 *            The BasicCalculation to be performed when this
	 *            AbstractNEPCalculation is processed
	 */
	public AbstractNEPCalculation(BasicCalculation<T> calc)
	{
		basicCalc = calc;
	}

	/**
	 * @see pcgen.base.calculation.CalculationInfo#getVariableFormat()
	 */
	@Override
	public Class<T> getVariableFormat()
	{
		return basicCalc.getVariableFormat();
	}

	/**
	 * @see pcgen.base.calculation.CalculationInfo#getIdentification()
	 */
	@Override
	public String getIdentification()
	{
		return basicCalc.getIdentification();
	}

	/**
	 * @see pcgen.base.calculation.NEPCalculation#getDependencies(pcgen.base.formula.manager.ScopeInformation,
	 *      pcgen.base.formula.dependency.DependencyManager)
	 */
	@Override
	public void getDependencies(ScopeInformation scopeInfo,
		DependencyManager fdm)
	{
	}

	/**
	 * @see pcgen.base.calculation.CalculationInfo#getInherentPriority()
	 */
	@Override
	public int getInherentPriority()
	{
		return basicCalc.getInherentPriority();
	}

	/**
	 * Returns the BasicCalculation underlying this AbstractNEPCalculation.
	 * 
	 * @return The BasicCalculation underlying this AbstractNEPCalculation
	 */
	public BasicCalculation<T> getBasicCalculation()
	{
		return basicCalc;
	}
}

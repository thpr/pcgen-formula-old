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
package pcgen.base.formula.error;

import pcgen.base.formula.base.FormulaSemantics;

/**
 * An InvalidVariableNotFound error represents a situation where a variable was
 * not found during evaluation of a formula.
 * 
 * Consider a formula such as 1+b. If there is not a value for variable "b" in
 * the evaluation context provided to the formula, then the evaluation can
 * result in an InvalidVariableNotFound error.
 */
public class InvalidVariableNotFound implements FormulaSemantics
{

	/**
	 * The variable name that was encountered, but was not in the evaluation
	 * context when the formula was evaluated.
	 */
	private final String varName;

	/**
	 * Constructs a new InvalidVariableNotFound for the given variable.
	 * 
	 * @param varName
	 *            The name of the variable encountered, but was not in the
	 *            evaluation context when the formula was evaluated.
	 * @throws IllegalArgumentException
	 *             if the given variable is null
	 */
	public InvalidVariableNotFound(String varName)
	{
		if (varName == null)
		{
			throw new IllegalArgumentException("Variable name cannot be null");
		}
		this.varName = varName;
	}

	/**
	 * Unconditionally returns FALSE, as InvalidVariableNotFound represents an
	 * error in a formula.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#isValid()
	 */
	@Override
	public boolean isValid()
	{
		return false;
	}

	/**
	 * Returns a report indicating details about this InvalidVariableNotFound
	 * error.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#getReport()
	 */
	@Override
	public String getReport()
	{
		return "Variable: " + varName + " was not found";
	}

	/**
	 * Not necessary to implement since isValid() returns false.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#getSemanticState()
	 */
	@Override
	public Class<?> getSemanticState()
	{
		throw new UnsupportedOperationException(
			"Meaningless: Formula is not valid");
	}
}

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
 * An InvalidFunctionNotFound represents an error in a formula because a
 * function was not found.
 * 
 * Consider the example of foo(a, b). If there is no function "foo" in the
 * evaluation context provided to the formula, then the evaluation can result in
 * an InvalidFunctionNotFound error.
 * 
 * Note: Any issues with the number or type of arguments to a function like
 * "foo" should NOT be reported with InvalidFunctionNotFound. This represents
 * that the function is NOT PRESENT, not that usage is not valid.
 */
public class InvalidFunctionNotFound implements FormulaSemantics
{
	/**
	 * Represents the function name in which the problem was encountered.
	 */
	private final String functionName;

	/**
	 * The full usage of the function.
	 * 
	 * This is provided as there may be multiple forms of functions. foo() and
	 * foo[] are different functions and the "functionName" should indicate just
	 * "foo". The fullName may provide additional information (such as "foo()")
	 * in order to fully qualify the function that it not contained in the
	 * equation context.
	 */
	private final String fullName;

	/**
	 * Constructs a new InvalidFunctionNotFound for the given function used in
	 * the given context.
	 * 
	 * Two arguments are provided as there may be multiple forms of functions.
	 * foo() and foo[] are different functions and the name should indicate just
	 * "foo". The context may provide additional information (such as "foo()")
	 * in order to fully qualify the function that it not contained in the
	 * equation context.
	 * 
	 * @param name
	 *            The name of the function not contained in the equation context
	 *            used to validate or evaluate a formula
	 * @param context
	 *            The full usage context of the function not contained in the
	 *            equation context used to validate or evaluate a formula
	 * @throws IllegalArgumentException
	 *             if any of the arguments are null
	 */
	public InvalidFunctionNotFound(String name, String context)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Function name may not be null");
		}
		if (context == null)
		{
			throw new IllegalArgumentException(
				"Function usage context may not be null");
		}
		functionName = name;
		fullName = context;
	}

	/**
	 * Unconditionally returns FALSE, as InvalidFunctionNotFound represents an
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
	 * Returns a report indicating details about this InvalidFunctionNotFound
	 * error.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#getReport()
	 */
	@Override
	public String getReport()
	{
		return "Function: " + functionName + " was not found (called as: "
			+ fullName + ")";
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

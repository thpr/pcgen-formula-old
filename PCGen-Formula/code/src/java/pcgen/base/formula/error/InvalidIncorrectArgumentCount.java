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

import java.util.Arrays;

import pcgen.base.formula.base.FormulaSemantics;

/**
 * An InvalidIncorrectArgumentCount represents an error when a function within a
 * formula has an incorrect number of arguments.
 * 
 * Consider the example of max(...), which normally would have 2 [or potentially
 * more] values. If the function appeared in a formula as max(4), then the
 * validation or evaluation of a formula may trigger an
 * InvalidIncorrectArgumentCount error.
 */
public class InvalidIncorrectArgumentCount implements FormulaSemantics
{

	/**
	 * The function name in which the problem was encountered.
	 */
	private final String functionName;

	/**
	 * The number of arguments that were expected for the function.
	 */
	private final int expectedArgCount;

	/**
	 * An array of the (incorrect number of) objects provided as arguments to
	 * the function.
	 */
	private final Object[] args;

	/**
	 * Constructs a new InvalidIncorrectArgumentCount for the given name,
	 * expected number of arguments, and actual arguments.
	 * 
	 * Note: It is assumed that ownership of the array provided to
	 * InvalidIncorrectArgumentCount is transferred to
	 * InvalidIncorrectArgumentCount. (InvalidIncorrectArgumentCount makes no
	 * attempt to clone the array or the objects contained in the array).
	 * Therefore, if the calling object needs to maintain control or otherwise
	 * modify its array, then this constructor must be provided a clone of the
	 * array.
	 * 
	 * @param name
	 *            The function name in which the problem was encountered
	 * @param expected
	 *            The number of arguments that were expected for the function
	 * @param arguments
	 *            An array of the (incorrect number of) objects provided as
	 *            arguments to the function
	 * @throws IllegalArgumentException
	 *             if any of the provided arguments are null
	 */
	public InvalidIncorrectArgumentCount(String name, int expected,
		Object[] arguments)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Function name may not be null");
		}
		if (arguments == null)
		{
			throw new IllegalArgumentException(
				"Actual Arguments may not be null");
		}
		functionName = name;
		args = arguments;
		expectedArgCount = expected;
	}

	/**
	 * Unconditionally returns FALSE, as InvalidIncorrectArgumentCount
	 * represents an error in a formula.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#isValid()
	 */
	@Override
	public boolean isValid()
	{
		return false;
	}

	/**
	 * Returns a report indicating details about this
	 * InvalidIncorrectArgumentCount error.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#getReport()
	 */
	@Override
	public String getReport()
	{
		return "Function " + functionName
			+ " received incorrect # of arguments, expected: "
			+ expectedArgCount + " got " + args.length + " "
			+ Arrays.asList(args);
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

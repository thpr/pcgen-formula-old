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
 * An InvalidBadParseStructure error represents an incorrect argument type
 * within a function.
 * 
 * Consider a function such as max(a,b), which would calculate the maximum of
 * two values. If a user provided max("A", B), that may produce an
 * InvalidBadParseStructure, as the two arguments to max should be numeric. In
 * the case of the first argument, because it is in quotes, it is a String.
 * Since the max function requires numeric values as inputs, it does not
 * understand how to process a String, and may throw an InvalidBadParseStructure
 * error.
 */
public class InvalidBadParseStructure implements FormulaSemantics
{

	/**
	 * Represents the function name in which the problem was encountered.
	 */
	private final String functionName;

	/**
	 * Represents the class that was expected as an argument to the function.
	 * Note that a function may take multiple different class types in different
	 * argument positions. This represents the expected class in the position
	 * where the expectation did not match reality.
	 * 
	 * Note also that this does not have to be a concrete class. It can be an
	 * interface or a parent class, so something like Number.class is a legal
	 * value.
	 */
	private final Class<?> expectedClass;

	/**
	 * Represents the object that was found in the position where the the
	 * expectation did not match this object. The object itself is captured,
	 * both because the class of the object is desired (so it can be diagnosed
	 * against the expectedClass), but also because the precise instance of the
	 * unexpected object may be helpful in diagnosing the issue (specifically
	 * because in the case of a multi-argument function, it helps identify
	 * precisely which argument was mismatched against the expectation).
	 */
	private final Object found;

	/**
	 * Constructs a new InvalidBadParseStructure error.
	 * 
	 * @param name
	 *            The function name in which the problem was encountered
	 * 
	 * @param expected
	 *            The class that was expected as an argument to the function
	 * @param arg
	 *            The object that was found in the position where the the
	 *            expectation did not match this object
	 * @throws IllegalArgumentException
	 *             if any of the arguments are null
	 */
	public InvalidBadParseStructure(String name, Class<?> expected, Object arg)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Function name is required");
		}
		if (expected == null)
		{
			throw new IllegalArgumentException("Expected Class is required");
		}
		if (arg == null)
		{
			throw new IllegalArgumentException("Violating Object is required");
		}
		functionName = name;
		expectedClass = expected;
		found = arg;
	}

	/**
	 * Unconditionally returns FALSE, as InvalidBadParseStructure represents an
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
	 * Returns a report indicating details about this InvalidBadParseStructure
	 * error.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#getReport()
	 */
	@Override
	public String getReport()
	{
		return "Parse Error: Function " + functionName
			+ " received invalid argument type, expected: "
			+ expectedClass.getName() + " got " + found.getClass().getName()
			+ ": " + found;
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

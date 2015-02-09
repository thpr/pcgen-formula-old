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
 * An InvalidNumber error represents an invalid numeric value in a function
 * tree.
 * 
 * Consider a formula like "C+4.5". This should contain a valid numeric value of
 * 4.5. If that value was "4..5" or "4.5." or other invalid value, then an
 * InvalidNumber error may be reported.
 * 
 * In effect, this is an "internal error" indicating that either the formula
 * parse or some modification of the formula tree has resulted in a formula that
 * is structurally unsound. InvalidNumber is thus an extremely severe error for
 * a formula to encounter and any presence likely indicates a code bug of some
 * form.
 */
public class InvalidNumber implements FormulaSemantics
{

	/**
	 * Indicates the class of the Node that encountered an invalid number.
	 */
	private final Class<?> clz;

	/**
	 * Indicates the actual text that was supposed to represent a valid number.
	 */
	private final String numberText;

	/**
	 * Constructs a new InvalidNumber indicating the class and the invalid text
	 * encountered in that class.
	 * 
	 * @param cl
	 *            The class of the Node that encountered an invalid number
	 * @param text
	 *            The actual text that was supposed to represent a valid number
	 * @throws IllegalArgumentException
	 *             if either argument is null
	 */
	public InvalidNumber(Class<?> cl, String text)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException(
				"Enclosing Node class may not be null");
		}
		if (text == null)
		{
			throw new IllegalArgumentException(
				"Invalid Number text may not be null");
		}
		clz = cl;
		numberText = text;
	}

	/**
	 * Unconditionally returns FALSE, as InvalidNumber represents an error in a
	 * formula.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#isValid()
	 */
	@Override
	public boolean isValid()
	{
		return false;
	}

	/**
	 * Returns a report indicating details about this InvalidNumber error.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#getReport()
	 */
	@Override
	public String getReport()
	{
		return clz.getClass() + " had invalid number: " + numberText;
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

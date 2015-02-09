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
 * An InvalidNotOperable error represents a situation where a visitor
 * encountered an item it cannot process in a sensible fashion (it cannot
 * operate on that item).
 * 
 * As an example, when a visitor parses the tree of a formula, it may encounter
 * a function. That function will have children (the function name and the
 * parenthesis surrounding the arguments). The children should be processed by
 * the function itself, so if the visitor encounters the string indicating the
 * function name then it is valid for it to report an InvalidNotOperable error.
 * 
 * In effect, this is an "internal error" indicating that either the formula
 * parse or some modification of the formula tree has resulted in a formula that
 * is structurally unsound. InvalidNotOperable is thus an extremely severe error
 * for a formula to encounter and any presence likely indicates a code bug of
 * some form.
 */
public class InvalidNotOperable implements FormulaSemantics
{

	/**
	 * The node that was encountered by the visitor (and upon which the visitor
	 * cannot operate).
	 */
	private final Object node;

	/**
	 * Constructs a new InvalidNotOperable for the given node.
	 * 
	 * @param n
	 *            The (inoperable) node that was encountered by the visitor
	 * @throws IllegalArgumentException
	 *             if the argument is null
	 */
	public InvalidNotOperable(Object n)
	{
		if (n == null)
		{
			throw new IllegalArgumentException(
				"Inoperable Node may not be null");
		}
		node = n;
	}

	/**
	 * Unconditionally returns FALSE, as InvalidNotOperable represents an error
	 * in a formula.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#isValid()
	 */
	@Override
	public boolean isValid()
	{
		return false;
	}

	/**
	 * Returns a report indicating details about this InvalidNotOperable error.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#getReport()
	 */
	@Override
	public String getReport()
	{
		return "Parse Error: Invalid Class: " + node.getClass().getName()
			+ " found in operable location (class cannot be evaluated)";
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

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
import pcgen.base.formula.parse.Operator;

/**
 * An InvalidSemantics error represents a situation where a visitor encountered
 * an item it cannot process in a sensible fashion (it cannot operate on that
 * item because it is not a number and a number is required for the given
 * operation).
 * 
 * As an example, the operation: "min(4<5,6)" will trigger an InvalidSemantics
 * error as "4<5" does not return a numeric value (it returns a Boolean
 * TRUE/FALSE).
 * 
 * This is usually an indication of a user error where a Boolean operation was
 * used in a function where a numeric calculation is required.
 */
public class InvalidSemantics implements FormulaSemantics
{

	/**
	 * The node that was encountered by the visitor (and upon which the visitor
	 * cannot operate).
	 */
	private final Object node;

	/**
	 * The Operator being processed, if any
	 * 
	 * null is a valid value
	 */
	private final Operator operator;

	/**
	 * The Class of object required, or if an Operator-based error, the first
	 * class provided to the operator.
	 */
	private final Class<?> reqFormat;

	/**
	 * The Class of object found (which was not what was required), or if an
	 * Operator-based error, the second class provided to the operator.
	 */
	private final Class<?> foundFormat;

	/**
	 * Constructs a new InvalidSemantics occurring on the given Node and with
	 * the given required and actual formats.
	 * 
	 * @param node
	 *            The (non-numeric) node that was encountered by the visitor
	 * @param reqFormat
	 *            The required format of object in the formula
	 * @param foundFormat
	 *            The format found when a different format was expected
	 * @throws IllegalArgumentException
	 *             if any argument is null
	 */
	public InvalidSemantics(Object node, Class<?> reqFormat,
		Class<?> foundFormat)
	{
		if (node == null)
		{
			throw new IllegalArgumentException(
				"Inoperable Node may not be null");
		}
		if (reqFormat == null)
		{
			throw new IllegalArgumentException(
				"Inoperable Required Format may not be null");
		}
		if (foundFormat == null)
		{
			throw new IllegalArgumentException(
				"Inoperable Node Format may not be null");
		}
		this.node = node;
		this.reqFormat = reqFormat;
		this.foundFormat = foundFormat;
		operator = null;
	}

	/**
	 * Constructs a new InvalidSemantics occurring on the given Node and with
	 * the non-numeric Class type.
	 * 
	 * @param node
	 *            The (non-numeric) node that was encountered by the visitor
	 * @param operator
	 *            The Operator performing a given calculation
	 * @param reqFormat
	 *            The first format provided to the operator
	 * @param foundFormat
	 *            The second format provided to the operator
	 * @throws IllegalArgumentException
	 *             if any argument is null
	 */
	public InvalidSemantics(Object node, Operator operator, Class<?> reqFormat,
		Class<?> foundFormat)
	{
		if (node == null)
		{
			throw new IllegalArgumentException(
				"Inoperable Node may not be null");
		}
		if (operator == null)
		{
			throw new IllegalArgumentException("Operator may not be null");
		}
		if (reqFormat == null)
		{
			throw new IllegalArgumentException(
				"Inoperable Required Format may not be null");
		}
		if (foundFormat == null)
		{
			throw new IllegalArgumentException(
				"Inoperable Node Format may not be null");
		}
		this.node = node;
		this.operator = operator;
		this.reqFormat = reqFormat;
		this.foundFormat = foundFormat;
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
		if (operator == null)
		{
			return "Parse Error: Invalid Value Format: " + foundFormat
				+ " found in " + node.getClass().getName()
				+ " found in location requiring a " + reqFormat.getSimpleName()
				+ " (class cannot be evaluated)";
		}
		else
		{
			return "Parse Error: Operator " + operator.getSymbol()
				+ " cannot process children: " + foundFormat.getSimpleName()
				+ " and " + reqFormat.getSimpleName() + " found in "
				+ node.getClass().getName();
		}
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

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
import pcgen.base.formula.parse.Node;

/**
 * An InvalidChildCount error represents an incorrect number of children in a
 * function tree.
 * 
 * Consider an operation like ADD. In a tree format, this should have 2 children
 * (the two objects to be added). If the operation has less or more children,
 * then it can be reported with InvalidChildCount.
 * 
 * In effect, this is an "internal error" indicating that either the formula
 * parse or some modification of the formula tree has resulted in a formula that
 * is structurally unsound. InvalidChildCount is thus an extremely severe error
 * for a formula to encounter and any presence likely indicates a code bug of
 * some form.
 * 
 * As a note, InvalidChildCount is should not used when a function is determined
 * to have an invalid number of arguments. This is for two reasons: (1)
 * Functions can have a variable number of arguments, and a more complete
 * diagnosis of arguments is needed and not provided in InvalidChildCount (2)
 * The implied severity of InvalidChildCount is higher than that of an invalid
 * number of arguments (InvalidChildCount is a tree structure problem, whereas
 * incorrect arguments is a user typo).
 */
public class InvalidChildCount implements FormulaSemantics
{

	/**
	 * The "parent" node which did not have the expected number of children.
	 */
	private final Node node;

	/**
	 * The number of children that were expected for the parent node.
	 */
	private final int expectedCount;

	/**
	 * Constructs a new InvalidChildCount, indicating an unexpected number of
	 * children for the given node.
	 * 
	 * @param n
	 *            The "parent" node which did not have the expected number of
	 *            children.
	 * @param expected
	 *            The number of children expected for the parent node type.
	 * @throws IllegalArgumentException
	 *             if the given node is null or the expected value is less than
	 *             zero
	 */
	public InvalidChildCount(Node n, int expected)
	{
		if (n == null)
		{
			throw new IllegalArgumentException("Node may not be null");
		}
		if (expected < 0)
		{
			throw new IllegalArgumentException(
				"Expected number of nodes must be >= 0");
		}
		node = n;
		expectedCount = expected;
	}

	/**
	 * Unconditionally returns FALSE, as InvalidChildCount represents an error
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
	 * Returns a report indicating details about this InvalidChildCount error.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#getReport()
	 */
	@Override
	public String getReport()
	{
		int argLength = node.jjtGetNumChildren();
		Node[] args = new Node[argLength];
		for (int i = 0; i < argLength; i++)
		{
			args[i] = node.jjtGetChild(i);
		}
		return "Parse Error: Item of type " + node.getClass().getName()
			+ " had incorrect children from parse. Expected " + expectedCount
			+ " got " + args.length + " " + Arrays.asList(args);
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

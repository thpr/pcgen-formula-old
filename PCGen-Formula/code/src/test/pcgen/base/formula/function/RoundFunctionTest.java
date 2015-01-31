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
package pcgen.base.formula.function;

import org.junit.Test;

import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.testsupport.AbstractFormulaTestCase;
import pcgen.base.formula.testsupport.TestUtilities;
import pcgen.base.formula.visitor.ReconstructionVisitor;

public class RoundFunctionTest extends AbstractFormulaTestCase
{

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		library.addFunction(new RoundFunction());
	}

	@Test
	public void testInvalidTooManyArg()
	{
		String formula = "round(2, 3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
	}

	@Test
	public void testNotValidNoVar()
	{
		String formula = "round(ab)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
	}

	@Test
	public void testIntegerPositive()
	{
		String formula = "round(1)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(1));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testIntegerNegative()
	{
		String formula = "round(-2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(-2));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoublePositive()
	{
		String formula = "round(6.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(6));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoublePositivePointFiveUp()
	{
		String formula = "round(6.5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(7));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoubleNegative()
	{
		String formula = "round(-5.8)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(-6));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoubleNegativePointFiveUp()
	{
		String formula = "round(-5.5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(-5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoubleNegativePointFiveUpLeadingSpace()
	{
		String formula = "round( -5.5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(-5));
	}

	@Test
	public void testDoubleNegativePointFiveUpTrailingSpace()
	{
		String formula = "round(-5.5 )";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(-5));
	}

	@Test
	public void testDoubleNegativePointFiveUpSeparatingSpace()
	{
		String formula = "round (-5.5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(-5));
	}

	//TODO Need to check variable capture
	//TODO Need to check static with a variable
}

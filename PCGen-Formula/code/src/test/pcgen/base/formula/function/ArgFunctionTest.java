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

import pcgen.base.formula.dependency.ArgumentDependencyManager;
import pcgen.base.formula.dependency.DependencyManager;
import pcgen.base.formula.operator.number.NumberEquals;
import pcgen.base.formula.operator.number.NumberGreaterThan;
import pcgen.base.formula.operator.number.NumberLessThan;
import pcgen.base.formula.parse.ASTNum;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.testsupport.AbstractFormulaTestCase;
import pcgen.base.formula.testsupport.TestUtilities;
import pcgen.base.formula.util.KeyUtilities;
import pcgen.base.formula.visitor.ReconstructionVisitor;

public class ArgFunctionTest extends AbstractFormulaTestCase
{

	private ArgumentDependencyManager argManager;
	private DependencyManager depManager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ASTNum four = new ASTNum(0);
		four.setToken("4");
		ASTNum five = new ASTNum(1);
		five.setToken("5");
		String formula = "abs(-4.5)";
		SimpleNode node = TestUtilities.doParse(formula);
		Node[] array = new Node[]{four, five, node};
		ftnLibrary.addFunction(new ArgFunction(array));
		ftnLibrary.addFunction(new AbsFunction());
		opLibrary.addAction(new NumberEquals());
		opLibrary.addAction(new NumberLessThan());
		opLibrary.addAction(new NumberGreaterThan());
		resetManager();
	}

	private void resetManager()
	{
		depManager = new DependencyManager();
		argManager = new ArgumentDependencyManager();
		depManager.addDependency(KeyUtilities.DEP_ARGUMENT, argManager);
	}

	@Test
	public void testInvalidWrongArg()
	{
		String formula = "arg()";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
		formula = "arg(2, 3)";
		node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
	}

	@Test
	public void testNoArg()
	{
		String formula = "4";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(-1, argManager.getMaximumArgument());
		evaluatesTo(formula, node, Integer.valueOf(4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testInvalidTooHigh()
	{
		String formula = "arg(4)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
	}

	@Test
	public void testInvalidTooLow()
	{
		String formula = "arg(-1)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
	}

	@Test
	public void testInvalidDouble()
	{
		String formula = "arg(1.5)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
	}

	@Test
	public void testInvalidNaN()
	{
		String formula = "arg(\"string\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
	}

	@Test
	public void testArgZero()
	{
		String formula = "arg(0)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(0, argManager.getMaximumArgument());
		evaluatesTo(formula, node, Integer.valueOf(4));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testArgOne()
	{
		String formula = "arg(1)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(1, argManager.getMaximumArgument());
		evaluatesTo(formula, node, Integer.valueOf(5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
		DependencyManager fdm = new DependencyManager();
		/*
		 * Safe and "ignored" - if this test fails, need to change what FDM is
		 * passed in - it should NOT contain an ArgumentDependencyManager
		 */
		assertTrue(null == fdm.getDependency(KeyUtilities.DEP_ARGUMENT));
		varCapture.visit(node, fdm);
	}

	@Test
	public void testComplex()
	{
		String formula = "arg(2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		varCapture.visit(node, depManager);
		assertEquals(2, argManager.getMaximumArgument());
		evaluatesTo(formula, node, Double.valueOf(4.5));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

}

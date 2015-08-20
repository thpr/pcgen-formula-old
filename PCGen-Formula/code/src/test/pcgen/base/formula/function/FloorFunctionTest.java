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

import java.util.List;

import org.junit.Test;

import pcgen.base.formula.dependency.DependencyManager;
import pcgen.base.formula.dependency.VariableDependencyManager;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.testsupport.AbstractFormulaTestCase;
import pcgen.base.formula.testsupport.TestUtilities;
import pcgen.base.formula.util.KeyUtilities;
import pcgen.base.formula.variable.VariableID;
import pcgen.base.formula.visitor.ReconstructionVisitor;

public class FloorFunctionTest extends AbstractFormulaTestCase
{

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		ftnLibrary.addFunction(new FloorFunction());
	}

	@Test
	public void testInvalidTooManyArg()
	{
		String formula = "floor(2, 3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
	}

	@Test
	public void testNotValidString()
	{
		String formula = "floor(\"ab\")";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
	}

	@Test
	public void testNotValidNoVar()
	{
		String formula = "floor(ab)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node);
	}

	@Test
	public void testIntegerPositive()
	{
		String formula = "floor(1)";
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
		String formula = "floor(-2)";
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
		String formula = "floor(6.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(6));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoubleNegative()
	{
		String formula = "floor(-5.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(-6));
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testDoubleNegativeLeadingSpace()
	{
		String formula = "floor( -5.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(-6));
	}

	@Test
	public void testDoubleNegativeTrailingSpace()
	{
		String formula = "floor(-5.3 )";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(-6));
	}

	@Test
	public void testDoubleNegativeSeparatingSpace()
	{
		String formula = "floor (-5.3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, true);
		evaluatesTo(formula, node, Integer.valueOf(-6));
	}

	@Test
	public void testVariable()
	{
		store.put(getVariable("a"), 5);
		String formula = "floor(a)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node);
		isStatic(formula, node, false);
		DependencyManager depManager = new DependencyManager();
		VariableDependencyManager varManager = new VariableDependencyManager();
		depManager.addDependency(KeyUtilities.DEP_VARIABLE, varManager);
		varCapture.visit(node, depManager);
		List<VariableID<?>> vars = varManager.getVariables();
		assertEquals(1, vars.size());
		VariableID<?> var = vars.get(0);
		assertEquals("a", var.getName());
	}
}

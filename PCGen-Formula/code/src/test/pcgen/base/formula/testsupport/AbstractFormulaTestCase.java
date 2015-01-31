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
package pcgen.base.formula.testsupport;

import java.util.List;

import junit.framework.TestCase;
import pcgen.base.formula.base.FormulaDependencyManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.manager.FormulaManager;
import pcgen.base.formula.manager.FunctionLibrary;
import pcgen.base.formula.manager.OperatorLibrary;
import pcgen.base.formula.manager.SimpleFormulaDependencyManager;
import pcgen.base.formula.manager.SimpleFunctionLibrary;
import pcgen.base.formula.manager.SimpleOperatorLibrary;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.variable.ScopeTypeDefLibrary;
import pcgen.base.formula.variable.ScopeTypeDefinition;
import pcgen.base.formula.variable.SimpleVariableStore;
import pcgen.base.formula.variable.VariableID;
import pcgen.base.formula.variable.VariableLibrary;
import pcgen.base.formula.variable.VariableScope;
import pcgen.base.formula.variable.VariableTypeDefinition;
import pcgen.base.formula.visitor.DependencyCaptureVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.formula.visitor.ValidVisitor;

public abstract class AbstractFormulaTestCase extends TestCase
{

	protected ValidVisitor valid;
	protected FunctionLibrary library;
	protected OperatorLibrary opLibrary;
	protected StaticVisitor staticVisitor;
	protected EvaluateVisitor eval;
	protected DependencyCaptureVisitor varCapture;
	protected SimpleVariableStore store;
	protected VariableScope<?> globalScope;
	private ScopeTypeDefLibrary stDefLib;
	private VariableLibrary varLibrary;
	protected ScopeTypeDefinition<?> globalScopeDef;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		stDefLib = new ScopeTypeDefLibrary();
		opLibrary = new SimpleOperatorLibrary();
		VariableTypeDefinition<Number> vtd =
				new VariableTypeDefinition<Number>(Number.class, "VAR");
		stDefLib.defineGlobalScopeDefinition(vtd);
		globalScopeDef = stDefLib.getGlobalScopeDefinition("VAR");
		varLibrary = new VariableLibrary(stDefLib);
		globalScope = varLibrary.instantiateScope(null, globalScopeDef);
		library = new SimpleFunctionLibrary();
		staticVisitor = new StaticVisitor(library);
		store = new SimpleVariableStore();
		FormulaManager fm = new FormulaManager(library, opLibrary, varLibrary, store);
		valid = new ValidVisitor(fm, globalScopeDef);
		eval = new EvaluateVisitor(fm, globalScope);
		varCapture = new DependencyCaptureVisitor(fm, globalScope);
	}

	public void isValid(String formula, SimpleNode node)
	{
		FormulaSemantics validity =
				(FormulaSemantics) node.jjtAccept(valid, null);
		if (!validity.isValid())
		{
			TestCase.fail("Expected Valid Formula: " + formula
				+ " but was told: " + validity.getReport());
		}
	}

	public void isStatic(String formula, SimpleNode node, boolean b)
	{
		Boolean isStatic = (Boolean) node.jjtAccept(staticVisitor, null);
		if (isStatic.booleanValue() != b)
		{
			TestCase.fail("Expected Static (" + b + ") Formula: " + formula);
		}
	}

	public void evaluatesTo(String formula, SimpleNode node, Object valueOf)
	{
		Object result = node.jjtAccept(eval, null);
		if (result.equals(valueOf))
		{
			return;
		}
		//Try ints as double as well just in case (temporary)
		if (valueOf instanceof Integer)
		{
			if (result.equals(valueOf))
			{
				return;
			}
		}
		//Give Doubles a bit of fuzz
		else if (valueOf instanceof Double)
		{
			if (TestUtilities.doubleEqual(((Double) valueOf).doubleValue(),
				((Number) result).doubleValue(), TestUtilities.SMALL_ERROR))
			{
				return;
			}
		}
		TestCase.fail("Expected " + valueOf.getClass().getSimpleName() + " ("
			+ valueOf + ") for Formula: " + formula + ", was " + result + " ("
			+ result.getClass().getSimpleName() + ")");
	}

	protected void isNotValid(String formula, SimpleNode node)
	{
		FormulaSemantics validity =
				(FormulaSemantics) node.jjtAccept(valid, null);
		if (validity.isValid())
		{
			TestCase.fail("Expected Invalid Formula: " + formula
				+ " but was valid");
		}
	}

	protected List<VariableID<?>> getVariables(String formula, SimpleNode node)
	{
		FormulaDependencyManager fdm =
				(FormulaDependencyManager) varCapture.visit(node,
					new SimpleFormulaDependencyManager());
		return fdm.getVariables();
	}

	protected VariableID<Number> getVariable(String formula)
	{
		varLibrary.assertVariableScope(globalScopeDef, formula);
		return (VariableID<Number>) varLibrary.getVariableID(globalScope, formula);
	}

}

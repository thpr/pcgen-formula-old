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
package pcgen.base.testsupport;

import java.util.List;

import junit.framework.TestCase;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.FormatManager;
import pcgen.base.format.NumberManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.dependency.DependencyManager;
import pcgen.base.formula.dependency.VariableDependencyManager;
import pcgen.base.formula.manager.FormulaManager;
import pcgen.base.formula.manager.FunctionLibrary;
import pcgen.base.formula.manager.LegalScopeLibrary;
import pcgen.base.formula.manager.OperatorLibrary;
import pcgen.base.formula.manager.VariableLibrary;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.semantics.FormulaSemantics;
import pcgen.base.formula.semantics.FormulaValidity;
import pcgen.base.formula.util.KeyUtilities;
import pcgen.base.formula.util.SplitFormulaSetup;
import pcgen.base.formula.util.SplitFormulaSetup.IndividualSetup;
import pcgen.base.formula.variable.SimpleLegalScope;
import pcgen.base.formula.variable.VariableID;
import pcgen.base.formula.variable.WriteableVariableStore;

public abstract class AbstractFormulaTestCase extends TestCase
{

	protected FormatManager<Number> numberManager = new NumberManager();
	protected FormatManager<Boolean> booleanManager = new BooleanManager();

	private SplitFormulaSetup setup;
	private IndividualSetup localSetup;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		setup = new SplitFormulaSetup();
		setup.getLegalScopeLibrary().registerScope(
			new SimpleLegalScope(null, "Global"));
		localSetup = setup.getIndividualSetup("Global");
	}

	public void isValid(String formula, SimpleNode node,
		FormatManager<?> formatManager)
	{
		FormulaSemantics semantics =
				localSetup.formulaManager.isValid(node, getGlobalScope(),
					formatManager);
		if (!semantics.getInfo(KeyUtilities.SEM_VALID).isValid())
		{
			TestCase.fail("Expected Valid Formula: " + formula
				+ " but was told: "
				+ semantics.getInfo(KeyUtilities.SEM_REPORT).getReport());
		}
	}

	public void isStatic(String formula, SimpleNode node, boolean b)
	{
		if (localSetup.scopeInfo.isStatic(node) != b)
		{
			TestCase.fail("Expected Static (" + b + ") Formula: " + formula);
		}
	}

	public void evaluatesTo(String formula, SimpleNode node, Object valueOf)
	{
		Object result = localSetup.scopeInfo.evaluate(node);
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

	protected void isNotValid(String formula, SimpleNode node,
		FormatManager<?> formatManager)
	{
		FormulaSemantics semantics =
				localSetup.formulaManager.isValid(node, getGlobalScope(),
					formatManager);
		FormulaValidity isValid = semantics.getInfo(KeyUtilities.SEM_VALID);
		if (isValid.isValid())
		{
			TestCase.fail("Expected Invalid Formula: " + formula
				+ " but was valid");
		}
	}

	protected List<VariableID<?>> getVariables(SimpleNode node)
	{
		DependencyManager fdm = new DependencyManager();
		VariableDependencyManager vdm = new VariableDependencyManager();
		fdm.addDependency(KeyUtilities.DEP_VARIABLE, vdm);
		localSetup.scopeInfo.getDependencies(node, fdm);
		return vdm.getVariables();
	}

	protected VariableID<Number> getVariable(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		variableLibrary.assertLegalVariableID(formula, localSetup.globalScope,
			numberManager);
		return (VariableID<Number>) variableLibrary.getVariableID(
			localSetup.globalScopeInst, formula);
	}

	protected VariableID<Boolean> getBooleanVariable(String formula)
	{
		VariableLibrary variableLibrary = getVariableLibrary();
		variableLibrary.assertLegalVariableID(formula, localSetup.globalScope,
			booleanManager);
		return (VariableID<Boolean>) variableLibrary.getVariableID(
			localSetup.globalScopeInst, formula);
	}

	protected FunctionLibrary getFunctionLibrary()
	{
		return localSetup.formulaManager.getLibrary();
	}

	protected OperatorLibrary getOperatorLibrary()
	{
		return localSetup.formulaManager.getOperatorLibrary();
	}

	protected VariableLibrary getVariableLibrary()
	{
		return localSetup.formulaManager.getFactory();
	}

	protected WriteableVariableStore getVariableStore()
	{
		return (WriteableVariableStore) localSetup.formulaManager.getResolver();
	}

	protected LegalScope getGlobalScope()
	{
		return localSetup.globalScope;
	}

	protected ScopeInstance getGlobalScopeInst()
	{
		return localSetup.globalScopeInst;
	}

	protected FormulaManager getFormulaManager()
	{
		return localSetup.formulaManager;
	}

	protected LegalScopeLibrary getScopeLibrary()
	{
		return setup.getLegalScopeLibrary();
	}
}

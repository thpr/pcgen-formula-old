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
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.dependency.DependencyManager;
import pcgen.base.formula.dependency.VariableDependencyManager;
import pcgen.base.formula.manager.FormulaManager;
import pcgen.base.formula.manager.FunctionLibrary;
import pcgen.base.formula.manager.LegalScopeLibrary;
import pcgen.base.formula.manager.OperatorLibrary;
import pcgen.base.formula.manager.ScopeInstanceFactory;
import pcgen.base.formula.manager.SimpleFunctionLibrary;
import pcgen.base.formula.manager.SimpleOperatorLibrary;
import pcgen.base.formula.manager.VariableLibrary;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.semantics.FormulaSemantics;
import pcgen.base.formula.semantics.FormulaSemanticsUtilities;
import pcgen.base.formula.semantics.FormulaValidity;
import pcgen.base.formula.util.KeyUtilities;
import pcgen.base.formula.variable.NamespaceDefinition;
import pcgen.base.formula.variable.SimpleLegalScope;
import pcgen.base.formula.variable.SimpleVariableStore;
import pcgen.base.formula.variable.VariableID;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;

public abstract class AbstractFormulaTestCase extends TestCase
{

	private ScopeInstanceFactory instanceFactory;
	private SemanticsVisitor valid;
	protected FunctionLibrary ftnLibrary;
	protected OperatorLibrary opLibrary;
	private StaticVisitor staticVisitor;
	private EvaluateVisitor eval;
	protected DependencyVisitor varCapture;
	protected SimpleVariableStore store;
	protected LegalScope globalScope;
	protected ScopeInstance globalScopeInst;
	private LegalScopeLibrary scopeLibrary;
	protected VariableLibrary varLibrary;
	NamespaceDefinition<Number> varNSdef = new NamespaceDefinition<Number>(
		Number.class, "VAR");

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scopeLibrary = new LegalScopeLibrary();
		instanceFactory = new ScopeInstanceFactory(scopeLibrary);
		opLibrary = new SimpleOperatorLibrary();
		varLibrary = new VariableLibrary(scopeLibrary);
		globalScope = new SimpleLegalScope(null, "Global");
		globalScopeInst = instanceFactory.getInstance(null, globalScope);
		ftnLibrary = new SimpleFunctionLibrary();
		staticVisitor = new StaticVisitor(ftnLibrary);
		store = new SimpleVariableStore();
		FormulaManager fm =
				new FormulaManager(ftnLibrary, opLibrary, varLibrary, store);
		valid = new SemanticsVisitor(fm, globalScope, varNSdef);
		eval = new EvaluateVisitor(fm, globalScopeInst, varNSdef);
		varCapture = new DependencyVisitor(fm, globalScopeInst, varNSdef);
	}

	public void isValid(String formula, SimpleNode node)
	{
		FormulaSemantics semantics =
				FormulaSemanticsUtilities.getInitializedSemantics();
		node.jjtAccept(valid, semantics);
		if (!semantics.getInfo(KeyUtilities.SEM_VALID).isValid())
		{
			TestCase.fail("Expected Valid Formula: " + formula
				+ " but was told: "
				+ semantics.getInfo(KeyUtilities.SEM_REPORT).getReport());
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
		FormulaSemantics fs =
				FormulaSemanticsUtilities.getInitializedSemantics();
		node.jjtAccept(valid, fs);
		FormulaValidity isValid = fs.getInfo(KeyUtilities.SEM_VALID);
		if (isValid.isValid())
		{
			TestCase.fail("Expected Invalid Formula: " + formula
				+ " but was valid");
		}
	}

	protected List<VariableID<?>> getVariables(String formula, SimpleNode node)
	{
		DependencyManager fdm = new DependencyManager();
		VariableDependencyManager vdm = new VariableDependencyManager();
		fdm.addDependency(KeyUtilities.DEP_VARIABLE, vdm);
		varCapture.visit(node, fdm);
		return vdm.getVariables();
	}

	protected VariableID<Number> getVariable(String formula)
	{
		varLibrary.assertLegalVariableID(globalScope, varNSdef, formula);
		return varLibrary.getVariableID(globalScopeInst, varNSdef, formula);
	}

}

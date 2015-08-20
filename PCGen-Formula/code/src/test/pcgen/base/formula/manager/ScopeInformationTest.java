/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.formula.manager;

import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.dependency.DependencyManager;
import pcgen.base.formula.dependency.VariableDependencyManager;
import pcgen.base.formula.parse.FormulaParser;
import pcgen.base.formula.parse.ParseException;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.util.FormulaUtilities;
import pcgen.base.formula.util.KeyUtilities;
import pcgen.base.formula.variable.NamespaceDefinition;
import pcgen.base.formula.variable.SimpleLegalScope;
import pcgen.base.formula.variable.SimpleVariableStore;
import pcgen.base.formula.variable.VariableID;

public class ScopeInformationTest extends TestCase
{

	private ScopeInstanceFactory instanceFactory;
	private LegalScopeLibrary scopeLibrary;
	private VariableLibrary varLibrary;
	private SimpleFunctionLibrary ftnLibrary;
	private SimpleOperatorLibrary opLibrary;
	private SimpleVariableStore resultsStore;
	private DependencyManager depManager;
	private VariableDependencyManager varManager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scopeLibrary = new LegalScopeLibrary();
		instanceFactory = new ScopeInstanceFactory(scopeLibrary);
		varLibrary = new VariableLibrary(scopeLibrary);
		opLibrary = new SimpleOperatorLibrary();
		ftnLibrary = new SimpleFunctionLibrary();
		resultsStore = new SimpleVariableStore();
		depManager = new DependencyManager();
		varManager = new VariableDependencyManager();
		depManager.addDependency(KeyUtilities.DEP_VARIABLE, varManager);
	}

	@Test
	public void testDoubleConstructor()
	{
		FormulaManager fManager =
				new FormulaManager(ftnLibrary, opLibrary, varLibrary,
					resultsStore);
		NamespaceDefinition<Number> varDef =
				new NamespaceDefinition<Number>(Number.class, "VAR");
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = instanceFactory.getInstance(null, varScope);
		try
		{
			new ScopeInformation(null, null, null);
			fail("nulls must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
		try
		{
			new ScopeInformation(fManager, globalInst, null);
			fail("null namespace must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
		try
		{
			new ScopeInformation(fManager, null, varDef);
			fail("null scope must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
		try
		{
			new ScopeInformation(null, globalInst, varDef);
			fail("null manager must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
		try
		{
			ScopeInformation scopeInfo =
					new ScopeInformation(fManager, globalInst, varDef);
			assertEquals(fManager, scopeInfo.getFormulaManager());
			assertEquals(globalInst, scopeInfo.getScope());
		}
		catch (NullPointerException e)
		{
			fail(e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetDependencies()
	{
		FormulaManager fManager =
				new FormulaManager(ftnLibrary, opLibrary, varLibrary,
					resultsStore);
		NamespaceDefinition<Number> nsDef =
				new NamespaceDefinition<Number>(Number.class, "VAR");
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = instanceFactory.getInstance(null, varScope);
		ScopeInformation scopeInfo =
				new ScopeInformation(fManager, globalInst, nsDef);
		try
		{
			scopeInfo.getDependencies(null, depManager);
			fail("getDependencies should reject null root");
		}
		catch (IllegalArgumentException e)
		{
			//yep
		}
		try
		{
			SimpleNode fp =
					new FormulaParser(new StringReader("myvar+yourvar"))
						.query();
			scopeInfo.getDependencies(fp, null);
			fail("getDependencies should reject null dependency manager");
		}
		catch (IllegalArgumentException e)
		{
			//yep
		}
		catch (ParseException e)
		{
			fail(e.getMessage());
		}
		FormulaUtilities.loadBuiltInOperators(opLibrary);
		varLibrary.assertLegalVariableID(varScope, nsDef, "myvar");
		varLibrary.assertLegalVariableID(varScope, nsDef, "yourvar");
		try
		{
			SimpleNode fp =
					new FormulaParser(new StringReader("myvar+yourvar"))
						.query();
			scopeInfo.getDependencies(fp, depManager);
			List<VariableID<?>> vars = varManager.getVariables();
			assertEquals(2, vars.size());
			VariableID<?> v1 = vars.get(0);
			assertEquals("myvar", v1.getName());
			assertEquals(Number.class, v1.getVariableFormat());
			assertEquals(globalInst, v1.getScope());
			VariableID<?> v2 = vars.get(1);
			assertEquals("yourvar", v2.getName());
			assertEquals(Number.class, v2.getVariableFormat());
			assertEquals(globalInst, v2.getScope());
			fp = new FormulaParser(new StringReader("3+4")).query();
			DependencyManager depManager2 = new DependencyManager();
			VariableDependencyManager varManager2 = new VariableDependencyManager();
			depManager2.addDependency(KeyUtilities.DEP_VARIABLE, varManager);
			scopeInfo.getDependencies(fp, depManager2);
			assertEquals(0, varManager2.getVariables().size());
		}
		catch (ParseException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testIsStatic()
	{
		FormulaManager fManager =
				new FormulaManager(ftnLibrary, opLibrary, varLibrary,
					resultsStore);
		NamespaceDefinition<Number> nsDef =
				new NamespaceDefinition<Number>(Number.class, "VAR");
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = instanceFactory.getInstance(null, varScope);
		ScopeInformation scopeInfo =
				new ScopeInformation(fManager, globalInst, nsDef);
		try
		{
			scopeInfo.isStatic(null);
			fail("isStatic should reject null root");
		}
		catch (IllegalArgumentException e)
		{
			//yep
		}
		FormulaUtilities.loadBuiltInOperators(opLibrary);
		varLibrary.assertLegalVariableID(varScope, nsDef, "myvar");
		varLibrary.assertLegalVariableID(varScope, nsDef, "yourvar");
		try
		{
			SimpleNode fp =
					new FormulaParser(new StringReader("myvar+yourvar"))
						.query();
			assertFalse(scopeInfo.isStatic(fp));
			fp = new FormulaParser(new StringReader("6+4")).query();
			assertTrue(scopeInfo.isStatic(fp));
		}
		catch (ParseException e)
		{
			fail(e.getMessage());
		}
	}

	@Test
	public void testEvaluate()
	{
		FormulaManager fManager =
				new FormulaManager(ftnLibrary, opLibrary, varLibrary,
					resultsStore);
		NamespaceDefinition<Number> nsDef =
				new NamespaceDefinition<Number>(Number.class, "VAR");
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = instanceFactory.getInstance(null, varScope);
		ScopeInformation scopeInfo =
				new ScopeInformation(fManager, globalInst, nsDef);
		try
		{
			scopeInfo.evaluate(null);
			fail("evaluate should reject null root");
		}
		catch (IllegalArgumentException e)
		{
			//yep
		}
		FormulaUtilities.loadBuiltInOperators(opLibrary);
		varLibrary.assertLegalVariableID(varScope, nsDef, "myvar");
		varLibrary.assertLegalVariableID(varScope, nsDef, "yourvar");
		try
		{
			SimpleNode fp;
			fp = new FormulaParser(new StringReader("6+4")).query();
			assertEquals(10, scopeInfo.evaluate(fp));
			fp = new FormulaParser(new StringReader("myvar+yourvar")).query();
			assertEquals(0, scopeInfo.evaluate(fp));
		}
		catch (ParseException e)
		{
			fail(e.getMessage());
		}
	}
}

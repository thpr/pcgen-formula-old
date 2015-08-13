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

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.formula.FormulaUtilities;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.parse.FormulaParser;
import pcgen.base.formula.parse.ParseException;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.variable.NamespaceDefinition;
import pcgen.base.formula.variable.ScopedNamespaceDefinition;
import pcgen.base.formula.variable.ScopedNamespaceDefinitionLibrary;
import pcgen.base.formula.variable.SimpleVariableStore;
import pcgen.base.formula.variable.VariableLibrary;

public class FormulaManagerTest extends TestCase
{

	private ScopedNamespaceDefinitionLibrary stDefLib;
	private VariableLibrary varLibrary;
	private SimpleFunctionLibrary ftnLibrary;
	private SimpleOperatorLibrary opLibrary;
	private SimpleVariableStore resultsStore;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		stDefLib = new ScopedNamespaceDefinitionLibrary();
		varLibrary = new VariableLibrary(stDefLib);
		opLibrary = new SimpleOperatorLibrary();
		ftnLibrary = new SimpleFunctionLibrary();
		resultsStore = new SimpleVariableStore();
	}

	@Test
	public void testDoubleConstructor()
	{
		try
		{
			new FormulaManager(null, null, null, null);
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
			new FormulaManager(null, opLibrary, varLibrary, resultsStore);
			fail("null ftn lib must be rejected");
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
			new FormulaManager(ftnLibrary, null, varLibrary, resultsStore);
			fail("null op lib must be rejected");
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
			new FormulaManager(ftnLibrary, opLibrary, null, resultsStore);
			fail("null var lib must be rejected");
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
			new FormulaManager(ftnLibrary, opLibrary, varLibrary, null);
			fail("null results must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
	}
	
	@Test
	public void testIsValid()
	{
		FormulaManager manager = new FormulaManager(ftnLibrary, opLibrary, varLibrary, resultsStore);
		NamespaceDefinition<Number> nsDef = new NamespaceDefinition<Number>(Number.class, "VAR");
		ScopedNamespaceDefinition<Number> snDef = stDefLib.defineGlobalNamespaceDefinition(nsDef);
		try
		{
			manager.isValid(null, snDef);
			fail("isValid should reject null root");
		}
		catch (IllegalArgumentException e)
		{
			//yep
		}
		try
		{
			SimpleNode fp = new FormulaParser(new StringReader("myvar+yourvar")).query();
			manager.isValid(fp, null);
			fail("isValid should reject null manager");
		}
		catch (ParseException e)
		{
			fail(e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			//yep
		}
		FormulaUtilities.loadBuiltInOperators(opLibrary);
		try
		{
			SimpleNode fp = new FormulaParser(new StringReader("4==1")).query();
			FormulaSemantics valid = manager.isValid(fp, snDef);
			assertFalse("Should reject Boolean return value", valid.isValid());
		}
		catch (ParseException e)
		{
			fail(e.getMessage());
		}
		varLibrary.assertVariableScope(snDef, "myvar");
		try
		{
			SimpleNode fp = new FormulaParser(new StringReader("myvar+yourvar")).query();
			FormulaSemantics valid = manager.isValid(fp, snDef);
			assertFalse("Should reject missing var", valid.isValid());
		}
		catch (ParseException e)
		{
			fail(e.getMessage());
		}
		varLibrary.assertVariableScope(snDef, "yourvar");
		try
		{
			SimpleNode fp = new FormulaParser(new StringReader("myvar+yourvar")).query();
			FormulaSemantics valid = manager.isValid(fp, snDef);
			assertTrue(valid.getReport(), valid.isValid());
		}
		catch (ParseException e)
		{
			fail(e.getMessage());
		}
	}

}
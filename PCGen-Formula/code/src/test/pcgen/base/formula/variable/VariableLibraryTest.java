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
package pcgen.base.formula.variable;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.manager.LegalScopeLibrary;
import pcgen.base.formula.manager.ScopeInstanceFactory;
import pcgen.base.formula.manager.VariableLibrary;

public class VariableLibraryTest extends TestCase
{

	private ScopeInstanceFactory instanceFactory;
	private LegalScopeLibrary varScopeLib;
	private VariableLibrary varLib;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		varScopeLib = new LegalScopeLibrary();
		instanceFactory = new ScopeInstanceFactory(varScopeLib);
		varLib = new VariableLibrary(varScopeLib);
	}

	@Test
	public void testNullConstructor()
	{
		try
		{
			new VariableLibrary(null);
			fail("null must be rejected in constructor");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testAssertVariableFail()
	{
		NamespaceDefinition moveDef =
				new NamespaceDefinition(Number.class, "MOVE");
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		try
		{
			varLib.assertLegalVariableID(globalScope, moveDef, null);
			fail("null var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.assertLegalVariableID(globalScope, moveDef, "");
			fail("empty var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.assertLegalVariableID(globalScope, moveDef, " Walk");
			fail("padded var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.assertLegalVariableID(globalScope, moveDef, "Walk ");
			fail("padded var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.assertLegalVariableID(globalScope, null, "Walk");
			fail("null namespace must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.assertLegalVariableID(null, moveDef, "Walk");
			fail("null scope must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		//Just to check
		try
		{
			assertFalse(varLib.isLegalVariableID(globalScope, moveDef, null));
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		assertFalse(varLib.isLegalVariableID(globalScope, moveDef, ""));
		assertFalse(varLib.isLegalVariableID(globalScope, moveDef, " Walk"));
		assertFalse(varLib.isLegalVariableID(globalScope, moveDef, "Walk "));
	}

	@Test
	public void testAssertVariable()
	{
		NamespaceDefinition moveDef =
				new NamespaceDefinition(Number.class, "MOVE");
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		SimpleLegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		SimpleLegalScope eqScope =
				new SimpleLegalScope(globalScope, "Equipment");
		SimpleLegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		assertTrue(varLib.assertLegalVariableID(globalScope, moveDef, "Walk"));
		//Dupe is safe
		assertTrue(varLib.assertLegalVariableID(globalScope, moveDef, "Walk"));
		//Check child
		assertFalse(varLib.assertLegalVariableID(eqScope, moveDef, "Walk"));
		//Check child recursive
		assertFalse(varLib.assertLegalVariableID(eqPartScope, moveDef, "Walk"));

		assertTrue(varLib.assertLegalVariableID(eqScope, moveDef, "Float"));
		//Check child
		assertFalse(varLib.assertLegalVariableID(eqPartScope, moveDef, "Float"));
		//Check parent
		assertFalse(varLib.assertLegalVariableID(globalScope, moveDef, "Float"));
		//Allow peer
		assertTrue(varLib.assertLegalVariableID(spScope, moveDef, "Float"));

		assertTrue(varLib.assertLegalVariableID(eqPartScope, moveDef, "Hover"));
		//Check parent
		assertFalse(varLib.assertLegalVariableID(eqScope, moveDef, "Hover"));
		//Check parent recursive
		assertFalse(varLib.assertLegalVariableID(globalScope, moveDef, "Hover"));

		assertTrue(varLib.assertLegalVariableID(spScope, moveDef, "Drive"));
		//Check peer child
		assertTrue(varLib.assertLegalVariableID(eqPartScope, moveDef, "Drive"));

		assertTrue(varLib.assertLegalVariableID(spScope, moveDef, "Fly"));
		//Check peer with children
		assertTrue(varLib.assertLegalVariableID(eqScope, moveDef, "Fly"));

	}

	@Test
	public void testIsLegalVIDFail()
	{
		NamespaceDefinition moveDef =
				new NamespaceDefinition(Number.class, "MOVE");
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		assertTrue(varLib.assertLegalVariableID(globalScope, moveDef, "Walk"));
		try
		{
			varLib.isLegalVariableID(null, moveDef, "Walk");
			fail("null namespace must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.isLegalVariableID(globalScope, null, "Walk");
			fail("null scope must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			assertFalse(varLib.isLegalVariableID(globalScope, moveDef, null));
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testIsLegalVID()
	{
		NamespaceDefinition moveDef =
				new NamespaceDefinition(Number.class, "MOVE");
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		SimpleLegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		SimpleLegalScope eqScope =
				new SimpleLegalScope(globalScope, "Equipment");
		SimpleLegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		assertTrue(varLib.assertLegalVariableID(globalScope, moveDef, "Walk"));
		assertTrue(varLib.isLegalVariableID(globalScope, moveDef, "Walk"));
		assertFalse(varLib.isLegalVariableID(globalScope, moveDef, "Run"));
		//Works for child
		assertTrue(varLib.isLegalVariableID(eqScope, moveDef, "Walk"));
		//Works for child recursively
		assertTrue(varLib.isLegalVariableID(eqPartScope, moveDef, "Walk"));

		assertTrue(varLib.assertLegalVariableID(eqScope, moveDef, "Float"));
		assertTrue(varLib.isLegalVariableID(eqScope, moveDef, "Float"));
		//Works for child 
		assertTrue(varLib.isLegalVariableID(eqPartScope, moveDef, "Float"));
		//but not parent
		assertFalse(varLib.isLegalVariableID(globalScope, moveDef, "Float"));
		//and not peer
		assertFalse(varLib.isLegalVariableID(spScope, moveDef, "Float"));

		assertTrue(varLib.assertLegalVariableID(eqPartScope, moveDef, "Hover"));
		assertTrue(varLib.isLegalVariableID(eqPartScope, moveDef, "Hover"));
		//but not parent
		assertFalse(varLib.isLegalVariableID(eqScope, moveDef, "Hover"));
		//or parent recursively
		assertFalse(varLib.isLegalVariableID(globalScope, moveDef, "Hover"));
		//and not unrelated
		assertFalse(varLib.isLegalVariableID(spScope, moveDef, "Hover"));
	}

	@Test
	public void testKnownVarScopeFail()
	{
		NamespaceDefinition moveDef =
				new NamespaceDefinition(Number.class, "MOVE");
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		assertTrue(varLib.assertLegalVariableID(globalScope, moveDef, "Walk"));
		try
		{
			varLib.getKnownLegalScopes(moveDef, null);
			fail("null name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getKnownLegalScopes(null, "Good");
			fail("null namespace must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getKnownLegalScopes(moveDef, "");
			fail("empty name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getKnownLegalScopes(moveDef, "Walk ");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getKnownLegalScopes(moveDef, " Walk");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testKnownVarScope()
	{
		NamespaceDefinition moveDef =
				new NamespaceDefinition(Number.class, "MOVE");
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		LegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		LegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		LegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		assertTrue(varLib.assertLegalVariableID(globalScope, moveDef, "Walk"));
		assertTrue(varLib.assertLegalVariableID(eqScope, moveDef, "Float"));
		assertTrue(varLib.assertLegalVariableID(eqPartScope, moveDef, "Hover"));
		assertTrue(varLib.assertLegalVariableID(spScope, moveDef, "Hover"));
		List<LegalScope> list = varLib.getKnownLegalScopes(moveDef, "Walk");
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(globalScope, list.iterator().next());
		//Assert independence (would be a conflict)
		try
		{
			list.add(spScope);
			assertFalse(varLib.getKnownLegalScopes(moveDef, "Walk").contains(
				spScope));
		}
		catch (UnsupportedOperationException e)
		{
			//also ok if list was unwriteable
		}

		list = varLib.getKnownLegalScopes(moveDef, "Float");
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(eqScope, list.iterator().next());
		//Assert independence (no conflict)
		try
		{
			list.add(spScope);
			assertFalse(varLib.getKnownLegalScopes(moveDef, "Float").contains(
				spScope));
		}
		catch (UnsupportedOperationException e)
		{
			//also ok if list was unwriteable
		}

		list = varLib.getKnownLegalScopes(moveDef, "Hover");
		assertNotNull(list);
		assertEquals(2, list.size());
		assertTrue(list.contains(spScope));
		assertTrue(list.contains(eqPartScope));
	}

	@Test
	public void testGetVIDFail()
	{
		NamespaceDefinition moveDef =
				new NamespaceDefinition(Number.class, "MOVE");
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst =
				instanceFactory.getInstance(null, globalScope);
		LegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		LegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		ScopeInstance eqInst = instanceFactory.getInstance(globalInst, eqScope);
		try
		{
			varLib.getVariableID(globalInst, null, "Walk");
			fail("null namespace must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(null, moveDef, "Walk");
			fail("null scope must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(globalInst, moveDef, null);
			fail("null name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(globalInst, moveDef, "");
			fail("empty name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(globalInst, moveDef, " Walk");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(globalInst, moveDef, "Walk ");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			varLib.getVariableID(globalInst, moveDef, "Walk");
			fail("undefined name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//undefined, ok
		}
		try
		{
			varLib.getVariableID(eqInst, moveDef, "Walk");
			fail("undefined name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//undefined, ok
		}
		assertTrue(varLib.assertLegalVariableID(spScope, moveDef, "Float"));
		try
		{
			varLib.getVariableID(eqInst, moveDef, "Float");
			fail("undefined name (unrelated scope) must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//undefined, ok
		}

	}

	@Test
	public void testGetVID()
	{
		NamespaceDefinition moveDef =
				new NamespaceDefinition(Number.class, "MOVE");
		NamespaceDefinition flagDef =
				new NamespaceDefinition(Boolean.class, "FLAG");
		LegalScope globalScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst =
				instanceFactory.getInstance(null, globalScope);
		LegalScope spScope = new SimpleLegalScope(globalScope, "Spell");
		ScopeInstance spInst = instanceFactory.getInstance(globalInst, spScope);
		LegalScope eqScope = new SimpleLegalScope(globalScope, "Equipment");
		ScopeInstance eqInst = instanceFactory.getInstance(globalInst, eqScope);
		LegalScope eqPartScope = new SimpleLegalScope(eqScope, "Part");
		ScopeInstance eqPartInst =
				instanceFactory.getInstance(eqInst, eqPartScope);
		assertTrue(varLib.assertLegalVariableID(globalScope, moveDef, "Walk"));
		assertTrue(varLib.assertLegalVariableID(eqScope, moveDef, "Float"));
		assertTrue(varLib.assertLegalVariableID(eqPartScope, moveDef, "Hover"));
		assertTrue(varLib.assertLegalVariableID(spScope, flagDef, "Hover"));
		VariableID vid = varLib.getVariableID(globalInst, moveDef, "Walk");
		assertEquals("Walk", vid.getName());
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(eqInst, moveDef, "Float");
		assertEquals("Float", vid.getName());
		assertEquals(eqInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(eqInst, moveDef, "Walk");
		assertEquals("Walk", vid.getName());
		//NOTE: Global scope here even though eqScope was passed into getVariableID
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(eqPartInst, moveDef, "Walk");
		assertEquals("Walk", vid.getName());
		//NOTE: Global scope here even though eqPartScope was passed into getVariableID
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(eqPartInst, moveDef, "Hover");
		assertEquals("Hover", vid.getName());
		assertEquals(eqPartInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(eqPartInst, moveDef, "Hover");
		assertEquals("Hover", vid.getName());
		assertEquals(eqPartInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = varLib.getVariableID(spInst, flagDef, "Hover");
		assertEquals("Hover", vid.getName());
		assertEquals(spInst, vid.getScope());
		assertEquals(Boolean.class, vid.getVariableFormat());
	}

	@Test
	public void testProveReuse()
	{
		NamespaceDefinition varDef =
				new NamespaceDefinition(Number.class, "VAR");
		NamespaceDefinition moveDef =
				new NamespaceDefinition(Number.class, "MOVE");
		NamespaceDefinition flagDef =
				new NamespaceDefinition(Boolean.class, "FLAG");
		SimpleLegalScope globalScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst =
				instanceFactory.getInstance(null, globalScope);
		SimpleLegalScope eqScope =
				new SimpleLegalScope(globalScope, "Equipment");

		assertTrue(varLib.assertLegalVariableID(globalScope, varDef, "Walk"));
		VariableID vidv = varLib.getVariableID(globalInst, varDef, "Walk");
		assertEquals("Walk", vidv.getName());
		assertEquals(globalInst, vidv.getScope());
		assertEquals(Number.class, vidv.getVariableFormat());

		assertTrue(varLib.assertLegalVariableID(globalScope, moveDef, "Walk"));
		VariableID vidm = varLib.getVariableID(globalInst, moveDef, "Walk");
		assertEquals("Walk", vidm.getName());
		assertEquals(globalInst, vidm.getScope());
		assertEquals(Number.class, vidm.getVariableFormat());

		assertTrue(varLib.assertLegalVariableID(globalScope, flagDef, "Walk"));
		VariableID vidf = varLib.getVariableID(globalInst, flagDef, "Walk");
		assertEquals("Walk", vidf.getName());
		assertEquals(globalInst, vidf.getScope());
		assertEquals(Boolean.class, vidf.getVariableFormat());

		assertFalse(vidv.equals(vidf));
		assertFalse(vidv.equals(vidm));
		assertFalse(vidm.equals(vidf));
		assertFalse(vidm.equals(vidv));
		assertFalse(vidf.equals(vidm));
		assertFalse(vidf.equals(vidv));

	}
}

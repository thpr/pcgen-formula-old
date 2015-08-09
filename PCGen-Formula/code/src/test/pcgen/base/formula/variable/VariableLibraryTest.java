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

public class VariableLibraryTest extends TestCase
{

	private ScopeTypeDefLibrary stDefLib;
	private VariableLibrary library;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		stDefLib = new ScopeTypeDefLibrary();
		library = new VariableLibrary(stDefLib);
	}

	@Test
	public void testAssertVariableFail()
	{
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		try
		{
			library.assertVariableScope(gmDef, null);
			fail("null var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.assertVariableScope(gmDef, "");
			fail("empty var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.assertVariableScope(gmDef, " Walk");
			fail("padded var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.assertVariableScope(gmDef, "Walk ");
			fail("padded var must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.assertVariableScope(null, "Walk");
			fail("null def must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		//Just to check
		try
		{
			assertFalse(library.isLegalVariableID(gmDef, null));
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		assertFalse(library.isLegalVariableID(gmDef, ""));
		assertFalse(library.isLegalVariableID(gmDef, " Walk"));
		assertFalse(library.isLegalVariableID(gmDef, "Walk "));
	}

	@Test
	public void testAssertVariable()
	{
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		assertTrue(library.assertVariableScope(gmDef, "Walk"));
		//Dupe is safe
		assertTrue(library.assertVariableScope(gmDef, "Walk"));
		ScopeTypeDefinition eqMove =
				stDefLib.getScopeDefinition(gmDef, "EQUIPMENT");
		ScopeTypeDefinition eqPartMove =
				stDefLib.getScopeDefinition(eqMove, "EQUIPMENT.PART");
		ScopeTypeDefinition spMove =
				stDefLib.getScopeDefinition(gmDef, "SPELL");
		//Check child
		assertFalse(library.assertVariableScope(eqMove, "Walk"));
		//Check child recursive
		assertFalse(library.assertVariableScope(eqPartMove, "Walk"));

		assertTrue(library.assertVariableScope(eqMove, "Float"));
		//Check child
		assertFalse(library.assertVariableScope(eqPartMove, "Float"));
		//Check parent
		assertFalse(library.assertVariableScope(gmDef, "Float"));
		//Allow peer
		assertTrue(library.assertVariableScope(spMove, "Float"));

		assertTrue(library.assertVariableScope(eqPartMove, "Hover"));
		//Check parent
		assertFalse(library.assertVariableScope(eqMove, "Hover"));
		//Check parent recursive
		assertFalse(library.assertVariableScope(gmDef, "Hover"));

		assertTrue(library.assertVariableScope(spMove, "Drive"));
		//Check peer child
		assertTrue(library.assertVariableScope(eqPartMove, "Drive"));

		assertTrue(library.assertVariableScope(spMove, "Fly"));
		//Check peer with children
		assertTrue(library.assertVariableScope(eqMove, "Fly"));

	}

	@Test
	public void testIsLegalVIDFail()
	{
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		assertTrue(library.assertVariableScope(gmDef, "Walk"));
		try
		{
			library.isLegalVariableID(null, "Walk");
			fail("null def must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			assertFalse(library.isLegalVariableID(gmDef, null));
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testIsLegalVID()
	{
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		ScopeTypeDefinition eqMove =
				stDefLib.getScopeDefinition(gmDef, "EQUIPMENT");
		ScopeTypeDefinition eqPartMove =
				stDefLib.getScopeDefinition(eqMove, "EQUIPMENT.PART");
		ScopeTypeDefinition spMove =
				stDefLib.getScopeDefinition(gmDef, "SPELL");
		assertTrue(library.assertVariableScope(gmDef, "Walk"));
		assertTrue(library.isLegalVariableID(gmDef, "Walk"));
		assertFalse(library.isLegalVariableID(gmDef, "Run"));
		//Works for child
		assertTrue(library.isLegalVariableID(eqMove, "Walk"));
		//Works for child recursively
		assertTrue(library.isLegalVariableID(eqPartMove, "Walk"));

		assertTrue(library.assertVariableScope(eqMove, "Float"));
		assertTrue(library.isLegalVariableID(eqMove, "Float"));
		//Works for child 
		assertTrue(library.isLegalVariableID(eqPartMove, "Float"));
		//but not parent
		assertFalse(library.isLegalVariableID(gmDef, "Float"));
		//and not peer
		assertFalse(library.isLegalVariableID(spMove, "Float"));

		assertTrue(library.assertVariableScope(eqPartMove, "Hover"));
		assertTrue(library.isLegalVariableID(eqPartMove, "Hover"));
		//but not parent
		assertFalse(library.isLegalVariableID(eqMove, "Hover"));
		//or parent recursively
		assertFalse(library.isLegalVariableID(gmDef, "Hover"));
		//and not unrelated
		assertFalse(library.isLegalVariableID(spMove, "Hover"));
	}

	@Test
	public void testKnownVarScopeFail()
	{
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		assertTrue(library.assertVariableScope(gmDef, "Walk"));
		try
		{
			library.getKnownVariableScopes(null);
			fail("null name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getKnownVariableScopes("");
			fail("empty name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getKnownVariableScopes("Walk ");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getKnownVariableScopes(" Walk");
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
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		ScopeTypeDefinition eqMove =
				stDefLib.getScopeDefinition(gmDef, "EQUIPMENT");
		ScopeTypeDefinition eqPartMove =
				stDefLib.getScopeDefinition(eqMove, "EQUIPMENT.PART");
		ScopeTypeDefinition spMove =
				stDefLib.getScopeDefinition(gmDef, "SPELL");
		assertTrue(library.assertVariableScope(gmDef, "Walk"));
		assertTrue(library.assertVariableScope(eqMove, "Float"));
		assertTrue(library.assertVariableScope(eqPartMove, "Hover"));
		assertTrue(library.assertVariableScope(spMove, "Hover"));
		List<ScopeTypeDefinition<?>> list =
				library.getKnownVariableScopes("Walk");
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(gmDef, list.iterator().next());
		//Assert independence (would be a conflict)
		try
		{
			list.add(spMove);
			assertFalse(library.getKnownVariableScopes("Walk").contains(spMove));
		}
		catch (UnsupportedOperationException e)
		{
			//ok
		}

		list = library.getKnownVariableScopes("Float");
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(eqMove, list.iterator().next());
		//Assert independence (no conflict)
		try
		{
			list.add(spMove);
			assertFalse(library.getKnownVariableScopes("Float")
				.contains(spMove));
		}
		catch (UnsupportedOperationException e)
		{
			//ok
		}

		list = library.getKnownVariableScopes("Hover");
		assertNotNull(list);
		assertEquals(2, list.size());
		assertTrue(list.contains(spMove));
		assertTrue(list.contains(eqPartMove));
	}

	@Test
	public void testInstantiateScopeFail()
	{
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		ScopeTypeDefinition eqMove =
				stDefLib.getScopeDefinition(gmDef, "EQUIPMENT");
		ScopeTypeDefinition eqPartMove =
				stDefLib.getScopeDefinition(eqMove, "EQUIPMENT.PART");
		ScopeTypeDefinition spMove =
				stDefLib.getScopeDefinition(gmDef, "SPELL");

		try
		{
			library.instantiateScope(null, null);
			fail("null def must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		VariableScope parent = library.instantiateScope(null, gmDef);
		try
		{
			library.instantiateScope(parent, null);
			fail("null def must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.instantiateScope(parent, gmDef);
			fail("instantation of global with parent must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.instantiateScope(null, eqMove);
			fail("null parent with non-global def must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.instantiateScope(parent, eqPartMove);
			fail("attempt to instantiate non-child must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}

	}

	@Test
	public void testInstantiateScope()
	{
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		ScopeTypeDefinition eqMove =
				stDefLib.getScopeDefinition(gmDef, "EQUIPMENT");
		ScopeTypeDefinition eqPartMove =
				stDefLib.getScopeDefinition(eqMove, "EQUIPMENT.PART");
		ScopeTypeDefinition spMove =
				stDefLib.getScopeDefinition(gmDef, "SPELL");
		VariableScope globalScope = library.instantiateScope(null, gmDef);
		assertNull(globalScope.getParentScope());
		assertEquals(gmDef, globalScope.getScopeDefinition());
		VariableScope eqScope = library.instantiateScope(globalScope, eqMove);
		assertNotNull(eqScope.getParentScope());
		assertEquals(globalScope, eqScope.getParentScope());
		assertEquals(eqMove, eqScope.getScopeDefinition());
		VariableScope eqPartScope =
				library.instantiateScope(eqScope, eqPartMove);
		assertNotNull(eqPartScope.getParentScope());
		assertEquals(eqScope, eqPartScope.getParentScope());
		assertEquals(eqPartMove, eqPartScope.getScopeDefinition());
		//independence...
		VariableScope eqPartScope2 =
				library.instantiateScope(eqScope, eqPartMove);
		assertFalse(eqPartScope2 == eqPartScope);
		assertFalse(eqPartScope2.equals(eqPartScope));
	}

	@Test
	public void testGetVIDFail()
	{
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		ScopeTypeDefinition eqMove =
				stDefLib.getScopeDefinition(gmDef, "EQUIPMENT");
		ScopeTypeDefinition eqPartMove =
				stDefLib.getScopeDefinition(eqMove, "EQUIPMENT.PART");
		ScopeTypeDefinition spMove =
				stDefLib.getScopeDefinition(gmDef, "SPELL");
		VariableScope globalScope = library.instantiateScope(null, gmDef);
		VariableScope eqScope = library.instantiateScope(globalScope, eqMove);
		VariableScope eqPartScope =
				library.instantiateScope(eqScope, eqPartMove);
		try
		{
			library.getVariableID(null, "Walk");
			fail("null scope must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getVariableID(globalScope, null);
			fail("null name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getVariableID(globalScope, "");
			fail("empty name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getVariableID(globalScope, " Walk");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getVariableID(globalScope, "Walk ");
			fail("padded name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getVariableID(globalScope, "Walk");
			fail("undefined name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//undefined, ok
		}
		try
		{
			library.getVariableID(eqScope, "Walk");
			fail("undefined name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//undefined, ok
		}
		assertTrue(library.assertVariableScope(spMove, "Float"));
		try
		{
			library.getVariableID(eqScope, "Float");
			fail("undefined name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//undefined, ok
		}

	}

	@Test
	public void testGetVID()
	{
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		NamespaceDefinition flag =
				new NamespaceDefinition(Boolean.class, "FLAG");
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		ScopeTypeDefinition gfDef = stDefLib.defineGlobalScopeDefinition(flag);
		ScopeTypeDefinition eqMove =
				stDefLib.getScopeDefinition(gmDef, "EQUIPMENT");
		ScopeTypeDefinition eqPartMove =
				stDefLib.getScopeDefinition(eqMove, "EQUIPMENT.PART");
		ScopeTypeDefinition spFlag =
				stDefLib.getScopeDefinition(gfDef, "SPELL");
		VariableScope globalScope = library.instantiateScope(null, gmDef);
		VariableScope globalFlagScope = library.instantiateScope(null, gfDef);
		VariableScope eqScope = library.instantiateScope(globalScope, eqMove);
		VariableScope eqPartScope =
				library.instantiateScope(eqScope, eqPartMove);
		VariableScope spScope =
				library.instantiateScope(globalFlagScope, spFlag);
		assertTrue(library.assertVariableScope(gmDef, "Walk"));
		assertTrue(library.assertVariableScope(eqMove, "Float"));
		assertTrue(library.assertVariableScope(eqPartMove, "Hover"));
		assertTrue(library.assertVariableScope(spFlag, "Hover"));
		VariableID vid = library.getVariableID(globalScope, "Walk");
		assertEquals("Walk", vid.getName());
		assertEquals(globalScope, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = library.getVariableID(eqScope, "Float");
		assertEquals("Float", vid.getName());
		assertEquals(eqScope, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = library.getVariableID(eqScope, "Walk");
		assertEquals("Walk", vid.getName());
		//NOTE: Global scope here even though eqScope was passed into getVariableID
		assertEquals(globalScope, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = library.getVariableID(eqPartScope, "Walk");
		assertEquals("Walk", vid.getName());
		//NOTE: Global scope here even though eqPartScope was passed into getVariableID
		assertEquals(globalScope, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = library.getVariableID(eqPartScope, "Hover");
		assertEquals("Hover", vid.getName());
		assertEquals(eqPartScope, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = library.getVariableID(eqPartScope, "Hover");
		assertEquals("Hover", vid.getName());
		assertEquals(eqPartScope, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());

		vid = library.getVariableID(spScope, "Hover");
		assertEquals("Hover", vid.getName());
		assertEquals(spScope, vid.getScope());
		assertEquals(Boolean.class, vid.getVariableFormat());
	}


	@Test
	public void testProveReuse()
	{
		NamespaceDefinition var =
				new NamespaceDefinition(Number.class, "VAR");
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		NamespaceDefinition flag =
				new NamespaceDefinition(Boolean.class, "FLAG");
		ScopeTypeDefinition gvDef = stDefLib.defineGlobalScopeDefinition(var);
		assertNotNull(gvDef);
		ScopeTypeDefinition gmDef = stDefLib.defineGlobalScopeDefinition(move);
		assertNotNull(gmDef);
		ScopeTypeDefinition gfDef = stDefLib.defineGlobalScopeDefinition(flag);
		assertNotNull(gfDef);

		VariableScope globalVarScope = library.instantiateScope(null, gvDef);
		assertNotNull(globalVarScope);
		VariableScope globalMoveScope = library.instantiateScope(null, gmDef);
		assertNotNull(globalMoveScope);
		VariableScope globalFlagScope = library.instantiateScope(null, gfDef);
		assertNotNull(globalFlagScope);

		assertTrue(library.assertVariableScope(gvDef, "Walk"));
		VariableID vidv = library.getVariableID(globalVarScope, "Walk");
		assertEquals("Walk", vidv.getName());
		assertEquals(globalVarScope, vidv.getScope());
		assertEquals(Number.class, vidv.getVariableFormat());

		assertTrue(library.assertVariableScope(gmDef, "Walk"));
		VariableID vidm = library.getVariableID(globalMoveScope, "Walk");
		assertEquals("Walk", vidm.getName());
		assertEquals(globalMoveScope, vidm.getScope());
		assertEquals(Number.class, vidm.getVariableFormat());

		assertTrue(library.assertVariableScope(gfDef, "Walk"));
		VariableID vidf = library.getVariableID(globalFlagScope, "Walk");
		assertEquals("Walk", vidf.getName());
		assertEquals(globalFlagScope, vidf.getScope());
		assertEquals(Boolean.class, vidf.getVariableFormat());
		
		assertFalse(vidv.equals(vidf));
		assertFalse(vidv.equals(vidm));
		assertFalse(vidm.equals(vidf));
		assertFalse(vidm.equals(vidv));
		assertFalse(vidf.equals(vidm));
		assertFalse(vidf.equals(vidv));
		
	}
}

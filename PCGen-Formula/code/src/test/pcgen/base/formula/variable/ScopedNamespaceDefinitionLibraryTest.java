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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

public class ScopedNamespaceDefinitionLibraryTest extends TestCase
{

	private ScopedNamespaceDefinitionLibrary library;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		library = new ScopedNamespaceDefinitionLibrary();
	}

	@Test
	public void testDefineGlobalNull()
	{
		try
		{
			library.defineGlobalNamespaceDefinition(null);
			fail("null must be rejected");
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
	public void testDefine()
	{
		NamespaceDefinition vtd =
				new NamespaceDefinition(Number.class, "VAR");
		library.defineGlobalNamespaceDefinition(vtd);
		//twice is legal
		library.defineGlobalNamespaceDefinition(vtd);
		//and so is .equals() VTD
		NamespaceDefinition matching =
				new NamespaceDefinition(Number.class, "VAR");
		library.defineGlobalNamespaceDefinition(matching);

		//Allow other types of same format
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		library.defineGlobalNamespaceDefinition(move);

		try
		{
			//Different type same name (MOVE)
			NamespaceDefinition conflict =
					new NamespaceDefinition(Boolean.class, "MOVE");
			library.defineGlobalNamespaceDefinition(conflict);
			fail("conflict must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testGetGlobal()
	{
		try
		{
			library.getGlobalScopeDefinition("MOVE");
			fail("unconstructed must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopedNamespaceDefinition origDef = library.defineGlobalNamespaceDefinition(move);
		ScopedNamespaceDefinition<?> stDef = library.getGlobalScopeDefinition("MOVE");
		assertTrue(origDef == stDef);
		assertEquals("", stDef.getName());
		assertEquals(null, stDef.getParent());
		assertEquals(move, stDef.getNamespaceDefinition());
	}

	@Test
	public void testGetTypes()
	{
		Collection<String> c = library.getGlobalScopeNamespaceNames();
		assertNotNull(c);
		assertEquals(0, c.size());
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		library.defineGlobalNamespaceDefinition(move);
		//We reload because it's no guarantee we have a wrapped set
		//No guarantee we don't though, so we don't test that either
		c = library.getGlobalScopeNamespaceNames();
		assertNotNull(c);
		assertEquals(1, c.size());
		assertEquals("MOVE", c.iterator().next());
		library.defineGlobalNamespaceDefinition(move);
		//make sure duplicates don't fool things
		c = library.getGlobalScopeNamespaceNames();
		assertNotNull(c);
		assertEquals(1, c.size());
		NamespaceDefinition flag =
				new NamespaceDefinition(Boolean.class, "FLAG");
		library.defineGlobalNamespaceDefinition(flag);
		//reload, same
		c = library.getGlobalScopeNamespaceNames();
		assertNotNull(c);
		assertEquals(2, c.size());
		Set<String> s = new HashSet<String>(c);
		//make sure they weren't the same in the collection :P
		assertEquals(2, s.size());
		assertTrue(s.remove("MOVE"));
		assertTrue(s.remove("FLAG"));
		//Assert independence
		try
		{
			c.add("AREA");
			assertFalse(library.getGlobalScopeNamespaceNames().contains("AREA"));
		}
		catch (UnsupportedOperationException e)
		{
			//ok
		}
	}

	@Test
	public void testGetDefFail()
	{
		NamespaceDefinition conflict =
				new NamespaceDefinition(Boolean.class, "MOVE");
		ScopedNamespaceDefinition parentDef =
				library.defineGlobalNamespaceDefinition(conflict);
		try
		{
			library.getScopeDefinition(parentDef, null);
			fail("null name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getScopeDefinition(null, "EQUIPMENT");
			fail("null parent must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getScopeDefinition(parentDef, "");
			fail("empty name must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getScopeDefinition(parentDef, " EQUIPMENT");
			fail("surrounding whitespace must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			library.getScopeDefinition(parentDef, "EQUIPMENT ");
			fail("surrounding whitespace must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testGetScopeDef()
	{
		NamespaceDefinition move =
				new NamespaceDefinition(Number.class, "MOVE");
		ScopedNamespaceDefinition parentDef =
				library.defineGlobalNamespaceDefinition(move);
		ScopedNamespaceDefinition eqDef =
				library.getScopeDefinition(parentDef, "EQUIPMENT");
		assertEquals("EQUIPMENT", eqDef.getName());
		assertEquals(parentDef, eqDef.getParent());
		assertEquals(move, eqDef.getNamespaceDefinition());

		ScopedNamespaceDefinition eqDef2 =
				library.getScopeDefinition(parentDef, "EQUIPMENT");
		assertTrue(eqDef == eqDef2);

		ScopedNamespaceDefinition spDef =
				library.getScopeDefinition(parentDef, "SPELL");
		assertEquals("SPELL", spDef.getName());
		assertEquals(parentDef, spDef.getParent());
		assertEquals(move, spDef.getNamespaceDefinition());

	}
}

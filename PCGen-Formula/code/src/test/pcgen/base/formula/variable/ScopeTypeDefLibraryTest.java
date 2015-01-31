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

public class ScopeTypeDefLibraryTest extends TestCase
{

	private ScopeTypeDefLibrary library;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		library = new ScopeTypeDefLibrary();
	}

	@Test
	public void testDefineGlobalNull()
	{
		try
		{
			library.defineGlobalScopeDefinition(null);
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
		VariableTypeDefinition vtd =
				new VariableTypeDefinition(Number.class, "VAR");
		library.defineGlobalScopeDefinition(vtd);
		//twice is legal
		library.defineGlobalScopeDefinition(vtd);
		//and so is .equals() VTD
		VariableTypeDefinition matching =
				new VariableTypeDefinition(Number.class, "VAR");
		library.defineGlobalScopeDefinition(matching);

		//Allow other types of same format
		VariableTypeDefinition move =
				new VariableTypeDefinition(Number.class, "MOVE");
		library.defineGlobalScopeDefinition(move);

		try
		{
			//Different type same name (MOVE)
			VariableTypeDefinition conflict =
					new VariableTypeDefinition(Boolean.class, "MOVE");
			library.defineGlobalScopeDefinition(conflict);
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
		VariableTypeDefinition move =
				new VariableTypeDefinition(Number.class, "MOVE");
		ScopeTypeDefinition origDef = library.defineGlobalScopeDefinition(move);
		ScopeTypeDefinition<?> stDef = library.getGlobalScopeDefinition("MOVE");
		assertTrue(origDef == stDef);
		assertEquals("", stDef.getName());
		assertEquals(null, stDef.getParent());
		assertEquals(move, stDef.getVariableTypeDef());
	}

	@Test
	public void testGetTypes()
	{
		Collection<String> c = library.getGlobalScopeTypeNames();
		assertNotNull(c);
		assertEquals(0, c.size());
		VariableTypeDefinition move =
				new VariableTypeDefinition(Number.class, "MOVE");
		library.defineGlobalScopeDefinition(move);
		//We reload because it's no guarantee we have a wrapped set
		//No guarantee we don't though, so we don't test that either
		c = library.getGlobalScopeTypeNames();
		assertNotNull(c);
		assertEquals(1, c.size());
		assertEquals("MOVE", c.iterator().next());
		library.defineGlobalScopeDefinition(move);
		//make sure duplicates don't fool things
		c = library.getGlobalScopeTypeNames();
		assertNotNull(c);
		assertEquals(1, c.size());
		VariableTypeDefinition flag =
				new VariableTypeDefinition(Boolean.class, "FLAG");
		library.defineGlobalScopeDefinition(flag);
		//reload, same
		c = library.getGlobalScopeTypeNames();
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
			assertFalse(library.getGlobalScopeTypeNames().contains("AREA"));
		}
		catch (UnsupportedOperationException e)
		{
			//ok
		}
	}

	@Test
	public void testGetDefFail()
	{
		VariableTypeDefinition conflict =
				new VariableTypeDefinition(Boolean.class, "MOVE");
		ScopeTypeDefinition parentDef =
				library.defineGlobalScopeDefinition(conflict);
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
		VariableTypeDefinition move =
				new VariableTypeDefinition(Number.class, "MOVE");
		ScopeTypeDefinition parentDef =
				library.defineGlobalScopeDefinition(move);
		ScopeTypeDefinition eqDef =
				library.getScopeDefinition(parentDef, "EQUIPMENT");
		assertEquals("EQUIPMENT", eqDef.getName());
		assertEquals(parentDef, eqDef.getParent());
		assertEquals(move, eqDef.getVariableTypeDef());

		ScopeTypeDefinition eqDef2 =
				library.getScopeDefinition(parentDef, "EQUIPMENT");
		assertTrue(eqDef == eqDef2);

		ScopeTypeDefinition spDef =
				library.getScopeDefinition(parentDef, "SPELL");
		assertEquals("SPELL", spDef.getName());
		assertEquals(parentDef, spDef.getParent());
		assertEquals(move, spDef.getVariableTypeDef());

	}
}

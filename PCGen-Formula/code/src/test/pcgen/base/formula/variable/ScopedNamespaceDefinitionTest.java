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

import junit.framework.TestCase;

import org.junit.Test;

public class ScopedNamespaceDefinitionTest extends TestCase
{

	@Test
	public void testSingleConstructor()
	{
		try
		{
			new ScopedNamespaceDefinition(null);
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
	public void testDoubleConstructor()
	{
		try
		{
			new ScopedNamespaceDefinition(null, null);
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
		NamespaceDefinition vtd =
				new NamespaceDefinition(Number.class, "VAR");
		ScopedNamespaceDefinition parent = new ScopedNamespaceDefinition(vtd);
		try
		{
			new ScopedNamespaceDefinition(parent, null);
			fail("null name must be rejected");
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
			new ScopedNamespaceDefinition(null, "EQUIPMENT");
			fail("null parent must be rejected");
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
			new ScopedNamespaceDefinition(parent, "");
			fail("empty name must be rejected");
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

	public void testGlobal()
	{
		NamespaceDefinition vtd =
				new NamespaceDefinition(Number.class, "VAR");
		ScopedNamespaceDefinition global = new ScopedNamespaceDefinition(vtd);
		assertNull(global.getParent());
		assertEquals("", global.getName());
		assertEquals(vtd, global.getNamespaceDefinition());
	}
	
	public void testChild()
	{
		NamespaceDefinition vtd =
				new NamespaceDefinition(Number.class, "VAR");
		ScopedNamespaceDefinition global = new ScopedNamespaceDefinition(vtd);
		ScopedNamespaceDefinition child =
				new ScopedNamespaceDefinition(global, "EQUIPMENT");
		assertEquals(global, child.getParent());
		assertEquals("EQUIPMENT", child.getName());
		assertEquals(vtd, global.getNamespaceDefinition());
	}
}

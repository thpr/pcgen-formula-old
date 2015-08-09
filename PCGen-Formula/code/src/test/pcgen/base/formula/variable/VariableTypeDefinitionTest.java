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

public class VariableTypeDefinitionTest extends TestCase
{

	@Test
	public void testDoubleConstructor()
	{
		try
		{
			new NamespaceDefinition(null, null);
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
			new NamespaceDefinition(Number.class, null);
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
			new NamespaceDefinition(null, "VAR");
			fail("null class must be rejected");
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
			new NamespaceDefinition(Number.class, "");
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
		assertEquals("VAR", vtd.getVariableTypeName());
		assertEquals(Number.class, vtd.getVariableClass());
	}

	public void testEquals()
	{
		NamespaceDefinition vid1 =
				new NamespaceDefinition(Number.class, "VAR");
		NamespaceDefinition vid2 =
				new NamespaceDefinition(Number.class, "VAR");
		NamespaceDefinition vid3 =
				new NamespaceDefinition(Number.class, "MOVE");
		NamespaceDefinition vid4 =
				new NamespaceDefinition(Boolean.class, "VAR");
		assertFalse(vid1.equals(null));
		assertFalse(vid1.equals(new Object()));
		assertTrue(vid1.equals(vid1));
		assertTrue(vid1.equals(vid2));
		assertTrue(vid2.equals(vid1));
		assertFalse(vid1.equals(vid3));
		assertFalse(vid1.equals(vid4));
	}

	public void testHashCode()
	{
		NamespaceDefinition vid1 =
				new NamespaceDefinition(Number.class, "VAR");
		NamespaceDefinition vid2 =
				new NamespaceDefinition(Number.class, "VAR");
		NamespaceDefinition vid3 =
				new NamespaceDefinition(Number.class, "MOVE");
		NamespaceDefinition vid4 =
				new NamespaceDefinition(Boolean.class, "VAR");
		int hc1 = vid1.hashCode();
		int hc2 = vid2.hashCode();
		int hc3 = vid3.hashCode();
		int hc4 = vid4.hashCode();
		assertTrue(hc1 == hc2);
		assertFalse(hc2 == hc3);
		assertFalse(hc2 == hc4);
		assertFalse(hc3 == hc4);
	}
}

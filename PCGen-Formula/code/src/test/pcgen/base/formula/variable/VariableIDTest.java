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

public class VariableIDTest extends TestCase
{

	@Test
	public void testDoubleConstructor()
	{
		try
		{
			new VariableID(null, null);
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
		VariableTypeDefinition vtd =
				new VariableTypeDefinition(Number.class, "VAR");
		ScopeTypeDefinition global = new ScopeTypeDefinition(vtd);
		VariableScope scope = new VariableScope(global, null);
		try
		{
			new VariableID(scope, null);
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
			new VariableID(null, "VAR");
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
			new VariableID(scope, "");
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
		try
		{
			new VariableID(scope, " test");
			fail("padded name must be rejected");
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
		VariableTypeDefinition vtd =
				new VariableTypeDefinition(Number.class, "VAR");
		ScopeTypeDefinition global = new ScopeTypeDefinition(vtd);
		VariableScope scope = new VariableScope(global, null);
		VariableID vid = new VariableID(scope, "test");
		assertEquals("test", vid.getName());
		assertEquals(scope, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());
	}

	public void testEquals()
	{
		VariableTypeDefinition vtd =
				new VariableTypeDefinition(Number.class, "VAR");
		ScopeTypeDefinition global = new ScopeTypeDefinition(vtd);
		VariableScope scope = new VariableScope(global, null);
		VariableScope scope2 = new VariableScope(global, null);
		VariableID vid1 = new VariableID(scope, "test");
		VariableID vid2 = new VariableID(scope, "test");
		VariableID vid3 = new VariableID(scope, "test2");
		VariableID vid4 = new VariableID(scope2, "test");
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
		VariableTypeDefinition vtd =
				new VariableTypeDefinition(Number.class, "VAR");
		ScopeTypeDefinition global = new ScopeTypeDefinition(vtd);
		VariableScope scope = new VariableScope(global, null);
		VariableScope scope2 = new VariableScope(global, null);
		VariableID vid1 = new VariableID(scope, "test");
		VariableID vid2 = new VariableID(scope, "test");
		VariableID vid3 = new VariableID(scope, "bummer");
		VariableID vid4 = new VariableID(scope2, "test");
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

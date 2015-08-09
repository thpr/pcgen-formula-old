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

public class SimpleVariableStoreTest extends TestCase
{

	public void testNulls()
	{
		SimpleVariableStore svs = new SimpleVariableStore();
		NamespaceDefinition vtd =
				new NamespaceDefinition(Number.class, "VAR");
		ScopeTypeDefinition global = new ScopeTypeDefinition(vtd);
		VariableScope scope = new VariableScope(global, null);
		VariableID vid = new VariableID(scope, "test");
		try
		{
			svs.put(null, Integer.valueOf(4));
		}
		catch (IllegalArgumentException e)
		{
			//yep
		}
		try
		{
			svs.put(vid, null);
		}
		catch (IllegalArgumentException e)
		{
			//yep
		}
		try
		{
			svs.put(vid, "NotANumber!");
		}
		catch (IllegalArgumentException e)
		{
			//yep
		}
	}

	public void testGlobal()
	{
		SimpleVariableStore svs = new SimpleVariableStore();
		NamespaceDefinition vtd =
				new NamespaceDefinition(Number.class, "VAR");
		ScopeTypeDefinition global = new ScopeTypeDefinition(vtd);
		VariableScope scope = new VariableScope(global, null);
		VariableID vid = new VariableID(scope, "test");
		assertFalse(svs.containsKey(vid));
		assertNull(svs.put(vid, Integer.valueOf(9)));
		assertTrue(svs.containsKey(vid));
		assertEquals(Integer.valueOf(9), svs.get(vid));
		assertEquals(Integer.valueOf(9), svs.put(vid, Integer.valueOf(4)));
		assertTrue(svs.containsKey(vid));
		assertEquals(Integer.valueOf(4), svs.get(vid));
	}

	public void testIndependence()
	{
		SimpleVariableStore svs = new SimpleVariableStore();
		NamespaceDefinition vtd =
				new NamespaceDefinition(Number.class, "VAR");
		ScopeTypeDefinition global = new ScopeTypeDefinition(vtd);
		VariableScope scope = new VariableScope(global, null);
		VariableScope scope2 = new VariableScope(global, null);
		VariableID vid1 = new VariableID(scope, "test");
		VariableID vid2 = new VariableID(scope, "test");
		VariableID vid3 = new VariableID(scope, "test2");
		VariableID vid4 = new VariableID(scope2, "test");
		assertNull(svs.put(vid1, Integer.valueOf(9)));
		assertTrue(svs.containsKey(vid1));
		assertTrue(svs.containsKey(vid2));
		assertFalse(svs.containsKey(vid3));
		assertFalse(svs.containsKey(vid4));
		assertEquals(Integer.valueOf(9), svs.put(vid2, Integer.valueOf(4)));
		assertTrue(svs.containsKey(vid1));
		assertTrue(svs.containsKey(vid2));
		assertFalse(svs.containsKey(vid3));
		assertFalse(svs.containsKey(vid4));
		assertEquals(Integer.valueOf(4), svs.get(vid1));
		assertNull(svs.put(vid4, Integer.valueOf(3)));
		assertTrue(svs.containsKey(vid1));
		assertTrue(svs.containsKey(vid2));
		assertFalse(svs.containsKey(vid3));
		assertTrue(svs.containsKey(vid4));
		assertEquals(Integer.valueOf(4), svs.get(vid1));
		assertEquals(Integer.valueOf(3), svs.get(vid4));
	}

}

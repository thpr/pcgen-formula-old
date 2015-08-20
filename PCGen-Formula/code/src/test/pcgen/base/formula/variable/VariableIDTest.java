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

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.manager.ScopeInstanceFactory;

public class VariableIDTest extends TestCase
{

	private ScopeInstanceFactory instanceFactory = new ScopeInstanceFactory(null);

	@Test
	public void testDoubleConstructor()
	{
		try
		{
			new VariableID(null, null, null);
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
		NamespaceDefinition vtd = new NamespaceDefinition(Number.class, "VAR");
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = instanceFactory.getInstance(null, varScope);
		try
		{
			new VariableID(globalInst, vtd, null);
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
			new VariableID(globalInst, null, "VAR");
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
			new VariableID(null, vtd, "VAR");
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
			new VariableID(globalInst, vtd, "");
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
			new VariableID(globalInst, vtd, " test");
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
		NamespaceDefinition varDef =
				new NamespaceDefinition(Number.class, "VAR");
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = instanceFactory.getInstance(null, varScope);
		VariableID vid = new VariableID(globalInst, varDef, "test");
		assertEquals("test", vid.getName());
		assertEquals(globalInst, vid.getScope());
		assertEquals(Number.class, vid.getVariableFormat());
	}

	public void testEquals()
	{
		NamespaceDefinition varDef =
				new NamespaceDefinition(Number.class, "VAR");
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = instanceFactory.getInstance(null, varScope);
		ScopeInstance globalInst2 = instanceFactory.getInstance(null, varScope);
		VariableID vid1 = new VariableID(globalInst, varDef, "test");
		VariableID vid2 = new VariableID(globalInst, varDef, "test");
		VariableID vid3 = new VariableID(globalInst, varDef, "test2");
		VariableID vid4 = new VariableID(globalInst2, varDef, "test");
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
		NamespaceDefinition varDef =
				new NamespaceDefinition(Number.class, "VAR");
		LegalScope varScope = new SimpleLegalScope(null, "Global");
		ScopeInstance globalInst = instanceFactory.getInstance(null, varScope);
		ScopeInstance globalInst2 = instanceFactory.getInstance(null, varScope);
		VariableID vid1 = new VariableID(globalInst, varDef, "test");
		VariableID vid2 = new VariableID(globalInst, varDef, "test");
		VariableID vid3 = new VariableID(globalInst, varDef, "bummer");
		VariableID vid4 = new VariableID(globalInst2, varDef, "test");
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

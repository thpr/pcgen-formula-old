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

public class VariableScopeInstTest extends TestCase
{

	@Test
	public void testDoubleConstructor()
	{
		NamespaceDefinition vtd =
				new NamespaceDefinition(Number.class, "VAR");
		ScopeTypeDefinition global = new ScopeTypeDefinition(vtd);
		VariableScope parent = new VariableScope(global, null);
		try
		{
			new VariableScope(null, null);
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
			new VariableScope(null, parent);
			fail("null def must be rejected");
		}
		catch (NullPointerException e)
		{
			//ok
		}
		catch (IllegalArgumentException e)
		{
			//ok, too			
		}
		ScopeTypeDefinition sub = new ScopeTypeDefinition(global, "EQUIPMENT");
		try
		{
			new VariableScope(sub, null);
			fail("null parent must be rejected when not global");
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
		ScopeTypeDefinition global = new ScopeTypeDefinition(vtd);
		VariableScope scope = new VariableScope(global, null);
		assertEquals(null, scope.getParentScope());
		assertEquals(global, scope.getScopeDefinition());
		ScopeTypeDefinition sub = new ScopeTypeDefinition(global, "EQUIPMENT");
		VariableScope subscope = new VariableScope(sub, scope);
		assertEquals(scope, subscope.getParentScope());
		assertEquals(sub, subscope.getScopeDefinition());
	}
}

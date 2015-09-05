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

import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;

public class NamespaceDefinitionTest extends TestCase
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
			new NamespaceDefinition(new NumberManager(), null);
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
			new NamespaceDefinition(new NumberManager(), "");
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
		NamespaceDefinition varDef =
				new NamespaceDefinition(new NumberManager(), "VAR");
		assertEquals("VAR", varDef.getNamespaceName());
		assertEquals(new NumberManager(), varDef.getFormatManager());
	}

	public void testEquals()
	{
		NamespaceDefinition varDef1 =
				new NamespaceDefinition(new NumberManager(), "VAR");
		NamespaceDefinition varDef2 =
				new NamespaceDefinition(new NumberManager(), "VAR");
		NamespaceDefinition moveDef =
				new NamespaceDefinition(new NumberManager(), "MOVE");
		NamespaceDefinition varDefBool =
				new NamespaceDefinition(new BooleanManager(), "VAR");
		assertFalse(varDef1.equals(null));
		assertFalse(varDef1.equals(new Object()));
		assertTrue(varDef1.equals(varDef1));
		assertTrue(varDef1.equals(varDef2));
		assertTrue(varDef2.equals(varDef1));
		assertFalse(varDef1.equals(moveDef));
		assertFalse(varDef1.equals(varDefBool));
	}

	public void testHashCode()
	{
		NamespaceDefinition varDef1 =
				new NamespaceDefinition(new NumberManager(), "VAR");
		NamespaceDefinition varDef2 =
				new NamespaceDefinition(new NumberManager(), "VAR");
		NamespaceDefinition moveDef =
				new NamespaceDefinition(new NumberManager(), "MOVE");
		NamespaceDefinition varDefBool =
				new NamespaceDefinition(new BooleanManager(), "VAR");
		int hc1 = varDef1.hashCode();
		int hc2 = varDef2.hashCode();
		int hc3 = moveDef.hashCode();
		int hc4 = varDefBool.hashCode();
		assertTrue(hc1 == hc2);
		assertFalse(hc2 == hc3);
		assertFalse(hc2 == hc4);
		assertFalse(hc3 == hc4);
	}
}

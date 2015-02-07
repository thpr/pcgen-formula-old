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
package pcgen.base.formula.manager;

import junit.framework.TestCase;

import org.junit.Test;

public class SimpleFormulaDependencyManagerTest extends TestCase
{
	private SimpleFormulaDependencyManager manager;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		manager = new SimpleFormulaDependencyManager();
	}

	@Test
	public void testInvalidNull()
	{
		try
		{
			manager.addVariable(null);
			fail("Expected null VariableID to be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//Yep
		}
	}


	@Test
	public void testIsEmpty()
	{
		assertTrue(manager.isEmpty());
		//TODO Need to add Something :/
	}

}

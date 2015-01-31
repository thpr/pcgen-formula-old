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

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formula.base.FormulaDependencyManager;
import pcgen.base.formula.variable.VariableID;

/**
 * A SimpleFormulaDependencyManager is a class to capture Formula dependencies,
 * implementing the minimum features of the FormulaDependencyManager interface.
 */
public class SimpleFormulaDependencyManager implements FormulaDependencyManager
{
	/**
	 * The internal list of VariableIDs upon which the formula this
	 * SimpleFormulaDependencyManager represents is dependent.
	 */
	private final List<VariableID<?>> list = new ArrayList<VariableID<?>>();

	/**
	 * Adds a Variable (identified by the VariableID) to the list of
	 * dependencies for a Formula.
	 * 
	 * @param vid
	 *            The VariableID to be added as a dependency of the Formula this
	 *            SimpleFormulaDependencyManager represents
	 * @throws IllegalArgumentException
	 *             if the given VariableID is null
	 */
	public void addVariable(VariableID<?> vid)
	{
		if (vid == null)
		{
			throw new IllegalArgumentException("VariableID may not be null");
		}
		list.add(vid);
	}

	/**
	 * Returns a list of VariableID objects that identify the list of
	 * dependencies of the Formula this SimpleFormulaDependencyManager
	 * represents.
	 * 
	 * This method is value-semantic in that ownership of the returned List is
	 * transferred to the class calling this method.
	 * 
	 * @return A list of VariableID objects that identify the list of
	 *         dependencies of the Formula this SimpleFormulaDependencyManager
	 *         represents
	 * 
	 * @see pcgen.base.formula.base.FormulaDependencyManager#getVariables()
	 */
	public List<VariableID<?>> getVariables()
	{
		return new ArrayList<VariableID<?>>(list);
	}

	/**
	 * Returns true if this SimpleFormulaDependencyManager has an empty list of
	 * VariableIDs upon which the formula this SimpleFormulaDependencyManager
	 * represents is dependent.
	 * 
	 * @see pcgen.base.formula.base.FormulaDependencyManager#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return list.isEmpty();
	}

}

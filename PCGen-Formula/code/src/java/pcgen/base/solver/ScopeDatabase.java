/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.base.solver;

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.manager.FormulaManager;
import pcgen.base.formula.manager.ScopeInformation;
import pcgen.base.formula.variable.NamespaceDefinition;
import pcgen.base.util.DoubleKeyMap;

/**
 * ScopeDatabase is a utility class providing a database that maps VariableScope
 * objects to ScopeInformation objects.
 */
public final class ScopeDatabase
{

	/**
	 * The map that associates VariableScope & NamespaceDefintion objects to
	 * ScopeInformation objects
	 */
	private DoubleKeyMap<ScopeInstance, NamespaceDefinition<?>, ScopeInformation<?>> map =
			new DoubleKeyMap<ScopeInstance, NamespaceDefinition<?>, ScopeInformation<?>>();

	/**
	 * Returns the ScopeInformation Object for the given VariableScope.
	 * 
	 * If a ScopeInformation has not yet been created, a new ScopeInformation is
	 * build and initialized with the given VariableScope and FormulaManager.
	 * 
	 * @param fm
	 *            The FormulaManager to be used to initialize a new
	 *            ScopeInformation, if necessary
	 * @param scope
	 *            The VariableScope for which the ScopeInformation should be
	 *            returned
	 * @param nsDef
	 *            The NamespaceDefinition for which the ScopeInformation should
	 *            be returned
	 * @return The ScopeInformation for the given VariableScope
	 */
	public <T> ScopeInformation<T> getScopeInformation(FormulaManager fm,
		ScopeInstance scope, NamespaceDefinition<T> nsDef)
	{
		@SuppressWarnings("unchecked")
		ScopeInformation<T> scopeInfo =
				(ScopeInformation<T>) map.get(scope, nsDef);
		if (scopeInfo == null)
		{
			scopeInfo = new ScopeInformation<>(fm, scope, nsDef);
			map.put(scope, nsDef, scopeInfo);
		}
		return scopeInfo;
	}
}

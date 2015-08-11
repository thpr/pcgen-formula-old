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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.base.util.HashMapToList;

/**
 * ScopedNamespaceDefinitionLibrary performs the management of
 * ScopedNamespaceDefinitions.
 */
public class ScopedNamespaceDefinitionLibrary
{

	/**
	 * Holds the Global Scope Definition for each namespace (for this
	 * ScopedNamespaceDefinitionLibrary).
	 */
	private Map<String, ScopedNamespaceDefinition<?>> globalScopes =
			new HashMap<String, ScopedNamespaceDefinition<?>>();

	/**
	 * Stores a map of parent ScopedNamespaceDefinition objects to their child
	 * ScopedNamespaceDefinition objects. The child->parent relationship is held
	 * in the ScopedNamespaceDefinition object itself.
	 */
	private HashMapToList<ScopedNamespaceDefinition<?>, ScopedNamespaceDefinition<?>> scopeChildren =
			new HashMapToList<ScopedNamespaceDefinition<?>, ScopedNamespaceDefinition<?>>();

	/**
	 * Asserts (and if valid, returns) the existence of a "Global"
	 * ScopedNamespaceDefinition for the given NamespaceDefinition.
	 * 
	 * If an existing "Global" ScopedNamespaceDefinition exists for the Type
	 * Name of the given NamespaceDefintion, and it not based on the given
	 * NamespaceDefinition, then this method will throw an Exception. Note that
	 * this means the method either returns a non-null value or throws an
	 * Exception.
	 * 
	 * @param nsDef
	 *            The NamespaceDefinition for which the existence of a "Global"
	 *            ScopedNamespaceDefinition is being asserted.
	 * @return The "Global" ScopedNamespaceDefinition for the given
	 *         NamespaceDefinition
	 * @throws IllegalArgumentException
	 *             if an existing "Global" ScopedNamespaceDefinition exists for
	 *             the Type Name of the given NamespaceDefinition, but does not
	 *             match the given NamespaceDefinition
	 */
	@SuppressWarnings("unchecked")
	public <T> ScopedNamespaceDefinition<T> defineGlobalNamespaceDefinition(
		NamespaceDefinition<T> nsDef)
	{
		if (nsDef == null)
		{
			throw new IllegalArgumentException(
				"Cannot define Global Namespace definition for null Namespace");
		}
		String nsName = nsDef.getNamespaceName();
		ScopedNamespaceDefinition<T> globalScope =
				(ScopedNamespaceDefinition<T>) globalScopes.get(nsName);
		if (globalScope == null)
		{
			globalScope = new ScopedNamespaceDefinition<T>(nsDef);
			globalScopes.put(nsName, globalScope);
		}
		else
		{
			if (!globalScope.getNamespaceDefinition().equals(nsDef))
			{
				String oldClass =
						globalScope.getNamespaceDefinition()
							.getVariableFormat().getSimpleName();
				throw new IllegalArgumentException(
					"Attempt to redefine Global Namespace Definition for: "
						+ nsName + " from " + oldClass + " to "
						+ nsDef.getVariableFormat().getSimpleName());
			}
		}
		return globalScope;
	}

	/**
	 * Returns the Global ScopedNamespaceDefinition for the given variable
	 * namespace for this ScopedNamespaceDefinitionLibrary.
	 * 
	 * @param namespace
	 *            The name of the variable namespace for which the global
	 *            ScopedNamespaceDefinition is to be retrieved
	 * @return The Global ScopedNamespaceDefinition in this
	 *         ScopedNamespaceDefinitionLibrary for the given variable namespace
	 */
	public ScopedNamespaceDefinition<?> getGlobalScopeDefinition(
		String namespace)
	{
		ScopedNamespaceDefinition<?> globalScope = globalScopes.get(namespace);
		if (globalScope == null)
		{
			throw new IllegalArgumentException(
				"Global Scope Namespace Definition for Variable Namespace "
					+ namespace + " is not defined");
		}
		return globalScope;
	}

	/**
	 * Returns a non-null Collection of the Type Names of all of the Global
	 * Scope Types contained in this ScopedNamespaceDefinitionLibrary.
	 * 
	 * @return A Collection of the Type Names of all of the Global Scope Types
	 *         contained in this ScopedNamespaceDefinitionLibrary
	 */
	public Collection<String> getGlobalScopeTypeNames()
	{
		return Collections.unmodifiableSet(globalScopes.keySet());
	}

	/**
	 * Returns a ScopedNamespaceDefinition given the parent
	 * ScopedNamespaceDefinition and the name of the ScopedNamespaceDefinition
	 * to be returned.
	 * 
	 * If a ScopedNamespaceDefinition that is a child of the given parent
	 * ScopedNamespaceDefinition with a matching name already exists, it will be
	 * returned. If not, a new ScopedNamespaceDefinition will be returned.
	 * 
	 * @param <T>
	 *            The type of object contained in the VariableScopes defined by
	 *            the ScopedNamespaceDefinition to be returned
	 * @param parentDef
	 *            The parent ScopedNamespaceDefinition for the
	 *            ScopedNamespaceDefinition to be returned
	 * @param scopeName
	 *            The scope name of the ScopedNamespaceDefinition to be returned
	 * @return A ScopedNamespaceDefinition with the given parent
	 *         ScopedNamespaceDefinition and name
	 * @throws IllegalArgumentException
	 *             if either argument is null or if the given scope definition
	 *             name is empty
	 */
	@SuppressWarnings("unchecked")
	/*
	 * TODO need a get that does NOT create :(
	 */
	public <T> ScopedNamespaceDefinition<T> getScopeDefinition(
		ScopedNamespaceDefinition<T> parentDef, String scopeName)
	{
		if (parentDef == null)
		{
			throw new IllegalArgumentException(
				"Parent definition cannot be null");
		}
		checkLegalVarName(scopeName);
		/*
		 * TODO Do we need a check that the given parentDef actually belongs to
		 * this VariableLibrary?
		 */
		List<ScopedNamespaceDefinition<?>> subscopes =
				scopeChildren.getListFor(parentDef);
		if (subscopes != null)
		{
			//Look for existing
			for (ScopedNamespaceDefinition<?> subscope : subscopes)
			{
				if (subscope.getName().equalsIgnoreCase(scopeName))
				{
					return (ScopedNamespaceDefinition<T>) subscope;
				}
			}
		}
		//Is new
		ScopedNamespaceDefinition<T> snDef =
				new ScopedNamespaceDefinition<T>(parentDef, scopeName);
		scopeChildren.addToListFor(parentDef, snDef);
		return snDef;
	}

	/**
	 * Returns a list of the "children" of the given ScopedNamespaceDefinition.
	 * These were created by calling getScopeDefinition on this
	 * ScopedNamespaceDefinitionLibrary.
	 * 
	 * This method is value-semantic in that no changes are made to the object
	 * passed into the method and ownership of the returned List is transferred
	 * to the class calling this method. Changes to this object will not cause
	 * the list to change, and changes to the list will not cause the internal
	 * contents of this object to change.
	 * 
	 * @param snDef
	 *            The ScopedNamespaceDefinition for which the children
	 *            ScopedNamespaceDefinitions should be returned
	 * @return A list of ScopedNamespaceDefinition objects which are "children"
	 *         of the given ScopedNamespaceDefinition
	 */
	public List<ScopedNamespaceDefinition<?>> getChildScopes(
		ScopedNamespaceDefinition<?> snDef)
	{
		return scopeChildren.getListFor(snDef);
	}

	/**
	 * Ensure a name is not null, zero length, or whitespace padded
	 */
	private void checkLegalVarName(String varName)
	{
		if (varName == null)
		{
			throw new IllegalArgumentException("Variable Name cannot be null");
		}
		if (varName.length() == 0)
		{
			throw new IllegalArgumentException("Variable Name cannot be empty");
		}
		String trimmed = varName.trim();
		if (!varName.equals(trimmed))
		{
			throw new IllegalArgumentException(
				"Variable Name cannot start/end with whitespace");
		}
	}
}

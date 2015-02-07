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

import java.util.List;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.GenericMapToList;

/**
 * VariableLibrary performs the management of Scope Definition relationships,
 * Scopes, and VariableID objects. This ensures that when one of those objects
 * is built, it is in an appropriate structure to be evaluated.
 */
public class VariableLibrary
{

	private final ScopeTypeDefLibrary library;

	/**
	 * Constructs a new VariableLibrary, which uses the given
	 * ScopeTypeDefLibrary to ensure variables are legal within a given scope.
	 * 
	 * @param lib
	 *            The ScopeTypeDefLibrary used to to ensure variables are legal
	 *            within a given scope
	 * @throws IllegalArgumentException
	 *             if the given library is null
	 */
	public VariableLibrary(ScopeTypeDefLibrary lib)
	{
		if (lib == null)
		{
			throw new IllegalArgumentException(
				"Scope Type Definition Library cannot be null");
		}
		library = lib;
	}

	/**
	 * Holds a map from variable names to the ScopeTypeDefinition objects where
	 * that variable name is legal.
	 */
	/*
	 * Is stored from String->ScopeTypeDefinition for 2 reasons:
	 * 
	 * (1) Case Sensitivity is easier to avoid because we have
	 * CaseInsensitiveMap rather than having to do case insensitivity as things
	 * are added to a list
	 * 
	 * (2) If a variable is not in ANY scope, this can shortcut verification. If
	 * this was VSD->String then all related VSDs would have to be queried to
	 * get null responses. Here, this MapToList is queried, provides a null
	 * response and the validation process is complete.
	 */
	private GenericMapToList<String, ScopeTypeDefinition<?>> variableDefs =
			GenericMapToList.getMapToList(CaseInsensitiveMap.class);

	/**
	 * Asserts the given variable name is valid within the given
	 * ScopeTypeDefinition.
	 * 
	 * If no previous definition for the given variable name was encountered,
	 * then the assertion automatically passes, and the given
	 * ScopeTypeDefinition is stored as the definition for the given variable
	 * name.
	 * 
	 * If a previous ScopeTypeDefinition exists for the given variable name,
	 * then this will return true if and only if the given ScopeTypeDefinition
	 * is equal to the already stored ScopeTypeDefinition.
	 * 
	 * @param stDef
	 *            The asserted ScopeTypeDefinition for the given variable name
	 * @param varName
	 *            The variable name for which the given ScopeTypeDefinition is
	 *            being asserted as valid
	 * @return true if the assertion of this being a valid ScopeTypeDefinition
	 *         for the given variable name passes; false otherwise
	 * @throws IllegalArgumentException
	 *             if either argument is null of if the variable name is
	 *             otherwise illegal (is empty or starts/ends with whitespace)
	 */
	public boolean assertVariableScope(ScopeTypeDefinition<?> stDef,
		String varName)
	{
		/*
		 * TODO Do we need a check that the given stDef actually belongs to this
		 * VariableLibrary?
		 */
		checkLegalVarName(varName);
		if (stDef == null)
		{
			throw new IllegalArgumentException(
				"ScopeTypeDefinition cannot be null");
		}
		if (!variableDefs.containsListFor(varName))
		{
			//Can't be a conflict
			variableDefs.addToListFor(varName, stDef);
			return true;
		}
		if (variableDefs.containsInList(varName, stDef))
		{
			//Asserted Scope Already there
			return true;
		}
		//Now, need to check for conflicts
		ScopeTypeDefinition<?> parent = stDef.getParent();
		while (parent != null)
		{
			if (variableDefs.containsInList(varName, parent))
			{
				//Conflict with a higher level scope
				return false;
			}
			parent = parent.getParent();
		}
		boolean hasChildConflict = hasChildConflict(varName, stDef);
		if (!hasChildConflict)
		{
			variableDefs.addToListFor(varName, stDef);
		}
		return !hasChildConflict;
	}

	private boolean hasChildConflict(String varName,
		ScopeTypeDefinition<?> stDef)
	{
		List<ScopeTypeDefinition<?>> children = library.getChildScopes(stDef);
		if (children == null)
		{
			return false;
		}
		for (ScopeTypeDefinition<?> childScope : children)
		{
			if (variableDefs.containsInList(varName, childScope)
				|| hasChildConflict(varName, childScope))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the given VariableScope and variable name are a legal
	 * combination, knowing previous assertions of a ScopeTypeDefinition object
	 * for the given variable name.
	 * 
	 * If no previous ScopeTypeDefinition was stored via assertVariableScope for
	 * the given variable name, then this will unconditionally return false.
	 * 
	 * If a ScopeTypeDefinition was stored via assertVariableScope for the given
	 * variable name, then this will return true if the given VariableScope is
	 * compatible with the stored ScopeTypeDefinition.
	 * 
	 * @param stDef
	 *            The ScopeTypeDefinition used to determine if the scope and
	 *            name are a legal combination
	 * @param varName
	 *            The variable name used to determine if the scope and name are
	 *            a legal combination
	 * @return true if the given VariableScope and variable name are a legal
	 *         combination; false otherwise
	 * @throws IllegalArgumentException
	 *             if the given scope type definition is null
	 */
	public boolean isLegalVariableID(ScopeTypeDefinition<?> stDef,
		String varName)
	{
		if (stDef == null)
		{
			throw new IllegalArgumentException(
				"Scope Type Definition cannot be null");
		}
		if (variableDefs.containsInList(varName, stDef))
		{
			return true;
		}
		ScopeTypeDefinition<?> parent = stDef.getParent();
		return (parent != null) && isLegalVariableID(parent, varName);
	}

	/**
	 * Returns a non-null list of known ScopeTypeDefinition objects for the
	 * given variable name.
	 * 
	 * This is typically used for debugging (e.g. to list potential conflicts)
	 * 
	 * Ownership of the returned list is transferred to the calling object and
	 * no reference to it is maintained by VariableLibrary. Changing the
	 * returned list will not alter the VariableLibrary.
	 * 
	 * @param varName
	 *            The Variable name for which the relevant ScopeTypeDefinition
	 *            objects should be returned
	 * @return The List of ScopeTypeDefinition objects asserted for the given
	 *         variable name
	 * @throws IllegalArgumentException
	 *             if the given variable name is null, empty, or has
	 *             leading/trailing whitespace
	 */
	public List<ScopeTypeDefinition<?>> getKnownVariableScopes(String varName)
	{
		checkLegalVarName(varName);
		return variableDefs.getListFor(varName);
	}

	/**
	 * Returns a VariableScope with the given ScopeTypeDefinition and parent
	 * VariableScope.
	 * 
	 * @param <T>
	 *            The type of object contained in the VariableScope to be
	 *            instantiated
	 * @param parentScope
	 *            The VariableScope that is the parent of this VaribleScope
	 * @param stDef
	 *            The ScopeTypeDefinition that underlies the VariableScope to be
	 *            returned
	 * @return A VariableScope with the given ScopeTypeDefinition and parent
	 *         VariableScope
	 * @throws IllegalArgumentException
	 *             if the ScopeTypeDefinition of the given parent VariableScope
	 *             is not the parent ScopeTypeDefinition of the given
	 *             ScopeTypeDefinition
	 */
	public <T> VariableScope<T> instantiateScope(VariableScope<T> parentScope,
		ScopeTypeDefinition<T> stDef)
	{
		if (stDef == null)
		{
			throw new IllegalArgumentException("Definition cannot be null");
		}
		/*
		 * TODO Do we need a check that the given parentScope, stDef actually
		 * belong to this VariableLibrary?
		 */
		if (parentScope == null)
		{
			if (stDef.getParent() != null)
			{
				throw new IllegalArgumentException(
					"Cannot instantiate a scope with no parent unless it is a global scope");
			}
		}
		else if (!parentScope.getScopeDefinition().equals(stDef.getParent()))
		{
			if (stDef.getParent() == null)
			{
				throw new IllegalArgumentException(
					"Cannot instantiate a global scope with a parent");
			}
			throw new IllegalArgumentException("Parent Scope Definition was: "
				+ parentScope.getScopeDefinition().getName()
				+ " but parent of requested definition is: "
				+ stDef.getParent().getName());
		}
		return new VariableScope<T>(stDef, parentScope);
	}

	/**
	 * Returns a VariableID for the given scope and variable name, if legal.
	 * 
	 * The rules for legality are defined in the isLegalVariableID method
	 * description.
	 * 
	 * If isLegalVariableID returns false, then this method will throw an
	 * exception. isLegalVariableID should be called first to determine if
	 * calling this method is safe.
	 * 
	 * @param <T>
	 *            The type of object identified by the VariableID to be returned
	 * @param scope
	 *            The VariableScope used to determine if the scope and name are
	 *            a legal combination
	 * @param varName
	 *            The variable name used to determine if the scope and name are
	 *            a legal combination
	 * @return A VariableID of the given VariableScope and variable name if they
	 *         are are a legal combination
	 * @throws IllegalArgumentException
	 *             if the given scope is null, the name is invalid, or if the
	 *             scope and variable name are not a legal combination
	 */
	public <T> VariableID<T> getVariableID(VariableScope<T> scope,
		String varName)
	{
		return getVarIDMessaged(scope, varName, scope);
	}

	private <T> VariableID<T> getVarIDMessaged(VariableScope<T> scope,
		String varName, VariableScope<T> messagescope)
	{
		if (scope == null)
		{
			throw new IllegalArgumentException("Cannot get VariableID "
				+ varName + " for " + messagescope + " scope");
		}
		checkLegalVarName(varName);
//System.err.println(variableDefs);
		if (variableDefs.containsInList(varName, scope.getScopeDefinition()))
		{
			return new VariableID<T>(scope, varName);
		}
		return getVarIDMessaged(scope.getParentScope(), varName, scope);
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

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

	/**
	 * The ScopedNamespaceDefinitionLibrary that supports to be used to
	 * determine "child" scopes from any ScopedNamespaceDefinition (in order to
	 * avoid variable name conflicts between different but non disjoint scopes).
	 */
	private final ScopedNamespaceDefinitionLibrary library;

	/**
	 * Constructs a new VariableLibrary, which uses the given
	 * ScopedNamespaceDefinitionLibrary to ensure variables are legal within a
	 * given scope.
	 * 
	 * @param sndLibrary
	 *            The ScopedNamespaceDefinitionLibrary used to to ensure
	 *            variables are legal within a given scope
	 * @throws IllegalArgumentException
	 *             if the given library is null
	 */
	public VariableLibrary(ScopedNamespaceDefinitionLibrary sndLibrary)
	{
		if (sndLibrary == null)
		{
			throw new IllegalArgumentException(
				"Scope Namespace Definition Library cannot be null");
		}
		library = sndLibrary;
	}

	/**
	 * Holds a map from variable names to the ScopedNamespaceDefinition objects
	 * where that variable name is legal.
	 */
	/*
	 * Is stored from String->ScopedNamespaceDefinition for 2 reasons:
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
	private GenericMapToList<String, ScopedNamespaceDefinition<?>> variableDefs =
			GenericMapToList.getMapToList(CaseInsensitiveMap.class);

	/**
	 * Asserts the given variable name is valid within the given
	 * ScopedNamespaceDefinition.
	 * 
	 * If no previous definition for the given variable name was encountered,
	 * then the assertion automatically passes, and the given
	 * ScopedNamespaceDefinition is stored as the definition for the given
	 * variable name.
	 * 
	 * If a previous ScopedNamespaceDefinition exists for the given variable
	 * name, then this will return true if and only if the given
	 * ScopedNamespaceDefinition is equal to the already stored
	 * ScopedNamespaceDefinition.
	 * 
	 * @param snDef
	 *            The asserted ScopedNamespaceDefinition for the given variable
	 *            name
	 * @param varName
	 *            The variable name for which the given
	 *            ScopedNamespaceDefinition is being asserted as valid
	 * @return true if the assertion of this being a valid
	 *         ScopedNamespaceDefinition for the given variable name passes;
	 *         false otherwise
	 * @throws IllegalArgumentException
	 *             if either argument is null of if the variable name is
	 *             otherwise illegal (is empty or starts/ends with whitespace)
	 */
	public boolean assertVariableScope(ScopedNamespaceDefinition<?> snDef,
		String varName)
	{
		/*
		 * TODO Do we need a check that the given stDef actually belongs to this
		 * VariableLibrary?
		 */
		checkLegalVarName(varName);
		if (snDef == null)
		{
			throw new IllegalArgumentException(
				"ScopedNamespaceDefinition cannot be null");
		}
		if (!variableDefs.containsListFor(varName))
		{
			//Can't be a conflict
			variableDefs.addToListFor(varName, snDef);
			return true;
		}
		if (variableDefs.containsInList(varName, snDef))
		{
			//Asserted Scope Already there
			return true;
		}
		//Now, need to check for conflicts
		ScopedNamespaceDefinition<?> parent = snDef.getParent();
		while (parent != null)
		{
			if (variableDefs.containsInList(varName, parent))
			{
				//Conflict with a higher level scope
				return false;
			}
			parent = parent.getParent();
		}
		boolean hasChildConflict = hasChildConflict(varName, snDef);
		if (!hasChildConflict)
		{
			variableDefs.addToListFor(varName, snDef);
		}
		return !hasChildConflict;
	}

	/**
	 * Returns true if there is a conflict the a child Scope for the given
	 * variable name.
	 */
	private boolean hasChildConflict(String varName,
		ScopedNamespaceDefinition<?> snDef)
	{
		List<ScopedNamespaceDefinition<?>> children =
				library.getChildScopes(snDef);
		if (children == null)
		{
			return false;
		}
		for (ScopedNamespaceDefinition<?> childScope : children)
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
	 * combination, knowing previous assertions of a ScopedNamespaceDefinition
	 * object for the given variable name.
	 * 
	 * If no previous ScopedNamespaceDefinition was stored via
	 * assertVariableScope for the given variable name, then this will
	 * unconditionally return false.
	 * 
	 * If a ScopedNamespaceDefinition was stored via assertVariableScope for the
	 * given variable name, then this will return true if the given
	 * VariableScope is compatible with the stored ScopedNamespaceDefinition.
	 * 
	 * @param snDef
	 *            The ScopedNamespaceDefinition used to determine if the scope
	 *            and name are a legal combination
	 * @param varName
	 *            The variable name used to determine if the scope and name are
	 *            a legal combination
	 * @return true if the given VariableScope and variable name are a legal
	 *         combination; false otherwise
	 * @throws IllegalArgumentException
	 *             if the given ScopedNamespaceDefinition is null
	 */
	public boolean isLegalVariableID(ScopedNamespaceDefinition<?> snDef,
		String varName)
	{
		if (snDef == null)
		{
			throw new IllegalArgumentException(
				"Scope Namespace Definition cannot be null");
		}
		if (variableDefs.containsInList(varName, snDef))
		{
			return true;
		}
		ScopedNamespaceDefinition<?> parent = snDef.getParent();
		return (parent != null) && isLegalVariableID(parent, varName);
	}

	/**
	 * Returns a non-null list of known ScopedNamespaceDefinition objects for
	 * the given variable name.
	 * 
	 * This is typically used for debugging (e.g. to list potential conflicts)
	 * 
	 * Ownership of the returned list is transferred to the calling object and
	 * no reference to it is maintained by VariableLibrary. Changing the
	 * returned list will not alter the VariableLibrary.
	 * 
	 * @param varName
	 *            The Variable name for which the relevant
	 *            ScopedNamespaceDefinition objects should be returned
	 * @return The List of ScopedNamespaceDefinition objects asserted for the
	 *         given variable name
	 * @throws IllegalArgumentException
	 *             if the given variable name is null, empty, or has
	 *             leading/trailing whitespace
	 */
	public List<ScopedNamespaceDefinition<?>> getKnownVariableScopes(
		String varName)
	{
		checkLegalVarName(varName);
		return variableDefs.getListFor(varName);
	}

	/**
	 * Returns a VariableScope with the given ScopedNamespaceDefinition and
	 * parent VariableScope.
	 * 
	 * @param <T>
	 *            The format of object contained in the VariableScope to be
	 *            instantiated
	 * @param parentScope
	 *            The VariableScope that is the parent of this VaribleScope
	 * @param snDef
	 *            The ScopedNamespaceDefinition that underlies the VariableScope
	 *            to be returned
	 * @return A VariableScope with the given ScopedNamespaceDefinition and
	 *         parent VariableScope
	 * @throws IllegalArgumentException
	 *             if the ScopedNamespaceDefinition of the given parent
	 *             VariableScope is not the parent ScopedNamespaceDefinition of
	 *             the given ScopedNamespaceDefinition
	 */
	public <T> VariableScope<T> instantiateScope(VariableScope<T> parentScope,
		ScopedNamespaceDefinition<T> snDef)
	{
		if (snDef == null)
		{
			throw new IllegalArgumentException("Definition cannot be null");
		}
		/*
		 * TODO Do we need a check that the given parentScope, stDef actually
		 * belong to this VariableLibrary?
		 */
		if (parentScope == null)
		{
			if (snDef.getParent() != null)
			{
				throw new IllegalArgumentException(
					"Cannot instantiate a scope with no parent unless it is a global scope");
			}
		}
		else if (!parentScope.getScopeDefinition().equals(snDef.getParent()))
		{
			if (snDef.getParent() == null)
			{
				throw new IllegalArgumentException(
					"Cannot instantiate a global scope with a parent");
			}
			throw new IllegalArgumentException("Parent Scope Definition was: "
				+ parentScope.getScopeDefinition().getName()
				+ " but parent of requested definition is: "
				+ snDef.getParent().getName());
		}
		return new VariableScope<T>(snDef, parentScope);
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
	 *            The format of object identified by the VariableID to be returned
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

	/**
	 * Returns a VariableID for the given name that is valid in the given scope
	 * (or any parent scope - recursively)
	 */
	private <T> VariableID<T> getVarIDMessaged(VariableScope<T> scope,
		String varName, VariableScope<T> messageScope)
	{
		if (scope == null)
		{
			throw new IllegalArgumentException("Cannot get VariableID "
				+ varName + " for " + messageScope + " scope");
		}
		checkLegalVarName(varName);
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

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

import java.util.HashMap;
import java.util.List;

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.variable.NamespaceDefinition;
import pcgen.base.formula.variable.VariableID;
import pcgen.base.util.CaseInsensitiveMap;
import pcgen.base.util.DoubleKeyMapToList;

/**
 * VariableLibrary performs the management of legal variable names within a
 * LegalScope. This ensures that when a VariableID is built, it is in an
 * appropriate structure to be evaluated.
 */
public class VariableLibrary
{

	/**
	 * The LegalScopeLibrary that supports to be used to determine "child"
	 * scopes from any LegalScope (in order to avoid variable name conflicts
	 * between different but non disjoint scopes).
	 */
	private final LegalScopeLibrary library;

	/**
	 * Constructs a new VariableLibrary, which uses the given LegalScopeLibrary
	 * to ensure variables are legal within a given scope.
	 * 
	 * @param vsLibrary
	 *            The LegalScopeLibrary used to to ensure variables are legal
	 *            within a given scope
	 * @throws IllegalArgumentException
	 *             if the given library is null
	 */
	public VariableLibrary(LegalScopeLibrary vsLibrary)
	{
		if (vsLibrary == null)
		{
			throw new IllegalArgumentException(
				"Scope Namespace Definition Library cannot be null");
		}
		library = vsLibrary;
	}

	/**
	 * Holds a map from variable names and namespaces to the LegalScope objects
	 * where that variable name is legal.
	 */
	/*
	 * Is stored from VarName->Namespace->LegalScope for 2 reasons:
	 * 
	 * (1) Case Sensitivity is easier to avoid because we have
	 * CaseInsensitiveMap rather than having to do case insensitivity as things
	 * are added to a list
	 * 
	 * (2) If a variable is not in ANY namespace/scope, this can shortcut
	 * verification. If this was NS->VS->String then all related VSs would have
	 * to be queried to get null responses. Here, this MapToList is queried,
	 * provides a null response and the validation process is complete.
	 */
	private DoubleKeyMapToList<String, NamespaceDefinition<?>, LegalScope> variableDefs =
			new DoubleKeyMapToList<>(CaseInsensitiveMap.class, HashMap.class);

	/**
	 * Asserts the given variable name is valid within the given LegalScope
	 * NamespaceDefinition.
	 * 
	 * If no previous definition for the given variable name and
	 * NamespaceDefinition was encountered, then the assertion automatically
	 * passes, and the given LegalScope is stored as the definition for the
	 * given variable name.
	 * 
	 * If a previous LegalScope exists for the given variable
	 * NamespaceDefinition and name, then this will return true if and only if
	 * the given LegalScope is equal to the already stored LegalScope.
	 * 
	 * @param legalScope
	 *            The asserted LegalScope for the given variable name
	 * @param namespaceDef
	 *            The NamespaceDefinition for the given variable name
	 * @param varName
	 *            The variable name for which the given
	 *            ScopedNamespaceDefinition is being asserted as valid
	 * 
	 * @return true if the assertion of this being a valid LegalScope for the
	 *         given variable NamespaceDefintion and name passes; false
	 *         otherwise
	 * @throws IllegalArgumentException
	 *             if any argument is null of if the variable name is otherwise
	 *             illegal (is empty or starts/ends with whitespace)
	 */
	public boolean assertLegalVariableID(LegalScope legalScope,
		NamespaceDefinition<?> namespaceDef, String varName)
	{
		if (namespaceDef == null)
		{
			throw new IllegalArgumentException(
				"NamespaceDefinition cannot be null");
		}
		if (legalScope == null)
		{
			throw new IllegalArgumentException("LegalScope cannot be null");
		}
		checkLegalVarName(varName);
		if (!variableDefs.containsListFor(varName, namespaceDef))
		{
			//Can't be a conflict
			addLegalVariable(legalScope, namespaceDef, varName);
			return true;
		}
		if (variableDefs.containsInList(varName, namespaceDef, legalScope))
		{
			//Asserted Scope Already there
			return true;
		}
		//Now, need to check for conflicts
		LegalScope parent = legalScope.getParentScope();
		while (parent != null)
		{
			if (variableDefs.containsInList(varName, namespaceDef, parent))
			{
				//Conflict with a higher level scope
				return false;
			}
			parent = parent.getParentScope();
		}
		boolean hasChildConflict =
				hasChildConflict(legalScope, namespaceDef, varName);
		if (!hasChildConflict)
		{
			addLegalVariable(legalScope, namespaceDef, varName);
		}
		return !hasChildConflict;
	}

	/**
	 * Adds a variable to this Library, including the necessary side effect of
	 * registering the LegalScope to ensure we know children as well as parent
	 * scopes.
	 */
	private void addLegalVariable(LegalScope legalScope,
		NamespaceDefinition<?> namespaceDef, String varName)
	{
		library.registerScope(legalScope);
		variableDefs.addToListFor(varName, namespaceDef, legalScope);
	}

	/**
	 * Returns true if there is a conflict the a child Scope for the given
	 * variable name.
	 */
	private boolean hasChildConflict(LegalScope legalScope,
		NamespaceDefinition<?> namespaceDef, String varName)
	{
		List<LegalScope> children = library.getChildScopes(legalScope);
		if (children == null)
		{
			return false;
		}
		for (LegalScope childScope : children)
		{
			if (variableDefs.containsInList(varName, namespaceDef, childScope)
				|| hasChildConflict(childScope, namespaceDef, varName))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the given LegalScope, NamespaceDefinition, and variable
	 * name are a legal combination, knowing previous assertions of a LegalScope
	 * object for the given NamespaceDefinition and variable name.
	 * 
	 * If no previous LegalScope was stored via assertLegalScope for the given
	 * NamespaceDefinition and variable name, then this will unconditionally
	 * return false.
	 * 
	 * If a LegalScope was stored via assertLegalScope for the given
	 * NamespaceDefinition and variable name, then this will return true if the
	 * given LegalScope is compatible with the stored LegalScope.
	 * 
	 * @param legalScope
	 *            The LegalScope to be used to determine if the given
	 *            combination is legal
	 * @param namespaceDef
	 *            The NamespaceDefinition to be used to determine if the given
	 *            combination is legal
	 * @param varName
	 *            The variable name to be used to determine if the given
	 *            combination is legal
	 * 
	 * @return true if the given LegalScope, NamespaceDefinition and variable
	 *         name are a legal combination; false otherwise
	 * @throws IllegalArgumentException
	 *             if the given LegalScope or NamespaceDefinition is null
	 */
	public boolean isLegalVariableID(LegalScope legalScope,
		NamespaceDefinition<?> namespaceDef, String varName)
	{
		if (namespaceDef == null)
		{
			throw new IllegalArgumentException(
				"Namespace Definition cannot be null");
		}
		if (legalScope == null)
		{
			throw new IllegalArgumentException("LegalScope cannot be null");
		}
		if (variableDefs.containsInList(varName, namespaceDef, legalScope))
		{
			return true;
		}
		LegalScope parent = legalScope.getParentScope();
		return (parent != null)
			&& isLegalVariableID(parent, namespaceDef, varName);
	}

	/**
	 * Returns a non-null list of known LegalScope objects for the given
	 * NamespaceDefinition and variable name.
	 * 
	 * This is typically used for debugging (e.g. to list potential conflicts)
	 * 
	 * Ownership of the returned list is transferred to the calling object and
	 * no reference to it is maintained by VariableLibrary. Changing the
	 * returned list will not alter the VariableLibrary.
	 * 
	 * @param namespaceDef
	 *            The NamespaceDefinition in which the relevant LegalScope
	 *            objects should be returned
	 * @param varName
	 *            The Variable name for which the relevant LegalScope objects
	 *            should be returned
	 * 
	 * @return The List of LegalScope objects asserted for the given variable
	 *         name
	 * @throws IllegalArgumentException
	 *             if the NamespaceDefinition is null or if the given variable
	 *             name is null, empty, or has leading/trailing whitespace
	 */
	public List<LegalScope> getKnownLegalScopes(
		NamespaceDefinition<?> namespaceDef, String varName)
	{
		if (namespaceDef == null)
		{
			throw new IllegalArgumentException(
				"Namespace Definition cannot be null");
		}
		checkLegalVarName(varName);
		return variableDefs.getListFor(varName, namespaceDef);
	}

	/**
	 * Returns a VariableID for the given ScopeInstance, NamespaceDefinition and
	 * variable name, if legal.
	 * 
	 * The rules for legality are defined in the isLegalVariableID method
	 * description.
	 * 
	 * If isLegalVariableID returns false, then this method will throw an
	 * exception. isLegalVariableID should be called first to determine if
	 * calling this method is safe.
	 * 
	 * @param <T>
	 *            The format of object identified by the VariableID to be
	 *            returned
	 * @param scopeInst
	 *            The ScopeInstance used to determine if the ScopeInstance,
	 *            NamespaceDefinition and name are a legal combination
	 * @param varName
	 *            The variable name used to determine if the ScopeInstance,
	 *            NamespaceDefinition and name are a legal combination
	 * @return A VariableID of the given ScopeInstance, NamespaceDefinition and
	 *         variable name if they are are a legal combination
	 * @throws IllegalArgumentException
	 *             if the given ScopeInstance is null, NamespaceDefinition is
	 *             null, the name is invalid, or if the ScopeInstance,
	 *             NamespaceDefinition and variable name are not a legal
	 *             combination
	 */
	public <T> VariableID<T> getVariableID(ScopeInstance scopeInst,
		NamespaceDefinition<T> namespaceDef, String varName)
	{
		return getVarIDMessaged(scopeInst, namespaceDef, varName, scopeInst);
	}

	/**
	 * Returns a VariableID for the given name that is valid in the given
	 * ScopeInstance (or any parent ScopeInstance - recursively).
	 */
	private <T> VariableID<T> getVarIDMessaged(ScopeInstance scopeInst,
		NamespaceDefinition<T> namespaceDef, String varName,
		ScopeInstance messageScope)
	{
		if (scopeInst == null)
		{
			throw new IllegalArgumentException("Cannot get VariableID "
				+ varName + " for " + messageScope + " scope");
		}
		checkLegalVarName(varName);
		if (variableDefs.containsInList(varName, namespaceDef,
			scopeInst.getLegalScope()))
		{
			return new VariableID<T>(scopeInst, namespaceDef, varName);
		}
		return getVarIDMessaged(scopeInst.getParentScope(), namespaceDef,
			varName, messageScope);
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

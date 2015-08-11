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

/**
 * VariableScope identifies a scope in which a particular part of a formula
 * (usually a variable) is valid.
 * 
 * This effectively providing a concept similar to namespaces or local
 * variables, depending on the language used as an analogy.
 * 
 * Scope provides the ability to not only distinguish the different scopes, but
 * also to define the relationship between the scopes (each scope can identify
 * its parent scope - if you need to know child scopes, see ScopeLibrary).
 * 
 * It is perhaps important to recognize that a "scope" is simply an identifier.
 * It does not actually contain information about what the scope contains (it
 * does not, for example, have a list of legal variables for the scope). This is
 * because the VariableID HAS-A scope, not a scope HAS-A list of VariableIDs.
 * (The exact reasoning for this relates to how VariableIDs are constructed, as
 * well as the fact that they can be "destroyed" by losing a reference to the
 * VariableID, in which case we do not want the contractual requirement on a
 * developer to have to call back to the scope in order to clean up a
 * collection).
 * 
 * @param <T>
 *            The format of object contained within this VariableScope
 */
public class VariableScope<T>
{

	/**
	 * The VariableScope that is a parent of this VariableScope.
	 */
	private final VariableScope<T> parent;

	/**
	 * The ScopedNamespaceDefinition that underlies this VariableScope.
	 */
	private final ScopedNamespaceDefinition<T> snDef;

	/**
	 * Constructs a new VariableScope with the given ScopedNamespaceDefinition
	 * and parent.
	 * 
	 * @param snDef
	 *            The ScopedNamespaceDefinition that underlies this
	 *            VariableScope
	 * @param parentScope
	 *            The VariableScope that is a parent of this VariableScope. May
	 *            be null to represent global if the given
	 *            ScopedNamespaceDefinition has no parent (is global)
	 * @throws IllegalArgumentException
	 *             if the given ScopedNamespaceDefinition is null or the
	 *             parentScope is null when the given ScopedNamespaceDefinition
	 *             has a non-null parent
	 */
	VariableScope(ScopedNamespaceDefinition<T> snDef,
		VariableScope<T> parentScope)
	{
		if (snDef == null)
		{
			throw new IllegalArgumentException("Definition cannot be null");
		}
		if ((parentScope == null) && (snDef.getParent() != null))
		{
			throw new IllegalArgumentException(
				"Cannot create Scope with null parent unless definition has no parent");
		}
		this.snDef = snDef;
		this.parent = parentScope;
	}

	/**
	 * Returns the VariableScope that serves as a "parent" for this
	 * VariableScope.
	 * 
	 * Null is a legal return value for a "master" scope. ScopeLibrary enforces
	 * that only one Scope may have no parent.
	 * 
	 * @return The VariableScope that serves as a "parent" for this
	 *         VariableScope
	 */
	public VariableScope<T> getParentScope()
	{
		return parent;
	}

	/**
	 * Returns the ScopedNamespaceDefinition for this VariableScope. This was
	 * used as the framework for constructing this scope, and is necessary as
	 * the "key" (in a java.util.Map sense of "key") to get the list of legal
	 * variables from the ScopeLibrary.
	 * 
	 * @return The ScopedNamespaceDefinition used to define this VariableScope
	 */
	public ScopedNamespaceDefinition<T> getScopeDefinition()
	{
		return snDef;
	}

	/**
	 * Returns the format (e.g. Number.class) of this VariableScope (as
	 * controlled by the ScopedNamespaceDefinition).
	 * 
	 * @return The format (e.g. Number.class) of this VariableScope
	 */
	public Class<T> getVariableFormat()
	{
		return snDef.getNamespaceDefinition().getVariableFormat();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (parent == null)
		{
			return "Global (" + snDef + ")";
		}
		else
		{
			return parent + " (" + snDef + ")";
		}
	}
}

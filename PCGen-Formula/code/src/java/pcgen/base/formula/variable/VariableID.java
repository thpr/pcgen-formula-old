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

import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.lang.CaseInsensitiveString;

/**
 * A VariableID is a combination of a variable name and scope that is used to
 * uniquely identify a variable. This class is effectively a composite
 * (design-pattern) object designed to keep the two "primary keys" necessary to
 * uniquely identify a variable in one location.
 * 
 * The variable name is considered case-insensitive. The scope is a
 * ScopeInstance.
 * 
 * Note that a user should not directly construct a VariableID. Since it is
 * intended that only VariableIDs in a specific scope is legal, a system should
 * depend on pcgen.base.formula.manager.VariableLibrary to provide construction
 * services for VariableID objects. Exact details of VariableID uniqueness are
 * provided in VariableIDFactory.
 * 
 * @param <T>
 *            The format of object identified by this VariableID
 */
public class VariableID<T>
{

	/**
	 * The Namespace for this VariableID.
	 */
	private final NamespaceDefinition<T> namespace;

	/**
	 * The ScopeInstance for this VariableID.
	 */
	private final ScopeInstance scope;

	/**
	 * The name for this VariableID (how it will appear in a formula).
	 */
	private final CaseInsensitiveString varName;

	/*
	 * We choose not to do any enforcement of variable names beyond what is
	 * already enforced (non-null, non-empty, no leading/trailing whitespace).
	 * (In other words, we are doing a simple set of enforcement, NOT the same
	 * enforcement as done by the formula parser). This is consciously done to
	 * allow "virtual VariableIDs" that may not otherwise be within a formula,
	 * but which are necessary in a larger system.
	 */

	/**
	 * Constructs a new VariableID with the given ScopeInstance,
	 * NamespaceDefinition and name.
	 * 
	 * Note: While this is public, it is highly advised to use a
	 * VariableIDFactory to construct new instances of VariableID.
	 * 
	 * @param scopeInst
	 *            The ScopeInstance of the variable represented by this
	 *            VariableID
	 * @param namespace
	 *            The NamespaceDefinition of the variable represented by this
	 *            VariableID
	 * @param varName
	 *            The name of the variable represented by this VariableID
	 * @throws IllegalArgumentException
	 *             if any argument is null, or if the name is empty or
	 *             starts/ends with whitespace
	 */
	public VariableID(ScopeInstance scopeInst,
		NamespaceDefinition<T> namespace, String varName)
	{
		if (namespace == null)
		{
			throw new IllegalArgumentException(
				"NamespaceDefinition cannot be null");
		}
		if (scopeInst == null)
		{
			throw new IllegalArgumentException("Scope cannot be null");
		}
		if (varName == null)
		{
			throw new IllegalArgumentException("Variable Name cannot be null");
		}
		String trimmed = varName.trim();
		if (!varName.equals(trimmed))
		{
			throw new IllegalArgumentException(
				"Variable Name cannot start/end with whitespace");
		}
		if (varName.length() == 0)
		{
			throw new IllegalArgumentException("Variable Name cannot be empty");
		}
		this.namespace = namespace;
		this.scope = scopeInst;
		this.varName = new CaseInsensitiveString(varName);
	}

	/**
	 * Returns the ScopeInstance of the variable represented by this VariableID.
	 * Guaranteed to be non-null.
	 * 
	 * @return The ScopeInstance of the variable represented by this VariableID
	 */
	public ScopeInstance getScope()
	{
		return scope;
	}

	/**
	 * Returns the name of the variable represented by this VariableID.
	 * Guaranteed to be non-null, non-empty.
	 * 
	 * @return The name of the variable represented by this VariableID
	 */
	public String getName()
	{
		return varName.toString();
	}

	/**
	 * Returns the NamespaceDefinition of this VariableID.
	 * 
	 * @return The NamespaceDefinition of this VariableID
	 */
	public NamespaceDefinition<T> getNamespace()
	{
		return namespace;
	}

	/**
	 * Returns the format (e.g. Number.class) of this VariableID (as controlled
	 * by the NamespaceDefinition).
	 * 
	 * @return The format (e.g. Number.class) of this VariableID
	 */
	public Class<T> getVariableFormat()
	{
		return namespace.getFormatManager().getManagedClass();
	}

	/**
	 * Consistent-with-equals hash code.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int prime = 31;
		int result = prime + varName.hashCode();
		result = prime * result + scope.hashCode();
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		VariableID<?> other = (VariableID<?>) obj;
		return varName.equals(other.varName)
			&& namespace.equals(other.namespace) && scope.equals(other.scope);
	}

}

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
 * A ScopedNamespaceDefinition is a generic definition defining how a series of
 * VariableScope objects can be instantiated.
 * 
 * Understanding why there is a Scope and a ScopeDefinition is perhaps best
 * understood through an example of the intended life-cycle of variables and
 * formulas.
 * 
 * It is expected that a variable will be defined by some definition statement.
 * In that statement, the variable Scope will be defined (e.g. Global or
 * Local). If the scope is local, then it will have a specific context for that
 * locality based on where it was defined. In PCGen terms, that might be
 * "Equipment". There may also be a Sub-Scope such as "Equipment Part". (The
 * exact naming is up to the application, ScopedNamespaceDefinition does no
 * relationship enforcement based on the name).
 * 
 * At Runtime, we then parse a formula to determine what variables it contains.
 * This formula will have with it a FormulaManager that contains a
 * VariableScope. This VariableScope is checked against the
 * ScopedNamespaceDefinition to see if it is legal. The particular thing to
 * understand here is that we are no longer operating within a generic
 * "Equipment.class" definition. The scope is more concrete than the definition,
 * in that it is specific to a certain *instance* of Equipment.class.
 * 
 * @param <T>
 *            The format of variable object to be contained in VariableScope
 *            objects defined by this ScopedNamespaceDefinition
 */
public class ScopedNamespaceDefinition<T>
{
	/**
	 * Identifies the ScopedNamespaceDefinition that is the parent of this
	 * ScopedNamespaceDefinition.
	 */
	private final ScopedNamespaceDefinition<T> parentDef;

	/**
	 * Identifies the name of this ScopedNamespaceDefinition.
	 */
	private final String scopeName;

	/**
	 * Identifies the Namespace of object covered by this
	 * ScopedNamespaceDefinition.
	 */
	private final NamespaceDefinition<T> namespaceDef;

	/**
	 * Note: For use by Global Scope only. The Global Scope is represented by
	 * the empty name ("").
	 * 
	 * @param namespace
	 *            The Namespace of object covered by this
	 *            ScopedNamespaceDefinition
	 * @throws IllegalArgumentException
	 *             if any of the parameters are null
	 */
	ScopedNamespaceDefinition(NamespaceDefinition<T> namespace)
	{
		if (namespace == null)
		{
			throw new IllegalArgumentException(
				"Namespace for this ScopedNamespaceDefinition cannot be null");
		}
		parentDef = null;
		this.scopeName = "";
		this.namespaceDef = namespace;
	}

	/**
	 * Constructs a new ScopedNamespaceDefinition with the given parent and
	 * scope definition name.
	 * 
	 * Package protected in order to have ScopeLibrary be the exclusive source
	 * of creation for a ScopedNamespaceDefinition.
	 * 
	 * The format of object covered by this ScopedNamespaceDefinition will match
	 * that of the given parent definition.
	 * 
	 * @param parentDef
	 *            The parent ScopedNamespaceDefinition for this
	 *            ScopedNamespaceDefinition
	 * @param scopeName
	 *            The name of this ScopedNamespaceDefinition. Must not be null
	 *            or empty.
	 * @throws IllegalArgumentException
	 *             if any of the parameters are null or if the scopeName is zero
	 *             length
	 */
	ScopedNamespaceDefinition(ScopedNamespaceDefinition<T> parentDef,
		String scopeName)
	{
		if (parentDef == null)
		{
			throw new IllegalArgumentException(
				"Parent Definition cannot be null");
		}
		if ((scopeName == null) || (scopeName.length() == 0))
		{
			throw new IllegalArgumentException(
				"Scope Name cannot be null or empty");
		}
		this.parentDef = parentDef;
		this.scopeName = scopeName;
		this.namespaceDef = parentDef.namespaceDef;
	}

	/**
	 * Returns the (non-null) name of this ScopedNamespaceDefinition. This is an
	 * empty string ("") for a Global ScopedNamespaceDefinition.
	 * 
	 * @return The name of this ScopedNamespaceDefinition
	 */
	public String getName()
	{
		return scopeName;
	}

	/**
	 * Returns the parent ScopedNamespaceDefinition of this
	 * ScopedNamespaceDefinition.
	 * 
	 * Note that this can be null in the unique case of the Global Scope (this
	 * limit is enforced by ScopeLibrary)
	 * 
	 * @return The parent ScopedNamespaceDefinition of this
	 *         ScopedNamespaceDefinition
	 */
	public ScopedNamespaceDefinition<T> getParent()
	{
		return parentDef;
	}

	/**
	 * Returns the NamespaceDefinition representing the namespace covered by
	 * this ScopedNamespaceDefinition.
	 * 
	 * @return The NamespaceDefinition representing the namespace covered by
	 *         this ScopedNamespaceDefinition
	 */
	public NamespaceDefinition<T> getNamespaceDefinition()
	{
		return namespaceDef;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return scopeName + " " + namespaceDef;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return scopeName.hashCode() * 37 + namespaceDef.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof ScopedNamespaceDefinition)
		{
			ScopedNamespaceDefinition<?> other =
					(ScopedNamespaceDefinition<?>) o;
			return scopeName.equals(other.scopeName)
				&& namespaceDef.equals(other.namespaceDef);
		}
		return false;
	}
}

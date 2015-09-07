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
package pcgen.base.formula.variable;

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;

/**
 * A SimpleScopeInstance is a minimal implementation of the ScopeInstance
 * interface.
 */
public class SimpleScopeInstance implements ScopeInstance
{

	/**
	 * Contains the ScopeInstance that is the parent of this ScopeInstance.
	 */
	private final ScopeInstance parent;

	/**
	 * Contains the LegalScope in which this ScopeInstance was instantiated.
	 */
	private final LegalScope scope;

	/**
	 * Constructs a new SimpleScopeInstance with the given parent ScopeInstance
	 * and within the given LegalScope.
	 * 
	 * @param parent
	 *            the ScopeInstance that is the parent of this ScopeInstance
	 * @param scope
	 *            the LegalScope in which this ScopeInstance was instantiated
	 */
	public SimpleScopeInstance(ScopeInstance parent, LegalScope scope)
	{
		if (scope == null)
		{
			throw new IllegalArgumentException("LegalScope cannot be null");
		}
		if (parent == null)
		{
			if (scope.getParentScope() != null)
			{
				throw new IllegalArgumentException(
					"Incompatible ScopeInstance and LegalScope: "
						+ "Parent may only be null "
						+ "when LegalScope has no parent");
			}
		}
		else
		{
			if (!scope.getParentScope().equals(parent.getLegalScope()))
			{
				throw new IllegalArgumentException(
					"Incompatible ScopeInstance and LegalScope");
			}
		}
		this.parent = parent;
		this.scope = scope;
	}

	/**
	 * @see pcgen.base.formula.base.ScopeInstance#getLegalScope()
	 */
	@Override
	public LegalScope getLegalScope()
	{
		return scope;
	}

	/**
	 * @see pcgen.base.formula.base.ScopeInstance#getParentScope()
	 */
	@Override
	public ScopeInstance getParentScope()
	{
		return parent;
	}

}

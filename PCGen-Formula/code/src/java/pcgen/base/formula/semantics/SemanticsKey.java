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
package pcgen.base.formula.semantics;

/**
 * A SemanticsKey is a key to types of formula semantics.
 *
 * Note that the Generic type has no local effects, it is used solely as a
 * constraint when dealing with info stored by the SemanticsKey.
 *
 * @param <T>
 *            The type of object managed by a SemanticsKey when the given key is
 *            provided.
 */
public final class SemanticsKey<T>
{
	/**
	 * Cast the given object to the type contained by this SemanticsKey
	 * 
	 * @param object
	 *            The object to be cast to the type managed by this SemanticsKey
	 * @return The given object, cast to the type managed by this SemanticsKey
	 */
	@SuppressWarnings("unchecked")
	public T cast(Object object)
	{
		return (T) object;
	}
}

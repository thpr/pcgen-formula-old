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

import pcgen.base.format.FormatManager;

/**
 * A NamespaceDefinition is a combination of a name (e.g. "AREA") with a Format
 * Class (e.g. Point2D.class).
 * 
 * @param <T>
 *            The class indicating the format of object to be stored for
 *            variables in this namespace
 */
public class NamespaceDefinition<T>
{

	/**
	 * The FormatManager of objects stored for variables in this namespace.
	 */
	private final FormatManager<T> formatManager;

	/**
	 * The name of this Namespace.
	 */
	private final String namespaceName;

	/**
	 * Constructs a new NamespaceDefinition representing the given format of
	 * object and given Namespace name.
	 * 
	 * @param fmtManager
	 *            The FormatManager of objects stored for variables in this
	 *            namespace
	 * @param name
	 *            The name of this Namespacee
	 * @throws IllegalArgumentException
	 *             if any parameter is null or the namespace name is length zero
	 */
	public NamespaceDefinition(FormatManager<T> fmtManager, String name)
	{
		if ((name == null) || (name.length() == 0))
		{
			throw new IllegalArgumentException(
				"Variable Namespace Name cannot be null or empty");
		}
		if (fmtManager == null)
		{
			throw new IllegalArgumentException(
				"FormatManager cannot be null");
		}
		this.formatManager = fmtManager;
		this.namespaceName = name;
	}

	/**
	 * Returns the FormatManager representing the class (format) of object
	 * stored for variables in this namespace.
	 * 
	 * @return the FormatManager representing the class (format) of object
	 *         stored for variables in this namespace
	 */
	public FormatManager<T> getFormatManager()
	{
		return formatManager;
	}

	/**
	 * Returns the Namespace name.
	 * 
	 * @return The Namespace name.
	 */
	public String getNamespaceName()
	{
		return namespaceName;
	}

	/**
	 * Consistent-with-equals hashCode
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return formatManager.hashCode() ^ namespaceName.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof NamespaceDefinition)
		{
			NamespaceDefinition<?> other = (NamespaceDefinition<?>) o;
			return formatManager.equals(other.formatManager)
				&& namespaceName.equals(other.namespaceName);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return namespaceName + " ["
			+ formatManager.getManagedClass().getSimpleName() + "]";
	}
	
	public NamespaceDefinition<?> getComponentNamespace()
	{
		FormatManager<?> componentManager = formatManager.getComponentManager();
		return new NamespaceDefinition<>(componentManager, "*" + namespaceName);
	}
}

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
 * A VariableTypeDefinition is a combination of a type name (e.g. "AREA") with a
 * Format Class (e.g. Point2D.class).
 * 
 * @param <T>
 *            The class of object to be stored for variables of this type.
 */
public class VariableTypeDefinition<T>
{
	/**
	 * The class (format) of object stored for variables of this type.
	 */
	private final Class<T> variableFormat;

	/**
	 * The name of this type of variable.
	 */
	private final String variableTypeName;

	/**
	 * Constructs a new VariableTypeDefinition representing the given class of
	 * object and given type name.
	 * 
	 * @param varFormat
	 *            The class (format) of object stored for variables of this type
	 * @param varTypeName
	 *            The name of this type of variable
	 * @throws IllegalArgumentException
	 *             if any parameter is null or the type name is length zero
	 */
	public VariableTypeDefinition(Class<T> varFormat, String varTypeName)
	{
		if ((varTypeName == null) || (varTypeName.length() == 0))
		{
			throw new IllegalArgumentException(
				"Variable Type Name cannot be null or empty");
		}
		if (varFormat == null)
		{
			throw new IllegalArgumentException(
				"Variable Class (Format) cannot be null");
		}
		this.variableFormat = varFormat;
		this.variableTypeName = varTypeName;
	}

	/**
	 * Returns the Class representing the class (format) of object stored for
	 * variables of this type.
	 * 
	 * @return the Class representing the class (format) of object stored for
	 *         variables of this type
	 */
	public Class<T> getVariableClass()
	{
		return variableFormat;
	}

	/**
	 * Returns the type name of this type of variable.
	 * 
	 * @return The type name of this type of variable.
	 */
	public String getVariableTypeName()
	{
		return variableTypeName;
	}

	/**
	 * Consistent-with-equals hashCode
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return variableFormat.hashCode() ^ variableTypeName.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof VariableTypeDefinition)
		{
			VariableTypeDefinition<?> other = (VariableTypeDefinition<?>) o;
			return variableFormat.equals(other.variableFormat)
				&& variableTypeName.equals(other.variableTypeName);
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return variableTypeName + " [" + variableFormat.getSimpleName() + "]";
	}
}

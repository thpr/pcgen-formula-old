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
package pcgen.base.formula.base;

/**
 * FormulaSemanticsValid represents a report that a formula is valid, and
 * indicates the semantics of the Formula (what type it will return)
 */
public class FormulaSemanticsValid implements FormulaSemantics
{

	/**
	 * The class of object returned by the Formula
	 */
	private final Class<?> cl;

	/**
	 * Constructs a new FormulaSemanticsValid indicating the return format of
	 * the formula this FormulaSemanticsValid is reporting upon.
	 * 
	 * @param c
	 *            The class indicating the format of the formula this
	 *            FormulaSemanticsValid is reporting upon.
	 */
	public FormulaSemanticsValid(Class<?> c)
	{
		if (c == null)
		{
			throw new IllegalArgumentException(
				"Cannot initialize with null class");
		}
		cl = c;
	}

	/**
	 * Unconditionally return true (this represents a Valid Formula)
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#isValid()
	 */
	@Override
	public boolean isValid()
	{
		return true;
	}

	/**
	 * Return an empty String (we only have to guarantee not null)
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#getReport()
	 */
	@Override
	public String getReport()
	{
		return "";
	}

	/**
	 * Return class indicating the format of the formula this
	 * FormulaSemanticsValid is reporting upon.
	 * 
	 * @see pcgen.base.formula.base.FormulaSemantics#getSemanticState()
	 */
	@Override
	public Class<?> getSemanticState()
	{
		return cl;
	}

}

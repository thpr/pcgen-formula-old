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

import pcgen.base.formula.util.KeyUtilities;

/**
 * Utility classes related to the FormulaSemantics class.
 */
public final class FormulaSemanticsUtilities
{

	/**
	 * Private Constructor for Utility Class.
	 */
	private FormulaSemanticsUtilities()
	{
	}

	/**
	 * Sets a FormulaSemantics object to indicate a Formula is invalid, and with
	 * the given text as the report indicating why the Formula is invalid.
	 * 
	 * @param semantics
	 *            The FormulaSemantics object to be modified to indicate the
	 *            formula is invalid
	 * @param text
	 *            The String to be used as the report for why the Formula for
	 *            the given FormulaSemantics is invalid.
	 */
	public static void setInvalid(FormulaSemantics semantics, String text)
	{
		semantics.setInfo(KeyUtilities.SEM_VALID, new FormulaValidity(false));
		semantics.setInfo(KeyUtilities.SEM_REPORT, new FormulaInvalidReport(
			text));
	}

	/**
	 * Returns a new FormulaSemantics initialized to indicate that a formula is
	 * Valid (required starting point, since allowArgs only overrides when not
	 * valid - it cannot "guess" when processing is complete).
	 * 
	 * @return A new FormulaSemantics initialized to indicate that a formula is
	 *         Valid
	 */
	public static FormulaSemantics getInitializedSemantics()
	{
		FormulaSemantics semantics = new FormulaSemantics();
		semantics.setInfo(KeyUtilities.SEM_VALID, new FormulaValidity(true));
		return semantics;
	}

}

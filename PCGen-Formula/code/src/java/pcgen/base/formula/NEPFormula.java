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
package pcgen.base.formula;

import pcgen.base.formula.base.FormulaDependencyManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.manager.FormulaManager;
import pcgen.base.formula.manager.ScopeInformation;
import pcgen.base.formula.variable.ScopedNamespaceDefinition;

/**
 * A NEPFormula is a formula that is part of the "Native Equation Parser" for
 * PCGen.
 * 
 * @param <T>
 *            The Type of object returned by this NEPFormula
 */
public interface NEPFormula<T>
{

	/**
	 * Resolves the NEPFormula in the context of the given ScopeInformation. The
	 * given ScopeInformation must contain information about variable values,
	 * available functions, and other characteristics required for the formula
	 * to produce a value.
	 * 
	 * If variables and formulas required by the NEPFormula are not available in
	 * the given ScopeInformation, behavior is not guaranteed and NEPFormula or
	 * other methods called within this method reserve the right to throw an
	 * Exception or otherwise not fail gracefully. (The precise behavior is
	 * likely defined by the ScopeInformation).
	 * 
	 * Note that the exact type of the return value is not guaranteed by the
	 * NEPFormula. Rather, it is constrained to being a Number. The exact class
	 * returned is defined by the ScopeInformation, which can therefore
	 * implement the appropriate precision desired for the given calculation.
	 * 
	 * @param si
	 *            The ScopeInformation providing the context in which the
	 *            NEPFormula is to be resolved
	 * @return A Number representing the value calculated for the NEPFormula
	 * @throws IllegalArgumentException
	 *             if the given ScopeInformation is null.
	 */
	public T resolve(ScopeInformation si);

	/**
	 * Returns the FormulaSemantics for the NEPFormula.
	 * 
	 * The given FormulaManager must contain information about variable values,
	 * available functions, and other characteristics required for the formula
	 * to establish the list of variables contained within the NEPFormula. These
	 * must be valid within the context of the given ScopedNamespaceDefinition.
	 * 
	 * @param fm
	 *            The FormulaManager providing the context in which the
	 *            NEPFormula is to be resolved
	 * @param snDef
	 *            The ScopedNamespaceDefinition in which the NEPFormula should
	 *            be checked to ensure it is valid
	 * @return The FormulaSemantics for the NEPFormula
	 */
	public FormulaSemantics isValid(FormulaManager fm,
		ScopedNamespaceDefinition<T> snDef);

	/**
	 * Determines the dependencies for this formula, including the VariableID
	 * objects representing the variables within the NEPFormula.
	 * 
	 * The given ScopeInformation must contain information about variable
	 * values, available functions, and other characteristics required for the
	 * formula to establish the list of variables contained within the
	 * NEPFormula.
	 * 
	 * The given FormulaDependencyManager will be loaded with the dependency
	 * information.
	 * 
	 * @param scopeInfo
	 *            The ScopeInformation providing the context in which the
	 *            NEPFormula variables are to be determined
	 * @param fdm
	 *            The FormulaDependencyManager to be used to capture the
	 *            dependencies
	 * @throws IllegalArgumentException
	 *             if the given ScopeInformation is null
	 */
	public void getDependencies(ScopeInformation scopeInfo,
		FormulaDependencyManager fdm);
}

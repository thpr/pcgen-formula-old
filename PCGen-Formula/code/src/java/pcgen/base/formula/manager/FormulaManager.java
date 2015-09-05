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
package pcgen.base.formula.manager;

import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.semantics.FormulaSemantics;
import pcgen.base.formula.semantics.FormulaSemanticsUtilities;
import pcgen.base.formula.util.KeyUtilities;
import pcgen.base.formula.variable.NamespaceDefinition;
import pcgen.base.formula.variable.VariableStore;
import pcgen.base.formula.visitor.SemanticsVisitor;

/**
 * A FormulaManager exists as compound object to simplify those things that
 * require context to be resolved (legal functions, variables). This provides a
 * convenient, single location for consolidation of these capabilities (and thus
 * keeps the number of parameters that have to be passed around to a reasonable
 * level).
 * 
 * This is also an object used to "cache" the SemanticsVisitor (since the
 * visitor needs to know some of the contents in the FormulaManager, it can be
 * lazily instantiated but then effectively cached as long as that
 * FormulaManager is reused - especially valuable for things like the global
 * context which in the future we can create once for the PC and never have to
 * recreate...)
 */
public class FormulaManager
{

	/**
	 * The SemanticsVisitor for this FormulaManager. Can return the
	 * FormulaSemantics for a parsed tree. Lazily Instantiated.
	 */
	private SemanticsVisitor semanticsVisitor;

	/**
	 * The FunctionLibrary used to store valid functions in this FormulaManager.
	 */
	private final FunctionLibrary ftnLibrary;

	/**
	 * The OperatorLibrary used to store valid operators in this FormulaManager.
	 */
	private final OperatorLibrary opLibrary;

	/**
	 * The VariableLibrary used to get VariableIDs.
	 */
	private final VariableLibrary varLibrary;

	/**
	 * The active VariableStore used to cache results of items processed through
	 * this FormulaManager (thus serves as a storage location for variable
	 * values).
	 */
	private final VariableStore results;

	/**
	 * Constructs a new FormulaManager from the provided FunctionLibrary,
	 * OperatorLibrary, VariableLibrary, and VariableStore.
	 * 
	 * @param ftnLibrary
	 *            The FunctionLibrary used to store valid functions in this
	 *            FormulaManager
	 * @param opLibrary
	 *            The OperatorLibrary used to store valid operators in this
	 *            FormulaManager
	 * @param varLibrary
	 *            The VariableLibrary used to get VariableIDs
	 * @param resultStore
	 *            The VariableStore used to hold variables values for items
	 *            processed through this FormulaManager
	 * @throws IllegalArgumentException
	 *             if any parameter is null
	 */
	public FormulaManager(FunctionLibrary ftnLibrary,
		OperatorLibrary opLibrary, VariableLibrary varLibrary,
		VariableStore resultStore)
	{
		if (ftnLibrary == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null FunctionLibrary");
		}
		if (opLibrary == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null OperatorLibrary");
		}
		if (varLibrary == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null VariableLibrary");
		}
		if (resultStore == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null VariableStore");
		}
		this.ftnLibrary = ftnLibrary;
		this.opLibrary = opLibrary;
		this.varLibrary = varLibrary;
		this.results = resultStore;
	}

	/**
	 * Returns the VariableLibrary used to get VariableIDs.
	 * 
	 * @return The VariableLibrary used to get VariableIDs
	 */
	public VariableLibrary getFactory()
	{
		return varLibrary;
	}

	/**
	 * Returns the VariableStore used to hold variables values for items
	 * processed through this FormulaManager.
	 * 
	 * @return The VariableStore used to hold variables values for items
	 *         processed through this FormulaManager
	 */
	public VariableStore getResolver()
	{
		return results;
	}

	/**
	 * Returns the FunctionLibrary used to store valid functions in this
	 * FormulaManager.
	 * 
	 * @return The FunctionLibrary used to store valid functions in this
	 *         FormulaManager
	 */
	public FunctionLibrary getLibrary()
	{
		return ftnLibrary;
	}

	/**
	 * Returns the OperatorLibrary used to store valid operations in this
	 * FormulaManager.
	 * 
	 * @return The OperatorLibrary used to store valid operations in this
	 *         FormulaManager
	 */
	public OperatorLibrary getOperatorLibrary()
	{
		return opLibrary;
	}

	/**
	 * Returns the FormulaSemantics for the formula starting with with the given
	 * SimpleNode as the root of the parsed tree of the formula.
	 * 
	 * @param root
	 *            The starting node in a parsed tree of a formula, to be used
	 *            for the semantics evaluation
	 * @param legalScope
	 *            The LegalScope used to check for validity of variables used
	 *            within the formula
	 * @param namespaceDef
	 *            The NamespaceDefinition used to check for validity of
	 *            variables used within the formula
	 * @return The FormulaSemantics for the formula starting with with the given
	 *         SimpleNode as the root of the parsed tree of the formula
	 * @throws IllegalArgumentException
	 *             if any parameter is null
	 */
	public FormulaSemantics isValid(SimpleNode root, LegalScope legalScope,
		NamespaceDefinition<?> namespaceDef)
	{
		if (root == null)
		{
			throw new IllegalArgumentException(
				"Cannot determine validity with null root");
		}
		if (namespaceDef == null)
		{
			throw new IllegalArgumentException(
				"Cannot determine validity with null ScopedNamespaceDefinition");
		}
		if (semanticsVisitor == null)
		{
			semanticsVisitor =
					new SemanticsVisitor(this, legalScope, namespaceDef);
		}
		FormulaSemantics semantics =
				FormulaSemanticsUtilities.getInitializedSemantics();
		semanticsVisitor.visit(root, semantics);
		if (!semantics.getInfo(KeyUtilities.SEM_VALID).isValid())
		{
			return semantics;
		}
		Class<?> nsFormat = namespaceDef.getVariableFormat();
		Class<?> formulaFormat =
				semantics.getInfo(KeyUtilities.SEM_FORMAT).getFormat();
		if (!formulaFormat.equals(nsFormat))
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Invalid Value Format: " + formulaFormat
					+ " found in " + root.getClass().getName()
					+ " found in location requiring a " + nsFormat
					+ " (class cannot be evaluated)");
		}
		return semantics;
	}

	public FormulaManager swapFunctionLibrary(FunctionLibrary ftnLib)
	{
		return new FormulaManager(ftnLib, opLibrary, varLibrary, results);
	}

}

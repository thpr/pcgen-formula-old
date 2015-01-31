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

import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.error.InvalidSemantics;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.variable.ScopeTypeDefinition;
import pcgen.base.formula.variable.VariableLibrary;
import pcgen.base.formula.variable.VariableStore;
import pcgen.base.formula.visitor.ValidVisitor;

/**
 * A FormulaManager exists as compound object to simplify those things that
 * require context to be resolved (legal functions, variables). This provides a
 * convenient, single location for consolidation of these capabilities (and thus
 * keeps the number of parameters that have to be passed around to a reasonable
 * level)
 * 
 * This is also an object used to "cache" the valid visitor (since the visitor
 * needs to know some of the contents in the FormulaManager, it can be lazily
 * instantiated but then effectively cached as long as that FormulaManager is
 * reused - especially valuable for things like the global context which in the
 * future we can create once for the PC and never have to recreate...)
 */
public class FormulaManager
{

	/**
	 * The ValidVisitor for this FormulaManager. Can return the FormulaSemantics
	 * for a parsed tree. Lazily Instantiated.
	 */
	private ValidVisitor validVisitor;

	/**
	 * The FunctionLibrary used to store valid functions in this FormulaManager.
	 */
	private final FunctionLibrary ftnLibrary;

	/**
	 * The OperatorLibrary used to store valid operators in this FormulaManager.
	 */
	private final OperatorLibrary opLibrary;

	/**
	 * The VariableLibrary used to get ScopeTypeDefinitions, VariableScopes, and
	 * VariableIDs.
	 */
	private final VariableLibrary factory;

	/**
	 * The active VariableStore used to cache results of items processed through
	 * this FormulaManager (thus serves as a storage location for variable
	 * values).
	 */
	private final VariableStore resolver;

	/**
	 * Constructs a new FormulaManager from the provided FunctionLibrary,
	 * VariableLibrary, VariableScope, and VariableStore.
	 * 
	 * @param fl
	 *            The FunctionLibrary used to store valid functions in this
	 *            FormulaManager
	 * @param ol
	 *            The OperatorLibrary used to store valid operators in this
	 *            FormulaManager
	 * @param sl
	 *            The VariableLibrary used to get ScopeTypeDefinitions,
	 *            VariableScopes, and VariableIDs
	 * @param vs
	 *            The VariableStore used to hold variables values for items
	 *            processed through this FormulaManager
	 * @throws IllegalArgumentException
	 *             if any parameter is null
	 */
	public FormulaManager(FunctionLibrary fl, OperatorLibrary ol,
		VariableLibrary sl, VariableStore vs)
	{
		if (fl == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null FunctionLibrary");
		}
		if (ol == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null OperatorLibrary");
		}
		if (sl == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null VariableIDFactory");
		}
		if (vs == null)
		{
			throw new IllegalArgumentException(
				"Cannot build FormulaManager with null VariableStore");
		}
		this.ftnLibrary = fl;
		this.opLibrary = ol;
		this.factory = sl;
		this.resolver = vs;
	}

	/**
	 * Returns the VariableLibrary used to get ScopeTypeDefinitions,
	 * VariableScopes, and VariableIDs.
	 * 
	 * @return The VariableLibrary used to get ScopeTypeDefinitions,
	 *         VariableScopes, and VariableIDs
	 */
	public VariableLibrary getFactory()
	{
		return factory;
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
		return resolver;
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
	 * @param def
	 *            The ScopeTypeDefinition used to check for validity of
	 *            variables used within the formula
	 * @return The FormulaSemantics for the formula starting with with the given
	 *         SimpleNode as the root of the parsed tree of the formula
	 * @throws IllegalArgumentException
	 *             if any parameter is null
	 */
	public FormulaSemantics isValid(SimpleNode root, ScopeTypeDefinition<?> def)
	{
		if (root == null)
		{
			throw new IllegalArgumentException(
				"Cannot determine validity with null root");
		}
		if (def == null)
		{
			throw new IllegalArgumentException(
				"Cannot determine validity with null ScopeTypeDefinition");
		}
		if (validVisitor == null)
		{
			validVisitor = new ValidVisitor(this, def);
		}
		FormulaSemantics fs = (FormulaSemantics) validVisitor.visit(root, null);
		if (!fs.isValid())
		{
			return fs;
		}
		if (!fs.getSemanticState().equals(
			def.getVariableTypeDef().getVariableClass()))
		{
			return new InvalidSemantics(root, def.getVariableTypeDef()
				.getVariableClass(), fs.getSemanticState());
		}
		return fs;
	}

}

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
package pcgen.base.formula.library;

import java.util.Arrays;

import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.analysis.FormulaSemanticsUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;

/**
 * GenericFunction can perform a varied calculation based on a pre-defined
 * Formula and given a set of arguments.
 * 
 * This effectively serves the purpose of providing the ability to predefine
 * macros. For example a function called d20Mod could be created, where the
 * Function loaded here is "floor((arg(1)-10)/2)". The value for arg(1) is the
 * first argument to the d20Mod function when it is actually called in data. So
 * it would be called as something like "d20Mod(14)". The resulting effect is a
 * calculation of "floor((14-10)/2)"
 */
public class GenericFunction implements Function
{

	/**
	 * The name for this function (how the user refers to this function).
	 */
	private final String functionName;

	/**
	 * The root node of the tree representing the calculation of this
	 * GenericFunction.
	 * 
	 * Note that while this object is private, it is intended that this object
	 * will escape from the GenericFunction instance (This is because the method
	 * of evaluating or processing a GenericFunction uses a visitor pattern on
	 * the tree of objects). Given that this root object and the resulting tree
	 * is shared, a GenericFunction is not immutable; it is up to the behavior
	 * of the visitor to ensure that it treats the GenericFunction in an
	 * appropriate fashion.
	 */
	private final SimpleNode root;

	/**
	 * Constructs a new GenericFunction with the given Function name and root
	 * node of the tree representing the calculation of this GenericFunction.
	 * 
	 * The Formula defined by the given Root node will be operated upon when
	 * this GenericFunction is called, with any arguments provided to the
	 * GenericFunction loaded into values stored in the arg(n) function
	 * available to the Formula with the root at the given node.
	 * 
	 * @param name
	 *            The Function name for this GenericFunction
	 * @param root
	 *            The root node of the tree representing the calculation of this
	 *            GenericFunction
	 */
	public GenericFunction(String name, SimpleNode root)
	{
		functionName = name;
		this.root = root;
	}

	/**
	 * Returns the function name for this function. This is how it is called by
	 * a user in a formula.
	 * 
	 * @see pcgen.base.formula.base.Function#getFunctionName()
	 */
	@Override
	public String getFunctionName()
	{
		return functionName;
	}

	/**
	 * Checks if the given arguments are valid using the given SemanticsVisitor.
	 * This will validate that the number (and format) of given arguments
	 * matches the number of arguments required by the formula provided at
	 * construction.
	 * 
	 * @see pcgen.base.formula.base.Function#allowArgs(pcgen.base.formula.visitor.SemanticsVisitor,
	 *      pcgen.base.formula.parse.Node[],
	 *      pcgen.base.formula.base.FormulaSemantics)
	 */
	@Override
	public final void allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		FormulaManager withArgs = getManager(args, visitor.getFormulaManager());
		LegalScope legalScope = visitor.getLegalScope();
		SemanticsVisitor subVisitor =
				new SemanticsVisitor(withArgs, legalScope);
		//Need to save original to handle "embedded" GenericFunction objects properly
		@SuppressWarnings("PMD.PrematureDeclaration")
		ArgumentDependencyManager original =
				semantics.removeInfo(FormulaSemanticsUtilities.SEM_ARGS);
		subVisitor.visit(root, semantics);
		ArgumentDependencyManager myArgs =
				semantics.getInfo(FormulaSemanticsUtilities.SEM_ARGS);
		if (myArgs == null)
		{
			if (args.length != 0)
			{
				FormulaSemanticsUtilities.setInvalid(semantics, "Function "
					+ getFunctionName()
					+ " received incorrect # of arguments, expected: 0 got "
					+ args.length + " " + Arrays.asList(args));
			}
			return;
		}
		int maxArg = myArgs.getMaximumArgument() + 1;
		if (maxArg != args.length)
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Function " + getFunctionName() + " required: " + maxArg
					+ " arguments, but was provided " + args.length + " "
					+ Arrays.asList(args));
			return;
		}
		//Need "reset" in case of embedded GenericFunction objects
		semantics.setInfo(FormulaSemanticsUtilities.SEM_ARGS, original);
	}

	/**
	 * Evaluates the given arguments using the given EvaluateVisitor.
	 * 
	 * This method assumes the arguments are valid values. See evaluate on the
	 * Function interface for important assumptions made when this method is
	 * called.
	 * 
	 * @see pcgen.base.formula.base.Function#evaluate(pcgen.base.formula.visitor.EvaluateVisitor,
	 *      pcgen.base.formula.parse.Node[])
	 */
	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args)
	{
		FormulaManager withArgs = getManager(args, visitor.getFormulaManager());
		ScopeInstance scopeInstance = visitor.getScopeInstance();
		EvaluateVisitor ev = new EvaluateVisitor(withArgs, scopeInstance);
		return ev.visit(root, null);
	}

	/**
	 * Checks if the given arguments are static using the given StaticVisitor.
	 * 
	 * This method assumes the arguments are valid values in a formula. See
	 * isStatic on the Function interface for important assumptions made when
	 * this method is called.
	 * 
	 * @see pcgen.base.formula.base.Function#isStatic(pcgen.base.formula.visitor.StaticVisitor,
	 *      pcgen.base.formula.parse.Node[])
	 */
	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		FunctionLibrary argLibrary =
				new ArgWrappingLibrary(visitor.getLibrary(), args);
		StaticVisitor subVisitor = new StaticVisitor(argLibrary);
		return (Boolean) subVisitor.visit(root, null);
	}

	/**
	 * Captures dependencies of the IF function. This includes Variables (in the
	 * form of VariableIDs), but is not limited to those as the only possible
	 * dependency.
	 * 
	 * Consistent with the contract of the Function interface, this list
	 * recursively includes all of the contents of items within this function
	 * (if this function calls another function, etc. all variables in the tree
	 * below this function are included)
	 * 
	 * This method assumes the arguments are valid values in a formula. See
	 * getVariables on the Function interface for important assumptions made
	 * when this method is called.
	 * 
	 * @see pcgen.base.formula.base.Function#getDependencies(pcgen.base.formula.visitor.DependencyVisitor,
	 *      pcgen.base.formula.base.DependencyManager,
	 *      pcgen.base.formula.parse.Node[])
	 */
	@Override
	public void getDependencies(DependencyVisitor visitor,
		DependencyManager fdm, Node[] args)
	{
		FormulaManager withArgs = getManager(args, visitor.getFormulaManager());
		ScopeInstance scopeInstance = visitor.getScopeInstance();
		DependencyVisitor dcv = new DependencyVisitor(withArgs, scopeInstance);
		dcv.visit(root, fdm);
	}

	private FormulaManager getManager(Node[] args, FormulaManager formulaManager)
	{
		FunctionLibrary argLibrary =
				new ArgWrappingLibrary(formulaManager.getLibrary(), args);
		return formulaManager.swapFunctionLibrary(argLibrary);
	}
}

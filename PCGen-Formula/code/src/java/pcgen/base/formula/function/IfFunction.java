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
package pcgen.base.formula.function;

import pcgen.base.formula.base.FormulaDependencyManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.error.InvalidIncorrectArgumentCount;
import pcgen.base.formula.error.InvalidSemantics;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyCaptureVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.formula.visitor.ValidVisitor;

/**
 * IfFunction returns different values based on a given calculation. It follows
 * the common form for an if function: if (conditional, return_if_true,
 * return_if_false).
 * 
 * In the case of this implementation, conditional is NOT a boolean: it is a
 * numeric value. Any non-zero value will result in the first return value
 * (return_if_true) being returned.
 * 
 */
public class IfFunction implements Function
{

	/**
	 * Returns the function name for this function. This is how it is called by
	 * a user in a formula.
	 * 
	 * @see pcgen.base.formula.function.Function#getFunctionName()
	 */
	@Override
	public String getFunctionName()
	{
		return "IF";
	}

	/**
	 * Checks if the given arguments are valid using the given ValidVisitor.
	 * Three arguments are required, and each must be a valid formula value
	 * (number, variable, another function, etc.)
	 * 
	 * @see pcgen.base.formula.function.Function#allowArgs(pcgen.base.formula.visitor.ValidVisitor, pcgen.base.formula.parse.Node[])
	 */
	@Override
	public FormulaSemantics allowArgs(ValidVisitor visitor, Node[] args)
	{
		int argCount = args.length;
		if (argCount != 3)
		{
			return new InvalidIncorrectArgumentCount(getFunctionName(), 3, args);
		}
		//Boolean conditional node
		Node conditionalNode = args[0];
		FormulaSemantics result =
				(FormulaSemantics) conditionalNode.jjtAccept(visitor, null);
		if (!result.isValid())
		{
			return result;
		}
		if (!result.getSemanticState().equals(Boolean.class))
		{
			return new InvalidSemantics(conditionalNode, Boolean.class,
				result.getSemanticState());
		}

		//If True node
		Node trueNode = args[1];
		FormulaSemantics tResult =
				(FormulaSemantics) trueNode.jjtAccept(visitor, null);
		if (!tResult.isValid())
		{
			return tResult;
		}
		//Semantics are arbitrary - just need True and False to match, see below

		//If False node
		Node falseNode = args[2];
		FormulaSemantics fResult =
				(FormulaSemantics) falseNode.jjtAccept(visitor, null);
		if (!fResult.isValid())
		{
			return fResult;
		}
		//Semantics are arbitrary - just need True and False to match, see below

		//Check for Mismatch in types between True and False results
		if (!tResult.getSemanticState().equals(fResult.getSemanticState()))
		{
			return new InvalidSemantics(conditionalNode,
				tResult.getSemanticState(), fResult.getSemanticState());
		}

		return tResult;
	}

	/**
	 * Evaluates the given arguments using the given EvaluateVisitor.
	 * 
	 * This method assumes there are three arguments, and the arguments are
	 * valid values. See evaluate on the Function interface for important
	 * assumptions made when this method is called.
	 * 
	 * @see pcgen.base.formula.function.Function#evaluate(pcgen.base.formula.visitor.EvaluateVisitor, pcgen.base.formula.parse.Node[])
	 */
	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args)
	{
		Boolean b = (Boolean) args[0].jjtAccept(visitor, null);
		/*
		 * Note no attempt to cast or interpret the return values since we do
		 * not know if they are Boolean or Double (see allowArgs)
		 */
		if (b.booleanValue())
		{
			return args[1].jjtAccept(visitor, null);
		}
		else
		{
			return args[2].jjtAccept(visitor, null);
		}
	}

	/**
	 * Checks if the given arguments are static using the given StaticVisitor.
	 * 
	 * This method assumes the arguments are valid values in a formula. See
	 * isStatic on the Function interface for important assumptions made when
	 * this method is called.
	 * 
	 * @see pcgen.base.formula.function.Function#isStatic(pcgen.base.formula.visitor.StaticVisitor, pcgen.base.formula.parse.Node[])
	 */
	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		/*
		 * Technically this is conservative, since if arg1 is static it could be
		 * evaluated to determine if arg2 or arg3 is the one that will always be
		 * used... but that is not a corner case we will spend time on right
		 * now...
		 */
		for (Node n : args)
		{
			Boolean result = (Boolean) n.jjtAccept(visitor, null);
			if (!result.booleanValue())
			{
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
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
	 * @see pcgen.base.formula.function.Function#getDependencies(pcgen.base.formula.visitor.DependencyCaptureVisitor,
	 *      pcgen.base.formula.base.FormulaDependencyManager,
	 *      pcgen.base.formula.parse.Node[])
	 */
	@Override
	public void getDependencies(DependencyCaptureVisitor visitor,
		FormulaDependencyManager fdm, Node[] args)
	{
		for (Node n : args)
		{
			n.jjtAccept(visitor, fdm);
		}
	}
}

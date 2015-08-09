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
import pcgen.base.formula.base.FormulaSemanticsValid;
import pcgen.base.formula.error.InvalidIncorrectArgumentCount;
import pcgen.base.formula.error.InvalidSemantics;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyCaptureVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.formula.visitor.ValidVisitor;

/**
 * AbstractUnaryFunction centralizes common behaviors for Functions that take a
 * variable number of arguments, with a minimum of two arguments.
 * 
 * It is important to understand that this is designed to work for functions
 * that use two values at a time, and when more than two are present, the
 * function is associative (the order in which the arguments are processed does
 * not matter). This is true of functions like calculating a maximum value
 * (where the order is not significant as long as all values are processed).
 * 
 * This is implemented as a repeated check between two values in order to
 * process the variable number of arguments to the function.
 */
public abstract class AbstractNaryFunction implements Function
{

	/**
	 * Checks if the given arguments are valid using the given ValidVisitor. A
	 * minimum of two arguments are required, and each must be a valid formula
	 * value (number, variable, another function, etc.)
	 * 
	 * @see pcgen.base.formula.function.Function#allowArgs(pcgen.base.formula.visitor.ValidVisitor, pcgen.base.formula.parse.Node[])
	 */
	@Override
	public final FormulaSemantics allowArgs(ValidVisitor visitor, Node[] args)
	{
		int argCount = args.length;
		if (argCount < 2)
		{
			return new InvalidIncorrectArgumentCount(getFunctionName(), 2, args);
		}
		for (Node n : args)
		{
			FormulaSemantics result =
					(FormulaSemantics) n.jjtAccept(visitor, null);
			if (!result.isValid())
			{
				return result;
			}
			if (!result.getSemanticState().equals(Number.class))
			{
				return new InvalidSemantics(n, Number.class,
					result.getSemanticState());
			}
		}
		return new FormulaSemanticsValid(Number.class);
	}

	/**
	 * Evaluates the given arguments using the given EvaluateVisitor. Two or
	 * more arguments are allowed, and each must be a valid numeric value.
	 * 
	 * This method assumes there are at least two arguments, and the arguments
	 * are valid values. See evaluate on the Function interface for important
	 * assumptions made when this method is called.
	 * 
	 * Actual processing is delegated to (potentially repeated calls to)
	 * evaluate(double, double)
	 * 
	 * @see pcgen.base.formula.function.Function#evaluate(pcgen.base.formula.visitor.EvaluateVisitor, pcgen.base.formula.parse.Node[])
	 */
	@Override
	public final Number evaluate(EvaluateVisitor visitor, Node[] args)
	{
		int argCount = args.length;
		Number solution = (Number) args[0].jjtAccept(visitor, null);
		//May be N args, so just loop until done
		for (int i = 1; i < argCount; i++)
		{
			Number next = (Number) args[i].jjtAccept(visitor, null);
			solution = evaluate(solution, next);
		}
		return solution;
	}

	/**
	 * Checks if the given arguments are static using the given StaticVisitor.
	 * 
	 * This method assumes there are at least two arguments, and the arguments
	 * are valid values in a formula. See isStatic on the Function interface for
	 * important assumptions made when this method is called.
	 * 
	 * @see pcgen.base.formula.function.Function#isStatic(pcgen.base.formula.visitor.StaticVisitor, pcgen.base.formula.parse.Node[])
	 */
	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
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
	 * Captures dependencies of this function. This includes Variables (in the
	 * form of VariableIDs), but is not limited to those as the only possible
	 * dependency.
	 * 
	 * Consistent with the contract of the Function interface, this list
	 * recursively includes all of the contents of items within this function
	 * (if this function calls another function, etc. all variables in the tree
	 * below this function are included)
	 * 
	 * This method assumes there are at least two arguments, and the arguments
	 * are valid values in a formula. See getDependencies on the Function
	 * interface for important assumptions made when this method is called.
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

	/**
	 * This method must be implemented by classes that extend
	 * AbstractNaryFunction. It performs the evaluation on the given numeric
	 * values.
	 * 
	 * @param n1
	 *            The first input value for the AbstractNaryFunction
	 * @param n2
	 *            the second input value for the AbstractNaryFunction
	 * @return The value calculated from the input values after applying the
	 *         AbstractNaryFunction
	 */
	protected abstract Number evaluate(Number n1, Number n2);
}

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
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.formula.visitor.ValidVisitor;
import pcgen.base.formula.visitor.DependencyCaptureVisitor;

/**
 * AbstractUnaryFunction centralizes common behaviors for Functions that only
 * take one argument.
 */
public abstract class AbstractUnaryFunction implements Function
{

	/**
	 * Checks if the given arguments are valid using the given ValidVisitor.
	 * Only one argument is allowed, and it must be a valid formula value
	 * (number, variable, another function, etc.)
	 * 
	 * @see pcgen.base.formula.function.Function#allowArgs(pcgen.base.formula.visitor
	 *      .ValidVisitor, pcgen.base.formula.parse.Node[])
	 */
	@Override
	public final FormulaSemantics allowArgs(ValidVisitor visitor, Node[] args)
	{
		if (args.length != 1)
		{
			return new InvalidIncorrectArgumentCount(getFunctionName(), 1, args);
		}
		return (FormulaSemantics) args[0].jjtAccept(visitor, null);
	}

	/**
	 * Evaluates the given arguments using the given EvaluateVisitor. Only one
	 * argument is allowed, and it must be a valid numeric value.
	 * 
	 * This method assumes there is at least one argument, and the argument is a
	 * valid value. See evaluate on the Function interface for important
	 * assumptions made when this method is called.
	 * 
	 * Actual processing is delegated to evaluate(Double)
	 * 
	 * @see pcgen.base.formula.function.Function#evaluate(pcgen.base.formula.visitor
	 *      .EvaluateVisitor, pcgen.base.formula.parse.Node[])
	 */
	@Override
	public final Number evaluate(EvaluateVisitor visitor, Node[] args)
	{
		return evaluate((Number) args[0].jjtAccept(visitor, null));
	}

	/**
	 * Checks if the given arguments are static using the given StaticVisitor.
	 * 
	 * This method assumes there is at least one argument, and the argument is a
	 * valid value in a formula. See isStatic on the Function interface for
	 * important assumptions made when this method is called.
	 * 
	 * @see pcgen.base.formula.function.Function#isStatic(pcgen.base.formula.visitor
	 *      .StaticVisitor, pcgen.base.formula.parse.Node[])
	 */
	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return (Boolean) args[0].jjtAccept(visitor, null);
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
	 * This method assumes there is at least one argument, and the argument is a
	 * valid value in a formula. See getDependencies on the Function interface
	 * for important assumptions made when this method is called.
	 * 
	 * @see pcgen.base.formula.function.Function#getDependencies(pcgen.base.formula.visitor.DependencyCaptureVisitor,
	 *      pcgen.base.formula.base.FormulaDependencyManager,
	 *      pcgen.base.formula.parse.Node[])
	 */
	@Override
	public void getDependencies(DependencyCaptureVisitor visitor,
		FormulaDependencyManager fdm, Node[] args)
	{
		args[0].jjtAccept(visitor, fdm);
	}

	/**
	 * This method must be implemented by classes that extend
	 * AbstractUnaryFunction. It performs the evaluation on the given numeric
	 * value.
	 * 
	 * The contract for the AbstractUnaryFunction interface guarantees that the
	 * provided value will not be null, and the returned value may not be null.
	 * 
	 * @param d
	 *            The input value for the AbstractUnaryFunction
	 * @return The value calculated from the input value after applying the
	 *         AbstractUnaryFunction
	 */
	protected abstract Number evaluate(Number d);

}

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
package pcgen.base.formula.inst;

import pcgen.base.formula.base.FunctionLibrary;
import pcgen.base.formula.base.OperatorLibrary;
import pcgen.base.formula.function.AbsFunction;
import pcgen.base.formula.function.CeilFunction;
import pcgen.base.formula.function.FloorFunction;
import pcgen.base.formula.function.IfFunction;
import pcgen.base.formula.function.MaxFunction;
import pcgen.base.formula.function.MinFunction;
import pcgen.base.formula.function.RoundFunction;
import pcgen.base.formula.operator.bool.BooleanAnd;
import pcgen.base.formula.operator.bool.BooleanEquals;
import pcgen.base.formula.operator.bool.BooleanNotEqual;
import pcgen.base.formula.operator.bool.BooleanOr;
import pcgen.base.formula.operator.number.NumberAdd;
import pcgen.base.formula.operator.number.NumberDivide;
import pcgen.base.formula.operator.number.NumberEquals;
import pcgen.base.formula.operator.number.NumberGreaterThan;
import pcgen.base.formula.operator.number.NumberGreaterThanOrEqualTo;
import pcgen.base.formula.operator.number.NumberLessThan;
import pcgen.base.formula.operator.number.NumberLessThanOrEqualTo;
import pcgen.base.formula.operator.number.NumberMultiply;
import pcgen.base.formula.operator.number.NumberNotEqual;
import pcgen.base.formula.operator.number.NumberRemainder;
import pcgen.base.formula.operator.number.NumberSubtract;

/**
 * FormulaUtilities are a general set of utilities for dealing with Formulas.
 * This generally assists with loading libraries with "built-in" capabilities.
 */
public final class FormulaUtilities
{

	/**
	 * Private Constructor for Utility Class.
	 */
	private FormulaUtilities()
	{
	}

	/**
	 * Load the "built-in" functions into the given FunctionLibrary.
	 * 
	 * @param functionLib
	 *            The FunctionLibrary to which the built in functions should be
	 *            added.
	 */
	public static void loadBuiltInFunctions(FunctionLibrary functionLib)
	{
		functionLib.addFunction(new AbsFunction());
		functionLib.addFunction(new CeilFunction());
		functionLib.addFunction(new FloorFunction());
		functionLib.addFunction(new IfFunction());
		functionLib.addFunction(new MaxFunction());
		functionLib.addFunction(new MinFunction());
		functionLib.addFunction(new RoundFunction());
	}

	/**
	 * Load the "built-in" operators into the given OperatorLibrary.
	 * 
	 * @param opLib
	 *            The OperatorLibrary to which the built in operators should be
	 *            added.
	 */
	public static void loadBuiltInOperators(OperatorLibrary opLib)
	{
		opLib.addAction(new BooleanAnd());
		opLib.addAction(new BooleanEquals());
		opLib.addAction(new BooleanNotEqual());
		opLib.addAction(new BooleanOr());
		opLib.addAction(new NumberAdd());
		opLib.addAction(new NumberDivide());
		opLib.addAction(new NumberEquals());
		opLib.addAction(new NumberGreaterThan());
		opLib.addAction(new NumberGreaterThanOrEqualTo());
		opLib.addAction(new NumberLessThan());
		opLib.addAction(new NumberLessThanOrEqualTo());
		opLib.addAction(new NumberMultiply());
		opLib.addAction(new NumberNotEqual());
		opLib.addAction(new NumberRemainder());
		opLib.addAction(new NumberSubtract());
	}

}

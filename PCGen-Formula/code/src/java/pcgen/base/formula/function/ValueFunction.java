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
package pcgen.base.formula.function;

import java.util.Arrays;

import pcgen.base.formula.dependency.DependencyManager;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.semantics.FormulaFormat;
import pcgen.base.formula.semantics.FormulaSemantics;
import pcgen.base.formula.semantics.FormulaSemanticsUtilities;
import pcgen.base.formula.util.KeyUtilities;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;

/**
 * ValueFunction is a zero-argument function designed to hold a specific value.
 * 
 * Note that this is indirectly used by a Solver in order to allow modifications
 * occurring in different priorities of a Solver to access the value before that
 * Modifier was encountered.
 */
public class ValueFunction implements Function
{
	private static final String FUNCTION_NAME = "VALUE";

	/**
	 * The "previous value" represented by this ValueFunction
	 */
	private final Object input;

	/**
	 * Constructs a new ValueFunction to represent the given value.
	 * 
	 * @param input
	 *            The "previous value" represented by this ValueFunction
	 */
	public ValueFunction(Object input)
	{
		this.input = input;
	}

	/**
	 * The function name "value"
	 * 
	 * @see pcgen.base.formula.function.Function#getFunctionName()
	 */
	@Override
	public String getFunctionName()
	{
		return FUNCTION_NAME;
	}

	/**
	 * @see pcgen.base.formula.function.Function#isStatic(pcgen.base.formula.visitor.StaticVisitor,
	 *      pcgen.base.formula.parse.Node[])
	 */
	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return true;
	}

	/**
	 * Must be zero-argument to be valid.
	 * 
	 * @see pcgen.base.formula.function.Function#allowArgs(pcgen.base.formula.visitor.SemanticsVisitor,
	 *      pcgen.base.formula.parse.Node[],
	 *      pcgen.base.formula.semantics.FormulaSemantics)
	 */
	@Override
	public final void allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		if (args.length == 0)
		{
			semantics.setInfo(KeyUtilities.SEM_FORMAT,
				new FormulaFormat(input.getClass()));
			return;
		}
		FormulaSemanticsUtilities.setInvalid(semantics, "Function "
			+ FUNCTION_NAME
			+ " received incorrect # of arguments, expected: 0 got "
			+ args.length + " " + Arrays.asList(args));
	}

	/**
	 * Always returns the previous value.
	 * 
	 * @see pcgen.base.formula.function.Function#evaluate(pcgen.base.formula.visitor.EvaluateVisitor,
	 *      pcgen.base.formula.parse.Node[])
	 */
	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args)
	{
		return input;
	}

	/**
	 * Never has any dependencies.
	 * 
	 * @see pcgen.base.formula.function.Function#getDependencies(pcgen.base.formula.visitor.DependencyVisitor,
	 *      pcgen.base.formula.dependency.DependencyManager,
	 *      pcgen.base.formula.parse.Node[])
	 */
	@Override
	public void getDependencies(DependencyVisitor visitor,
		DependencyManager fdm, Node[] args)
	{
		//No dependencies
	}
}

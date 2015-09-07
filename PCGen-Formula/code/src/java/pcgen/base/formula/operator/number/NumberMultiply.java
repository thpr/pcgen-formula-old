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
package pcgen.base.formula.operator.number;

import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;

/**
 * NumberMultiply performs multiplication on two Number values.
 */
public class NumberMultiply implements OperatorAction
{

	/**
	 * Cache of the Number class.
	 */
	private static final Class<Number> NUMBER_CLASS = Number.class;

	/**
	 * Indicates that NumberMultiply Performs Multiplication.
	 * 
	 * @see pcgen.base.formula.base.OperatorAction#getOperator()
	 */
	@Override
	public Operator getOperator()
	{
		return Operator.MUL;
	}

	/**
	 * Performs Abstract Evaluation, checking that the two arguments are
	 * Number.class and returns Number.class.
	 * 
	 * @see pcgen.base.formula.base.OperatorAction#abstractEvaluate(java.lang.Class,
	 *      java.lang.Class)
	 */
	@Override
	public Class<?> abstractEvaluate(Class<?> format1, Class<?> format2)
	{
		if (NUMBER_CLASS.isAssignableFrom(format1)
			&& NUMBER_CLASS.isAssignableFrom(format2))
		{
			return NUMBER_CLASS;
		}
		return null;
	}

	/**
	 * Performs multiplication on the given arguments.
	 * 
	 * @see pcgen.base.formula.base.OperatorAction#evaluate(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public Object evaluate(Object l, Object r)
	{
		if (l instanceof Integer && r instanceof Integer)
		{
			return (Integer) l * (Integer) r;
		}

		double lDbl = ((Number) l).doubleValue();
		double rDbl = ((Number) r).doubleValue();
		//Special case
		if ((lDbl == 0.0d) || (rDbl == 0.0d))
		{
			return Integer.valueOf(0);
		}
		return lDbl * rDbl;
	}

}

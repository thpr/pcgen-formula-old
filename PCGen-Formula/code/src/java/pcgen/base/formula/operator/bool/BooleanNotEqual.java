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
package pcgen.base.formula.operator.bool;

import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;

/**
 * BooleanNotEqual performs an inequality comparison on two Boolean values.
 */
public class BooleanNotEqual implements OperatorAction
{

	private static final Class<Boolean> BOOLEAN_CLASS = Boolean.class;

	/**
	 * Indicates that BooleanNotEqual Performs a comparison for logical inequality
	 * 
	 * @see pcgen.base.formula.base.OperatorAction#getOperator()
	 */
	@Override
	public Operator getOperator()
	{
		return Operator.NEQ;
	}

	/**
	 * Performs Abstract Evaluation, checking that the two arguments are
	 * Boolean.class and returns Boolean.class
	 * 
	 * @see pcgen.base.formula.base.OperatorAction#abstractEvaluate(java.lang.Class,
	 *      java.lang.Class)
	 */
	@Override
	public Class<?> abstractEvaluate(Class<?> c1, Class<?> c2)
	{
		if (BOOLEAN_CLASS.isAssignableFrom(c1)
			&& BOOLEAN_CLASS.isAssignableFrom(c2))
		{
			return BOOLEAN_CLASS;
		}
		return null;
	}

	/**
	 * Performs a logical inequality comparison on the given arguments
	 * 
	 * @see pcgen.base.formula.base.OperatorAction#evaluate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object evaluate(Object l, Object r)
	{
		//Force boolean values here to produce problems with nulls
		return ((Boolean) l).booleanValue() != ((Boolean) r).booleanValue();
	}

}

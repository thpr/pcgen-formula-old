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

import java.util.List;

import pcgen.base.formula.base.OperatorAction;
import pcgen.base.formula.parse.Operator;
import pcgen.base.util.HashMapToList;

/**
 * SimpleOperatorLibrary is a simple implementation of the OperatorLibrary
 * interface.
 */
public class SimpleOperatorLibrary implements OperatorLibrary
{

	/**
	 * HashMapToList from the Operators to the available OperatorActions for the
	 * Operator.
	 */
	private final HashMapToList<Operator, OperatorAction> actionMTL =
			new HashMapToList<Operator, OperatorAction>();

	/**
	 * Add a new OperatorAction to this SimpleOperatorLibrary.
	 * 
	 * @see pcgen.base.formula.manager.OperatorLibrary#addAction(pcgen.base.formula.base.OperatorAction)
	 */
	@Override
	public void addAction(OperatorAction oa)
	{
		actionMTL.addToListFor(oa.getOperator(), oa);
	}

	/**
	 * @see pcgen.base.formula.manager.OperatorLibrary#evaluate(pcgen.base.formula.parse.Operator,
	 *      java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object evaluate(Operator op, Object o1, Object o2)
	{
		List<OperatorAction> list = actionMTL.getListFor(op);
		if (list != null)
		{
			for (OperatorAction action : list)
			{
				/*
				 * null indicates the OperatorAction can't evaluate these, but
				 * we should try another in list (don't unconditionally fail
				 * because another OperatorAction might work)
				 */
				if (action.abstractEvaluate(o1.getClass(), o2.getClass()) != null)
				{
					return action.evaluate(o1, o2);
				}
			}
		}
		throw new IllegalStateException("Evaluate called on invalid Operator: "
			+ op.getSymbol() + " cannot process "
			+ o1.getClass().getSimpleName() + " and "
			+ o2.getClass().getSimpleName());
	}

	/**
	 * @see pcgen.base.formula.manager.OperatorLibrary#processAbstract(pcgen.base.formula.parse.Operator,
	 *      java.lang.Class, java.lang.Class)
	 */
	@Override
	public Class<?> processAbstract(Operator op, Class<?> c1, Class<?> c2)
	{
		List<OperatorAction> list = actionMTL.getListFor(op);
		if (list != null)
		{
			for (OperatorAction action : list)
			{
				Class<?> result = action.abstractEvaluate(c1, c2);
				/*
				 * null indicates the OperatorAction can't evaluate these, but
				 * try another (don't unconditionally return result because
				 * another OperatorAction might work)
				 */
				if (result != null)
				{
					return result;
				}
			}
		}
		return null;
	}

}

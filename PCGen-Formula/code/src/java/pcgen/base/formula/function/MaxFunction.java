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

/**
 * MinFunction calculates the minimum value of two or more arguments. The same
 * situations/boundary condition rules apply as those in
 * java.lang.Math.max(double, double)
 */
public class MaxFunction extends AbstractNaryFunction
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
		return "MAX";
	}

	/**
	 * Returns the maximum of the two arguments provided.
	 * 
	 * The same situations/boundary condition rules apply as those in
	 * java.lang.Math.max(double, double)
	 * 
	 * @see pcgen.base.formula.function.AbstractNaryFunction#evaluate(java.lang.Number, java.lang.Number)
	 */
	@Override
	protected Number evaluate(Number n1, Number n2)
	{
		return (n1.doubleValue() > n2.doubleValue()) ? n1 : n2;
	}

}

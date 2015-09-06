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
package pcgen.base.solver.testsupport;

import java.lang.reflect.Array;

import pcgen.base.calculation.Modifier;
import pcgen.base.formula.dependency.DependencyManager;
import pcgen.base.formula.manager.ScopeInformation;
import pcgen.base.lang.NumberUtilities;

public abstract class AbstractModifier<T> implements Modifier<T>
{
	private static final Class<Number> NUMBER_CLASS = Number.class;
	private static final Class<Number[]> NUMBER_ARR_CLASS = (Class<Number[]>) new Number[]{}.getClass();

	private final Class<T> format;
	private final int priority;
	private final int inherent;

	public AbstractModifier(int inherent, Class<T> cl)
	{
		this(inherent, cl, 100);
	}

	public AbstractModifier(int inherent, Class<T> cl, int priority)
	{
		format = cl;
		this.priority = priority;
		this.inherent = inherent;
	}

	@Override
	public void getDependencies(ScopeInformation<T> scopeInfo,
		DependencyManager fdm)
	{
	}

	@Override
	public String getIdentification()
	{
		return "DO";
	}

	@Override
	public Class<T> getVariableFormat()
	{
		return format;
	}

	@Override
	public int getInherentPriority()
	{
		return inherent;
	}

	@Override
	public int getUserPriority()
	{
		return priority;
	}

	@Override
	public String getInstructions()
	{
		return "Ignored";
	}

	public static AbstractModifier<Number[]> addToArray(final int value, int priority)
	{
		return new AbstractModifier<Number[]>(0, NUMBER_ARR_CLASS, priority)
		{
			@Override
			public Number[] process(Number[] input,
				ScopeInformation<Number[]> scopeInfo)
			{
				Number[] newArray = (Number[]) Array.newInstance(NUMBER_CLASS, input.length + 1);
				System.arraycopy(input, 0, newArray, 0, input.length);
				newArray[newArray.length - 1] = value;
				return newArray;
			}
		};
	}

	public static AbstractModifier<Number[]> setEmptyArray(int priority)
	{
		return new AbstractModifier<Number[]>(0, NUMBER_ARR_CLASS, priority)
		{
			@Override
			public Number[] process(Number[] input,
				ScopeInformation<Number[]> scopeInfo)
			{
				return new Number[]{};
			}
		};
	}

	public static AbstractModifier<Number> setNumber(final int value, int priority)
	{
		return new AbstractModifier<Number>(0, NUMBER_CLASS, priority)
		{
			@Override
			public Number process(Number input,
				ScopeInformation<Number> scopeInfo)
			{
				return value;
			}
		};
	}

	public static AbstractModifier<String> setString()
	{
		return new AbstractModifier<String>(0, String.class)
		{
			@Override
			public String process(String input,
				ScopeInformation<String> scopeInfo)
			{
				return "Something";
			}
		};
	}

	public static AbstractModifier<Number> multiply(final int value, int priority)
	{
		return new AbstractModifier<Number>(1, NUMBER_CLASS, priority)
		{
			@Override
			public Number process(Number input,
				ScopeInformation<Number> scopeInfo)
			{
				return NumberUtilities.multiply(input, value);
			}
		};
	}

	public static AbstractModifier<Number> add(final int value, int priority)
	{
		return new AbstractModifier<Number>(2, NUMBER_CLASS, priority)
		{
			@Override
			public Number process(Number input,
				ScopeInformation<Number> scopeInfo)
			{
				return NumberUtilities.add(input, value);
			}
		};
	}

}
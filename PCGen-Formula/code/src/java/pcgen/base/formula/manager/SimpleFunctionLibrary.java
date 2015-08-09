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

import pcgen.base.formula.function.Function;
import pcgen.base.util.CaseInsensitiveMap;

/**
 * SimpleFunctionLibrary is a simple implementation of the FunctionLibrary
 * interface.
 * 
 * This triggers exceptions if a null function or function with a null name is
 * added to the FunctionLibrary.
 * 
 * Note also that SimpleFunctionLibrary treats function names as
 * case-insensitive. Therefore, "Ceil" and "ceil" are identical functions.
 */
public class SimpleFunctionLibrary implements FunctionLibrary
{

	/**
	 * Stores the "paren functions" in this FunctionLibrary.
	 * 
	 * These are () functions for world-wide clarity :D
	 */
	private final CaseInsensitiveMap<Function> parenMap =
			new CaseInsensitiveMap<Function>();

	/**
	 * Stores the "bracket functions" in this FunctionLibrary.
	 * 
	 * These are [] functions for world-wide clarity :D
	 */
	private final CaseInsensitiveMap<Function> bracketMap =
			new CaseInsensitiveMap<Function>();

	/**
	 * Adds a "paren" function to the SimpleFunctionLibrary.
	 * 
	 * A null Function or a function which returns null from getFunctionName()
	 * will both trigger an exception.
	 * 
	 * It is important that this method only be called once per Function name.
	 * If there is an attempt to add a second function with a name already
	 * matching a "paren" Function within the SimpleFunctionLibrary, then an
	 * exception will be thrown.
	 * 
	 * @see pcgen.base.formula.manager.FunctionLibrary#addFunction(pcgen.base.formula.function.Function)
	 */
	@Override
	public void addFunction(Function f)
	{
		if (f == null)
		{
			throw new IllegalArgumentException("Cannot add null Function");
		}
		String fName = f.getFunctionName();
		if (fName == null)
		{
			throw new IllegalArgumentException(
				"Cannot add Function with null name");
		}
		if (parenMap.containsKey(fName))
		{
			throw new IllegalArgumentException(
				"Cannot load two functions of name: " + fName);
		}
		parenMap.put(fName, f);
	}

	/**
	 * Returns the "paren" Function with the given function name (evaluated on a
	 * case-insensitive basis).
	 * 
	 * Per the contractual requirement of FunctionLibrary, will return null if
	 * no "paren" Function with the given function name is in the
	 * SimpleFunctionLibrary.
	 * 
	 * @see pcgen.base.formula.manager.FunctionLibrary#getFunction(java.lang.String)
	 */
	@Override
	public Function getFunction(String fname)
	{
		return parenMap.get(fname);
	}

	/**
	 * Adds a "bracket" function to the SimpleFunctionLibrary.
	 * 
	 * A null Function or a function which returns null from getFunctionName()
	 * will both trigger an exception.
	 * 
	 * It is important that this method only be called once per Function name.
	 * If there is an attempt to add a second function with a name already
	 * matching a "bracket" Function within the SimpleFunctionLibrary, then an
	 * exception will be thrown.
	 * 
	 * @see pcgen.base.formula.manager.FunctionLibrary#addBracketFunction(pcgen.base.formula.function.Function)
	 */
	@Override
	public void addBracketFunction(Function f)
	{
		if (f == null)
		{
			throw new IllegalArgumentException(
				"Cannot add null Bracket Function");
		}
		String fName = f.getFunctionName();
		if (fName == null)
		{
			throw new IllegalArgumentException(
				"Cannot add Bracket Function with null name");
		}
		if (bracketMap.containsKey(fName))
		{
			throw new IllegalArgumentException(
				"Cannot load two bracket functions of name: " + fName);
		}
		bracketMap.put(fName, f);
	}

	/**
	 * Returns the "bracket" Function with the given function name (evaluated on
	 * a case-insensitive basis).
	 * 
	 * Per the contractual requirement of FunctionLibrary, will return null if
	 * no "bracket" Function with the given function name is in the
	 * SimpleFunctionLibrary.
	 * 
	 * @see pcgen.base.formula.manager.FunctionLibrary#getBracketFunction(java.lang.String)
	 */
	@Override
	public Function getBracketFunction(String fname)
	{
		return bracketMap.get(fname);
	}

}

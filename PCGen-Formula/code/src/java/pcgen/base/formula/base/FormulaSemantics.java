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
package pcgen.base.formula.base;

/**
 * FormulaSemantics represents a report on whether a formula is valid, and if
 * valid the semantics of the Formula (what type it will return).
 * 
 * If a formula is valid, then the isValid() method will return true. In such a
 * case, there is only a limited guarantee what the return value of getReport()
 * must be (the only guarantee is that it will not be null). Including any
 * message or details of success are optional. Also, if a formula is valid,
 * getSemanticState() will return a non-null value.
 * 
 * If a formula is not valid, then a FormulaSemantics must contain a non-empty
 * value when getReport() is called. This value should indicate with some
 * precision the issue with the Formula. Note that if there is more than one
 * issue, only one issue needs to be returned (fast fail is acceptable). In
 * addition, if a formula is not valid, then the results of calling the
 * getSemanticState() method are not defined.
 */
public interface FormulaSemantics
{

	/**
	 * Returns true if the Formula evaluated was valid. If this method returns
	 * false, then a FormulaSemantics must contain a non-empty value when
	 * getReport() is called.
	 * 
	 * @return true if the formula evaluated was valid; false otherwise
	 */
	boolean isValid();

	/**
	 * Returns a report describing the validity of a formula. Note that if there
	 * is more than one issue, only one issue needs to be returned (fast fail is
	 * acceptable).
	 * 
	 * This method is only guaranteed to have a non-empty value if the isValid()
	 * method returns false.
	 * 
	 * It is a contract of the FormulaSemantics interface that any class
	 * extending FormulaSemantics must not return null from this method, under
	 * any circumstances.
	 * 
	 * @return A non-null String representing a report describing the validity
	 *         of the formula being reported upon.
	 */
	String getReport();

	/**
	 * Returns the Semantic state of the Formula - what it type of object it
	 * will return if evaluated.
	 * 
	 * This method is only guaranteed to function if isValid() returns true. The
	 * results of calling this method are not defined if isValid() returns
	 * false, and an implementation may throw an Exception. If isValid() returns
	 * true, then this method must return a non-null value.
	 * 
	 * @return The Class that would be returned if the formula is evaluated.
	 */
	Class<?> getSemanticState();

}

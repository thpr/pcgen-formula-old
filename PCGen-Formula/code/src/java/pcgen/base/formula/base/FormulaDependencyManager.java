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

import java.util.List;

import pcgen.base.formula.variable.VariableID;

/**
 * A FormulaDependencyManager is a class to capture Formula dependencies.
 * 
 * At a minimum, this includes dependencies on variables, which are captured by
 * their VariableID.
 * 
 * Since this is an interface, the implementing class can define other forms of
 * dependencies, and domain-specific functions can then set those dependencies
 * when the FormulaDependencyManager is passed into the getDependencies method
 * on the Formula.
 */
public interface FormulaDependencyManager
{
	/**
	 * Adds a Variable (identified by the VariableID) to the list of
	 * dependencies for a Formula.
	 * 
	 * The results of calling this function are not defined (exceptions may be
	 * thrown) if the VariableID provided is null.
	 * 
	 * @param vid
	 *            The VariableID to be added as a dependency of the Formula this
	 *            FormulaDependencyManager represents.
	 */
	void addVariable(VariableID<?> vid);

	/**
	 * Returns a non-null list of VariableID objects that identify the list of
	 * dependencies of the Formula this FormulaDependencyManager represents.
	 * 
	 * The contract of the FormulaDependencyManager interface also requires that
	 * ownership of the returned List is transferred to the calling Object. This
	 * does not imply a guarantee that the List can be modified, only that the
	 * contents of the List will not be modified as a result of the
	 * FormulaDependencyManager maintaining or otherwise transferring a
	 * reference to the List to another object.
	 * 
	 * @return A non-null list of VariableID objects that identify the list of
	 *         dependencies of the Formula this FormulaDependencyManager
	 *         represents
	 */
	List<VariableID<?>> getVariables();

	/**
	 * Returns true if this FormulaDependencyManager possesses no dependencies.
	 * 
	 * Note that this is NOT limited to ensuring getVariables returns an empty
	 * list. Any domain-specific dependencies uniquely implemented by the class
	 * implementing FormulaDependencyManager must also be evaluated.
	 * 
	 * @return true if this FormulaDependencyManager possesses no dependencies;
	 *         false otherwise
	 */
	boolean isEmpty();

}

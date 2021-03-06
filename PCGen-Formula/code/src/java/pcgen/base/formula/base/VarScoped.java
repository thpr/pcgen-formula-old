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
package pcgen.base.formula.base;

/**
 * A VarScoped object is an object that is designed to be an "owning object" in
 * a variable processing hierarchy (supporting local variables).
 */
public interface VarScoped
{

	/**
	 * Returns the name of this VarScoped object. Intended mainly for user
	 * interpretation (uniqueness is valuable).
	 * 
	 * @return The name of this VarScoped object
	 */
	public String getName();

	/**
	 * Returns the Local Scope name for this VarScoped object.
	 * 
	 * If this object does not possess a local processing scope, the returned
	 * value may be null. Note that "possess a local processing scope" simply
	 * means a scope unique to that object, it may still have a parent object
	 * (See getVariableParent()) that has a local variable scope.
	 * 
	 * @return The Local Scope name for this VarScoped object
	 */
	public String getLocalScopeName();

	/**
	 * Returns the object that is the parent of this VarScoped object.
	 * 
	 * May return null if the parent of this object is the "global" scope.
	 * 
	 * @return The object that is the parent of this VarScoped object
	 */
	public VarScoped getVariableParent();

}

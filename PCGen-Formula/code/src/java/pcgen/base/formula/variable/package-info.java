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
/**
 * pcgen.base.formula.variable is a package that represents items related to
 * variables within a formula. Variables are text strings in the formula that
 * can take on various values (and are not functions). For example, in the
 * formula "1+T", "T" is a variable.
 * 
 * The classes here are designed to assist through the variable life-cycle.
 * Specifically, they manage the definition of a variable (with that definition
 * possessing a specific scope). There are also classes that can cache values of
 * variables, and a class that can define the combination of a variable name
 * (e.g. "T") and scope (e.g. "Global")
 */
package pcgen.base.formula.variable;

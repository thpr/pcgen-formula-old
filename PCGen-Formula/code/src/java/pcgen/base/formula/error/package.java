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
package pcgen.base.formula.error;

/**
 * pcgen.base.formula.error is a package that contains the possible problems
 * with a formula.
 * 
 * In general, these are issues that will not be caught by the parser (so this
 * will not include things like unbalanced parenthesis). Rather errors that are
 * contained here are things that would be driven by user error above and beyond
 * what a parser can detect. This includes things like user-defined functions
 * having an incorrect number of arguments, or including a variable that is not
 * found within the context provided to resolve a formula.
 * 
 * In this case, errors are not represented by Exceptions, but rather are
 * instances of FormulaValidity. This allows a "gentle" method of returning
 * messages to the user about formula validity, without using a framework like
 * exceptions that may imply a more severe issue than an error of this form
 * represents.
 * 
 * Items in this package are permitted and encouraged to "fail fast" in the case
 * of critical construction errors. If parameters passed to the constructor are
 * null or non-sensical values, the class is encouraged to throw an
 * IllegalArgumentException. Given the low-level nature of this item in the
 * stack, we would rather encounter a stack trace where a problem enters the
 * system rather than at a future point when the object is evaluated.
 */

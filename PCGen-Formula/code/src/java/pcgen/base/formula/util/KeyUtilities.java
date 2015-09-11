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
package pcgen.base.formula.util;

import pcgen.base.formula.dependency.ArgumentDependencyManager;
import pcgen.base.formula.dependency.DependencyKey;
import pcgen.base.formula.dependency.VariableDependencyManager;
import pcgen.base.formula.semantics.FormulaFormat;
import pcgen.base.formula.semantics.FormulaInvalidReport;
import pcgen.base.formula.semantics.FormulaValidity;
import pcgen.base.formula.semantics.SemanticsKey;

/**
 * KeyUtilities is a storage location for keys within the PCGen-Formula
 * infrastructure.
 * 
 * External storage helps avoid dependency loops.
 */
@SuppressWarnings("checkstyle:JavadocVariableCheck")
public final class KeyUtilities
{

	/**
	 * Private Constructor for Utility Class.
	 */
	private KeyUtilities()
	{
	}

	public static final DependencyKey<ArgumentDependencyManager> DEP_ARGUMENT =
			new DependencyKey<>();
	public static final DependencyKey<VariableDependencyManager> DEP_VARIABLE =
			new DependencyKey<>();

	public static final SemanticsKey<FormulaValidity> SEM_VALID =
			new SemanticsKey<>();
	public static final SemanticsKey<FormulaInvalidReport> SEM_REPORT =
			new SemanticsKey<>();
	public static final SemanticsKey<FormulaFormat> SEM_FORMAT =
			new SemanticsKey<>();
	public static final SemanticsKey<ArgumentDependencyManager> SEM_ARGS =
			new SemanticsKey<>();

}

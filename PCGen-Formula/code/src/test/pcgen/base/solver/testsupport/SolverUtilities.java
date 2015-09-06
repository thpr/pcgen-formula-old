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

import pcgen.base.formula.manager.FormulaManager;
import pcgen.base.formula.manager.FunctionLibrary;
import pcgen.base.formula.manager.LegalScopeLibrary;
import pcgen.base.formula.manager.OperatorLibrary;
import pcgen.base.formula.manager.SimpleFunctionLibrary;
import pcgen.base.formula.manager.SimpleOperatorLibrary;
import pcgen.base.formula.manager.VariableLibrary;
import pcgen.base.formula.variable.SimpleVariableStore;
import pcgen.base.formula.variable.VariableStore;

public class SolverUtilities
{

	public static FormulaManager getEmptyFormulaManager()
	{
		FunctionLibrary fl = new SimpleFunctionLibrary();
		OperatorLibrary ol = new SimpleOperatorLibrary();
		LegalScopeLibrary vsLib = new LegalScopeLibrary();
		VariableLibrary sl = new VariableLibrary(vsLib);
		VariableStore vs = new SimpleVariableStore();
		return new FormulaManager(fl, ol, sl, vs);
	}
}

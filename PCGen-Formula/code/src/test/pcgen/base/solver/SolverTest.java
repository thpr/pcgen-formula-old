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
package pcgen.base.solver;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import pcgen.base.calculation.Modifier;
import pcgen.base.format.FormatManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.OrderedPairManager;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.dependency.DependencyManager;
import pcgen.base.formula.manager.FormulaManager;
import pcgen.base.formula.manager.FunctionLibrary;
import pcgen.base.formula.manager.LegalScopeLibrary;
import pcgen.base.formula.manager.OperatorLibrary;
import pcgen.base.formula.manager.ScopeInformation;
import pcgen.base.formula.manager.SimpleFunctionLibrary;
import pcgen.base.formula.manager.SimpleOperatorLibrary;
import pcgen.base.formula.manager.VariableLibrary;
import pcgen.base.formula.variable.NamespaceDefinition;
import pcgen.base.formula.variable.SimpleLegalScope;
import pcgen.base.formula.variable.SimpleScopeInstance;
import pcgen.base.formula.variable.SimpleVariableStore;
import pcgen.base.formula.variable.VariableStore;
import pcgen.base.lang.NumberUtilities;
import pcgen.base.math.OrderedPair;

public class SolverTest extends TestCase
{
	private static final Class<Number> NUMBER_CLASS = Number.class;
	private final FunctionLibrary fl = new SimpleFunctionLibrary();
	private final OperatorLibrary ol = new SimpleOperatorLibrary();
	private LegalScopeLibrary vsLib = new LegalScopeLibrary();
	private final VariableLibrary sl = new VariableLibrary(vsLib);
	private final VariableStore vs = new SimpleVariableStore();
	private NamespaceDefinition<Number> vtd;
	private LegalScope globalScope = new SimpleLegalScope(null, "Global");
	private FormulaManager fm;
	private ScopeInformation<Number> si;
	private FormatManager<Number> numberManager = new NumberManager();
	private FormatManager<OrderedPair> opManager = new OrderedPairManager();

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		vtd = new NamespaceDefinition<Number>(numberManager, "VAR");
		fm = new FormulaManager(fl, ol, sl, vs);
		SimpleScopeInstance scopeInst =
				new SimpleScopeInstance(null, globalScope);
		si = new ScopeInformation<>(fm, scopeInst, vtd);
	}

	@Test
	public void testIllegalConstruction()
	{
		Modifier<Number> mod = add(1, 100);
		try
		{
			new Solver<Number>(mod, si);
			fail("Default Modifier was not static");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		mod = setNumber(6, 0);
		try
		{
			new Solver<Number>(mod, null);
			fail("null Scope Info must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			new Solver<Number>(null, si);
			fail("null default must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}

	}

	@Test
	public void testIllegalAdd()
	{
		Modifier<Number> mod = setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		mod = add(1, 100);
		try
		{
			solver.addModifier(null, new Object());
			fail("Null modifier must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			solver.addModifier(mod, null);
			fail("Null source must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		Modifier<String> badm = setString();
		try
		{
			//have to be bad about generics to even get this to be set up to fail
			Modifier m = badm;
			solver.addModifier(m, new Object());
			fail("wrong type must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}

	}

	@Test
	public void testIllegalRemove()
	{
		Modifier<Number> mod = setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		mod = add(1, 100);
		try
		{
			solver.removeModifier(null, new Object());
			fail("Null modifier must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		try
		{
			solver.removeModifier(mod, null);
			fail("Null source must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
		//CONSIDER is this worth enforcing?
		//		plugin.modifier.gridpoint.SetModifier bad =
		//				new plugin.modifier.gridpoint.SetModifier();
		//		NamespaceDefinition<GridPoint> vtd =
		//				new NamespaceDefinition<GridPoint>(GridPoint.class, "AREA");
		//		ScopedNamespaceDefinition<GridPoint> stDef =
		//				sl.defineGlobalScopeDefinition(vtd);
		//		bad.getModifier(0, "6,6", null, stDef);
		//		try
		//		{
		//			//have to be bad about generics to even get this to be set up to fail
		//			Modifier m = bad;
		//			solver.removeModifier(m, new Object());
		//			fail("wrong type must be rejected");
		//		}
		//		catch (IllegalArgumentException e)
		//		{
		//			//ok
		//		}
	}

	@Test
	public void testIllegalRemoveFromSource()
	{
		Modifier<Number> mod = setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		try
		{
			solver.removeFromSource(null);
			fail("Null source must be rejected");
		}
		catch (IllegalArgumentException e)
		{
			//ok
		}
	}

	@Test
	public void testHarmless()
	{
		Modifier<Number> addm = add(1, 100);
		Modifier<Number> mod = setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		solver.removeModifier(addm, this);
		assertEquals(Integer.valueOf(6), solver.process());
	}

	@Test
	public void testRemoveFromSource()
	{
		Modifier<Number> addm = add(1, 100);
		Modifier<Number> multm = multiply(2, 100);
		Modifier<Number> setm = setNumber(4, 100);
		Modifier<Number> mod = setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		//harmless
		solver.removeFromSource(this);
		assertEquals(Integer.valueOf(6), solver.process());
		//now do real stuff
		solver.addModifier(addm, this);
		solver.addModifier(multm, new Object());
		solver.addModifier(setm, this);
		assertEquals(Integer.valueOf(9), solver.process());
		solver.removeFromSource(this);
		assertEquals(Integer.valueOf(12), solver.process());
		//Harmless
		solver.removeFromSource(new Object());
		assertEquals(Integer.valueOf(12), solver.process());
	}

	@Test
	public void testProcessSamePriority()
	{
		Modifier<Number> addm = add(1, 100);
		Modifier<Number> multm = multiply(2, 100);
		Modifier<Number> setm = setNumber(4, 100);
		Modifier<Number> mod = setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		solver.addModifier(addm, this);
		solver.addModifier(multm, this);
		solver.addModifier(setm, this);
		assertEquals(Integer.valueOf(9), solver.process());
		solver.removeModifier(addm, this);
		assertEquals(Integer.valueOf(8), solver.process());
	}

	@Test
	public void testProcessUserPriority1()
	{
		//Will be ignored due to later set
		Modifier<Number> addm = add(1, 100);
		Modifier<Number> setm = setNumber(4, 200);
		Modifier<Number> multm = multiply(2, 300);
		Modifier<Number> mod = setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		solver.addModifier(addm, this);
		solver.addModifier(multm, this);
		solver.addModifier(setm, this);
		assertEquals(Integer.valueOf(8), solver.process());
	}

	@Test
	public void testProcessUserPriority2()
	{
		Modifier<Number> addm = add(1, 100);
		Modifier<Number> multm = multiply(2, 300);
		Modifier<Number> mod = setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		solver.addModifier(addm, this);
		solver.addModifier(multm, this);
		assertEquals(Integer.valueOf(14), solver.process());
	}

	@Test
	public void testDiagnose()
	{
		Modifier<Number> addm = add(1, 100);
		Modifier<Number> setm = setNumber(4, 100);
		Modifier<Number> multm = multiply(2, 100);
		Modifier<Number> mod = setNumber(6, 0);
		Solver<Number> solver = new Solver<Number>(mod, si);
		List<ProcessStep<Number>> list = solver.diagnose();
		assertNotNull(list);
		assertEquals(1, list.size());
		ProcessStep<Number> step = list.get(0);
		assertEquals(solver, step.getSource());
		assertEquals(6, step.getResult());
		assertEquals(mod, step.getModifier());
		solver.addModifier(addm, this);
		//Shouldn't be related (verify list is not reference semantic)
		assertEquals(1, list.size());
		Object multSrc = new Object();
		solver.addModifier(multm, multSrc);
		solver.addModifier(setm, this);
		list = solver.diagnose();
		assertEquals(4, list.size());
		step = list.get(0);
		assertEquals(solver, step.getSource());
		assertEquals(6, step.getResult());
		assertEquals(mod, step.getModifier());
		step = list.get(1);
		assertEquals(this, step.getSource());
		assertEquals(4, step.getResult());
		assertEquals(setm, step.getModifier());
		step = list.get(2);
		assertEquals(multSrc, step.getSource());
		assertEquals(8, step.getResult());
		assertEquals(multm, step.getModifier());
		step = list.get(3);
		assertEquals(this, step.getSource());
		assertEquals(9, step.getResult());
		assertEquals(addm, step.getModifier());

	}

	private AbstractModifier<String> setString()
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

	private AbstractModifier<Number> add(final int value, int priority)
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

	private AbstractModifier<Number> multiply(final int value, int priority)
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

	private AbstractModifier<Number> setNumber(final int value, int priority)
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

	public static abstract class AbstractModifier<T> implements Modifier<T>
	{

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

	}
}

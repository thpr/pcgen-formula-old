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
package pcgen.base.formula.library;

import java.util.Arrays;

import pcgen.base.formula.analysis.ArgumentDependencyManager;
import pcgen.base.formula.analysis.DependencyKeyUtilities;
import pcgen.base.formula.analysis.FormulaSemanticsUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.parse.ASTNum;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;

/**
 * ArgFunction is a one-argument function designed to delegate to a value
 * provided as an argument to a GenericFunction.
 * 
 * This is indirectly used within a GenericFunction in order to reference values
 * passed into that function.
 */
public class ArgFunction implements Function
{
	/**
	 * The function name for this Function.
	 */
	private static final String FUNCTION_NAME = "ARG";

	/**
	 * The "arguments" provided to the GenericFunction.
	 */
	private final Node[] masterArgs;

	/**
	 * Constructs a new ArgFunction to delegate to the appropriate values.
	 * 
	 * @param masterArgs
	 *            The "arguments" provided to the GenericFunction that is being
	 *            processed
	 */
	public ArgFunction(Node[] masterArgs)
	{
		this.masterArgs = masterArgs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFunctionName()
	{
		return FUNCTION_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		ASTNum node = (ASTNum) args[0];
		int argNum = Integer.parseInt(node.getText());
		return (Boolean) visitor.visit((SimpleNode) masterArgs[argNum], null);
	}

	/**
	 * Must be a single, numeric, integer argument (and within the range of
	 * legal items in the master arguments provided when ArgFunction was
	 * constructed) to be valid.
	 * 
	 * @see pcgen.base.formula.base.Function#allowArgs(pcgen.base.formula.visitor.SemanticsVisitor,
	 *      pcgen.base.formula.parse.Node[],
	 *      pcgen.base.formula.base.FormulaSemantics)
	 */
	@Override
	public final void allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		if (args.length != 1)
		{
			FormulaSemanticsUtilities.setInvalid(semantics, "Function "
				+ FUNCTION_NAME
				+ " received incorrect # of arguments, expected: 0 got "
				+ args.length + " " + Arrays.asList(args));
			return;
		}
		Node node = args[0];
		if (!(node instanceof ASTNum))
		{
			FormulaSemanticsUtilities.setInvalid(semantics,
				"Parse Error: Function " + FUNCTION_NAME
					+ " received invalid argument format,"
					+ " expected: ASTNum got " + node.getClass().getName()
					+ ": " + node);
			return;
		}
		String nodeText = ((ASTNum) node).getText();
		try
		{
			int argNum = Integer.parseInt(nodeText);
			if ((argNum < 0) || (argNum >= masterArgs.length))
			{
				FormulaSemanticsUtilities.setInvalid(semantics,
					"Function " + FUNCTION_NAME
						+ " received incorrect # of arguments, expected: "
						+ (argNum + 1) + " got " + masterArgs.length + " "
						+ Arrays.asList(masterArgs));
				return;
			}
			assertArgs(semantics, argNum);
			Node n = masterArgs[argNum];
			n.jjtAccept(visitor, semantics);
		}
		catch (NumberFormatException e)
		{
			FormulaSemanticsUtilities
				.setInvalid(semantics, "Parse Error: Invalid Class: "
					+ node.getClass().getName()
					+ " found in operable location (class cannot be evaluated)");
		}
	}

	private void assertArgs(FormulaSemantics semantics, int argNum)
	{
		ArgumentDependencyManager argManager =
				semantics.getInfo(FormulaSemanticsUtilities.SEM_ARGS);
		if (argManager == null)
		{
			argManager = new ArgumentDependencyManager();
			semantics.setInfo(FormulaSemanticsUtilities.SEM_ARGS, argManager);
		}
		argManager.addArgument(argNum);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args)
	{
		ASTNum node = (ASTNum) args[0];
		int argNum = Integer.parseInt(node.getText());
		return visitor.visit((SimpleNode) masterArgs[argNum], null);
	}

	/**
	 * Captures dependencies for this ArgFunction. This will load an
	 * ArgumentDependencyManager if and only if one is present within the
	 * DependencyManager.
	 * 
	 * @see pcgen.base.formula.base.Function#getDependencies(pcgen.base.formula.visitor.DependencyVisitor,
	 *      pcgen.base.formula.base.DependencyManager,
	 *      pcgen.base.formula.parse.Node[])
	 */
	@Override
	public void getDependencies(DependencyVisitor visitor,
		DependencyManager fdm, Node[] args)
	{
		ASTNum node = (ASTNum) args[0];
		int argNum = Integer.parseInt(node.getText());
		ArgumentDependencyManager argManager =
				fdm.getDependency(DependencyKeyUtilities.DEP_ARGUMENT);
		if (argManager != null)
		{
			argManager.addArgument(argNum);
		}
		visitor.visit((SimpleNode) masterArgs[argNum], fdm);
	}
}

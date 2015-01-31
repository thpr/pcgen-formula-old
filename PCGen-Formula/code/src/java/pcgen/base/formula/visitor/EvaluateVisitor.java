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
package pcgen.base.formula.visitor;

import pcgen.base.formula.function.Function;
import pcgen.base.formula.manager.FormulaManager;
import pcgen.base.formula.parse.ASTArithmetic;
import pcgen.base.formula.parse.ASTEquality;
import pcgen.base.formula.parse.ASTExpon;
import pcgen.base.formula.parse.ASTFParen;
import pcgen.base.formula.parse.ASTGeometric;
import pcgen.base.formula.parse.ASTLogical;
import pcgen.base.formula.parse.ASTNum;
import pcgen.base.formula.parse.ASTPCGenBracket;
import pcgen.base.formula.parse.ASTPCGenLookup;
import pcgen.base.formula.parse.ASTPCGenSingleWord;
import pcgen.base.formula.parse.ASTParen;
import pcgen.base.formula.parse.ASTQuotString;
import pcgen.base.formula.parse.ASTRelational;
import pcgen.base.formula.parse.ASTRoot;
import pcgen.base.formula.parse.ASTUnary;
import pcgen.base.formula.parse.FormulaParserVisitor;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.parse.Operator;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.variable.VariableID;
import pcgen.base.formula.variable.VariableLibrary;
import pcgen.base.formula.variable.VariableScope;
import pcgen.base.formula.variable.VariableStore;

/**
 * EvaluateVisitor visits a formula in tree form in order to solve the formula -
 * It calculates the numeric value as a result of substituting the variables and
 * evaluating the functions contained in the formula.
 * 
 * EvaluateVisitor returns but does not accumulate results, since it is only
 * processing items that are present in the formula. Therefore, the data
 * parameter to the methods is ignored. The result of the evaluation will be a
 * Double.
 * 
 * EvaluateVisitor enforces no contract that it will validate a formula, but
 * reserves the right to do so. As a result, the behavior of EvaluationVisitor
 * is not defined if ValidVisitor returned a FormulaSemantics that indicated
 * isValid() was false.
 * 
 * Also, a user of EvaluateVisitor should ensure that DependencyCaptureVisitor
 * has been called and successfully processed to ensure that evaluation will run
 * without an Exception.
 */
public class EvaluateVisitor implements FormulaParserVisitor
{

	/**
	 * The scope in which the formula resides, in order to determine the exact
	 * variable (with context) used in the formula.
	 */
	private final VariableScope<?> scope;

	/**
	 * The FormulaManager used to get information about functions and other key
	 * parameters of a Formula.
	 */
	private final FormulaManager fm;

	/**
	 * Constructs a new EvaluateVisitor with the given items used to perform the
	 * evaluation, as necessary.
	 * 
	 * @param fm
	 *            The FormulaManager used to get information about functions and
	 *            other key parameters of a Formula
	 * @param scope
	 *            The scope in which the formula resides, in order to validate
	 *            if variables used in the formula are valid
	 * @throws IllegalArgumentException
	 *             if any of the parameters are null
	 */
	public EvaluateVisitor(FormulaManager fm, VariableScope<?> scope)
	{
		if (fm == null)
		{
			throw new IllegalArgumentException("FormulaManager cannot be null");
		}
		if (scope == null)
		{
			throw new IllegalArgumentException("Function Scope cannot be null");
		}
		this.fm = fm;
		this.scope = scope;
	}

	/**
	 * Visits a SimpleNode. Because this cannot be processed, due to lack of
	 * knowledge as to the exact type of SimpleNode encountered, the node is
	 * visited, which - through double dispatch - will result in another method
	 * on this EvaluateVisitor being called.
	 * 
	 * @see pcgen.base.formula.parse.FormulaParserVisitor#visit(pcgen.base.formula.parse.SimpleNode,
	 *      java.lang.Object)
	 */
	@Override
	public Object visit(SimpleNode node, Object data)
	{
		//Delegate to the appropriate class
		return node.jjtAccept(this, data);
	}

	/**
	 * Processes the (single) child of this node, as a root is simply a
	 * structural placeholder
	 */
	@Override
	public Object visit(ASTRoot node, Object data)
	{
		return evaluateSingleNumericChild(node);
	}

	/**
	 * Evaluates the node, based on the Operator in the node
	 */
	@Override
	public Object visit(ASTLogical node, Object data)
	{
		return evaluateOperatorNode(node);
	}

	/**
	 * Evaluates the node, based on the Operator in the node
	 */
	@Override
	public Object visit(ASTEquality node, Object data)
	{
		return evaluateOperatorNode(node);
	}

	/**
	 * Evaluates the node, based on the Operator in the node
	 */
	@Override
	public Object visit(ASTRelational node, Object data)
	{
		return evaluateOperatorNode(node);
	}

	/**
	 * Evaluates the node, based on the Operator in the node
	 */
	@Override
	public Object visit(ASTArithmetic node, Object data)
	{
		return evaluateOperatorNode(node);
	}

	/**
	 * Evaluates the node, based on the Operator in the node
	 */
	@Override
	public Object visit(ASTGeometric node, Object data)
	{
		return evaluateOperatorNode(node);
	}

	/**
	 * Evaluates the node, which is a unary negation
	 */
	@Override
	public Object visit(ASTUnary node, Object data)
	{
		/*
		 * Note we only support unary minus for Number.class. This was enforced
		 * by ValidVisitor.
		 */
		Number n = (Number) evaluateSingleNumericChild(node);
		if (n instanceof Integer)
		{
			return Integer.valueOf(-((Integer) n).intValue());
		}
		return Double.valueOf(-n.doubleValue());
	}

	/**
	 * Evaluates the exponential node.
	 */
	@Override
	public Object visit(ASTExpon node, Object data)
	{
		/*
		 * Note we only support exponent (^) for Number.class. This was enforced
		 * by ValidVisitor.
		 */
		int ccount = node.jjtGetNumChildren();

		Number n1 = (Number) node.jjtGetChild(0).jjtAccept(this, null);
		Number n2 = (Number) node.jjtGetChild(1).jjtAccept(this, null);
		//"Cheat" to reduce calls to EXP in that X^Y^Z == X^(Y*Z)
		for (int i = 2; i < ccount; i++)
		{
			Number n = (Number) node.jjtGetChild(i).jjtAccept(this, null);
			n2 = Double.valueOf(n2.doubleValue() * n.doubleValue());
		}
		return Math.pow(((Number) n1).doubleValue(),
			((Number) n2).doubleValue());
	}

	/**
	 * Processes the (single) child of this node, as grouping parenthesis are
	 * logically present only to define order of operations (now implicit in the
	 * tree structure)
	 */
	@Override
	public Object visit(ASTParen node, Object data)
	{
		return evaluateSingleNumericChild(node);
	}

	/**
	 * Returns the contents of the node, which is a numeric value.
	 */
	@Override
	public Object visit(ASTNum node, Object data)
	{
		String nodeText = node.getText();
		try
		{
			return Integer.valueOf(nodeText);
		}
		catch (NumberFormatException e)
		{
			return Double.valueOf(nodeText);
		}
	}

	/**
	 * Processes a function encountered in the formula.
	 * 
	 * This will decode what function is being called, using the
	 * FunctionLibrary, and then call evaluate() on the Function, relying on the
	 * behavior of that method (as defined in the contract of the Function
	 * interface) to calculate the return value.
	 */
	@Override
	public Object visit(ASTPCGenLookup node, Object data)
	{
		Function pcgf = VisitorUtilities.getFunction(fm.getLibrary(), node);
		Node[] args = VisitorUtilities.accumulateArguments(node.jjtGetChild(1));
		//evaluate the function
		return pcgf.evaluate(this, args);
	}

	/**
	 * Processes a variable within the formula. This relies on the
	 * VariableIDFactory and the VariableScope to precisely determine the
	 * VariableID and then fetch the value for that VariableID from the
	 * VariableStore (cache).
	 */
	@Override
	public Object visit(ASTPCGenSingleWord node, Object data)
	{
		String varName = node.getText();
		VariableLibrary varLib = fm.getFactory();
		if (varLib.isLegalVariableID(scope.getScopeDefinition(), varName))
		{
			VariableID<?> id = varLib.getVariableID(scope, varName);
			VariableStore resolver = fm.getResolver();
			if (resolver.containsKey(id))
			{
				return resolver.get(id);
			}
		}
		System.out.println("Evaluation called on invalid variable: '" + varName
			+ "', assuming zero");
		return Integer.valueOf(0);
		//		throw new IllegalStateException(
		//			"Evaluation called on invalid Formula (reached invalid non-term: "
		//				+ termName + ")");
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * StaticVisitor, reaching this node in EvaluateVisitor indicates either an
	 * error in the implementation of the formula or a tree structure problem in
	 * the formula.
	 */
	@Override
	public Object visit(ASTPCGenBracket node, Object data)
	{
		//Should be stripped by the function
		throw new IllegalStateException(
			"Evaluation called on invalid Formula (reached Function Brackets)");
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * StaticVisitor, reaching this node in EvaluateVisitor indicates either an
	 * error in the implementation of the formula or a tree structure problem in
	 * the formula.
	 */
	@Override
	public Object visit(ASTFParen node, Object data)
	{
		//Should be stripped by the function
		throw new IllegalStateException(
			"Evaluation called on invalid Formula (reached Function Parenthesis)");
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * StaticVisitor, reaching this node in EvaluateVisitor indicates either an
	 * error in the implementation of the formula or a tree structure problem in
	 * the formula.
	 */
	@Override
	public Object visit(ASTQuotString node, Object data)
	{
		//Should be stripped by the function
		throw new IllegalStateException(
			"Evaluation called on invalid Formula (reached Quoted String)");
	}

	/**
	 * Evaluates an operator node. Must have 2 children and a node that contains
	 * an Operator.
	 * 
	 * @param node
	 *            The node that contains an Operator and has exactly 2 children.
	 * @return The result of the operation acting on the 2 children
	 */
	private Object evaluateOperatorNode(SimpleNode node)
	{
		Operator op = node.getOperator();
		if (op == null)
		{
			throw new IllegalStateException(getClass().getSimpleName()
				+ " must have an operator");
		}
		int ccount = node.jjtGetNumChildren();
		if (ccount != 2)
		{
			throw new IllegalStateException(getClass().getSimpleName()
				+ " must only have 2 children, was: " + ccount);
		}
		Object o1 = node.jjtGetChild(0).jjtAccept(this, null);
		Object o2 = node.jjtGetChild(1).jjtAccept(this, null);
		return fm.getOperatorLibrary().evaluate(op, o1, o2);
	}

	/**
	 * Evaluates a single child node. Effectively extracts the child and then
	 * performs a double-dispatch to get back into one of the methods on this
	 * EvaluateVisitor.
	 * 
	 * @param node
	 *            The node for which the (single) child will be evaluated
	 * @return The result of the evaluation on the child of the given node
	 */
	private Object evaluateSingleNumericChild(Node node)
	{
		int ccount = node.jjtGetNumChildren();
		if (ccount != 1)
		{
			throw new IllegalStateException(getClass().getSimpleName()
				+ " must only have 1 child, was: " + ccount);
		}
		Node child = node.jjtGetChild(0);
		return child.jjtAccept(this, null);
	}
}

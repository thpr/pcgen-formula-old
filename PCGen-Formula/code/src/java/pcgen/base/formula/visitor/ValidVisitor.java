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

import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.FormulaSemanticsValid;
import pcgen.base.formula.error.InvalidBadParseStructure;
import pcgen.base.formula.error.InvalidChildCount;
import pcgen.base.formula.error.InvalidFunctionNotFound;
import pcgen.base.formula.error.InvalidMissingOperator;
import pcgen.base.formula.error.InvalidNotOperable;
import pcgen.base.formula.error.InvalidNumber;
import pcgen.base.formula.error.InvalidSemantics;
import pcgen.base.formula.error.InvalidVariableNotFound;
import pcgen.base.formula.function.Function;
import pcgen.base.formula.manager.FormulaManager;
import pcgen.base.formula.manager.FunctionLibrary;
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
import pcgen.base.formula.variable.ScopedNamespaceDefinition;

/**
 * ValidVisitor visits a formula in tree form to determine if the formula is
 * valid. As a note, this is checking for structural validity.
 * 
 * "Structural Validity" includes checking items such as: (a) Ensure Formula
 * arguments are a legal form (b) Ensure formula arguments are possibly legal
 * values (see below for clarification of "possibly legal") (c) Ensure there are
 * no nodes in the tree that can be reached but are non-sensical when reached
 * (d) Ensure nodes of the tree identified as numerical can actually be parsed
 * into numbers.
 * 
 * Note that ValidVisitor will not produce an error in a situations which is
 * "possibly legal": Specifically, where an String referred to in a formula may
 * or may not be valid at a later time. For details, see the Function interface
 * and examples provided there (and the different requirements on allowArgs() -
 * called by ValidVisitor - and getDependencies() - not called by ValidVisitor)
 * 
 * ValidVisitor does not accumulate results, since it is only detecting a
 * Formula validity. Therefore, the data parameter to the methods is ignored.
 * Rather, a "fast fail" implementation will return a FormulaSemantics with
 * isValid() indicating FALSE as soon as such an invalid situation is detected.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ValidVisitor implements FormulaParserVisitor
{
	/*
	 * Implementation note: As a result of the "fast fail" behavior of returning
	 * an invalid FormulaVaility as soon as it is detected, a method in
	 * ValidVisitor that needs to analyze the children of a node must perform
	 * one of two operations when any node is passed ValidVisitor as the first
	 * argument to .jjtAccept().
	 * 
	 * If only one child node is being passed the ValidVisitor, the method
	 * should directly return the contents of the call to .jjtAccept()
	 * 
	 * If more than one child node is being processed, then the return value of
	 * .jjtAccept() must be checked, and if the FormulaSemantics returns FALSE
	 * from the isValid() method, then the method in ValidVisitor should
	 * immediately return that "invalid" FormulaSemantics.
	 */

	private static final Class<Number> NUMBER_CLASS = Number.class;

	/**
	 * The FormulaManager used to get information about functions and other key
	 * parameters of a Formula.
	 */
	private final FormulaManager fm;

	/**
	 * The scope namespace definition in which the formula resides, in order to
	 * validate if variables used in the formula are legal.
	 */
	private final ScopedNamespaceDefinition<?> snDef;

	/**
	 * Constructs a new ValidVisitor with the given FormulaManager and
	 * ScopedNamespaceDefinition.
	 * 
	 * @param fm
	 *            The FormulaManager used to get information about functions and
	 *            other key parameters of a Formula
	 * @param snDef
	 *            The scope definition in which the formula resides, in order to
	 *            validate if variables used in the formula are valid
	 * @throws IllegalArgumentException
	 *             if any of the parameters are null
	 */
	public ValidVisitor(FormulaManager fm, ScopedNamespaceDefinition<?> snDef)
	{
		if (fm == null)
		{
			throw new IllegalArgumentException("FormulaManager cannot be null");
		}
		if (snDef == null)
		{
			throw new IllegalArgumentException(
				"Scope Definition cannot be null");
		}
		this.fm = fm;
		this.snDef = snDef;
	}

	/**
	 * Visits a SimpleNode. Because this cannot be processed, due to lack of
	 * knowledge as to the exact type of SimpleNode encountered, the node is
	 * visited, which - through double dispatch - will result in another method
	 * on this ValidVisitor being called.
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
	 * Processes the child of this node (this will enforce that the node has
	 * only one child).
	 */
	@Override
	public Object visit(ASTRoot node, Object data)
	{
		return singleChildValid(node);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTLogical node, Object data)
	{
		return visitOperatorNode(node);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTEquality node, Object data)
	{
		return visitOperatorNode(node);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTRelational node, Object data)
	{
		return visitOperatorNode(node);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTArithmetic node, Object data)
	{
		return visitOperatorNode(node);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2
	 * children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTGeometric node, Object data)
	{
		return visitOperatorNode(node);
	}

	/**
	 * Processes the child of this node (this will enforce that the node has
	 * only one child).
	 */
	@Override
	public Object visit(ASTUnary node, Object data)
	{
		FormulaSemantics result = (FormulaSemantics) singleChildValid(node);
		//Consistent with the "fail fast" behavior in the implementation note
		if (!result.isValid())
		{
			return result;
		}
		/*
		 * Note: We only implement unary minus for Number.class today. This is a
		 * "known" limitation, but would be nice to escape. However, this means
		 * we need an entire equivalent to OperatorAction for 1-argument
		 * operations :/
		 */
		if (!result.getSemanticState().equals(NUMBER_CLASS))
		{
			return new InvalidSemantics(node.jjtGetChild(0), NUMBER_CLASS,
				result.getSemanticState());
		}
		return result;
	}

	/**
	 * Processes a variable argument Operator node. Enforces that the node 2 or
	 * more children, and a non-null Operator.
	 */
	@Override
	public Object visit(ASTExpon node, Object data)
	{
		if (node.getOperator() == null)
		{
			return new InvalidMissingOperator(node.getClass());
		}
		for (int i = 0; i < node.jjtGetNumChildren(); i++)
		{
			FormulaSemantics result =
					(FormulaSemantics) node.jjtGetChild(i)
						.jjtAccept(this, null);
			//Consistent with the "fail fast" behavior in the implementation note
			if (!result.isValid())
			{
				return result;
			}
			/*
			 * Note: We only implement ^ for Number.class today. This is a
			 * "known" limitation, but would be nice to escape. However, this
			 * means we can't shortcut the item in evaluate... (see
			 * EvaluationVisitor)
			 */
			if (!result.getSemanticState().equals(NUMBER_CLASS))
			{
				return new InvalidSemantics(node.jjtGetChild(i), NUMBER_CLASS,
					result.getSemanticState());
			}
		}
		return new FormulaSemanticsValid(NUMBER_CLASS);
	}

	/**
	 * Processes the child of this node (this will enforce that the node has
	 * only one child).
	 */
	@Override
	public Object visit(ASTParen node, Object data)
	{
		return singleChildValid(node);
	}

	/**
	 * Processes a numeric node. This ensures that the node has no children and
	 * that it can be parsed as a numeric value.
	 */
	@Override
	public Object visit(ASTNum node, Object data)
	{
		if (node.jjtGetNumChildren() != 0)
		{
			return new InvalidChildCount(node, 0);
		}
		try
		{
			Double.parseDouble(node.getText());
			return new FormulaSemanticsValid(NUMBER_CLASS);
		}
		catch (NumberFormatException e)
		{
			return new InvalidNumber(node.getClass(), node.getText());
		}
	}

	/**
	 * Processes a function encountered in the formula. This will validate the
	 * structure of the nodes making up the Function within the formula, decode
	 * what function is being called, using the FunctionLibrary, and then call
	 * allowArgs() on the Function, relying on the behavior of that method (as
	 * defined in the contract of the Function interface) to determine if the
	 * function represents a valid call to the Function.
	 */
	@Override
	public Object visit(ASTPCGenLookup node, Object data)
	{
		//Two children are function name and the grouping (parens/brackets)
		if (node.jjtGetNumChildren() != 2)
		{
			return new InvalidChildCount(node, 2);
		}
		Node firstChild = node.jjtGetChild(0);

		if (!(firstChild instanceof ASTPCGenSingleWord))
		{
			return new InvalidBadParseStructure("Formula Name",
				ASTPCGenSingleWord.class, firstChild);
		}

		/*
		 * Validate the function contents (remember it can have other complex
		 * structures inside of it)
		 */
		ASTPCGenSingleWord ftnNode = (ASTPCGenSingleWord) firstChild;
		String ftnName = ftnNode.getText();
		Node argNode = node.jjtGetChild(1);
		Function function;
		String functionForm;
		FunctionLibrary library = fm.getLibrary();
		if (argNode instanceof ASTFParen)
		{
			function = library.getFunction(ftnName);
			functionForm = "()";
		}
		else if (argNode instanceof ASTPCGenBracket)
		{
			function = library.getBracketFunction(ftnName);
			functionForm = "[]";
		}
		else
		{
			return new InvalidBadParseStructure("Formula Arguments",
				ASTFParen.class, firstChild);
		}
		if (function == null)
		{
			return new InvalidFunctionNotFound(ftnName, ftnName + functionForm);
		}
		//Extract arguments from the grouping to give them to the function
		int argLength = argNode.jjtGetNumChildren();
		Node[] args = new Node[argLength];
		for (int i = 0; i < argLength; i++)
		{
			args[i] = argNode.jjtGetChild(i);
		}
		return function.allowArgs(this, args);
	}

	/**
	 * Visits a variable name within the formula. This will validate with the
	 * VariableIDFactory that the variable usage is valid within the scope
	 * recognized by this ValidVisitor.
	 */
	@Override
	public Object visit(ASTPCGenSingleWord node, Object data)
	{
		if (node.jjtGetNumChildren() != 0)
		{
			return new InvalidChildCount(node, 0);
		}
		String varName = node.getText();
		if (fm.getFactory().isLegalVariableID(snDef, varName))
		{
			return new FormulaSemanticsValid(NUMBER_CLASS);
		}
		return new InvalidVariableNotFound(varName);
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * ValidVisitor, reaching this node in ValidVisitor indicates either an
	 * error in the implementation of the formula or a tree structure problem in
	 * the formula.
	 */
	@Override
	public Object visit(ASTPCGenBracket node, Object data)
	{
		//Should be stripped by the function
		return new InvalidNotOperable(node);
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * ValidVisitor, reaching this node in ValidVisitor indicates either an
	 * error in the implementation of the formula or a tree structure problem in
	 * the formula.
	 */
	@Override
	public Object visit(ASTFParen node, Object data)
	{
		//Should be stripped by the function
		return new InvalidNotOperable(node);
	}

	/**
	 * This type of node is ONLY encountered as part of a function. Since the
	 * function should have "consumed" these elements and not called back into
	 * ValidVisitor, reaching this node in ValidVisitor indicates either an
	 * error in the implementation of the formula or a tree structure problem in
	 * the formula.
	 */
	@Override
	public Object visit(ASTQuotString node, Object data)
	{
		//Should be stripped by the function
		return new InvalidNotOperable(node);
	}

	/**
	 * Processes an Operator node. Enforces that the node has exactly 2 valid
	 * children, and a non-null Operator.
	 * 
	 * @param node
	 *            The node to be validated to ensure it has valid children and a
	 *            non-null Operator
	 * @return A FormulaSemantics object, which will indicate isValid() true if
	 *         this operator has 2 valid children and a non-null Operator.
	 *         Otherwise, the FormulaSemantics will indicate isValid() false
	 */
	private Object visitOperatorNode(SimpleNode node)
	{
		Operator op = node.getOperator();
		if (op == null)
		{
			return new InvalidMissingOperator(node.getClass());
		}
		if (node.jjtGetNumChildren() != 2)
		{
			return new InvalidChildCount(node, 2);
		}
		Node child1 = node.jjtGetChild(0);
		FormulaSemantics result0 =
				(FormulaSemantics) child1.jjtAccept(this, null);
		//Consistent with the "fail fast" behavior in the implementation note
		if (!result0.isValid())
		{
			return result0;
		}
		Node child2 = node.jjtGetChild(1);
		FormulaSemantics result1 =
				(FormulaSemantics) child2.jjtAccept(this, null);
		//Consistent with the "fail fast" behavior in the implementation note
		if (!result1.isValid())
		{
			return result1;
		}
		Class<?> format1 = result0.getSemanticState();
		Class<?> format2 = result1.getSemanticState();
		Class<?> returnedFormat =
				fm.getOperatorLibrary().processAbstract(op, format1, format2);
		//null response means the library couldn't find an appropriate operator
		if (returnedFormat == null)
		{
			return new InvalidSemantics(node, op, format1, format2);
		}
		return new FormulaSemanticsValid(returnedFormat);
	}

	/**
	 * Processes a node enforcing that the given node has a single child and
	 * enforcing that the child is valid.
	 * 
	 * @param node
	 *            The node to be validated to ensure it has a single, valid
	 *            child.
	 * @return A FormulaSemantics object, which will indicate isValid() true if
	 *         this operator has a single, valid child; Otherwise, the
	 *         FormulaSemantics will indicate isValid() false
	 */
	private Object singleChildValid(SimpleNode node)
	{
		if (node.jjtGetNumChildren() != 1)
		{
			return new InvalidChildCount(node, 1);
		}
		Node child = node.jjtGetChild(0);
		return child.jjtAccept(this, null);
	}
}

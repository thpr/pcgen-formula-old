/* Generated By:JJTree: Do not edit this line. ASTQuotString.java */

package pcgen.base.formula.parse;

public class ASTQuotString extends SimpleNode {
  public ASTQuotString(int id) {
    super(id);
  }

  public ASTQuotString(FormulaParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(FormulaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
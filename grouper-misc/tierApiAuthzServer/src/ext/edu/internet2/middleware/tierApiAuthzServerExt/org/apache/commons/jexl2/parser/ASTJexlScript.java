/* Generated By:JJTree: Do not edit this line. ASTJexlScript.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser;

import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.JexlNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.Parser;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ParserVisitor;

public
class ASTJexlScript extends JexlNode {
  public ASTJexlScript(int id) {
    super(id);
  }

  public ASTJexlScript(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=b62697e705272eef929d6d4d9a6fb6ee (do not edit this line) */

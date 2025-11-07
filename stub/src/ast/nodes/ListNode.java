package ast.nodes;

import java.util.LinkedList;

import ast.EvaluationException;
import ast.typesystem.TypeException;
import ast.typesystem.inferencer.Inferencer;
import ast.typesystem.types.Type;
import environment.Environment;
import environment.TypeEnvironment;

public class ListNode extends SyntaxNode {
    private LinkedList<SyntaxNode> exprs;

    public ListNode(LinkedList<SyntaxNode> exprs, long line) {
        super(line);
        this.exprs = exprs;
    }

    public void displaySubtree(int indentAmt) {
        printIndented("List [", indentAmt);
        for (SyntaxNode node : exprs)
        {
            node.displaySubtree(indentAmt + 2);
        }
        printIndented("]", indentAmt);
    }

    @Override
    public Object evaluate(Environment env) throws EvaluationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
    }

     public Type typeOf(TypeEnvironment tenv, Inferencer inferencer){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
        
     }
}

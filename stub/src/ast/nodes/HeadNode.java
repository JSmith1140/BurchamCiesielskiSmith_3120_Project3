package ast.nodes;

import environment.Environment;
import environment.TypeEnvironment;

import java.util.List;

import ast.EvaluationException;
import ast.typesystem.inferencer.Inferencer;
import ast.typesystem.types.Type;

public class HeadNode extends SyntaxNode {

    private SyntaxNode expr;

    public HeadNode(SyntaxNode expr, long line) {
        super(line);
        this.expr = expr;
    }

    public void displaySubtree(int indentAmt) {
        printIndented("hd(", indentAmt);
        expr.displaySubtree(indentAmt + 2);
        printIndented(")", indentAmt);
    }

    @Override
    public Object evaluate(Environment env) throws EvaluationException {
        Object value = expr.evaluate(env);

        if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            if (list.isEmpty()) {
                throw new EvaluationException();
            }
            return list.get(0);
        }
        throw new EvaluationException();
    }

    @Override
    public Type typeOf(TypeEnvironment tenv, Inferencer inferencer){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
        
    }
    
}

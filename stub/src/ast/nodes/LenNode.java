package ast.nodes;

import environment.Environment;
import environment.TypeEnvironment;

import java.util.List;

import ast.EvaluationException;
import ast.typesystem.inferencer.Inferencer;
import ast.typesystem.types.Type;

public class LenNode extends SyntaxNode {

    private SyntaxNode expr;

    public LenNode(SyntaxNode expr, long line) {
        super(line);
        this.expr = expr;

    }

    public void displaySubtree(int indentAmt) {
        printIndented("len(", indentAmt);
        expr.displaySubtree(indentAmt + 2);
        printIndented(")", indentAmt);
    }

    @Override
    public Object evaluate(Environment env) throws EvaluationException {
        Object value = expr.evaluate(env);

        if (value instanceof List<?>) {
            return ((List<?>) value).size();
        }
        throw new EvaluationException();
    }

    @Override
    public Type typeOf(TypeEnvironment tenv, Inferencer inferencer){
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluate'");
        
    }
    
}

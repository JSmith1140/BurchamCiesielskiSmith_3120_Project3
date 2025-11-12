package ast.nodes;

import environment.Environment;
import environment.TypeEnvironment;

import java.util.ArrayList;
import java.util.List;

import ast.EvaluationException;
import ast.typesystem.TypeException;
import ast.typesystem.inferencer.Inferencer;
import ast.typesystem.types.ListType;
import ast.typesystem.types.Type;
import ast.typesystem.types.VarType;

public class TailNode extends SyntaxNode {

    private SyntaxNode expr;

    public TailNode(SyntaxNode expr, long line) {
        super(line);
        this.expr = expr;
    }

    public void displaySubtree(int indentAmt) {
        printIndented("tl(", indentAmt);
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
            return new ArrayList<>(list.subList(1, list.size()));
        }
        throw new EvaluationException();
    }

    @Override
    public Type typeOf(TypeEnvironment tenv, Inferencer inferencer) throws TypeException {
        // Get the type of the expression (should be a list)
        Type exprType = expr.typeOf(tenv, inferencer);
        
        // Create a fresh type variable for the list element type
        VarType elemVar = tenv.getTypeVariable();
        
        // Create a ListType with the fresh element type variable
        ListType expectedListType = new ListType(elemVar);
        
        // Unify the expression type with our expected list type
        // This ensures the argument is a list and binds elemVar to the actual element type
        inferencer.unify(exprType, expectedListType, 
            "Tail operation requires a list argument");
        
        // Apply substitutions to get the most specific list type
        Type resultType = inferencer.getSubstitutions().apply(exprType);
        
        // Return the list type (tail returns a list of the same type)
        return resultType;
    }
    
}

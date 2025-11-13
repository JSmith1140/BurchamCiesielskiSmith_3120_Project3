package ast.nodes;

import java.util.LinkedList;
import java.util.List;

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
        printIndented("List[", indentAmt);
        for (SyntaxNode node : exprs)
        {
            node.displaySubtree(indentAmt + 2);
        }
        printIndented("]", indentAmt);
    }

    @Override
    public Object evaluate(Environment env) throws EvaluationException {
        List<Object> evaluatedList = new LinkedList<>();
        Class<?> firstType = null;

        for (SyntaxNode node : exprs) {
            Object value = node.evaluate(env);

            if (firstType == null && value != null)
                firstType = value.getClass();

            if (value != null && firstType != null && !firstType.equals(value.getClass())) {
                logError("Type error: all elements in a list must be of the same type.");
                throw new EvaluationException();
            }
            evaluatedList.add(value);
        }
        return evaluatedList;
    }

    @Override
    public Type typeOf(TypeEnvironment tenv, Inferencer inferencer) throws TypeException{
        if (exprs.isEmpty()) {
            Type elemType = tenv.getTypeVariable();
            return new ast.typesystem.types.ListType(elemType);
        }

        SyntaxNode firstNode = exprs.getFirst();
        Type firstType = firstNode.typeOf(tenv, inferencer);

        for (int i = 1; i < exprs.size(); i++) {
            SyntaxNode node = exprs.get(i);
            Type nodeType = node.typeOf(tenv, inferencer);

            inferencer.unify(firstType, nodeType, "All elements in a list must have the same type.");
        }

        Type elementType = inferencer.getSubstitutions().apply(firstType);

        return new ast.typesystem.types.ListType(elementType);
    }
}

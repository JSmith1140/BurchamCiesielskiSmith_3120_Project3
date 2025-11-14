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

    /**
     * Constructor for a ListNode.
     * 
     * @param exprs The list of expressions that form this list node.
     * @param line  The line number in the source code (for error reporting).
     */
    public ListNode(LinkedList<SyntaxNode> exprs, long line) {
        super(line);
        this.exprs = exprs;
    }

    /**
     * Recursively displays this list node and its children, with indentation.
     * 
     * @param indentAmt The number of spaces to indent this node.
     */
    public void displaySubtree(int indentAmt) {
        printIndented("List[", indentAmt);
        for (SyntaxNode node : exprs)
        {
            node.displaySubtree(indentAmt + 2);
        }
        printIndented("]", indentAmt);
    }

    /**
     * Evaluates this list node in the given environment.
     * 
     * @param env The runtime environment for variable lookup.
     * @return A List containing the evaluated elements.
     * @throws EvaluationException if elements are of different types.
     */
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

    /**
     * Infers the type of this list node using the type environment and inferencer.
     * 
     * @param tenv       The type environment.
     * @param inferencer The type inferencer for unification.
     * @return A ListType representing the type of the list elements.
     * @throws TypeException if there is a type mismatch among elements.
     */
    @Override
    public Type typeOf(TypeEnvironment tenv, Inferencer inferencer) throws TypeException{
        // Handle the empty list case by creating a new type variable for the element type
        if (exprs.isEmpty()) {
            Type elemType = tenv.getTypeVariable();
            return new ast.typesystem.types.ListType(elemType);
        }

        // Determine the type of the first element
        SyntaxNode firstNode = exprs.getFirst();
        Type firstType = firstNode.typeOf(tenv, inferencer);

        // Ensure all other elements unify with the type of the first element
        for (int i = 1; i < exprs.size(); i++) {
            SyntaxNode node = exprs.get(i);
            Type nodeType = node.typeOf(tenv, inferencer);

            inferencer.unify(firstType, nodeType, "All elements in a list must have the same type.");
        }

        // Apply any substitutions resulting from unification to get the final element type
        Type elementType = inferencer.getSubstitutions().apply(firstType);

        // Return the type of the list as a ListType of the element type
        return new ast.typesystem.types.ListType(elementType);
    }
}

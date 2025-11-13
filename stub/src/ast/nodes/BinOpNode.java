/*
 *   Copyright (C) 2022 -- 2025  Zachary A. Kissel
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package ast.nodes;

import ast.EvaluationException;
import ast.typesystem.TypeException;
import ast.typesystem.inferencer.Inferencer;
import ast.typesystem.types.Type;
import environment.Environment;
import environment.TypeEnvironment;
import lexer.TokenType;

/**
 * This node represents a binary operation.
 * 
 * @author Zach Kissel
 */
public final class BinOpNode extends SyntaxNode {
    private TokenType op;
    private SyntaxNode leftTerm;
    private SyntaxNode rightTerm;

    /**
     * Constructs a new binary operation syntax node.
     * 
     * @param lterm the left operand.
     * @param op    the binary operation to perform.
     * @param rterm the right operand.
     * @param line  the line of code the node is associated with.
     */
    public BinOpNode(SyntaxNode lterm, TokenType op, SyntaxNode rterm,
            long line) {
        super(line);
        this.op = op;
        this.leftTerm = lterm;
        this.rightTerm = rterm;
    }

    /**
     * Display a AST inferencertree with the indentation specified.
     * 
     * @param indentAmt the amout of indentation to perform.
     */
    public void displaySubtree(int indentAmt) {
        printIndented("BinOp[" + op + "](", indentAmt);
        leftTerm.displaySubtree(indentAmt + 2);
        rightTerm.displaySubtree(indentAmt + 2);
        printIndented(")", indentAmt);
    }

    /**
     * Evaluate the node.
     * 
     * @param env the executional environment we should evaluate the node under.
     * @return the object representing the result of the evaluation.
     * @throws EvaluationException if the evaluation fails.
     */
    @Override
    public Object evaluate(Environment env) throws EvaluationException {
        Object lval = leftTerm.evaluate(env);
        Object rval = rightTerm.evaluate(env);

        if (op == TokenType.CONCAT) {
            if (lval instanceof java.util.List<?> leftList && rval instanceof java.util.List<?> rightList) {
                if (!leftList.isEmpty() && !rightList.isEmpty()) {
                    Class<?> leftType = leftList.get(0).getClass();
                    Class<?> rightType = rightList.get(0).getClass();
                    if (!leftType.equals(rightType)) {
                        logError("Error: Concatenation requires lists of the same element type.");
                        throw new EvaluationException();
                    }
                }
                java.util.List<Object> newList = new java.util.ArrayList<>(leftList);
                newList.addAll(rightList);
                return newList;
            } else {
                logError("Error: Concatenation requires two list operands.");
                throw new EvaluationException();
            }
        }

        if (!(lval instanceof Integer || lval instanceof Double || lval instanceof Boolean) ||
            !(rval instanceof Integer || rval instanceof Double || rval instanceof Boolean)) {
            logError("Invalid operands for binary operation.");
            throw new EvaluationException();
        }

        boolean useDouble = (lval instanceof Double || rval instanceof Double);

        switch (op) {
            case ADD:
                if (useDouble)
                    return ((Number) lval).doubleValue() + ((Number) rval).doubleValue();
                return ((Number) lval).intValue() + ((Number) rval).intValue();
            case SUB:
                if (useDouble)
                    return ((Number) lval).doubleValue() - ((Number) rval).doubleValue();
                return ((Number) lval).intValue() - ((Number) rval).intValue();
            case MULT:
                if (useDouble)
                    return ((Number) lval).doubleValue() * ((Number) rval).doubleValue();
                return ((Number) lval).intValue() * ((Number) rval).intValue();
            case DIV:
                if (useDouble)
                    return ((Number) lval).doubleValue() / ((Number) rval).doubleValue();
                return ((Number) lval).intValue() / ((Number) rval).intValue();
            case MOD:
                if (useDouble) {
                    logError("Error: Mod requires integer arguments.");
                    throw new EvaluationException();
                }
                return ((Integer) lval) % ((Integer) rval);
            case AND:
                return ((Boolean) lval) && ((Boolean) rval);
            case OR:
                return ((Boolean) lval) || ((Boolean) rval);
            default:
                throw new EvaluationException();
        }
    }

    /**
     * Determine the type of the syntax node. In particluar bool, int, real,
     * generic, or function.
     * 
     * @param tenv       the type environment.
     * @param inferencer the type inferencer
     * @return The type of the syntax node.
     * @throws TypeException if there is a type error.
     */
    @Override
    public Type typeOf(TypeEnvironment tenv, Inferencer inferencer) throws TypeException {
        // Infer the type of both operands
        Type leftType = leftTerm.typeOf(tenv, inferencer);
        Type rightType = rightTerm.typeOf(tenv, inferencer);

        // Attempt to unify them
        inferencer.unify(leftType, rightType, "Binary operation operand mismatch");

        // Apply the substitution map to reflect any updated constraints
        leftType = inferencer.getSubstitutions().apply(leftType);
        rightType = inferencer.getSubstitutions().apply(rightType);

        switch (op) {
            // Arithmetic operators
            case ADD:
            case SUB:
            case MULT:
            case DIV:
                if (leftType instanceof ast.typesystem.types.IntType)
                    return new ast.typesystem.types.IntType();
                else if (leftType instanceof ast.typesystem.types.RealType)
                    return new ast.typesystem.types.RealType();
                else
                    throw new TypeException(buildErrorMessage(
                            "Arithmetic operations require Int or Real types, got: " + leftType));

                // Modulus
            case MOD:
                if (leftType instanceof ast.typesystem.types.IntType)
                    return new ast.typesystem.types.IntType();
                else
                    throw new TypeException(buildErrorMessage(
                            "Modulus operation requires Int type, got: " + leftType));

                // Logical operators
            case AND:
            case OR:
                if (leftType instanceof ast.typesystem.types.BoolType)
                    return new ast.typesystem.types.BoolType();
                else
                    throw new TypeException(buildErrorMessage(
                            "Logical operation requires Bool type, got: " + leftType));

                // List concatenation
            case CONCAT:
                // Get a fresh type variable from the type environment for list elements
                ast.typesystem.types.VarType elemVar = tenv.getTypeVariable();
                ast.typesystem.types.ListType listType = new ast.typesystem.types.ListType(elemVar);

                // Ensure both sides are list types with compatible element types
                inferencer.unify(leftType, listType, "List concatenation requires list operands");
                inferencer.unify(rightType, listType, "List concatenation requires list operands");

                // Apply substitutions
                leftType = inferencer.getSubstitutions().apply(leftType);
                rightType = inferencer.getSubstitutions().apply(rightType);

                // Return the unified list type
                return leftType;

            // Unknown operation
            default:
                throw new TypeException(buildErrorMessage(
                        "Unknown binary operator: " + op));
        }
    }
}

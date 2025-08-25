package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.utils.loader.ValueGetter;

import java.util.Map;


public class VarOperationNode implements RunnableNode {

    private String var;
    private Expression expression;

    public VarOperationNode(String var, ScriptParserLineContext context) {
        this.var = var;
        if(var.startsWith("!")) return;

        for(Expression.Operation operation : Expression.Operation.values()) {
            if(var.contains(operation.getSymbol().replaceAll("\\\\",""))) {
                expression = new Expression(var, operation, context);
                return;
            }
        }

        throw new RuntimeException("Unknown Operation: " + var);
    }

    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        if(var.startsWith("!")) {
            var Var = Minepy.getFromSAN(var.substring(1).trim(), variableMap);
            return Variable.ofBoolean(Var.getName(), !Var.getAsBoolean().getValue());
        }

        return opera(expression, variableMap);
    }

    public static class Expression {
        public VarGetterNode left;
        public VarGetterNode right;
        public Operation operation;

        public Expression(String var, Operation operation, ScriptParserLineContext context) {
            String[] vars = var.split(operation.getSymbol());
            left = ValueGetter.getWhenReading(vars[0].trim(), context);
            right = ValueGetter.getWhenReading(vars[1].trim(), context);
            this.operation = operation;
        }

        public enum Operation{
            SUM("\\+"),
            SUB("-"),
            MUL("\\*"),
            DIV("/"),
            MORE_OR_EQUAL(">="),
            LESS_OR_EQUAL("<="),
            MORE(">"),
            LESS("<"),
            EQUAL("=="),
            NOT_EQUAL("!="),
            AND("&&"),
            OR("\\|\\|");

            private final String symbol;

            Operation(String symbol) {
                this.symbol = symbol;
            }

            public String getSymbol() {
                return symbol;
            }
        }
    }

    public static Variable<?> opera(Expression expression, Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        Variable<?> left = expression.left.runWithArg(variableMap);
        Variable<?> right = expression.right.runWithArg(variableMap);
        return switch (expression.operation) {
            case SUM -> left.sum(right);
            case SUB -> left.sub(right);
            case MUL -> left.mul(right);
            case DIV -> left.div(right);
            case OR -> left.or(right);
            case MORE -> left.isMoreThan(right);
            case LESS -> left.isLessThan(right);
            case MORE_OR_EQUAL -> left.isMoreThanOrEqual(right);
            case LESS_OR_EQUAL -> left.isLessThanOrEqual(right);
            case EQUAL -> left.isEqual(right);
            case NOT_EQUAL -> left.isEqual(right).InvertBoolean();
            case AND ->  left.and(right);
        };
    }
}

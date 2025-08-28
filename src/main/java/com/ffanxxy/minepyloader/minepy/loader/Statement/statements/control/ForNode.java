package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.control;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.StatementManager;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.AssignmentNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VariableDeclarationNode;

import java.util.List;
import java.util.Map;

public class ForNode extends ControlNode{

    private VariableDeclarationNode declarationNode;
    private AssignmentNode varOperationNode;

    public ForNode(String cod, ScriptParserLineContext context) {
        super(List.of(cod.split(";")).get(1).trim(), context);
        var sms = List.of(cod.split(";"));
        declarationNode = StatementManager.parseVariableDeclaration(ScriptParserLineContext.createWithNewLine(context, sms.get(0)));
        varOperationNode = StatementManager.parseVarAssignment(ScriptParserLineContext.createWithNewLine(context, sms.get(2)));
    }

    @Override
    public boolean getRepeating() {
        return true;
    }

    @Override
    public void beforeRunning(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        if(super.cyclingCount == 0) {
            declarationNode.runWithArg(variableMap);
        }
        super.beforeRunning(variableMap);
    }

    @Override
    public void afterCycling(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        varOperationNode.runWithArg(variableMap);
        super.afterCycling(variableMap);
    }

    @Override
    public void beforeReturn(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        for(Minepy.ScopeAndName scopeAndName : variableMap.keySet()) {
            if(scopeAndName.name().equals(declarationNode.getVarName())) {
                variableMap.remove(scopeAndName);
                break;
            }
        }

        super.beforeReturn(variableMap);
    }
}

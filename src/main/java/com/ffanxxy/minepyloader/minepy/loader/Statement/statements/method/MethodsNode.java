package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.GameInterface.World.MpyWorld;
import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.RunnableNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.MpyList;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MethodsNode implements RunnableNode {

    protected MethodBuilder builder;

    protected String methodName;
    protected int chosenIndex;
    protected String method;

    private final List<VarGetterNode> inputArguments;

    public MethodsNode(List<VarGetterNode> inputs, ScriptParserLineContext context, String method) {
        builder = getPlansBuilder(new MethodBuilder());
        this.method = method;
        inputArguments = inputs;
    }

    public abstract @NotNull MethodBuilder getPlansBuilder(MethodBuilder builder);

    public abstract @NotNull Variable<?> run(int index, InputArgument args);

    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {

        List<Variable<?>> Args = new ArrayList<>();

        for(VarGetterNode node : inputArguments) {
            Args.add(node.runWithArg(variableMap));
        }

        for(MethodBuilder.MethodIndex methodIndex : builder.getPlans()) {
            if(!methodIndex.name().equals(PackageStructure.create(method).getLast())) continue;

            List<DataType> planParameters = methodIndex.dataTypes();

            if(inputArguments.size() != planParameters.size()) continue;

            if(inputArguments.isEmpty()) {
                this.chosenIndex = builder.plans.get(methodIndex);
                return run(this.chosenIndex, new InputArgument(Args));
            }

            boolean isSame = true;
            for (int i = 0; i < planParameters.size(); i++) {
                isSame = isSame && planParameters.get(i).isSameTypeAs( Args.get(i).getDataType());
            }
            if(isSame) {
                this.chosenIndex = builder.plans.get(methodIndex);
                return run(this.chosenIndex, new InputArgument(Args));
            } else {
                throw new RuntimeException("There was no plan in method! Please check:" + this.method);
            }
        }

        throw new RuntimeException("Unknow method: " + this.method);
    }

    public static class InputArgument {

        private List<Variable<?>> variables;

        public InputArgument(List<Variable<?>> variables) {
            this.variables = variables;
        }

        public String getString(int index) {
            return variables.get(index).toString();
        }

        public int getInteger(int index) {
            return variables.get(index).getAsInt().getValue();
        }

        public float getFloat(int index) {
            return variables.get(index).getAsFloat().getValue();
        }

        public double getDouble(int index) {
            return variables.get(index).getAsDouble().getValue();
        }

        public MpyWorld getWorld(int index) {
            return variables.get(index).getAsWorld().getValue();
        }

        public PlayerEntity getPlayer(int index) {
            return variables.get(index).getAsPlayer().getValue();
        }

        public char getChar(int index) {
            return variables.get(index).getAsChar().getValue();
        }

        public Object getObject(int index) {
            return variables.get(index).getValue();
        }

        public MpyList getList(int index) {
            return variables.get(index).getAsList().getValue();
        }

        public Variable<?> getVariable(int index) {
            return variables.get(index);
        }
    }
}

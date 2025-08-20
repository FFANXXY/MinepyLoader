package com.ffanxxy.minepyloader.minepy.loader.Statement.statements;

import com.ffanxxy.minepyloader.minepy.loader.Loader.Minepy;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.PackageStructure;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Parameter;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodParametersBuilder;
import com.ffanxxy.minepyloader.minepy.utils.loader.DataTypeHelper;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class MethodNode implements RunnableNode {

    protected MethodParametersBuilder builder;

    protected String chosenPlan;

    private final List<Parameter> inputParas;

    public MethodNode(List<Parameter> inputParameters, ScriptParserLineContext context, String method) {
        builder = getPlansBuilder(new MethodParametersBuilder());
        inputParas = inputParameters;

        var defineContext = context.defineVarContext();

        for(String plan : builder.getPlans().keySet()) {
            if(!plan.equals(PackageStructure.create(method).getLast())) continue;

            List<DataType> planParameters = builder.getPlans().get(plan);

            if(inputParameters.size() != planParameters.size()) continue;

            if(inputParameters.isEmpty()) {
                this.chosenPlan = plan;
                return;
            }

            boolean isSame = true;
            for (int i = 0; i < planParameters.size(); i++) {
                Parameter parameter = inputParameters.get(i);
                DataType inputDataType = DataTypeHelper.getDataType(parameter, defineContext);

                isSame = isSame && planParameters.get(i).isSameTypeAs(inputDataType);
            }

            if(isSame) {
                this.chosenPlan = plan;
                return;
            } else {
                throw new RuntimeException("There was no plan in method! Please check:" + context.line());
            }
        }
    }

    public abstract @NotNull MethodParametersBuilder getPlansBuilder(MethodParametersBuilder builder);

    public abstract @NotNull Variable<?> run(String plan, List<Variable<?>> variables);

    @Override
    public Variable<?> runWithArg(Map<Minepy.ScopeAndName, Variable<?>> variableMap) {
        List<Variable<?>> Args = new ArrayList<>();
        for(Parameter parameter : inputParas) {
            switch (parameter.dataType) {
                case LITERAL_STRING -> Args.add(Variable.ofString("%LIT", parameter.name));
                case LITERAL_INTEGER -> Args.add(Variable.ofInteger("%LIT", Integer.parseInt(parameter.name)));
                case LITERAL_DOUBLE -> Args.add(Variable.ofDouble("%LIT", Double.parseDouble(parameter.name)));
                case LITERAL_NULL -> Args.add(Variable.NULL());
                case LITERAL_BOOLEAN -> Args.add(Variable.ofBoolean("%LIT", Boolean.parseBoolean(parameter.name)));
                default -> Args.add(Minepy.getFromSAN(parameter.name, variableMap));
            }
        }
        return run(this.chosenPlan, Args);
    }
}

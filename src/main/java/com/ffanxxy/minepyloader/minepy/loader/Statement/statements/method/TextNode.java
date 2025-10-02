package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.GameInterface.Text.MpyText;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodBuilder;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TextNode extends MethodsNode{
    public TextNode(List<VarGetterNode> inputs, ScriptParserLineContext context, String method) {
        super(inputs, context, method);
    }

    @Override
    public @NotNull MethodBuilder getPlansBuilder(MethodBuilder builder) {
        builder.add("new",0, DataType.STRING);
        builder.add("styled",1,DataType.TEXT,DataType.STYLE);
        builder.add("getText",2, DataType.TEXT);
        builder.add("byTranslation",3, DataType.STRING);
        builder.add("byTranslation",4, DataType.STRING,DataType.LIST);
        return builder;
    }

    @Override
    public @NotNull Variable<?> run(int index, InputArgument args) {
        return switch (index) {
            case 0 -> Variable.ofText("%Temp", new MpyText(args.getString(0)));
            case 1 -> {
                args.getText(0).styled(args.getStyle(1));
                yield Variable.VOID();
            }
            case 2 -> Variable.ofString("%TEMP",args.getText(0).getText());
            case 3 -> Variable.ofText("%Temp", new MpyText(Text.translatable(args.getString(0)).getString()));
            case 4 -> {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < args.getList(1).size().getValue(); i++) {
                    list.add(args.getList(1).get(i).toString());
                }
                yield Variable.ofText("%Temp", new MpyText(Text.translatable(args.getString(0), list).getString()));
            }
            default -> Variable.VOID();
        };
    }
}

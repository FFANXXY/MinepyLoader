package com.ffanxxy.minepyloader.minepy.loader.Statement.statements.method;

import com.ffanxxy.minepyloader.GameInterface.Text.MpyStyle;
import com.ffanxxy.minepyloader.GameInterface.Text.MpyText;
import com.ffanxxy.minepyloader.minepy.loader.Loader.ScriptParserLineContext;
import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import com.ffanxxy.minepyloader.minepy.loader.Statement.statements.var.VarGetterNode;
import com.ffanxxy.minepyloader.minepy.loader.Statement.type.DataType;
import com.ffanxxy.minepyloader.minepy.utils.builder.MethodBuilder;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StyleNode extends MethodsNode{
    public StyleNode(List<VarGetterNode> inputs, ScriptParserLineContext context, String method) {
        super(inputs, context, method);
    }

    @Override
    public @NotNull MethodBuilder getPlansBuilder(MethodBuilder builder) {
        builder.add("new",0);
        builder.add("setBold",1,DataType.STYLE,DataType.BOOLEAN);
        builder.add("setItalic", 2, DataType.STYLE, DataType.BOOLEAN);
        builder.add("setThrough", 3, DataType.STYLE, DataType.BOOLEAN);
        builder.add("setUnderlined", 4, DataType.STYLE, DataType.BOOLEAN);
        builder.add("setObfuscated", 5, DataType.STYLE, DataType.BOOLEAN);
        builder.add("setColor", 6, DataType.STYLE, DataType.INT);
        builder.add("setColor", 7, DataType.STYLE, DataType.STRING);
        return builder;
    }

    @Override
    public @NotNull Variable<?> run(int index, InputArgument args) {
        return switch (index) {
            case 0 -> Variable.ofStyle("%Temp", new MpyStyle());
            case 1 -> {
                args.getStyle(0).setBold(args.getBoolean(1));
                yield  Variable.VOID();
            }
            case 2 -> {
                args.getStyle(0).setItalic(args.getBoolean(1));
                yield  Variable.VOID();
            }
            case 3 -> {
                args.getStyle(0).setStrikethrough(args.getBoolean(1));
                yield  Variable.VOID();
            }
            case 4 -> {
                args.getStyle(0).setUnderlined(args.getBoolean(1));
                yield  Variable.VOID();
            }
            case 5 -> {
                args.getStyle(0).setObfuscated(args.getBoolean(1));
                yield  Variable.VOID();
            }
            case 6 -> {
                args.getStyle(0).setColor(TextColor.fromRgb(args.getInteger(1)));
                yield  Variable.VOID();
            }case 7 -> {
                args.getStyle(0).setColor(TextColor.fromFormatting(Formatting.byName(args.getString(1))));
                yield  Variable.VOID();
            }

            default -> Variable.VOID();
        };
    }
}

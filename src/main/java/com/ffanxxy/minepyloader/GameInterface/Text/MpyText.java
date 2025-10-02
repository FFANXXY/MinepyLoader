package com.ffanxxy.minepyloader.GameInterface.Text;

import com.ffanxxy.minepyloader.minepy.loader.Statement.Variable.Variable;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class MpyText {
    private String text;
    private MpyStyle style;
    private TextIndexStyle styleIndex;

    public MpyText(String string) {
        text = string;
        styleIndex = TextIndexStyle.LITERAL;
    }

    public void styled(MpyStyle s) {
        this.style = s;
    }

    public Text build() {
        return switch (styleIndex) {
            case LITERAL -> Text.literal(text).styled(style1 -> style.build(style1));
            case TRANSLATE -> Text.translatable(text).styled(style1 -> style.build(style1));
        };
    }

    public @NotNull String getText() {
        return text;
    }

    public enum TextIndexStyle{
        LITERAL,
        TRANSLATE
    }
}

package com.ffanxxy.minepyloader.GameInterface.Text;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class MpyStyle {
    @Nullable
    private TextColor color;
    @Nullable
    private Boolean bold;
    @Nullable
    private Boolean italic;
    @Nullable
    private Boolean underlined;
    @Nullable
    private Boolean strikethrough;
    @Nullable
    private Boolean obfuscated;
    @Nullable
    private ClickEvent clickEvent;
    @Nullable
    private HoverEvent hoverEvent;
    @Nullable
    private String insertion;
    @Nullable
    private Identifier font;
    
    public MpyStyle() {
        setColor(TextColor.fromFormatting(Formatting.WHITE));
        setBold(false);
        setItalic(false);
        setUnderlined(false);
        setStrikethrough(false);
        setObfuscated(false);
        setClickEvent(null);
        setHoverEvent(null);
        setInsertion(null);
        setFont(null);
    };


    public void setColor(@Nullable TextColor color) {
        this.color = color;
    }

    public @Nullable TextColor getColor() {
        return color;
    }

    public @Nullable Boolean getBold() {
        return bold;
    }

    public void setBold(@Nullable Boolean bold) {
        this.bold = bold;
    }

    public @Nullable Boolean getItalic() {
        return italic;
    }

    public void setItalic(@Nullable Boolean italic) {
        this.italic = italic;
    }

    public @Nullable Boolean getUnderlined() {
        return underlined;
    }

    public void setUnderlined(@Nullable Boolean underlined) {
        this.underlined = underlined;
    }

    public @Nullable Boolean getStrikethrough() {
        return strikethrough;
    }

    public void setStrikethrough(@Nullable Boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public @Nullable Boolean getObfuscated() {
        return obfuscated;
    }

    public void setObfuscated(@Nullable Boolean obfuscated) {
        this.obfuscated = obfuscated;
    }

    public @Nullable ClickEvent getClickEvent() {
        return clickEvent;
    }

    public void setClickEvent(@Nullable ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }

    public @Nullable HoverEvent getHoverEvent() {
        return hoverEvent;
    }

    public void setHoverEvent(@Nullable HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
    }

    public @Nullable String getInsertion() {
        return insertion;
    }

    public void setInsertion(@Nullable String insertion) {
        this.insertion = insertion;
    }

    public @Nullable Identifier getFont() {
        return font;
    }

    public void setFont(@Nullable Identifier font) {
        this.font = font;
    }

    public Style build(Style style) {
        return style.withColor(color)
        .withBold(bold)
        .withItalic(italic)
        .withUnderline(underlined)
        .withObfuscated(obfuscated)
        .withStrikethrough(strikethrough)
        .withHoverEvent(hoverEvent)
        .withClickEvent(clickEvent)
        .withInsertion(insertion)
        .withFont(font);
    }
}

package com.wdiscute.sellingbin.emi;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public class HoverTextWidget extends Widget
{
    protected final int x, y, sizeX, sizeY;
    private final List<Component> components;

    public HoverTextWidget(int x, int y, int sizeX, int sizeY, List<Component> hoverText)
    {
        this.x = x;
        this.y = y;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.components = hoverText;
    }

    @Override
    public Bounds getBounds()
    {
        return new Bounds(x, y, 18, 18);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta)
    {
        if (mouseX > x && mouseX < x + sizeX && mouseY > y && mouseY < y + sizeY)
            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
    }
}

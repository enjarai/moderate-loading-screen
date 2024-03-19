package dev.enjarai.mls;

import dev.enjarai.mls.mixin.DrawContextAccessor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

// This exists to provide a unified interface for rendering
public class DrawContextWrapper {
    /*? if >=1.20 {*//*
    private final net.minecraft.client.gui.DrawContext context;
    public DrawContextWrapper(net.minecraft.client.gui.DrawContext context) {
        this.context = context;
    }

    public MatrixStack matrices() {
        return context.getMatrices();
    }

    public void drawTexturedQuad(Identifier identifier, int x0, int x1, int y0, int y1) {
        ((DrawContextAccessor) context).loadingScreen$drawTexturedQuad(
                identifier,
                x0, x1, y0, y1, 0,
                0.0f, 1.0f, 0.0f, 1.0f
        );
    }
    *//*?} else {*/
    private final MatrixStack stack;
    public DrawContextWrapper(MatrixStack stack) {
        this.stack = stack;
    }

    public MatrixStack matrices() {
        return stack;
    }
    public void drawTexturedQuad(Identifier identifier, int x0, int x1, int y0, int y1) {
        DrawContextAccessor.loadingScreen$drawTexturedQuad(
                matrices().peek().getPositionMatrix(),
                x0, x1, y0, y1, 0,
                0.0f, 1.0f, 0.0f, 1.0f
        );
    }
    /*?} */
}
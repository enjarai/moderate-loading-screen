package dev.enjarai.mls.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


/*? if >=1.20 {*//*
@Mixin(net.minecraft.client.gui.DrawContext.class)
public interface DrawContextAccessor {
    @Invoker("drawTexturedQuad")
    void loadingScreen$drawTexturedQuad(
            net.minecraft.util.Identifier identifier,
            int x0,
            int x1,
            int y0,
            int y1,
            int z,
            float u0,
            float u1,
            float v0,
            float v1);
}
*//*?} else {*/
@Mixin(net.minecraft.client.gui.DrawableHelper.class)
public interface DrawContextAccessor {
    @Invoker("drawTexturedQuad")
    static void loadingScreen$drawTexturedQuad(
            org.joml.Matrix4f matrix,
            int x0,
            int x1,
            int y0,
            int y1,
            int z,
            float u0,
            float u1,
            float v0,
            float v1) {
        throw new UnsupportedOperationException();
    }
}
/*?} */
package nl.enjarai.mls.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
    @Invoker("drawTexturedQuad")
    void loadingScreen$drawTexturedQuad(Identifier identifier, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1);
}

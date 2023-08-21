package nl.enjarai.mls.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.resource.ResourceReload;
import nl.enjarai.mls.ModerateLoadingScreen;
import nl.enjarai.mls.screens.LoadingScreen;
import nl.enjarai.mls.screens.SnowFlakesScreen;
import nl.enjarai.mls.screens.StackingScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin extends Overlay {
    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    private static int withAlpha(int color, int alpha) {
        throw new UnsupportedOperationException("Shadowed method somehow called outside mixin. Exorcise your computer.");
    }

    @Unique
    private LoadingScreen moderateLoadingScreen$loadingScreen;

    @Inject(
            method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/resource/ResourceReload;Ljava/util/function/Consumer;Z)V",
            at = @At("TAIL")
    )
    private void moderateLoadingScreen$constructor(MinecraftClient client, ResourceReload monitor, Consumer<Optional<Throwable>> exceptionHandler, boolean reloading, CallbackInfo ci) {
        moderateLoadingScreen$loadingScreen = switch (ModerateLoadingScreen.CONFIG.screenType()) {
            case SNOWFLAKES -> new SnowFlakesScreen(this.client);
            case STACKING -> new StackingScreen(this.client);
        };
    }

    // Replace the color used for the background fill of the splash screen
    @ModifyArg(
            method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIII)V"),
            index = 5
    )
    private int moderateLoadingScreen$changeColor(int in) {
        if (this.client.options.getMonochromeLogo().getValue())
            return in;

        return withAlpha(ModerateLoadingScreen.CONFIG.backgroundColor().rgb(), in >> 24); // Use existing transparency
    }

    // For some reason Mojang decided to not use `fill` in a specific case, so we have to replace a local variable
    @ModifyVariable(
            method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/function/IntSupplier;getAsInt()I", ordinal = 2),
            ordinal = 4 // int m (or int o according to mixin apparently)
    )
    private int moderateLoadingScreen$changeColorGl(int in) {
        return this.client.options.getMonochromeLogo().getValue() ? in : ModerateLoadingScreen.CONFIG.backgroundColor().rgb();
    }

    // Render before third getWindow to render before the logo
    @Inject(
            method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I", ordinal = 2),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void moderateLoadingScreen$renderPatches(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci,
                                                     int i, int j, long l, float f, float g, float h) {
        moderateLoadingScreen$loadingScreen.renderPatches(context, delta, f >= 1.0f);
    }

    // Modify logo transparency if needed, multiplies with the original to ensure transitions work normally
    @ModifyArg(
            method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;setShaderColor(FFFF)V"),
            index = 3
    )
    private float moderateLoadingScreen$modifyLogoTransparency(float original) {
        return original * ModerateLoadingScreen.CONFIG.logoOpacity() / 100f;
    }

    // Reset RenderSystem shader color to prevent rendering everything else with the modified transparency
    @Inject(
            method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void moderateLoadingScreen$resetTransparency(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci,
                                                         int i, int j, long l, float f, float g, float h) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, h);
    }

    // Modify loading bar transparency if needed, again multiplying with the original
    @ModifyArg(
            method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;renderProgressBar(Lnet/minecraft/client/gui/DrawContext;IIIIF)V"),
            index = 5
    )
    private float moderateLoadingScreen$modifyBarTransparency(float original) {
        return original * ModerateLoadingScreen.CONFIG.barOpacity() / 100f;
    }
}

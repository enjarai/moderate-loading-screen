/*
 * Copyright (c) 2021 enjarai
 * Copyright (c) 2021 darkerbit
 * Copyright (c) 2021 wafflecoffee
 * Copyright (c) 2020 TeamMidnightDust (MidnightConfig only)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package nl.enjarai.mls.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceReload;
import nl.enjarai.mls.config.ModConfig;
import nl.enjarai.mls.screens.LoadingScreen;
import nl.enjarai.mls.screens.SnowFlakesScreen;
import nl.enjarai.mls.screens.StackingScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

  private LoadingScreen loadingScreen$loadingScreen;

  @Inject(
          method = "<init>(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/resource/ResourceReload;Ljava/util/function/Consumer;Z)V",
          at = @At("TAIL")
  )
  private void constructor(MinecraftClient client, ResourceReload monitor, Consumer<Optional<Throwable>> exceptionHandler, boolean reloading, CallbackInfo ci) {
    loadingScreen$loadingScreen = switch (ModConfig.INSTANCE.screenType) {
      case SNOWFLAKES -> new SnowFlakesScreen(this.client);
      case STACKING -> new StackingScreen(this.client);
    };
  }

  // Replace the colour used for the background fill of the splash screen
  @ModifyArg(
          method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"),
          index = 5
  )
  private int changeColor(int in) {
    if (this.client.options.monochromeLogo)
      return in;

    return withAlpha(ModConfig.INSTANCE.backgroundColor, in >> 24); // Use existing transparency
  }

  // For some reason Mojang decided to not use `fill` in a specific case so I have to replace a local variable
  @ModifyVariable(
          method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
          at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/function/IntSupplier;getAsInt()I", ordinal = 2),
          ordinal = 4 // int m (or int o according to mixin apparently)
  )
  private int changeColorGl(int in) {
    return this.client.options.monochromeLogo ? in : ModConfig.INSTANCE.backgroundColor;
  }

  // Render before third getWindow to render before the logo
  @Inject(
          method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getWindow()Lnet/minecraft/client/util/Window;", ordinal = 2),
          locals = LocalCapture.CAPTURE_FAILSOFT
  )
  private void renderPatches(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci,
                             int i, int j, long l, float f) {
    loadingScreen$loadingScreen.renderPatches(matrices, delta, f >= 1.0f);
  }

  // Modify logo transparency if needed, multiplies with the original to ensure transitions work normally
  @ModifyArg(
          method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
          at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"),
          index = 3
  )
  private float modifyLogoTransparency(float original) {
    return original * ModConfig.INSTANCE.logoOpacity;
  }

  // Reset RenderSystem shader color to prevent rendering everything else with the modified transparency
  @Inject(
          method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
          at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;defaultBlendFunc()V"),
          locals = LocalCapture.CAPTURE_FAILSOFT
  )
  private void resetTransparency(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci,
                                 int i, int j, long l, float f, float g, float h) {
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, h);
  }

  // Modify loading bar transparency if needed, again multiplying with the original
  @ModifyArg(
          method = "render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;renderProgressBar(Lnet/minecraft/client/util/math/MatrixStack;IIIIF)V"),
          index = 5
  )
  private float modifyBarTransparency(float original) {
    return original * ModConfig.INSTANCE.barOpacity;
  }
}

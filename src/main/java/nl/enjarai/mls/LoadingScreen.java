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

package nl.enjarai.mls;

import nl.enjarai.mls.config.ModConfig;
import nl.enjarai.mls.mixin.DrawableHelperAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.apache.commons.lang3.Validate;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class LoadingScreen {
  public static final String MODID = "moderate-loading-screen";

  private final int patchSize;

  private final MinecraftClient client;

  private final Random random = new Random();

  private final ArrayList<FallingPatch> fallingPatches = new ArrayList<>();

  private float patchTimer = 0f;

  private final ArrayList<Identifier> icons = new ArrayList<>();

  public LoadingScreen(MinecraftClient client) {
    this.client = client;

    patchSize = ModConfig.INSTANCE.iconSize;

    // Construct list of mod icons, main principles copied from mod menu
    for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
      ModMetadata metadata = mod.getMetadata();

      String path = metadata.getIconPath(128).orElse("assets/" + metadata.getId() + "/icon.png");
      NativeImageBackedTexture texture = getIconTexture(mod, path);

      if (texture != null) {
        Identifier iconLocation = new Identifier(MODID, metadata.getId() + "_icon");

        this.client.getTextureManager().registerTexture(iconLocation, texture);
        icons.add(iconLocation);
      }
    }

    // Summon the holy tater if enabled
    if (ModConfig.INSTANCE.showTater) createPatch(new Identifier(MODID, "textures/gui/tiny_potato.png"));
  }

  public NativeImageBackedTexture getIconTexture(ModContainer iconSource, String iconPath) {
    try {
      Path path = iconSource.getPath(iconPath);
      try (InputStream inputStream = Files.newInputStream(path)) {
        NativeImage image = NativeImage.read(Objects.requireNonNull(inputStream));
        Validate.validState(image.getHeight() == image.getWidth(), "Must be square icon");
        return new NativeImageBackedTexture(image);
      }

    } catch (Throwable t) {
      return null;
    }
  }

  public void createPatch(Identifier texture) {
    fallingPatches.add(new FallingPatch(
            random.nextDouble() * this.client.getWindow().getScaledWidth(), -patchSize, 0,
            (random.nextDouble() - 0.5) * 0.6,
            random.nextDouble() * 3.0 + 1.0,
            (random.nextDouble() - 0.5) * 6.0,
            random.nextDouble() / 2 + 0.5,
            texture, patchSize
    ));
  }

  public void updatePatches(float delta, boolean ending) {
    for (FallingPatch patch : fallingPatches) {
      if (ending)
        patch.fallSpeed *= 1.0 + delta / 3;

      patch.update(delta);
    }

    if (!icons.isEmpty()) {
      patchTimer -= delta;

      if (patchTimer < 0f && !ending) {
        Identifier icon = icons.get(random.nextInt(icons.size()));

        if (ModConfig.INSTANCE.modsOnlyOnce) {
          icons.remove(icon);
        }
        createPatch(icon);

        patchTimer = random.nextFloat();
      }
    }
  }

  public void renderPatches(MatrixStack matrices, float delta, boolean ending) {
    // spike prevention
    if (delta < 2.0f)
      updatePatches(delta, ending);

    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    RenderSystem.enableBlend();
    RenderSystem.defaultBlendFunc();

    for (FallingPatch patch : fallingPatches) {
      RenderSystem.setShaderTexture(0, patch.texture);
      patch.render(matrices, client.options.monochromeLogo);
    }
  }

  private static class FallingPatch {
    private double x, y, rot;
    private final Identifier texture;

    private final double horizontal, rotSpeed;
    private final double scale;

    public double fallSpeed;

    private final int patchSize;

    public FallingPatch(double x, double y, double rot, double horizontal, double fallSpeed, double rotSpeed, double scale, Identifier texture, int patchSize) {
      this.x = x;
      this.y = y;
      this.rot = rot;

      this.horizontal = horizontal;
      this.fallSpeed = fallSpeed;
      this.rotSpeed = rotSpeed;

      this.scale = scale;

      this.texture = texture;

      this.patchSize = patchSize;
    }

    public void update(float delta) {
      x += horizontal * delta;
      y += fallSpeed * delta;

      rot += rotSpeed * delta;
    }

    public void render(MatrixStack matrices, boolean monochrome) {
      matrices.push();
      matrices.translate(x, y, 0);

      Matrix4f matrix = matrices.peek().getModel();
      matrix.multiply(new Quaternion(0.0f, 0.0f, (float) rot, true));
      matrix.multiply(Matrix4f.scale((float) scale, (float) scale, (float) scale));

      double x1 = -patchSize / (double) 2;
      double y1 = -patchSize / (double) 2;
      double x2 = patchSize / (double) 2;
      double y2 = patchSize / (double) 2;

//      float u0 = 1.0f / PATCH_COUNT * type;
//      float u1 = u0 + 1.0f / PATCH_COUNT;

      float offset = monochrome ? 0.5f : 0.0f;

      DrawableHelperAccessor.loadingScreen$drawTexturedQuad(
              matrix,
              (int) x1, (int) x2, (int) y1, (int) y2, 0,
              0.0f, 1.0f, 0.0f + offset, 1.0f + offset
      );

      matrices.pop();
    }
  }
}

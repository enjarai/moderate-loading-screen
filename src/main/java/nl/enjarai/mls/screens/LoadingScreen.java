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

package nl.enjarai.mls.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixUtil;
import nl.enjarai.mls.ModerateLoadingScreen;
import nl.enjarai.mls.config.ConfigFile;
import nl.enjarai.mls.config.ModConfig;
import nl.enjarai.mls.mixin.DrawableHelperAccessor;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Random;

public abstract class LoadingScreen<C extends ConfigFile<C>> {
    protected final int patchSize;
    protected final MinecraftClient client;
    protected final ArrayList<Identifier> icons;
    protected final Random random = new Random();
    protected final ArrayList<Patch> patches = new ArrayList<>();
    protected double patchTimer = 0f;
    protected boolean tater = ModConfig.INSTANCE.showTater;
    protected final C config;

    public LoadingScreen(MinecraftClient client, C config) {
        patchSize = ModConfig.INSTANCE.iconSize;
        this.client = client;

        this.config = config;

        icons = ModerateLoadingScreen.compileIconList();
    }

    public abstract void createPatch(Identifier texture);

    protected Identifier getNextTexture() {
        // Summon the holy tater if enabled
        if (tater) {
            tater = false;
            return new Identifier(ModerateLoadingScreen.MODID, "textures/gui/tiny_potato.png");
        }

        return icons.get(random.nextInt(icons.size()));
    }

    public void updatePatches(float delta, boolean ending) {
        processPhysics(delta, ending);

        if (!icons.isEmpty()) {
            patchTimer -= delta;

            if (patchTimer < 0f && !ending) {
                Identifier icon = getNextTexture();

                if (ModConfig.INSTANCE.modsOnlyOnce) {
                    icons.remove(icon);
                }
                createPatch(icon);

                patchTimer = getPatchTimer();
            }
        }
    }

    protected double getPatchTimer() {
        return random.nextFloat();
    }

    protected double getOffsetX() {
        return 0;
    }

    protected double getOffsetY() {
        return 0;
    }

    protected void processPhysics(float delta, boolean ending) {
        for (Patch patch : patches) {
            if (ending)
                patch.fallSpeed *= 1.0 + delta / 3;

            patch.update(delta);
        }
    }

    public void renderPatches(MatrixStack matrices, float delta, boolean ending) {
        // spike prevention
        if (delta < 2.0f)
            updatePatches(delta, ending);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (Patch patch : patches) {
            RenderSystem.setShaderTexture(0, patch.texture);
            patch.render(matrices, getOffsetX(), getOffsetY());
        }
    }

    protected static class Patch {
        protected double x, y, rot;
        protected final Identifier texture;

        protected final double horizontal, rotSpeed;
        protected final double scale;

        public double fallSpeed;

        protected final int patchSize;

        public Patch(double x, double y, double rot, double horizontal, double fallSpeed, double rotSpeed, double scale, Identifier texture, int patchSize) {
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

        public void render(MatrixStack matrices, double offsetX, double offsetY) {
            matrices.push();
            matrices.translate(x + offsetX, y + offsetY, 0);

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            MatrixUtil.scale(matrix.rotate((float) rot * 0.017453292F, 0, 0, 1), (float) scale);

            double x1 = -patchSize / (double) 2;
            double y1 = -patchSize / (double) 2;
            double x2 = patchSize / (double) 2;
            double y2 = patchSize / (double) 2;

            DrawableHelperAccessor.moderateLoadingScreen$drawTexturedQuad(
                    matrix,
                    (int) x1, (int) x2, (int) y1, (int) y2, 0,
                    0.0f, 1.0f, 0.0f, 1.0f
            );

            matrices.pop();
        }
    }
}

package dev.enjarai.mls.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.enjarai.mls.DrawContextWrapper;
import dev.enjarai.mls.ModerateLoadingScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixUtil;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Random;

public abstract class LoadingScreen {
    protected final int patchSize = ModerateLoadingScreen.CONFIG.iconSize();
    protected final MinecraftClient client;
    protected final ArrayList<Identifier> icons;
    protected final Random random = new Random();
    protected final ArrayList<Patch> patches = new ArrayList<>();
    protected double patchTimer = 0f;
    protected boolean tater = ModerateLoadingScreen.CONFIG.showTater();
    protected boolean modsOnlyOnce = ModerateLoadingScreen.CONFIG.modsOnlyOnce();

    public LoadingScreen(MinecraftClient client) {
        this.client = client;

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

                if (modsOnlyOnce) {
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

    public void renderPatches(DrawContextWrapper wrapper, float delta, boolean ending) {
        // spike prevention
        if (delta < 2.0f)
            updatePatches(delta, ending);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (Patch patch : patches) {
            /*? if <1.20 */ RenderSystem.setShaderTexture(0, patch.texture);
            patch.render(wrapper, getOffsetX(), getOffsetY());
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

        public void render(DrawContextWrapper wrapper, double offsetX, double offsetY) {
            MatrixStack matrices = wrapper.matrices();
            matrices.push();
            matrices.translate(x + offsetX, y + offsetY, 0);

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            MatrixUtil.scale(matrix.rotate((float) rot * 0.017453292F, 0, 0, 1), (float) scale);

            double x1 = -patchSize / (double) 2;
            double y1 = -patchSize / (double) 2;
            double x2 = patchSize / (double) 2;
            double y2 = patchSize / (double) 2;

            wrapper.drawTexturedQuad(texture, (int) x1, (int) x2, (int) y1, (int) y2);
            matrices.pop();
        }
    }
}

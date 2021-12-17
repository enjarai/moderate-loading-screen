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

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import nl.enjarai.mls.config.ModConfig;

import java.util.ArrayList;
import java.util.HashMap;

public class StackingScreen extends LoadingScreen {
    protected final HashMap<Double, Double> stacksHeight = new HashMap<>();
    protected final double scroll = 0;

    public StackingScreen(MinecraftClient client) {
        super(client);
    }

    @Override
    public void createPatch(Identifier texture) {
        patches.add(new Patch(
                random.nextInt(client.getWindow().getScaledWidth() / patchSize + 1) * patchSize,
                -patchSize - client.getWindow().getScaledHeight() + scroll,
                8 * getPatchesPerSecond(), 1.0,
                texture, patchSize
        ));
    }

    protected double getPatchesPerSecond() {
        return ((client.getWindow().getScaledWidth() / 32f + 1) * (client.getWindow().getScaledHeight() / 32f + 1))
                / ModConfig.INSTANCE.stackingConfig.cycleSeconds / 2;
    }

    @Override
    protected double getPatchTimer() {
        return 4 / getPatchesPerSecond();
    }

    @Override
    protected double getOffsetX() {
        return client.getWindow().getScaledWidth() % patchSize / 2.0;
    }

    @Override
    protected double getOffsetY() {
        return scroll + client.getWindow().getScaledHeight();
    }

    @Override
    protected void processPhysics(float delta, boolean ending) {
        for (LoadingScreen.Patch patch : patches) {
            if (ending) {
                patch.unlock();
                patch.fallSpeed *= 1.0 + delta / 3;
            }

            if (!patch.getLock()) {
                patch.update(delta);

                if (!ending) {
                    Double stackHeight = stacksHeight.get(patch.x);
                    stackHeight = stackHeight == null ? -patchSize / 2 : stackHeight;
                    if (patch.y > stackHeight) {
                        patch.y = stackHeight;
                        patch.lock();
                        stacksHeight.put(patch.x, patch.y - patchSize);
                    }
                }
            }
        }
    }

    protected static class Patch extends LoadingScreen.Patch {

        public Patch(double x, double y, double fallSpeed, double scale, Identifier texture, int patchSize) {
            super(x, y, 0, 0, fallSpeed, 0, scale, texture, patchSize);
        }

        public static int getBlockX(double x, int patchSize) {
            return (int) (x / patchSize);
        }

        public static int getBlockY(double y, int patchSize) {
            return (int) -(y / patchSize);
        }

        public static double getRealY(int blockY, int patchSize) {
            return -blockY * patchSize;
        }
    }
}

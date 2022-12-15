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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.isxander.yacl.api.ConfigCategory;
import dev.isxander.yacl.api.Option;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import dev.isxander.yacl.gui.controllers.slider.DoubleSliderController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.mls.config.ConfigFile;
import nl.enjarai.mls.config.ModConfig;

import java.util.HashMap;

public class StackingScreen extends LoadingScreen<StackingScreen.Config> {
    protected final HashMap<Double, Double> stacksHeight = new HashMap<>();
    protected final HashMap<Integer, Integer> patchesInColumn = new HashMap<>();
    protected double scroll = 0;
    protected double scrollDelta = 0;

    public StackingScreen(MinecraftClient client, Config config) {
        super(client, config);
    }

    @Override
    public void createPatch(Identifier texture) {
        Integer column = pickPatchColumn();

        if (column != null) {
            patches.add(new Patch(
                    column * patchSize - (patchSize / 2d),
                    -patchSize - client.getWindow().getScaledHeight() + scroll,
                    0, 0, 8 * getPatchesPerSecond(),
                    0, 1.0, texture, patchSize
            ));
            patchesInColumn.compute(column, (k, v) -> (v == null) ? 1 : v+1);
        }
    }

    protected Integer pickPatchColumn() {
        int width = (int) Math.ceil(client.getWindow().getScaledWidth() / (double) patchSize);
        int height = (int) Math.ceil(client.getWindow().getScaledHeight() / (double) patchSize);

        double totalWeight = 0.0;
        for (int i = 0; i <= width; i++) {
            totalWeight += getColumnWeight(height, i);
        }

        if (totalWeight > 0.0) {
            double r = random.nextDouble() * totalWeight;
            for (int i = 0; i <= width; i++) {
                r -= getColumnWeight(height, i);
                if (r <= 0.0) return i;
            }
        }
        return null;
    }

    protected double getColumnWeight(int height, int column) {
        Integer patchesAmount = patchesInColumn.get(column);
        return height - (patchesAmount == null ? 0 : patchesAmount);
    }

    protected double getPatchesPerSecond() {
        return ((client.getWindow().getScaledWidth() / (double) patchSize + 1) *
                (client.getWindow().getScaledHeight() / (double) patchSize + 1))
                / ModConfig.INSTANCE.stackingConfig.cycleSeconds / 2;
    }

    @Override
    protected double getPatchTimer() {
        return 6 / getPatchesPerSecond();
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
        if (ending) {
            scrollDelta *= 1.0 + delta / 3;
            if (scrollDelta == 0) scrollDelta = 0.5;
            scroll += scrollDelta;
        }

        for (Patch patch : patches) {
            if (patch.fallSpeed != 0) {
                patch.update(delta);

                Double stackHeight = stacksHeight.get(patch.x);
                stackHeight = stackHeight == null ? -patchSize / 2 : stackHeight;
                if (patch.y > stackHeight) {
                    patch.y = stackHeight;
                    patch.fallSpeed = 0;
                    stacksHeight.put(patch.x, patch.y - patchSize);
                }
            }
        }
    }

    public static class Config implements ConfigFile<Config> {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.optionalFieldOf("cycle_seconds", 20.0).forGetter(config -> config.cycleSeconds)
        ).apply(instance, Config::new));

        public double cycleSeconds;

        private Config(double cycleSeconds) {
            this.cycleSeconds = cycleSeconds;
        }

        @Override
        public Codec<Config> getCodec() {
            return CODEC;
        }

        @Override
        public void buildScreen(YetAnotherConfigLib.Builder builder) {
            builder.category(ConfigCategory.createBuilder()
                    .name(Text.translatable("config.moderate-loading-screen.type.stacking.title"))
                    .option(Option.createBuilder(Double.class)
                            .name(Text.translatable("config.moderate-loading-screen.type.stacking.option.cycle_seconds"))
                            .tooltip(Text.translatable("config.moderate-loading-screen.type.stacking.option.cycle_seconds.tooltip"))
                            .binding(20.0, () -> cycleSeconds, value -> cycleSeconds = value)
                            .controller(option -> new DoubleSliderController(option, 1.0, 120.0, 1.0))
                            .build())
                    .build());
        }
    }
}

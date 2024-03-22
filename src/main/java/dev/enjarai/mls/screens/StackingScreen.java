package dev.enjarai.mls.screens;

import dev.enjarai.mls.ModerateLoadingScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class StackingScreen extends LoadingScreen {
    protected final HashMap<Double, Double> stacksHeight = new HashMap<>();
    protected final HashMap<Integer, Integer> patchesInColumn = new HashMap<>();
    protected double scroll = 0;
    protected double scrollDelta = 0;
    private final int cycleSeconds = ModerateLoadingScreen.CONFIG.stackingConfig.cycleSeconds();

    public StackingScreen(MinecraftClient client) {
        super(client);
    }

    @Override
    public void createPatch(Identifier texture) {
        Integer column = pickPatchColumn();

        if (column != null) {
            patches.add(new Patch(
                    column * patchSize - (patchSize / 2d),
                    -patchSize - getScreenHeight() + scroll,
                    0, 0, 8 * getPatchesPerSecond(),
                    0, 1.0, texture, patchSize
            ));
            patchesInColumn.compute(column, (k, v) -> (v == null) ? 1 : v + 1);
        }
    }

    protected Integer pickPatchColumn() {
        int width = (int) Math.ceil(getScreenWidth() / (double) patchSize);
        int height = (int) Math.ceil(getScreenHeight() / (double) patchSize);

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
        return ((getScreenWidth() / (double) patchSize + 1) *
                (getScreenHeight() / (double) patchSize + 1))
                / cycleSeconds / 2;
    }

    @Override
    protected double getPatchTimer() {
        return 6 / getPatchesPerSecond();
    }

    @Override
    protected double getOffsetX() {
        return getScreenWidth() % patchSize / 2.0;
    }

    @Override
    protected double getOffsetY() {
        return scroll + getScreenHeight();
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
}

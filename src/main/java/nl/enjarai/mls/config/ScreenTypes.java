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

package nl.enjarai.mls.config;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.enjarai.mls.ModerateLoadingScreen;
import nl.enjarai.mls.screens.LoadingScreen;
import nl.enjarai.mls.screens.SnowFlakesScreen;
import nl.enjarai.mls.screens.StackingScreen;

import java.util.HashMap;
import java.util.Map;

public class ScreenTypes {
    private static final Map<Identifier, ScreenType<?, ?>> SCREENS = new HashMap<>();

    static <C extends ConfigFile<C>, T extends LoadingScreen<C>> ScreenType<C, T> register(Identifier id, ScreenType<C, T> type) {
        SCREENS.put(id, type);
        return type;
    }

    static ScreenType<?, ?> get(Identifier id) {
        return SCREENS.get(id);
    }

    public static final ScreenType<SnowFlakesScreen.Config, SnowFlakesScreen> SNOWFLAKES = register(ModerateLoadingScreen.id("snowflakes"), new ScreenType<>(
            SnowFlakesScreen.Config.CODEC, SnowFlakesScreen::new,
            Text.translatable("config.moderate-loading-screen.type.snowflakes.name")
    ));
    public static final ScreenType<StackingScreen.Config, StackingScreen> STACKING = register(ModerateLoadingScreen.id("stacking"), new ScreenType<>(
            StackingScreen.Config.CODEC, StackingScreen::new,
            Text.translatable("config.moderate-loading-screen.type.stacking.name")
    ));
}

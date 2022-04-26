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

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;

import java.util.ArrayList;

@Config(name = "moderate-loading-screen")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static ModConfig INSTANCE;

    public static void init()
    {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @ConfigEntry.ColorPicker
    @ConfigEntry.Gui.Tooltip
    public int backgroundColor = 0x161616;

    @ConfigEntry.BoundedDiscrete(max = 1)
    @ConfigEntry.Gui.Tooltip
    public float logoOpacity = 1;

    @ConfigEntry.BoundedDiscrete(max = 1)
    @ConfigEntry.Gui.Tooltip
    public float barOpacity = 1;

    @ConfigEntry.Gui.Tooltip
    public boolean showTater = true;

    @ConfigEntry.Gui.Tooltip
    public boolean modsOnlyOnce = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean hideLibraries = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public ArrayList<String> modIdBlacklist = new ArrayList<>();

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 8, max = 128)
    public int iconSize = 48;

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip(count = 3)
    public ScreenTypes screenType = ScreenTypes.SNOWFLAKES;

    @ConfigEntry.Gui.CollapsibleObject()
    public StackingConfig stackingConfig = new StackingConfig();
    public static class StackingConfig {
        @ConfigEntry.Gui.Tooltip(count = 2)
        @ConfigEntry.BoundedDiscrete(min = 1, max = 60)
        public int cycleSeconds = 20;
    }
}

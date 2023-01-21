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

import io.wispforest.owo.config.annotation.*;
import io.wispforest.owo.ui.core.Color;

import java.util.ArrayList;
import java.util.List;

@Modmenu(modId = "moderate-loading-screen")
@Config(name = "moderate-loading-screen", wrapperName = "ModConfig")
public class ModConfigDef {
    @SectionHeader("colors")

    public Color backgroundColor = Color.ofRgb(0x161616);

    @RangeConstraint(min = 0, max = 100)
    public byte logoOpacity = 100;

    @RangeConstraint(min = 0, max = 100)
    public byte barOpacity = 100;

    @SectionHeader("iconOptions")

    public boolean showTater = true;

    public boolean modsOnlyOnce = false;

    public boolean hideLibraries = false;

    public List<String> modIdBlacklist = new ArrayList<>(List.of(
            "fabric-*",
            "fabric"
    ));

    @RangeConstraint(min = 8, max = 128)
    public int iconSize = 48;

    @SectionHeader("screenTypes")

    public ScreenTypes screenType = ScreenTypes.SNOWFLAKES;

    @Nest
    public StackingConfig stackingConfig = new StackingConfig();

    public static class StackingConfig {
        @RangeConstraint(min = 1, max = 60)
        public int cycleSeconds = 20;
    }
}

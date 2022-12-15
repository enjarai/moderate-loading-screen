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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.isxander.yacl.api.YetAnotherConfigLib;
import net.minecraft.util.Identifier;
import nl.enjarai.mls.ModerateLoadingScreen;

import java.util.List;

public class ModConfig implements ConfigFile<ModConfig> {
    public static Codec<ModConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("background_color", 0x161616).forGetter(config -> config.backgroundColor),
            Codec.FLOAT.optionalFieldOf("logo_opacity", 1f).forGetter(config -> config.logoOpacity),
            Codec.FLOAT.optionalFieldOf("bar_opacity", 1f).forGetter(config -> config.barOpacity),
            Codec.BOOL.optionalFieldOf("show_tater", true).forGetter(config -> config.showTater),
            Codec.BOOL.optionalFieldOf("mods_only_once", false).forGetter(config -> config.modsOnlyOnce),
            Codec.BOOL.optionalFieldOf("hide_libraries", false).forGetter(config -> config.hideLibraries),
            Codec.list(Codec.STRING).optionalFieldOf("mod_id_blacklist", List.of(
                    "fabric-*",
                    "fabric"
            )).forGetter(config -> config.modIdBlacklist),
            Codec.INT.optionalFieldOf("icon_size", 48).forGetter(config -> config.iconSize),
            Identifier.CODEC.optionalFieldOf("screen_type",
                    ModerateLoadingScreen.id("snowflakes")
            ).forGetter(config -> config.screenType)
    ).apply(instance, ModConfig::new));
    public static ModConfig INSTANCE;

    public static void init() {

    }

    public ModConfig(int backgroundColor, float logoOpacity, float barOpacity, boolean showTater, boolean modsOnlyOnce, boolean hideLibraries, List<String> modIdBlacklist, int iconSize, Identifier screenType) {
        this.backgroundColor = backgroundColor;
        this.logoOpacity = logoOpacity;
        this.barOpacity = barOpacity;
        this.showTater = showTater;
        this.modsOnlyOnce = modsOnlyOnce;
        this.hideLibraries = hideLibraries;
        this.modIdBlacklist = modIdBlacklist;
        this.iconSize = iconSize;
        this.screenType = screenType;
    }

    public int backgroundColor;
    public float logoOpacity;
    public float barOpacity;
    public boolean showTater;
    public boolean modsOnlyOnce;
    public boolean hideLibraries;
    public List<String> modIdBlacklist;
    public int iconSize;
    public Identifier screenType;

    @Override
    public Codec<ModConfig> getCodec() {
        return CODEC;
    }

    @Override
    public void buildScreen(YetAnotherConfigLib.Builder builder) {

    }
}

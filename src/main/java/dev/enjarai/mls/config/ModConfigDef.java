package dev.enjarai.mls.config;

import io.wispforest.owo.config.annotation.*;
import io.wispforest.owo.ui.core.Color;

import java.util.ArrayList;
import java.util.List;

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

    public Orientation orientation = Orientation.DOWN;

    @Nest
    public StackingConfig stackingConfig = new StackingConfig();

    public static class StackingConfig {
        @RangeConstraint(min = 1, max = 60)
        public int cycleSeconds = 20;
    }
}

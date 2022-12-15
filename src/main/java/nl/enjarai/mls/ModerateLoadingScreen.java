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

package nl.enjarai.mls;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import nl.enjarai.mls.config.ModConfig;
import org.apache.commons.lang3.Validate;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModerateLoadingScreen implements ClientModInitializer {
    public static final String MODID = "moderate-loading-screen";

    @Override
    public void onInitializeClient() {
        ModConfig.init();
        if (ModConfig.INSTANCE.modIdBlacklist.isEmpty()) {
            ModConfig.INSTANCE.modIdBlacklist.addAll(List.of(
                    "fabric-*",
                    "fabric"
            ));
        }
    }

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    // Construct list of mod icons, main principles copied from mod menu
    public static ArrayList<Identifier> compileIconList() {
        ArrayList<String> blacklistRegex = new ArrayList<>();
        for (String i : ModConfig.INSTANCE.modIdBlacklist) {
            blacklistRegex.add(createRegexFromGlob(i));
        }

        ArrayList<Identifier> result = new ArrayList<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata metadata = mod.getMetadata();

            String path = metadata.getIconPath(128).orElse("assets/" + metadata.getId() + "/icon.png");
            NativeImageBackedTexture texture = getIconTexture(mod, path);

            // Ignore blacklisted mods
            for (String i : blacklistRegex) {
                if (metadata.getId().matches(i)) {
                    texture = null;
                    break;
                }
            }

            // Ignore libraries if that option is enabled
            if (ModConfig.INSTANCE.hideLibraries) {
                CustomValue modObj = metadata.getCustomValue("modmenu");
                if (modObj != null && modObj.getAsObject().containsKey("badges")) {
                    for (CustomValue badge : modObj.getAsObject().get("badges").getAsArray()) {
                        if (Objects.equals(badge.getAsString(), "library")) {
                            texture = null;
                            break;
                        }
                    }
                }
            }

            if (texture != null) {
                Identifier iconLocation = new Identifier(MODID, metadata.getId() + "_icon");

                MinecraftClient.getInstance().getTextureManager().registerTexture(iconLocation, texture);
                result.add(iconLocation);
            }
        }

        return result;
    }

    private static NativeImageBackedTexture getIconTexture(ModContainer iconSource, String iconPath) {
        try {
            Path path = iconSource.getPath(iconPath);
            try (InputStream inputStream = Files.newInputStream(path)) {
                NativeImage image = NativeImage.read(Objects.requireNonNull(inputStream));
                Validate.validState(image.getHeight() == image.getWidth(), "Must be square icon");
                return new NativeImageBackedTexture(image);
            }

        } catch (Throwable t) {
            return null;
        }
    }

    // https://stackoverflow.com/questions/45321050/java-string-matching-with-wildcards
    private static String createRegexFromGlob(String glob) {
        StringBuilder out = new StringBuilder("^");
        for(int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '*' -> out.append(".*");
                case '?' -> out.append('.');
                case '.' -> out.append("\\.");
                case '\\' -> out.append("\\\\");
                default -> out.append(c);
            }
        }
        out.append('$');
        return out.toString();
    }
}

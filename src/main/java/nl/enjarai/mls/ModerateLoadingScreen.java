package nl.enjarai.mls;

import io.wispforest.owo.config.ui.ConfigScreen;
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
import nl.enjarai.mls.config.ModConfigScreen;
import org.apache.commons.lang3.Validate;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class ModerateLoadingScreen implements ClientModInitializer {
    public static final String MODID = "moderate-loading-screen";
    public static final ModConfig CONFIG = ModConfig.createAndLoad();

    @Override
    public void onInitializeClient() {
        ConfigScreen.registerProvider(MODID, (parent) -> new ModConfigScreen(id("config"), CONFIG, parent));
    }

    // Construct list of mod icons, main principles copied from mod menu
    public static ArrayList<Identifier> compileIconList() {
        ArrayList<String> blacklistRegex = new ArrayList<>();
        for (String i : CONFIG.modIdBlacklist()) {
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
            if (CONFIG.hideLibraries()) {
                try {
                    CustomValue modObj = metadata.getCustomValue("modmenu");
                    if (modObj != null && modObj.getAsObject().containsKey("badges")) {
                        for (CustomValue badge : modObj.getAsObject().get("badges").getAsArray()) {
                            if (Objects.equals(badge.getAsString(), "library")) {
                                texture = null;
                                break;
                            }
                        }
                    }
                } catch (Throwable ignored) {
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

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }
}

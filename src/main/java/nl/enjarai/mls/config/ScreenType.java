package nl.enjarai.mls.config;

import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import nl.enjarai.mls.screens.LoadingScreen;

import java.util.function.BiFunction;

public record ScreenType<C extends ConfigFile<C>, T extends LoadingScreen<C>>(
        Codec<C> configCodec,
        BiFunction<MinecraftClient, C, T> screenConstructor,
        Text name
) { }

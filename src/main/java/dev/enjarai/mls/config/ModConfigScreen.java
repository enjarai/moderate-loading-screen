package dev.enjarai.mls.config;

import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModConfigScreen extends ConfigScreen {
    public ModConfigScreen(Identifier modelId, ConfigWrapper<?> config, @Nullable Screen parent) {
        super(modelId, config, parent);
    }

    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    @Override
    protected void build(FlowLayout rootComponent) {
        super.build(rootComponent);
        rootComponent.childById(ButtonComponent.class, "preview-button").onPress(button -> {
            this.options.forEach((option, component) -> {
                if (!component.isValid()) return;
                option.set(component.parsedValue());
            });
            MinecraftClient.getInstance().reloadResources();
        });
    }
}

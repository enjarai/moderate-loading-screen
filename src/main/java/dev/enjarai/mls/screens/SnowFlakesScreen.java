package dev.enjarai.mls.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class SnowFlakesScreen extends LoadingScreen {

  public SnowFlakesScreen(MinecraftClient client) {
      super(client);
  }

  @Override
  public void createPatch(Identifier texture) {
      patches.add(new Patch(
              random.nextDouble() * (getScreenWidth() + patchSize),
              -patchSize, 0,
              (random.nextDouble() - 0.5) * 0.6,
              random.nextDouble() * 3.0 + 1.0,
              (random.nextDouble() - 0.5) * 6.0,
              random.nextDouble() / 2 + 0.5,
              texture, patchSize
      ));
  }
}

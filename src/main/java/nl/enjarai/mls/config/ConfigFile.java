package nl.enjarai.mls.config;

import com.mojang.serialization.Codec;
import dev.isxander.yacl.api.YetAnotherConfigLib;

public interface ConfigFile<SELF extends ConfigFile<SELF>> {
    Codec<SELF> getCodec();

    void buildScreen(YetAnotherConfigLib.Builder builder);
}

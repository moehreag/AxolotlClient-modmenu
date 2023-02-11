package io.github.axolotlclient.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import net.minecraft.client.MinecraftClient;

public class AxolotlClientModmenuModmenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            AxolotlClientConfigManager.getInstance().openConfigScreen(AxolotlClientModmenu.MOD_ID);
            return MinecraftClient.getInstance().currentScreen;
        };
    }
}

package com.terraformersmc.modmenu.api;


import com.google.common.collect.ImmutableMap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Map;

/**
 * This interface is part of ModMenu.
 * <a href="https://github.com/TerraformersMC/ModMenu/blob/8284d24d0445d5cc6fc17beb1b56718177c68508/src/main/java/com/terraformersmc/modmenu/api/ModMenuApi.java">Github Link.</a>
 */

public interface ModMenuApi {

    /**
     * Used for creating a {@link Screen} instance of the Mod Menu
     * "Mods" screen
     *
     * @param previous The screen before opening
     * @return A "Mods" Screen
     * <p>
     * Not used/needed here since the mods list screen is managed by the config lib.
     * Still provided to guarantee compatibility with existing mods using this interface.
     */
    static Screen createModsScreen(Screen previous) {
        return null;
    }

    /**
     * Used for creating a {@link Text} just like what would appear
     * on a Mod Menu Mods button
     *
     * @return The text that would be displayed on a Mods button
     */
    static Text createModsButtonText() {
        return null;
    }

    /**
     * Used to construct a new config screen instance when your mod's
     * configuration button is selected on the mod menu screen. The
     * screen instance parameter is the active mod menu screen.
     *
     * @return A factory for constructing config screen instances.
     */
    default ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> null;
    }

    /**
     * Used to provide config screen factories for other mods. This takes second
     * priority to a mod's own config screen factory provider. For example, if
     * mod `xyz` supplies a config screen factory, mod `abc` providing a config
     * screen to `xyz` will be pointless, as the one provided by `xyz` will be
     * used.
     * <p>
     * This method is NOT meant to be used to add a config screen factory to
     * your own mod.
     *
     * @return a map of mod ids to screen factories.
     */
    default Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return ImmutableMap.of();
    }
}

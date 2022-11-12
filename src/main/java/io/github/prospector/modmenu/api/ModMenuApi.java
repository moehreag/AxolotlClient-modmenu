package io.github.prospector.modmenu.api;

import java.util.function.Function;

import net.minecraft.client.gui.screen.Screen;

/**
 *
 * @deprecated Only provided for compatibility with the old versions of ModMenu that are still using this in 1.8.9.
 * <p>
 * Use {@link com.terraformersmc.modmenu.api.ModMenuApi} instead.
 * </p>
 */

@Deprecated()
public interface ModMenuApi extends com.terraformersmc.modmenu.api.ModMenuApi {
    /**
     * Used to determine the owner of this API implementation.
     * Will be deprecated and removed once Fabric has support
     * for providing ownership information about entry points.
     */
    String getModId();

    /**
     * Used to construct a new config assembleScreen instance when your mod's
     * configuration button is selected on the mod menu assembleScreen. The
     * assembleScreen instance parameter is the active mod menu assembleScreen.
     *
     * @return A factory function for constructing config assembleScreen instances.
     */
    default Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return screen -> null;
    }
}
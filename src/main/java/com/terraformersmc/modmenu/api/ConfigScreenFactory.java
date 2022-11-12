package com.terraformersmc.modmenu.api;

import net.minecraft.client.gui.screen.Screen;

/**
 * This interface is part of ModMenu.
 * <a href="https://github.com/TerraformersMC/ModMenu/blob/8284d24d0445d5cc6fc17beb1b56718177c68508/src/main/java/com/terraformersmc/modmenu/api/ConfigScreenFactory.java">Github Link.</a>
 */

@FunctionalInterface
public interface ConfigScreenFactory<S extends Screen> {
    S create(Screen parent);
}

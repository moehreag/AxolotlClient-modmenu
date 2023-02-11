package io.github.axolotlclient.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.AxolotlClientConfig.options.GenericOption;
import io.github.axolotlclient.AxolotlClientConfig.screen.widgets.GenericOptionWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;

public class ModOption extends GenericOption {

    private final String id;

    public ModOption(String name, String label, OnClick onClick, String modid) {
        super(name, label, onClick);
        this.id = modid;
    }

    @Override
    public ButtonWidget getWidget(int x, int y, int width, int height){
        return new Widget(x, y, width, height, this, id);
    }

    public static class Widget extends GenericOptionWidget {

        private final String modid;

        public Widget(int x, int y, int width, int height, GenericOption option, String id) {
            super(x, y, width, height, option);
            this.modid = id;
        }

        @Override
        public void render(MinecraftClient client, int mouseX, int mouseY) {
            super.render(client, mouseX, mouseY);
            GlStateManager.disableBlend();
            GlStateManager.enableTexture();
            GlStateManager.color3f(1, 1, 1);
            MinecraftClient.getInstance().getTextureManager().bindTexture(AxolotlClientModmenu.iconCache.get(modid));
            drawTexture(x-150, y+2, 0, 0, 16, height-4, 16, height-4);
        }
    }
}

package io.github.axolotlclient.modmenu;

import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.OSUtil;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IdentifibleBooleanConsumer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

import java.net.URI;
import java.util.function.Function;

public class ModScreen extends Screen implements IdentifibleBooleanConsumer {

    private final ModContainer container;
    private final Function<Screen, ? extends Screen> factory;

    private Identifier icon;
    private final Screen parent;

    private ModInfoList list;

    private final Rectangle iconPos = new Rectangle(50, 50, 100, 100);

    public ModScreen(ModContainer container, Function<Screen, ? extends Screen> factory, Screen parent){
        this.container = container;
        this.factory = factory;

        this.parent = parent;
    }

    @Override
    public void init() {

        iconPos.width = width/5;
        iconPos.x = width/4 - iconPos.width/2;
        iconPos.y = iconPos.x - iconPos.x/4;
        iconPos.height = width/5;

        if(height - (iconPos.y + iconPos.height + 65 + 40) < 25  && factory != null){
            iconPos.width = 50;
            iconPos.height = 50;

        }

        if(iconPos.width > 100){
            iconPos.width = 100;
            iconPos.height = 100;
        }

        if(factory != null) {
            this.buttons.add(new ButtonWidget(1, iconPos.x - iconPos.width/2, iconPos.y + iconPos.height + 15, iconPos.width*2, 20, I18n.translate("modmenu.configure")));
        }

        ButtonWidget homepage = new ButtonWidget(2, iconPos.x - iconPos.width/2, iconPos.y + iconPos.height + (factory != null ? 40 : 15), iconPos.width*2, 20, I18n.translate("modmenu.homepage"));
        homepage.active = container.getMetadata().getContact().get("homepage").isPresent();
        this.buttons.add(homepage);

        if(container.getMetadata().getContact().get("sources").isPresent()) {
            ButtonWidget sources = new ButtonWidget(3, iconPos.x - iconPos.width / 2, iconPos.y + iconPos.height + (factory != null ? 65 : 40), iconPos.width * 2, 20, I18n.translate("modmenu.sources"));
            //sources.active = container.getMetadata().getContact().get("sources").isPresent();
            this.buttons.add(sources);
        } else {
            ButtonWidget issues = new ButtonWidget(4, iconPos.x - iconPos.width / 2, iconPos.y + iconPos.height + (factory != null ? 65 : 40), iconPos.width * 2, 20, I18n.translate("modmenu.issues"));
            this.buttons.add(issues);
        }

        this.buttons.add(new ButtonWidget(0, this.width/2-100, this.height-40, 200, 20, I18n.translate("back")));

        list = ModInfoList.create(this.width/2 + 10, 50, width - width/2 - 20, height-100, container);

        int diff = iconPos.y + iconPos.height + (factory != null ? 65 :  40) + 20 - list.getYEnd();
        if(diff > 0){
            list.setYEnd(list.getYEnd()+diff);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {

        renderBackground();

        list.render(mouseX, mouseY, tickDelta);
        drawCenteredString(textRenderer, container.getMetadata().getName(), width/2, 25, -1);

        bindIconTexture();
        drawTexture(iconPos.x, iconPos.y, 0, 0, iconPos.width, iconPos.height, iconPos.width, iconPos.height);

        super.render(mouseX, mouseY, tickDelta);
    }

    public void bindIconTexture() {
        if (this.icon == null) {
            icon = AxolotlClientModmenu.iconCache.get(container.getMetadata().getId());
        }
        this.client.getTextureManager().bindTexture(this.icon);
    }

    @Override
    public void handleMouse() {
        list.handleMouse();
        super.handleMouse();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id){
            case 0:
                MinecraftClient.getInstance().openScreen(parent);
                break;
            case 1:
                MinecraftClient.getInstance().openScreen(factory.apply(this));
                break;
            case 2:
                    MinecraftClient.getInstance().openScreen(
                            new ConfirmChatLinkScreen(this, container.getMetadata().getContact().get("homepage").orElseThrow(RuntimeException::new),
                                    72834, true));
                    break;
            case 3:
                MinecraftClient.getInstance().openScreen(
                        new ConfirmChatLinkScreen(this, container.getMetadata().getContact().get("sources").orElseThrow(RuntimeException::new),
                                342561, true));
                break;
            case 4:
                MinecraftClient.getInstance().openScreen(
                        new ConfirmChatLinkScreen(this, container.getMetadata().getContact().get("issues").orElseThrow(RuntimeException::new),
                                342561, true));
        }
    }

    @Override
    public void confirmResult(boolean b, int id) {
        if (id == 72834) {
            if (b) {
                OSUtil.getOS().open(URI.create(container.getMetadata().getContact().get("homepage").orElseThrow(RuntimeException::new)));
            }

            this.client.openScreen(this);
        } else if (id==342561) {
            if (b) {
                OSUtil.getOS().open(URI.create(container.getMetadata().getContact().get("sources").orElseThrow(RuntimeException::new)));
            }

            this.client.openScreen(this);
        }
    }
}

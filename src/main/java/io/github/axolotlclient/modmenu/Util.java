package io.github.axolotlclient.modmenu;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.Validate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Util {


    static String getName(ModContainer container){
        return container.getMetadata().getName();
    }

    public static String getDescription(ModContainer container){
        if ("minecraft".equals(container.getMetadata().getId())) {
            return "The base game.";
        } else if ("java".equals(container.getMetadata().getId())) {
            return I18n.translate("modmenu.vendor") +" "+ System.getProperty("java.vendor");
        }
        return container.getMetadata().getDescription();
    }

    public static List<String> getContributors(ModContainer container, int width) {

        TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
        List<String> list = new LinkedList<>();

        list.add(I18n.translate("modmenu.contributorsTitle"));

        if(container.getMetadata().getId().equals("minecraft")){
            list.add("Mojang Studios");
            return list;
        }

        container.getMetadata().getAuthors().forEach(person -> {
            if(renderer.getStringWidth("  "+person.getName() + " "+I18n.translate("modmenu.author")) < width){
                list.add("  "+person.getName()+" "+I18n.translate("modmenu.author"));
            } else {
                list.add("  "+person.getName());
                list.add("       "+I18n.translate("modmenu.author"));
            }
        });

        if(list.size()>1) {
            list.add("");
        }

        container.getMetadata().getContributors().forEach(person -> {
            if(renderer.getStringWidth(" "+person.getName() + " "+I18n.translate("modmenu.contributor")) < width){
                list.add("  "+person.getName()+" "+I18n.translate("modmenu.contributor"));
            } else {
                list.add("  "+person.getName());
                list.add("        "+I18n.translate("modmenu.contributor"));
            }
        });

        return list;
    }

    public static void createIcon(ModContainer container) {
        ModMetadata metadata = container.getMetadata();

        try {
            Path path = container.findPath(metadata.getIconPath(64 * MinecraftClient.getInstance().options.guiScale)
                            .orElse("assets/" + metadata.getId() + "/icon.png"))
                    .orElse(new File("assets/" + container.getMetadata().getId() + "/icon.png").toPath());

            if(AxolotlClientModmenu.iconCache.containsKey(container.getMetadata().getId())){
                AxolotlClientModmenu.iconCache.get(container.getMetadata().getId());
                return;
            }

            if (!Files.exists(path)) {
                ModContainer modMenu = FabricLoader.getInstance()
                        .getModContainer(AxolotlClientModmenu.MOD_ID).orElseThrow(IllegalAccessError::new);
                if (AxolotlClientModmenu.fabricModPredicate.test(metadata.getId())) {
                    path = modMenu.findPath("assets/" + AxolotlClientModmenu.MOD_ID + "/fabric_icon.png").orElseThrow(UnknownError::new);
                } else if (metadata.getId().equals("minecraft")) {
                    path = modMenu.findPath("assets/" + AxolotlClientModmenu.MOD_ID + "/mc_icon.png").orElseThrow(UnknownError::new);
                } else {
                    path = modMenu.findPath("assets/" + AxolotlClientModmenu.MOD_ID + "/unknown_icon.png").orElseThrow(UnknownError::new);;
                }
            }

            try (InputStream inputStream = Files.newInputStream(path)) {
                BufferedImage image = TextureUtil.create(Objects.requireNonNull(inputStream));
                Validate.validState(image.getHeight() == image.getWidth(), "Must be square icon");
                NativeImageBackedTexture tex = new NativeImageBackedTexture(image);
                Identifier id = new Identifier("axolotlclient-modmenu", container.getMetadata().getId() + "_icon");
                MinecraftClient.getInstance().getTextureManager().loadTexture(id, tex);
                AxolotlClientModmenu.iconCache.put(container.getMetadata().getId(), id);
            }

        } catch (Throwable t) {
            AxolotlClientModmenu.LOGGER.error("Invalid icon for mod {}", container.getMetadata().getName(), t);
        }
    }
}

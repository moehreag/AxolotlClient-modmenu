package io.github.axolotlclient.modmenu;

import io.github.axolotlclient.modules.hud.util.Rectangle;
import io.github.axolotlclient.util.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Texts;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ModInfoList extends EntryListWidget {

    private Rectangle rect;
    private final List<Entry> entries = new ArrayList<>();

    public ModInfoList(MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
        super(client, width, height, top, bottom, entryHeight);
    }

    public void setYEnd(int end){
        yEnd = end;
        height = end - yStart;
        rect.height = height;
    }

    public int getYEnd(){
        return yEnd;
    }

    public void add(Entry t){
        entries.add(t);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.xEnd-10;
    }

    @Override
    protected void renderList(int x, int y, int mouseX, int mouseY) {
        Util.applyScissor(rect);
        super.renderList(x, y, mouseX, mouseY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static ModInfoList create(int x, int y, int width, int height, ModContainer container){
        ModInfoList list = new ModInfoList(MinecraftClient.getInstance(), width, height, 0, 0, 10);

        list.xStart = x;
        list.xEnd = x+width;
        list.yStart = y;
        list.yEnd = y+height;
        list.rect = new Rectangle(x, y, width, height);

        ModEntry.setX(x+5);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        List<Entry> entries = new ArrayList<>();
        if(!io.github.axolotlclient.modmenu.Util.getDescription(container).isEmpty()) {
            Texts.wrapLines(new LiteralText(io.github.axolotlclient.modmenu.Util.getDescription(container)), width - 25, textRenderer, false, false).forEach(text -> entries.add(new ModEntry(text)));
        }
        if(!entries.isEmpty()) {
            entries.add(ModEntry.createEmpty());
        }

        List<String> badges = new ArrayList<>();

        if(container.getMetadata().getEnvironment().name().equals(EnvType.CLIENT.name())){
            badges.add("client");
        }
        if(container.getMetadata().containsCustomValue("modmenu") && container.getMetadata().getCustomValue("modmenu").getAsObject().get("badges").getAsArray() != null) {
            container.getMetadata().getCustomValue("modmenu").getAsObject().get("badges").getAsArray().forEach(value -> {
                if(!badges.contains(value.getAsString())){
                    badges.add(value.getAsString());
                }
            });
        }

        if(!badges.isEmpty()) {
            entries.add(new BadgeEntry(badges));
            entries.add(ModEntry.createEmpty());
        }

        entries.add(new ModEntry("Version: "+container.getMetadata().getVersion()));
        entries.add(ModEntry.createEmpty());
        entries.add(new ModEntry("Mod ID: "+container.getMetadata().getId()));

        entries.add(ModEntry.createEmpty());
        entries.add(ModEntry.createEmpty());

        if(!container.getMetadata().getAuthors().isEmpty() && !container.getMetadata().getContributors().isEmpty()) {
            io.github.axolotlclient.modmenu.Util.getContributors(container, width-50).forEach(string -> entries.add(new ModEntry(string)));
            entries.add(ModEntry.createEmpty());
            entries.add(ModEntry.createEmpty());
        }

        if(!container.getMetadata().getLicense().isEmpty()) {
            entries.add(new ModEntry(I18n.translate("modmenu.license")));
            container.getMetadata().getLicense().forEach(s -> entries.add(new ModEntry("        " + s)));
        }

        entries.forEach(list::add);
        return list;
    }

    @Override
    public Entry getEntry(int index) {
        return entries.get(index);
    }

    @Override
    protected int getEntryCount() {
        return entries.size();
    }

    private static class ModEntry implements Entry {
        private final String content;
        protected static int x;

        public ModEntry(String t){
            content = t;
        }

        public ModEntry(Text t){
            this(t.asFormattedString());
        }

        @Override
        public void updatePosition(int index, int x, int y) {

        }

        @Override
        public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(content, ModEntry.x, y, -1);
        }

        @Override
        public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
            return false;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int button, int x, int y) {

        }

        public static void setX(int x){
            ModEntry.x = x;
        }

        public static ModEntry createEmpty(){
            return new ModEntry((String) null){
                @Override
                public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
                }
            };
        }
    }

    private static class BadgeEntry extends ModEntry {

        private boolean client;
        private boolean library;
        private boolean deprecated;

        private final int x;

        public BadgeEntry(List<String> badges) {
            super((String) null);
            for(String s:badges){
                if(s.equalsIgnoreCase("client")){
                    client = true;
                }
                if(s.equalsIgnoreCase("library")){
                    library = true;
                }
                if(s.equalsIgnoreCase("deprecated")){
                    deprecated = true;
                }
            }
            x = ModEntry.x;
        }

        @Override
        public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
            int currentX = this.x;
            if(client){
                currentX += drawBadge(currentX, y, I18n.translate("modmenu.client"), 0xff2b4b7c, 0xff0e2a55);
            }
            if(library){
                currentX += drawBadge(currentX, y, I18n.translate("modmenu.library"), 0xff107454, 0xff093929);
            }
            if(deprecated){
                drawBadge(currentX, y, I18n.translate("modmenu.deprecated"), 0xff841426, 0xff530C17);
            }

        }

        private int drawBadge(int x, int y, String text, int outline, int fill){
            int width = MinecraftClient.getInstance().textRenderer.getStringWidth(text) + 6;
            DrawableHelper.fill(x + 1, y - 1, x + width, y, outline);
            DrawableHelper.fill(x, y, x + 1, y + MinecraftClient.getInstance().textRenderer.fontHeight, outline);
            DrawableHelper.fill(x + 1, y + 1 + MinecraftClient.getInstance().textRenderer.fontHeight - 1, x + width, y + MinecraftClient.getInstance().textRenderer.fontHeight + 1, outline);
            DrawableHelper.fill(x + width, y, x + width + 1, y + MinecraftClient.getInstance().textRenderer.fontHeight, outline);
            DrawableHelper.fill(x + 1, y, x + width, y + MinecraftClient.getInstance().textRenderer.fontHeight, fill);

            MinecraftClient.getInstance().textRenderer.draw(text, x+1 + (width - MinecraftClient.getInstance().textRenderer.getStringWidth(text))/2, y+1, -1);

            return width + 2;
        }
    }
}
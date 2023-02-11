package io.github.axolotlclient.modmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.axolotlclient.modules.hud.util.Rectangle;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModOrigin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Texts;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
        io.github.axolotlclient.util.Util.applyScissor(rect.x, rect.y, rect.width, rect.height);
        super.renderList(x, y, mouseX, mouseY);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta){
        if (this.visible) {

            this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            this.client.getTextureManager().bindTexture(DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(this.xStart, yEnd, 0.0)
                    .texture(0.0, (float)yEnd / 32.0F)
                    .color(32, 32, 32, 255)
                    .next();
            bufferBuilder.vertex(this.xStart + this.width, yEnd, 0.0)
                    .texture((float)this.width / 32.0F, (float)yEnd / 32.0F)
                    .color(32, 32, 32, 255)
                    .next();
            bufferBuilder.vertex(this.xStart + this.width, yStart, 0.0)
                    .texture((float)this.width / 32.0F, (float)yStart / 32.0F)
                    .color(32, 32, 32, 255)
                    .next();
            bufferBuilder.vertex(this.xStart, yStart, 0.0)
                    .texture(0.0, (float)yStart / 32.0F)
                    .color(32, 32, 32, 255)
                    .next();
            tessellator.draw();

            GlStateManager.enableDepthTest();
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0, 0, 1F);
            this.renderBackground();
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            int i = this.getScrollbarPosition();
            int j = i + 6;
            this.capYPosition();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            int k = this.xStart + this.width / 2 - 125;
            int l = this.yStart + 4 - (int)this.scrollAmount;

            GlStateManager.enableTexture();

            this.renderList(k, l, mouseX, mouseY);

            int m = 4;
            GlStateManager.disableDepthTest();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableAlphaTest();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(this.xStart, this.yStart + m, 0.0).texture(0.0, 1.0).color(0, 0, 0, 0).next();
            bufferBuilder.vertex(this.xEnd, this.yStart + m, 0.0).texture(1.0, 1.0).color(0, 0, 0, 0).next();
            bufferBuilder.vertex(this.xEnd, this.yStart, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.xStart, this.yStart, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
            Tessellator.getInstance().draw();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(this.xStart, this.yEnd, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.xEnd, this.yEnd, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex(this.xEnd, this.yEnd - m, 0.0).texture(1.0, 0.0).color(0, 0, 0, 0).next();
            bufferBuilder.vertex(this.xStart, this.yEnd - m, 0.0).texture(0.0, 0.0).color(0, 0, 0, 0).next();
            Tessellator.getInstance().draw();

            int n = this.getMaxScroll();
            if (n > 0) {
                int o = (this.yEnd - this.yStart) * (this.yEnd - this.yStart) / this.getMaxPosition();
                o = MathHelper.clamp(o, 32, this.yEnd - this.yStart - 8);
                int p = (int)this.scrollAmount * (this.yEnd - this.yStart - o) / n + this.yStart;
                if (p < this.yStart) {
                    p = this.yStart;
                }
                GlStateManager.disableTexture();

                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(i, this.yEnd, 0.0).texture(0.0, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(j, this.yEnd, 0.0).texture(1.0, 1.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(j, this.yStart, 0.0).texture(1.0, 0.0).color(0, 0, 0, 255).next();
                bufferBuilder.vertex(i, this.yStart, 0.0).texture(0.0, 0.0).color(0, 0, 0, 255).next();
                tessellator.draw();
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(i, (p + o), 0.0).texture(0.0, 1.0).color(128, 128, 128, 255).next();
                bufferBuilder.vertex(j, (p + o), 0.0).texture(1.0, 1.0).color(128, 128, 128, 255).next();
                bufferBuilder.vertex(j, p, 0.0).texture(1.0, 0.0).color(128, 128, 128, 255).next();
                bufferBuilder.vertex(i, p, 0.0).texture(0.0, 0.0).color(128, 128, 128, 255).next();
                tessellator.draw();
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(i, (p + o - 1), 0.0).texture(0.0, 1.0).color(192, 192, 192, 255).next();
                bufferBuilder.vertex((j - 1), (p + o - 1), 0.0).texture(1.0, 1.0).color(192, 192, 192, 255).next();
                bufferBuilder.vertex((j - 1), p, 0.0).texture(1.0, 0.0).color(192, 192, 192, 255).next();
                bufferBuilder.vertex(i, p, 0.0).texture(0.0, 0.0).color(192, 192, 192, 255).next();
                tessellator.draw();
            }

            GlStateManager.enableTexture();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlphaTest();
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
        }
    }

    public static ModInfoList create(int x, int y, int width, int height, ModContainer container){
        ModInfoList list = new ModInfoList(MinecraftClient.getInstance(), width, height, 0, 0, 10);

        list.xStart = x;
        list.xEnd = x+width;
        list.yStart = y;
        list.renderHeader = false;
        list.yEnd = y+height;
        list.rect = new Rectangle(x, y, width, height);

        ModEntry.setX(x+5);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        List<Entry> entries = new ArrayList<>();
        if(!Util.getDescription(container).isEmpty()) {
            Texts.wrapLines(new LiteralText(Util.getDescription(container)), width - 25, textRenderer, false, false).forEach(text -> entries.add(new ModEntry(text)));
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
            entries.add(ModEntry.createEmpty());
        } else if(container.getMetadata().getId().equals("minecraft")){
            entries.add(new ModEntry(I18n.translate("modmenu.license")));
            entries.add(new ModEntry("        " + "Minecraft EULA"));
            entries.add(ModEntry.createEmpty());
        }


        AtomicReference<ModOrigin> origin = new AtomicReference<>(container.getOrigin());



        if(!origin.get().getKind().equals(ModOrigin.Kind.UNKNOWN)) {

            entries.add(new ModEntry(I18n.translate("modmenu.mod_location")));

            while (origin.get().getKind().equals(ModOrigin.Kind.NESTED)){
                container.getContainingMod().ifPresent(container1 -> origin.set(container1.getOrigin()));
            }

            List<String> locations = origin.get().getPaths().stream().map(path -> path.toFile().getAbsolutePath()).collect(Collectors.toList());
            locations.forEach(s ->
                    Util.wrapLines(s,
                                    width - 25 - MinecraftClient.getInstance().textRenderer.getStringWidth("        "),
                                    "/")
                            .forEach(text -> entries.add(new ModEntry("        " + text))));
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

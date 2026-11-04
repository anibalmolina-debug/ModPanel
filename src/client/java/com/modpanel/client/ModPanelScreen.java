package com.modpanel.client;

import com.modpanel.ModPanel;
import com.modpanel.core.ModRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Collectors;

public class ModPanelScreen extends Screen {
    private EditBox searchBox;
    private String query = "";
    private ModList modList;

    public ModPanelScreen() {
        super(Component.literal("ModPanel"));
    }

    @Override
    protected void init() {
        super.init();
        searchBox = new EditBox(this.font, 20, 20, this.width - 40, 18, Component.literal("Search"));
        searchBox.setResponder(s -> { query = s; if (modList != null) modList.refresh(); });
        addRenderableWidget(searchBox);

        modList = new ModList(this.minecraft, this.width, this.height, 44, this.height - 36);
        modList.refresh();
        addRenderableWidget(modList);

        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose())
                .bounds(this.width / 2 - 50, this.height - 28, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "ModPanel", this.width / 2, 6, 0xFFFFFF);
    }

    class ModList extends ObjectSelectionList<ModList.Entry> {
        ModList(Minecraft mc, int width, int height, int top, int bottom) {
            super(mc, width, height, top, bottom);
        }

        void refresh() {
            clearEntries();
            List<ModRegistry.ModInfo> mods = ModPanel.MOD_REGISTRY.getLoadedMods().stream()
                    .filter(m -> query.isBlank() || m.name().toLowerCase().contains(query.toLowerCase()) || m.id().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            for (ModRegistry.ModInfo mod : mods) {
                addEntry(new Entry(mod));
            }
        }

        @Override
        public int getRowWidth() {
            return ModPanelScreen.this.width - 40;
        }

        class Entry extends ObjectSelectionList.Entry<Entry> {
            private final ModRegistry.ModInfo mod;

            Entry(ModRegistry.ModInfo mod) { this.mod = mod; }

            @Override
            public void render(GuiGraphics g, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
                List<com.modpanel.model.ConfigSource> sources = ModPanel.DISCOVERY.discoverMod(mod.id(), mod.version());
                boolean has = !sources.isEmpty();
                String status = has ? sources.get(0).getStatus().toString().toLowerCase() : "unsupported";
                String label = mod.name() + " (" + mod.version() + ") - " + status;
                g.drawString(ModPanelScreen.this.font, label, left + 4, top + 6, has ? 0xFFFFFF : 0x888888);
            }

            @Override
            public Component getNarration() {
                return Component.literal(mod.name());
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                List<com.modpanel.model.ConfigSource> sources = ModPanel.DISCOVERY.discoverMod(mod.id(), mod.version());
                if (!sources.isEmpty()) {
                    minecraft.setScreen(new GeneratedConfigScreen(mod.id()));
                }
                return true;
            }
        }
    }
}
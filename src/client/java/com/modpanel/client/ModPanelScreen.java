package com.modpanel.client;

import com.modpanel.ModPanel;
import com.modpanel.core.ModRegistry;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.stream.Collectors;

public class ModPanelScreen extends Screen {
    private String query = "";
    private EditBox searchBox;

    public ModPanelScreen() {
        super(Component.literal("ModPanel"));
    }

    @Override
    protected void init() {
        super.init();
        searchBox = new EditBox(this.font, 20, 28, this.width - 40, 16, Component.literal("Search"));
        searchBox.setValue(query);
        searchBox.setResponder(s -> { query = s; rebuild(); });
        addRenderableWidget(searchBox);

        rebuild();
    }

    private void rebuild() {
        // remove old buttons except searchBox? Simpler: clear children and re-add
        this.clearWidgets();
        addRenderableWidget(searchBox);
        int y = 50;
        List<ModRegistry.ModInfo> mods = ModPanel.MOD_REGISTRY.getLoadedMods().stream()
                .filter(m -> query.isBlank() || m.name().toLowerCase().contains(query.toLowerCase()) || m.id().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        for (ModRegistry.ModInfo mod : mods) {
            List<com.modpanel.model.ConfigSource> sources = ModPanel.DISCOVERY.discoverMod(mod.id(), mod.version());
            boolean hasSource = !sources.isEmpty();
            String status = hasSource ? sources.get(0).getStatus().toString() : "unsupported";
            String label = mod.name() + " (" + mod.version() + ") - " + status;
            Button btn = Button.builder(Component.literal(label), b -> {
                if (hasSource) {
                    minecraft.setScreen(new GeneratedConfigScreen(mod.id()));
                }
            }).bounds(20, y, this.width - 40, 20).build();
            btn.active = hasSource;
            addRenderableWidget(btn);
            y += 24;
            if (y > this.height - 40) break;
        }
        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose())
                .bounds(this.width / 2 - 50, this.height - 30, 100, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, "ModPanel", this.width / 2, 10, 0xFFFFFF);
    }
}
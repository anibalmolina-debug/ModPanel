package com.modpanel.client;

import com.modpanel.ModPanel;
import com.modpanel.adapter.ConfigAdapter;
import com.modpanel.core.ApplyCoordinator;
import com.modpanel.core.ChangeSet;
import com.modpanel.model.ConfigEntry;
import com.modpanel.model.ConfigSnapshot;
import com.modpanel.model.ConfigSource;
import com.modpanel.model.SettingType;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GeneratedConfigScreen extends Screen {
    private final String modId;
    private boolean delegated = false;

    public GeneratedConfigScreen(String modId) {
        super(Component.literal("ModPanel - " + modId));
        this.modId = modId;
    }

    @Override
    protected void init() {
        super.init();
        if (delegated) return;
        delegated = true;

        List<ConfigSource> discovered = ModPanel.DISCOVERY.discoverMod(modId, ModPanel.MOD_REGISTRY.getVersion(modId));
        final ConfigSource source = discovered.isEmpty() ? null : discovered.get(0);
        if (source == null) {
            minecraft.setScreen(new ModPanelScreen());
            return;
        }

        ConfigAdapter selected = ModPanel.ADAPTER_MANAGER.select(new com.modpanel.adapter.AdapterContext(modId, ModPanel.MOD_REGISTRY.getVersion(modId), "1.21.1"));
        final ConfigAdapter adapter = selected == null ? new com.modpanel.adapter.LithiumAdapter() : selected;

        final ConfigSnapshot snapshot = adapter.read(source);
        ModPanel.CONFIG_REGISTRY.register(snapshot);

        final ChangeSet changeSet = new ChangeSet(source.getSourceId());

        ConfigBuilder builder = ConfigBuilder.create();
        builder.setParentScreen(this);
        builder.setTitle(Component.literal(modId + " settings"));
        if (source.getCapabilities().write()) {
            builder.setSavingRunnable(() -> {
                new ApplyCoordinator().apply(adapter, source, changeSet, snapshot.entries());
            });
        } else {
            builder.setEditable(false);
        }

        // group entries by category
        Map<String, List<ConfigEntry>> grouped = new LinkedHashMap<>();
        for (ConfigEntry e : snapshot.entries()) {
            String cat = e.getMetadata().category();
            if (cat == null || cat.isBlank()) cat = "Settings";
            grouped.computeIfAbsent(cat, k -> new ArrayList<>()).add(e);
        }

        ConfigEntryBuilder eb = builder.entryBuilder();
        for (Map.Entry<String, List<ConfigEntry>> g : grouped.entrySet()) {
            ConfigCategory category = builder.getOrCreateCategory(Component.literal(g.getKey()));
            for (ConfigEntry entry : g.getValue()) {
                addControl(category, eb, entry, changeSet, adapter);
            }
            // restart notice
            if (entryRestartRequired(g.getValue())) {
                category.addEntry(eb.startTextDescription(Component.literal("Restart required for changes")).build());
            }
        }

        Screen built = builder.build();
        minecraft.setScreen(built);
    }

    private boolean entryRestartRequired(List<ConfigEntry> entries) {
        for (ConfigEntry e : entries) if (e.getMetadata().restartRequired()) return true;
        return false;
    }

    private void addControl(ConfigCategory category, ConfigEntryBuilder eb, ConfigEntry entry, ChangeSet changeSet, ConfigAdapter adapter) {
        String label = entry.getMetadata().label() != null ? entry.getMetadata().label() : entry.getKey();
        Component comp = Component.literal(label);
        switch (entry.getType()) {
            case BOOLEAN: {
                boolean val = Boolean.TRUE.equals(entry.getCurrentValue());
                category.addEntry(eb.startBooleanToggle(comp, val)
                        .setDefaultValue(() -> Boolean.TRUE.equals(entry.getDefaultValue()))
                        .setErrorSupplier(v -> adapter.validate(entry, v).valid() ? java.util.Optional.empty() : java.util.Optional.of(Component.literal(adapter.validate(entry, v).message())))
                        .setErrorSupplier(v -> adapter.validate(entry, v).valid() ? java.util.Optional.empty() : java.util.Optional.of(Component.literal(adapter.validate(entry, v).message())))
                        .setSaveConsumer(v -> { entry.setCurrentValue(v); changeSet.stage(entry, v); })
                        .build());
                break;
            }
            case INTEGER: {
                int val = entry.getCurrentValue() instanceof Number ? ((Number) entry.getCurrentValue()).intValue() : 0;
                if (entry.getMinValue() instanceof Number && entry.getMaxValue() instanceof Number) {
                    category.addEntry(eb.startIntSlider(comp,
                            val,
                            ((Number) entry.getMinValue()).intValue(),
                            ((Number) entry.getMaxValue()).intValue())
                            .setDefaultValue(() -> entry.getDefaultValue() instanceof Number ? ((Number) entry.getDefaultValue()).intValue() : val)
                            .setErrorSupplier(v -> adapter.validate(entry, v).valid() ? java.util.Optional.empty() : java.util.Optional.of(Component.literal(adapter.validate(entry, v).message())))
                        .setSaveConsumer(v -> { entry.setCurrentValue(v); changeSet.stage(entry, v); })
                            .build());
                } else {
                    category.addEntry(eb.startIntField(comp, val)
                            .setDefaultValue(() -> entry.getDefaultValue() instanceof Number ? ((Number) entry.getDefaultValue()).intValue() : val)
                            .setErrorSupplier(v -> adapter.validate(entry, v).valid() ? java.util.Optional.empty() : java.util.Optional.of(Component.literal(adapter.validate(entry, v).message())))
                        .setSaveConsumer(v -> { entry.setCurrentValue(v); changeSet.stage(entry, v); })
                            .build());
                }
                break;
            }
            case DECIMAL: {
                double val = entry.getCurrentValue() instanceof Number ? ((Number) entry.getCurrentValue()).doubleValue() : 0.0;
                if (entry.getMinValue() instanceof Number && entry.getMaxValue() instanceof Number) {
                    category.addEntry(eb.startDoubleField(comp, val)
                            .setDefaultValue(() -> entry.getDefaultValue() instanceof Number ? ((Number) entry.getDefaultValue()).doubleValue() : val)
                            .setErrorSupplier(v -> adapter.validate(entry, v).valid() ? java.util.Optional.empty() : java.util.Optional.of(Component.literal(adapter.validate(entry, v).message())))
                        .setSaveConsumer(v -> { entry.setCurrentValue(v); changeSet.stage(entry, v); })
                            .build());
                } else {
                    category.addEntry(eb.startDoubleField(comp, val)
                            .setDefaultValue(() -> entry.getDefaultValue() instanceof Number ? ((Number) entry.getDefaultValue()).doubleValue() : val)
                            .setErrorSupplier(v -> adapter.validate(entry, v).valid() ? java.util.Optional.empty() : java.util.Optional.of(Component.literal(adapter.validate(entry, v).message())))
                        .setSaveConsumer(v -> { entry.setCurrentValue(v); changeSet.stage(entry, v); })
                            .build());
                }
                break;
            }
            case ENUM:
            case STRING: {
                if (entry.getChoices() != null && !entry.getChoices().isEmpty()) {
                    // use selector with choices as strings
                    String[] opts = entry.getChoices().stream().map(String::valueOf).toArray(String[]::new);
                    String cur = String.valueOf(entry.getCurrentValue());
                    category.addEntry(eb.startSelector(comp, opts, cur)
                            .setErrorSupplier(v -> adapter.validate(entry, v).valid() ? java.util.Optional.empty() : java.util.Optional.of(Component.literal(adapter.validate(entry, v).message())))
                        .setSaveConsumer(v -> { entry.setCurrentValue(v); changeSet.stage(entry, v); })
                            .build());
                } else {
                    String val = String.valueOf(entry.getCurrentValue());
                    category.addEntry(eb.startStrField(comp, val)
                            .setErrorSupplier(v -> adapter.validate(entry, v).valid() ? java.util.Optional.empty() : java.util.Optional.of(Component.literal(adapter.validate(entry, v).message())))
                        .setSaveConsumer(v -> { entry.setCurrentValue(v); changeSet.stage(entry, v); })
                            .build());
                }
                break;
            }
            case COLLECTION: {
                String val = entry.getCurrentValue() == null ? "" : String.valueOf(entry.getCurrentValue());
                category.addEntry(eb.startTextDescription(Component.literal(label + ": " + val + " (read-only)")).build());
                break;
            }
            default: {
                String val = entry.getCurrentValue() == null ? "" : String.valueOf(entry.getCurrentValue());
                category.addEntry(eb.startTextDescription(Component.literal(label + ": " + val)).build());
                break;
            }
        }
        if (entry.getMetadata().description() != null) {
            // description already not easily attached; skip
        }
    }
}
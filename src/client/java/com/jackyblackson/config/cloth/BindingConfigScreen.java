package com.jackyblackson.config.cloth;

import com.google.common.collect.Lists;
import com.jackyblackson.bindings.BindingNode;
import com.jackyblackson.bindings.CommandNode;
import com.jackyblackson.config.malilibconfig.Configs;
import com.jackyblackson.config.malilibconfig.Hotkeys;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static fi.dy.masa.malilib.util.StringUtils.translate;

public class BindingConfigScreen {

    public static Screen create(Screen parent) {
        return createMainScreen(parent);
    }

    private static Screen createMainScreen(Screen root) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(root)
                .setTitle(Text.translatable("b1ndc0mmandz.bindings.gui.edit.title"));

        builder.setSavingRunnable(() -> {
            Hotkeys.getHotkeyList();
            Configs.saveToFile();
        });

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("b1ndc0mmandz.bindings.category.general"));
        // Add Binding Button (Standard)
        general.addEntry(new ButtonEntry(
                Text.translatable("b1ndc0mmandz.bindings.gui.edit.add"),
                button -> {
                    BindingNode newNode = new BindingNode(
                            translate("b1ndc0mmandz.bindings.node.default.name"),
                            translate("b1ndc0mmandz.bindings.node.default.hotkey"),
                            translate("b1ndc0mmandz.bindings.node.default.comment")
                    );
                    MinecraftClient.getInstance().setScreen(createBindingScreen(root, newNode, true));
                }
        ));

        List<BindingNode> nodes = Configs.BINDING_NODES;

        for (BindingNode node : nodes) {
            // Node List Button (Transparent/Custom)
            general.addEntry(new NodeEntry(
                    Text.literal(node.getName() + " [" + node.getHotkeyStorageString() + "]"),
                    button -> {
                        MinecraftClient.getInstance().setScreen(createBindingScreen(root, node, false));
                    }
            ));
        }

        return builder.build();
    }

    private static Screen createBindingScreen(Screen root, BindingNode node, boolean isNew) {
        Screen parent = new ForwardingScreen(() -> createMainScreen(root));
        
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal(node.getName()));

        builder.setSavingRunnable(() -> {
            if (isNew && !Configs.BINDING_NODES.contains(node)) {
                Configs.BINDING_NODES.add(node);
            }
            Hotkeys.getHotkeyList();
            Configs.saveToFile();
        });

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("b1ndc0mmandz.bindings.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.details.name"), node.getName())
                .setSaveConsumer(node::setName)
                .build());

        general.addEntry(entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.details.keys"), node.getHotkeyStorageString())
                .setSaveConsumer(node::setHotkeyStorageString)
                .build());

        general.addEntry(entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.details.comment"), node.getComment())
                .setSaveConsumer(node::setComment)
                .build());

        ConfigCategory commands = builder.getOrCreateCategory(Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.commands"));

        // Add Command Button (Standard)
        commands.addEntry(new ButtonEntry(
                Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.add_command"),
                button -> {
                    if (isNew && !Configs.BINDING_NODES.contains(node)) {
                        Configs.BINDING_NODES.add(node);
                        Configs.saveToFile();
                    }
                    CommandNode newCmd = new CommandNode(
                            translate("b1ndc0mmandz.bindings.gui.edit.bindings.new.name"),
                            translate("b1ndc0mmandz.bindings.gui.edit.bindings.new.command"),
                            translate("b1ndc0mmandz.bindings.gui.edit.bindings.new.comment")
                    );
                    MinecraftClient.getInstance().setScreen(createCommandScreen(root, node, newCmd, true));
                }
        ));

        for (CommandNode cmd : node.getCommandNodes()) {
            // Command List Button (Transparent/Custom)
            commands.addEntry(new NodeEntry(
                    Text.literal(cmd.getName()),
                    button -> {
                         MinecraftClient.getInstance().setScreen(createCommandScreen(root, node, cmd, false));
                    }
            ));
        }

        if (!isNew) {
            general.addEntry(new ButtonEntry(
                    Text.translatable("b1ndc0mmandz.bindings.gui.edit.list.remove").append(" (DELETE)"),
                    button -> {
                        Configs.BINDING_NODES.remove(node);
                        Hotkeys.getHotkeyList();
                        Configs.saveToFile();
                        MinecraftClient.getInstance().setScreen(createMainScreen(root));
                    }
            ));
        }

        return builder.build();
    }

    private static Screen createCommandScreen(Screen root, BindingNode bindingNode, CommandNode node, boolean isNew) {
        Screen parent = new ForwardingScreen(() -> createBindingScreen(root, bindingNode, false));

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal(node.getName()));

        builder.setSavingRunnable(() -> {
            if (isNew && !bindingNode.getCommandNodes().contains(node)) {
                bindingNode.getCommandNodes().add(node);
            }
            Configs.saveToFile();
        });

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("b1ndc0mmandz.bindings.category.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.command.label.name"), node.getName())
                .setSaveConsumer(node::setName)
                .build());

        general.addEntry(entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.command.label.command"), node.getCommand())
                .setSaveConsumer(node::setCommand)
                .build());

        general.addEntry(entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.command.label.comment"), node.getComment())
                .setSaveConsumer(node::setComment)
                .build());

        if (!isNew) {
            general.addEntry(new ButtonEntry(
                    Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.entry.remove").append(" (DELETE)"),
                    button -> {
                        bindingNode.getCommandNodes().remove(node);
                        Configs.saveToFile();
                        MinecraftClient.getInstance().setScreen(createBindingScreen(root, bindingNode, false));
                    }
            ));
        }

        return builder.build();
    }

    private static class ForwardingScreen extends Screen {
        private final Supplier<Screen> target;

        protected ForwardingScreen(Supplier<Screen> target) {
            super(Text.empty());
            this.target = target;
        }

        @Override
        protected void init() {
            if (this.client != null) {
                this.client.setScreen(this.target.get());
            }
        }
    }

    // Standard Button (Add, Delete)
    private static class ButtonEntry extends AbstractConfigListEntry<Object> {
        private final ButtonWidget button;

        public ButtonEntry(Text message, ButtonWidget.PressAction onPress) {
            super(Text.empty(), false);
            this.button = ButtonWidget.builder(message, onPress)
                    .dimensions(0, 0, 200, 20)
                    .build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
            this.button.setX(x + entryWidth / 2 - this.button.getWidth() / 2);
            this.button.setY(y);
            this.button.render(context, mouseX, mouseY, delta);
        }

        @Override
        public List<? extends Element> children() {
            return Lists.newArrayList(this.button);
        }
        
        @Override
        public List<? extends Selectable> narratables() {
             return Lists.newArrayList(this.button);
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public Optional<Object> getDefaultValue() {
            return Optional.empty();
        }

        @Override
        public void save() {
        }
    }

    // Transparent Node Button (List Items)
    private static class NodeEntry extends AbstractConfigListEntry<Object> {
        private final TransparentButtonWidget button;

        public NodeEntry(Text message, ButtonWidget.PressAction onPress) {
            super(Text.empty(), false);
            this.button = new TransparentButtonWidget(0, 0, 200, 20, message, onPress);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
            this.button.setX(x + entryWidth / 2 - this.button.getWidth() / 2);
            this.button.setY(y);
            this.button.render(context, mouseX, mouseY, delta);
        }

        @Override
        public List<? extends Element> children() {
            return Lists.newArrayList(this.button);
        }

        @Override
        public List<? extends Selectable> narratables() {
            return Lists.newArrayList(this.button);
        }

        @Override
        public Object getValue() { return null; }

        @Override
        public Optional<Object> getDefaultValue() { return Optional.empty(); }

        @Override
        public void save() {}
    }

    private static class TransparentButtonWidget extends ButtonWidget {
        public TransparentButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int color = 0x20000000; // Default transparent dark
            
            if (this.isHovered()) {
                color = 0x60999999; // Lighter on hover
            }

            // Draw background
            context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color);

            // Draw text
            int textColor = this.active ? 0xFFFFFF : 0xA0A0A0;
            this.drawMessage(context, MinecraftClient.getInstance().textRenderer, textColor);
        }
    }
}

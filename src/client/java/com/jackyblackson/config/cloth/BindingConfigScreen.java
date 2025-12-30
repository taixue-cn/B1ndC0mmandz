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
import net.minecraft.util.math.ColorHelper;

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

//        general.addEntry(new ButtonEntry(
//                Text.translatable("b1ndc0mmandz.bindings.gui.edit.save"),
//                button -> {
//                    Hotkeys.getHotkeyList();
//                    Configs.saveToFile();
//                    MinecraftClient.getInstance().setScreen(root);
//                }
//        ));

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
            general.addEntry(new ComplexListEntry(
                    Text.literal(node.getName() + " [" + node.getHotkeyStorageString() + "]"),
                    button -> {
                        MinecraftClient.getInstance().setScreen(createBindingScreen(root, node, false));
                    },
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

        List<AbstractConfigListEntry> fields = Lists.newArrayList();

//        general.addEntry(new ButtonEntry(
//                Text.translatable("b1ndc0mmandz.bindings.gui.edit.save"),
//                button -> {
//                    fields.forEach(AbstractConfigListEntry::save);
//                    if (isNew && !Configs.BINDING_NODES.contains(node)) {
//                        Configs.BINDING_NODES.add(node);
//                    }
//                    Hotkeys.getHotkeyList();
//                    Configs.saveToFile();
//                    MinecraftClient.getInstance().setScreen(createMainScreen(root));
//                }
//        ));

        AbstractConfigListEntry nameEntry = entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.details.name"), node.getName())
                .setSaveConsumer(node::setName)
                .build();
        fields.add(nameEntry);
        general.addEntry(nameEntry);

        AbstractConfigListEntry keyEntry = entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.details.keys"), node.getHotkeyStorageString())
                .setSaveConsumer(node::setHotkeyStorageString)
                .build();
        fields.add(keyEntry);
        general.addEntry(keyEntry);

        AbstractConfigListEntry commentEntry = entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.details.comment"), node.getComment())
                .setSaveConsumer(node::setComment)
                .build();
        fields.add(commentEntry);
        general.addEntry(commentEntry);

//        ConfigCategory commands = builder.getOrCreateCategory(Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.commands"));

        // Add Command Button (Standard)
        general.addEntry(new ButtonEntry(
                Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.add_command"),
                button -> {
                    fields.forEach(AbstractConfigListEntry::save);
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
            general.addEntry(new ComplexListEntry(
                    Text.literal(cmd.getName()),
                    button -> {
                         fields.forEach(AbstractConfigListEntry::save);
                         MinecraftClient.getInstance().setScreen(createCommandScreen(root, node, cmd, false));
                    },
                    button -> {
                        node.getCommandNodes().remove(cmd);
                        Configs.saveToFile();
                        MinecraftClient.getInstance().setScreen(createBindingScreen(root, node, false));
                    }
            ));
        }

//        if (!isNew) {
//            general.addEntry(new ButtonEntry(
//                    Text.translatable("b1ndc0mmandz.bindings.gui.edit.list.remove").append(" (DELETE)"),
//                    button -> {
//                        Configs.BINDING_NODES.remove(node);
//                        Hotkeys.getHotkeyList();
//                        Configs.saveToFile();
//                        MinecraftClient.getInstance().setScreen(createMainScreen(root));
//                    }
//            ));
//        }

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

        List<AbstractConfigListEntry> fields = Lists.newArrayList();

//        general.addEntry(new ButtonEntry(
//                Text.translatable("b1ndc0mmandz.bindings.gui.edit.save"),
//                button -> {
//                    fields.forEach(AbstractConfigListEntry::save);
//                    if (isNew && !bindingNode.getCommandNodes().contains(node)) {
//                        bindingNode.getCommandNodes().add(node);
//                    }
//                    Configs.saveToFile();
//                    MinecraftClient.getInstance().setScreen(createBindingScreen(root, bindingNode, false));
//                }
//        ));

        AbstractConfigListEntry nameEntry = entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.command.label.name"), node.getName())
                .setSaveConsumer(node::setName)
                .build();
        fields.add(nameEntry);
        general.addEntry(nameEntry);

        AbstractConfigListEntry cmdEntry = entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.command.label.command"), node.getCommand())
                .setSaveConsumer(node::setCommand)
                .build();
        fields.add(cmdEntry);
        general.addEntry(cmdEntry);

        AbstractConfigListEntry commentEntry = entryBuilder.startStrField(Text.translatable("b1ndc0mmandz.bindings.gui.edit.command.label.comment"), node.getComment())
                .setSaveConsumer(node::setComment)
                .build();
        fields.add(commentEntry);
        general.addEntry(commentEntry);

//        if (!isNew) {
//            general.addEntry(new ButtonEntry(
//                    Text.translatable("b1ndc0mmandz.bindings.gui.edit.bindings.entry.remove").append(" (DELETE)"),
//                    button -> {
//                        bindingNode.getCommandNodes().remove(node);
//                        Configs.saveToFile();
//                        MinecraftClient.getInstance().setScreen(createBindingScreen(root, bindingNode, false));
//                    }
//            ));
//        }

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
        public Optional<Object> getDefaultValue() {
            return Optional.empty();
        }

        @Override
        public void save() {
        }

        @Override
        public boolean isEdited() {
            return true;
        }

        @Override
        public Object getValue() {
            return null;
        }

        @Override
        public List<? extends Selectable> narratables() {
            return null;
        }
    }

    private static class ComplexListEntry extends AbstractConfigListEntry<Object> {
        private final Text label;
        private final ButtonWidget editButton;
        private final ButtonWidget deleteButton;

        public ComplexListEntry(Text label, ButtonWidget.PressAction onEdit, ButtonWidget.PressAction onDelete) {
            super(Text.empty(), false);
            this.label = label;
            this.editButton = ButtonWidget.builder(Text.translatable("b1ndc0mmandz.bindings.gui.edit.list.edit"), onEdit)
                    .dimensions(0, 0, 50, 20)
                    .build();
            this.deleteButton = ButtonWidget.builder(Text.translatable("b1ndc0mmandz.bindings.gui.edit.list.remove"), onDelete)
                    .dimensions(0, 0, 50, 20)
                    .build();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, this.label, x + 10, y + (entryHeight - 8) / 2, ColorHelper.getArgb(255, 255, 255));

            this.deleteButton.setX(x + entryWidth - 55);
            this.deleteButton.setY(y + (entryHeight - 20) / 2);
            this.deleteButton.render(context, mouseX, mouseY, delta);

            this.editButton.setX(x + entryWidth - 110);
            this.editButton.setY(y + (entryHeight - 20) / 2);
            this.editButton.render(context, mouseX, mouseY, delta);
        }

        @Override
        public List<? extends Element> children() {
            return Lists.newArrayList(this.editButton, this.deleteButton);
        }

        @Override
        public List<? extends Selectable> narratables() {
            return Lists.newArrayList(this.editButton, this.deleteButton);
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

        @Override
        public boolean isEdited() {
            return true;
        }
    }
}

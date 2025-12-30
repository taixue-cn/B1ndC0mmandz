package com.jackyblackson.config.owo;

import com.jackyblackson.bindings.BindingNode;
import com.jackyblackson.bindings.CommandNode;
import com.jackyblackson.config.malilibconfig.Configs;
import com.jackyblackson.config.malilibconfig.Hotkeys;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static fi.dy.masa.malilib.util.StringUtils.translate;

public class EditBindingScreen extends BaseOwoScreen<FlowLayout> {

    public final BindingConfigScreen parent;
    public final BindingNode node;

    private FlowLayout root;

    public EditBindingScreen(BindingConfigScreen parent, BindingNode node) {
        this.parent = parent;
        this.node = node;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

//    @Override
//    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        super.render(context, mouseX, mouseY, delta);
//        refreshCommandList(this.root);
//    }

//    @Override
//    public @Nullable Element getFocused() {
//        refreshCommandList(this.root);
//        return super.getFocused();
//    }

    @Override
    protected void build(FlowLayout rootComponent) {
        this.root = rootComponent;
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        // Command List
        var commandsContainer = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        commandsContainer.padding(Insets.of(3));
        
        var scrollContainer = Containers.verticalScroll(Sizing.fill(90), Sizing.fill(60), commandsContainer);
        scrollContainer.surface(Surface.PANEL);
        
        refreshCommandList(commandsContainer);

        rootComponent.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.title") + node.getName())).shadow(true).margins(Insets.bottom(2)));
        rootComponent.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.hotkeys") + node.getHotkeyStorageString())).margins(Insets.bottom(5)));
        
//        rootComponent.child(
//                Components.button(Text.literal("Edit Name / Hotkey / Comment"), button -> {
//                    MinecraftClient.getInstance().setScreen(new EditBindingDetailsScreen(this, node));
//                }).margins(Insets.bottom(5))
//        );

        var layout = Containers.horizontalFlow(Sizing.content(), Sizing.content());

        layout.child(
                Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.metadata")), button -> {
                    MinecraftClient.getInstance().setScreen(new EditBindingDetailsScreen(this, node));
                })
                        .margins(Insets.right(2))
//                        .margins(Insets.bottom(5))
        );

        layout.child(
                Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.add_command")), button -> {
                    CommandNode newCmd = new CommandNode(
                            translate("b1ndc0mmandz.bindings.gui.edit.bindings.new.name"),
                            translate("b1ndc0mmandz.bindings.gui.edit.bindings.new.command"),
                            translate("b1ndc0mmandz.bindings.gui.edit.bindings.new.comment")
                    );
                    node.getCommandNodes().add(newCmd);
                    refreshCommandList(commandsContainer);
                })
//                        .margins(Insets.top(3))
        );

        rootComponent.child(layout);

        rootComponent.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.commands"))).margins(Insets.vertical(2)));
        rootComponent.child(scrollContainer);
//
//        rootComponent.child(
//                Components.button(Text.literal("Add Command"), button -> {
//                    CommandNode newCmd = new CommandNode("New Command", "say hello", "Description");
//                    node.getCommandNodes().add(newCmd);
//                    refreshCommandList(commandsContainer);
//                }).margins(Insets.top(3))
//        );

        rootComponent.child(
                Containers.horizontalFlow(Sizing.content(), Sizing.content())
                        .child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.save")), button -> {
                            Hotkeys.getHotkeyList();
                            Configs.saveToFile();
                            MinecraftClient.getInstance().setScreen(
                                    new BindingConfigScreen()
                            );
                        }).margins(Insets.right(10)))
                        .child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.cancel")), button -> {
                            MinecraftClient.getInstance().setScreen(
                                    new BindingConfigScreen()
                            );
                        }))
                        .margins(Insets.top(5))
        );
    }

    private void refreshCommandList(FlowLayout container) {
        container.clearChildren();
        List<CommandNode> commands = node.getCommandNodes();

        for (CommandNode cmd : commands) {
            var row = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
            row.verticalAlignment(VerticalAlignment.CENTER);
            row.padding(Insets.of(3));
            row.surface(Surface.DARK_PANEL);
            row.margins(Insets.bottom(2));

            row.child(Components.label(Text.literal(cmd.getName())).sizing(Sizing.fixed(120), Sizing.content()));
            row.child(Components.label(Text.literal(cmd.getCommand())).sizing(Sizing.fixed(150), Sizing.content()));

            row.child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.entry.edit")), button -> {
                 MinecraftClient.getInstance().setScreen(new EditCommandScreen(this, cmd));
            }).margins(Insets.left(10)));

            row.child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.entry.remove")), button -> {
                node.getCommandNodes().remove(cmd);
                refreshCommandList(container);
            }).margins(Insets.left(5)));

            container.child(row);
        }
    }
}
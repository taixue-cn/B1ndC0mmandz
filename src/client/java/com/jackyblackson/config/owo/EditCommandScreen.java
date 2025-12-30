package com.jackyblackson.config.owo;

import com.jackyblackson.bindings.CommandNode;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static fi.dy.masa.malilib.util.StringUtils.translate;

public class EditCommandScreen extends BaseOwoScreen<FlowLayout> {

    private final EditBindingScreen parent;
    private final CommandNode node;

    public EditCommandScreen(EditBindingScreen parent, CommandNode node) {
        this.parent = parent;
        this.node = node;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        // Name Field
        var nameField = Components.textBox(Sizing.fixed(250));
        nameField.setText(node.getName());
        nameField.setMaxLength(64);
        
        // Command Field
        var commandField = Components.textBox(Sizing.fixed(250));
        commandField.setText(node.getCommand());
        commandField.setMaxLength(256);

        // Comment Field
        var commentField = Components.textBox(Sizing.fixed(250));
        commentField.setText(node.getComment());
        commentField.setMaxLength(256);

        var fieldsContainer = Containers.verticalFlow(Sizing.content(), Sizing.content());
        fieldsContainer.padding(Insets.of(5));
        fieldsContainer.surface(Surface.DARK_PANEL);
        
        fieldsContainer.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.command.label.name"))).margins(Insets.bottom(1)));
        fieldsContainer.child(nameField.margins(Insets.bottom(5)));
        
        fieldsContainer.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.command.label.command"))).margins(Insets.bottom(1)));
        fieldsContainer.child(commandField.margins(Insets.bottom(5)));
        
        fieldsContainer.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.command.label.comment"))).margins(Insets.bottom(1)));
        fieldsContainer.child(commentField.margins(Insets.bottom(5)));

        rootComponent.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.command.button.edit"))).shadow(true).margins(Insets.bottom(5)));
        rootComponent.child(fieldsContainer);

        rootComponent.child(
                Containers.horizontalFlow(Sizing.content(), Sizing.content())
                        .child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.command.button.save")), button -> {
                            node.setName(nameField.getText());
                            node.setCommand(commandField.getText());
                            node.setComment(commentField.getText());
                            MinecraftClient.getInstance().setScreen(
                                    new EditBindingScreen(parent.parent, parent.node)
                            );
                        }).margins(Insets.right(10)))
                        .child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.command.button.cancel")), button -> {
                            MinecraftClient.getInstance().setScreen(
                                    new EditBindingScreen(parent.parent, parent.node)
                            );
                        }))
                        .margins(Insets.top(5))
        );
    }
}

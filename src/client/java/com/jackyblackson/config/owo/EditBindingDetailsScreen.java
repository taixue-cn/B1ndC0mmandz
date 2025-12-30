package com.jackyblackson.config.owo;

import com.jackyblackson.bindings.BindingNode;
import com.jackyblackson.config.malilibconfig.Configs;
import com.jackyblackson.config.malilibconfig.Hotkeys;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
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

public class EditBindingDetailsScreen extends BaseOwoScreen<FlowLayout> {

    private final EditBindingScreen parent;
    private final BindingNode node;

    public EditBindingDetailsScreen(EditBindingScreen parent, BindingNode node) {
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
        var nameField = Components.textBox(Sizing.fixed(200));
        nameField.setText(node.getName());
        nameField.setMaxLength(64);
        
        // Hotkey Field
        var hotkeyField = Components.textBox(Sizing.fixed(200));
        hotkeyField.setText(node.getHotkeyStorageString());
        hotkeyField.setMaxLength(64);

        // Comment Field
        var commentField = Components.textBox(Sizing.fixed(200));
        commentField.setText(node.getComment());
        commentField.setMaxLength(256);

        var fieldsContainer = Containers.verticalFlow(Sizing.content(), Sizing.content());
        fieldsContainer.padding(Insets.of(5));
        fieldsContainer.surface(Surface.DARK_PANEL);
        
        fieldsContainer.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.details.name"))).margins(Insets.bottom(1)));
        fieldsContainer.child(nameField.margins(Insets.bottom(5)));
        
        fieldsContainer.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.details.keys"))).margins(Insets.bottom(1)));
        fieldsContainer.child(hotkeyField.margins(Insets.bottom(5)));
        
        fieldsContainer.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.details.comment"))).margins(Insets.bottom(1)));
        fieldsContainer.child(commentField.margins(Insets.bottom(5)));

        rootComponent.child(Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.details.title"))).shadow(true).margins(Insets.bottom(5)));
        rootComponent.child(fieldsContainer);

        rootComponent.child(
                Containers.horizontalFlow(Sizing.content(), Sizing.content())
                        .child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.details.save")), button -> {
                            node.setName(nameField.getText());
                            node.setHotkeyStorageString(hotkeyField.getText());
                            node.setComment(commentField.getText());
                            // Update Malilib hotkey object
                            node.setHotkey(new ConfigHotkey(node.getName(), node.getHotkeyStorageString(), node.getComment()));
                            Hotkeys.getHotkeyList();
                            Configs.saveToFile();
                            MinecraftClient.getInstance().setScreen(
                                    new EditBindingScreen(this.parent.parent, this.parent.node)
                            );
                        }).margins(Insets.right(10)))
                        .child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.bindings.details.cancel")), button -> {
                            MinecraftClient.getInstance().setScreen(
                                    new EditBindingScreen(this.parent.parent, this.parent.node)
                            );
                        }))
                        .margins(Insets.top(5))
        );
    }
}

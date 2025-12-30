package com.jackyblackson.config.owo;

import com.jackyblackson.bindings.BindingNode;
import com.jackyblackson.config.malilibconfig.Configs;
import com.jackyblackson.config.malilibconfig.Hotkeys;
import fi.dy.masa.malilib.util.StringUtils;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static fi.dy.masa.malilib.util.StringUtils.translate;

public class BindingConfigScreen extends BaseOwoScreen<FlowLayout> {

    public BindingConfigScreen() {
    }

    private FlowLayout root;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

//    @Override
//    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        super.render(context, mouseX, mouseY, delta);
//        this.refreshBindingList(this.root);
//    }

    @Override
    protected void build(FlowLayout rootComponent) {
        this.root = rootComponent;
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        var bindingsContainer = Containers.verticalFlow(Sizing.fill(100), Sizing.content());
        bindingsContainer.padding(Insets.of(5));
        
        var scrollContainer = Containers.verticalScroll(Sizing.fill(80), Sizing.fill(70), bindingsContainer);
        scrollContainer.surface(Surface.PANEL);

        // Initial population
        refreshBindingList(bindingsContainer);

        rootComponent.child(
                Components.label(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.title")))
                        .shadow(true)
                        .margins(Insets.bottom(5))
        );
        
        rootComponent.child(scrollContainer);

        rootComponent.child(
                Containers.horizontalFlow(Sizing.content(), Sizing.content())
                        .child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.add")), button -> {
                            BindingNode newNode = new BindingNode(
                                    translate("b1ndc0mmandz.bindings.node.default.name"),
                                    translate("b1ndc0mmandz.bindings.node.default.hotkey"),
                                    translate("b1ndc0mmandz.bindings.node.default.comment")
                            );
                            Configs.BINDING_NODES.add(newNode);
                            refreshBindingList(bindingsContainer);
                            Hotkeys.getHotkeyList();
                            Configs.saveToFile();
                        }).margins(Insets.right(10)))
                        .child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.save")), button -> {
                            Hotkeys.getHotkeyList();
                            Configs.saveToFile();
                            this.close();
                        }))
                        .margins(Insets.top(5))
        );
    }

    private void refreshBindingList(FlowLayout container) {
        container.clearChildren();
        List<BindingNode> nodes = Configs.BINDING_NODES;
        
        for (BindingNode node : nodes) {
            var row = Containers.horizontalFlow(Sizing.fill(100), Sizing.content());
            row.verticalAlignment(VerticalAlignment.CENTER);
            row.padding(Insets.of(3));
            row.surface(Surface.DARK_PANEL);
            row.margins(Insets.bottom(2));

            row.child(Components.label(Text.literal(node.getName())).sizing(Sizing.fixed(150), Sizing.content()));
            row.child(Components.label(Text.literal(node.getHotkeyStorageString())).sizing(Sizing.fixed(100), Sizing.content()));
            
            row.child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.list.edit")), button -> {
                 MinecraftClient.getInstance().setScreen(new EditBindingScreen(this, node));
            }).margins(Insets.left(10)));

            row.child(Components.button(Text.literal(translate("b1ndc0mmandz.bindings.gui.edit.list.remove")), button -> {
                Configs.BINDING_NODES.remove(node);
                refreshBindingList(container);
                Hotkeys.getHotkeyList();
                Configs.saveToFile();
            }).margins(Insets.left(5)));

            container.child(row);
        }
    }
}

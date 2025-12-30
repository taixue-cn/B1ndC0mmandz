package com.jackyblackson.config.malilibconfig;

import com.google.common.collect.ImmutableList;
import com.jackyblackson.B1ndC0mmandzClient;
import com.jackyblackson.InitHandler;
import com.jackyblackson.Reference;
import com.jackyblackson.config.owo.BindingConfigScreen;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.Collections;
import java.util.List;

public class GuiConfigs extends GuiConfigsBase
{
    public static List<? extends IConfigBase> variableConfigs = null;
    private static ConfigGuiTab tab = ConfigGuiTab.HOTKEYS;

    public GuiConfigs()
    {
        super(10, 50, Reference.MOD_ID, null, "b1ndc0mmandz.gui.title.configs");
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.VALUES)
        {
            x += this.createButton(x, y, -1, tab);
        }
        x += this.createReloadButton(x, y, -1, "b1ndc0mmandz.gui.button.config_gui.reload");
        x += this.createOwoConfigButton(x, y, -1, ("b1ndc0mmandz.bindings.edit.entrance"));

    }

    private int createOwoConfigButton(int x, int y, int width, String label) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, false, label);
        button.setEnabled(true);
        this.addButton(button, new IButtonActionListener() {
            @Override
            public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
                MinecraftClient.getInstance().setScreen(new BindingConfigScreen());
            }
        });
        return button.getWidth() + 2;
    }

    private int createButton(int x, int y, int width, ConfigGuiTab tab)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, tab.getDisplayName());
        button.setEnabled(GuiConfigs.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));

        return button.getWidth() + 2;
    }

    private int createReloadButton(int x, int y, int width, String translationKey) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, StringUtils.translate(translationKey));
        button.setEnabled(true);
        this.addButton(button, new IButtonActionListener() {
            @Override
            public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
                Configs.getInstance().load();
                InitHandler.handler.onHotkeysUpdated();
                MinecraftClient.getInstance().setScreen(null);
                GuiBase.openGui(new GuiConfigs());
            }
        });
        return button.getWidth() + 2;
    }

    private int createScreenButton(int x, int y, int width, String translationKey, Screen screen) {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, StringUtils.translate(translationKey));
        button.setEnabled(true);
        this.addButton(button, new IButtonActionListener() {
            @Override
            public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
                MinecraftClient.getInstance().setScreen(null);
                GuiBase.openGui(screen);
            }
        });
        return button.getWidth() + 2;
    }

    @Override
    protected int getConfigWidth()
    {
        ConfigGuiTab tab = GuiConfigs.tab;

        if (tab == ConfigGuiTab.TOGGLES)
        {
            return 100;
        }

        return super.getConfigWidth();
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs()
    {
        List<? extends IConfigBase> configs;
        ConfigGuiTab tab = GuiConfigs.tab;
        variableConfigs = null;

        if (tab == ConfigGuiTab.TOGGLES)
        {
            configs = Configs.Toggles.OPTIONS;
        }
        else if (tab == ConfigGuiTab.HOTKEYS)
        {
            configs = Hotkeys.getHotkeyList();
        }
        else if (tab == ConfigGuiTab.VARIABLES) {
            // WRAP VARIABLES INTO SOMETHING CAN BE CONFIG HERE
            // configs = Configs.getVariableConfigs();  // 获取Variable的配置封装
            configs = Configs.createVariableConfigList();
            variableConfigs = configs;
        }
        else
        {
            return Collections.emptyList();
        }

        return ConfigOptionWrapper.createFor(configs);
    }

    private static class ButtonListener implements IButtonActionListener
    {
        private final GuiConfigs parent;
        private final ConfigGuiTab tab;

        public ButtonListener(ConfigGuiTab tab, GuiConfigs parent)
        {
            this.tab = tab;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton)
        {
            GuiConfigs.tab = this.tab;

            this.parent.reCreateListWidget(); // apply the new config width
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }

    public enum ConfigGuiTab
    {
        TOGGLES ("b1ndc0mmandz.gui.button.config_gui.generic"),
        HOTKEYS ("b1ndc0mmandz.gui.button.config_gui.hotkeys"),
        VARIABLES ("b1ndc0mmandz.gui.button.config_gui.variables");
        ;

        private final String translationKey;

        public static final ImmutableList<ConfigGuiTab> VALUES = ImmutableList.copyOf(values());

        ConfigGuiTab(String translationKey)
        {
            this.translationKey = translationKey;
        }

        public String getDisplayName()
        {
            return StringUtils.translate(this.translationKey);
        }
    }
}

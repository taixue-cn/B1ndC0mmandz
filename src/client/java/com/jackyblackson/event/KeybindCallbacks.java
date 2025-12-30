package com.jackyblackson.event;

import com.jackyblackson.config.malilibconfig.Configs;
import com.jackyblackson.config.malilibconfig.GuiConfigs;
import com.jackyblackson.config.malilibconfig.Hotkeys;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public class KeybindCallbacks implements IHotkeyCallback, IClientTickHandler {

    private static final KeybindCallbacks INSTANCE = new KeybindCallbacks();

    protected int massCraftTicker;

    public static KeybindCallbacks getInstance()
    {
        return INSTANCE;
    }



    private KeybindCallbacks()
    {
    }

    public void setCallbacks()
    {
        for (ConfigHotkey hotkey : Hotkeys.getHotkeyList())
        {
            hotkey.getKeybind().setCallback(this);
        }
        Configs.Toggles.OPEN_CONFIG_GUI.getKeybind().setCallback(this);
        Configs.Toggles.OPEN_TEST_GUI.getKeybind().setCallback(this);
    }

    public List<ConfigHotkey> setCallbacks(List<ConfigHotkey> hotkeys)
    {
        for (ConfigHotkey hotkey : hotkeys)
        {
            hotkey.getKeybind().setCallback(this);
        }
        return hotkeys;
    }

    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key) {
        return onKeyActionImpl(action, key);
    }

    private boolean onKeyActionImpl(KeyAction action, IKeybind key) {
        System.out.println(key.getStringValue());
        for(var binding : Configs.BINDING_NODES) {
            System.out.println("    -> " + binding.getName() + " with " + binding.getHotkey().getStringValue());
            if(key.getStringValue().equals(binding.getHotkey().getKeybind().getStringValue()) && action.equals(KeyAction.PRESS)) {
                System.out.println("    Prepared " + binding.getName() + " to execute!");
                binding.executes();
                return true;
            }
        }
        if (key == Configs.Toggles.OPEN_CONFIG_GUI.getKeybind())
        {
            GuiBase.openGui(new GuiConfigs());
            return true;
        }
        System.out.println("No match key!");
        return false;
    }

    @Override
    public void onClientTick(MinecraftClient mc) {

    }
}

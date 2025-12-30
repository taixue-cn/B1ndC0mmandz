package com.jackyblackson.event;

import com.jackyblackson.Reference;
import com.jackyblackson.config.malilibconfig.Configs;
import com.jackyblackson.config.malilibconfig.Hotkeys;
import fi.dy.masa.malilib.hotkeys.*;

import fi.dy.masa.malilib.event.InputEventHandler;

import java.util.ArrayList;

public class InputHandler implements IKeybindProvider, IKeyboardInputHandler, IMouseInputHandler {
    private final KeybindCallbacks callbacks;

    public InputHandler()
    {
        this.callbacks = KeybindCallbacks.getInstance();
    }

    public void onHotkeysUpdated() {
        IKeybindManager manager = InputEventHandler.getKeybindManager();

        manager.updateUsedKeys();
    }

    @Override
    public void addKeysToMap(IKeybindManager manager)
    {
        var hotkeys = KeybindCallbacks.getInstance().setCallbacks(Hotkeys.getHotkeyList());
        for (IHotkey hotkey : hotkeys)
        {
            System.out.println("addKeysToMap: adding " + hotkey.getName() + " with keybind: " + hotkey.getKeybind().getStringValue());
            manager.addKeybindToMap(hotkey.getKeybind());
        }
        manager.addKeybindToMap(Configs.Toggles.OPEN_CONFIG_GUI.getKeybind());
        manager.addKeybindToMap(Configs.Toggles.OPEN_TEST_GUI.getKeybind());
    }

    @Override
    public void addHotkeys(IKeybindManager manager)
    {
        var hotkeys = new ArrayList<>(KeybindCallbacks.getInstance().setCallbacks(Hotkeys.getHotkeyList())) ;
        System.out.println("addHotkeys: hotkey count: " + hotkeys.size());
        hotkeys.add(Configs.Toggles.OPEN_CONFIG_GUI);
        hotkeys.add(Configs.Toggles.OPEN_TEST_GUI);
        manager.addHotkeysForCategory(Reference.MOD_NAME, "b1ndc0mmandz.hotkeys.category.hotkeys", hotkeys);
        manager.getKeybindCategories().forEach(keybindCategory -> {
            System.out.println("    " + keybindCategory.getModName());
            keybindCategory.getHotkeys().forEach(iHotkey -> {
                System.out.println("        " + iHotkey.getName() + ": " + iHotkey.getKeybind().getStringValue());
            });
        });
    }

    public boolean onKeyInput(int keyCode, int scanCode, int modifiers, boolean eventKeyState)
    {
        return false;
    }

    public boolean onMouseScroll(int mouseX, int mouseY, double amount)
    {
        return false;
    }

    public boolean onMouseClick(int mouseX, int mouseY, int eventButton, boolean eventButtonState)
    {
        return false;
    }
}

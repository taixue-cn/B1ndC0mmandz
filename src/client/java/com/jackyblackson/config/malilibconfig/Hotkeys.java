package com.jackyblackson.config.malilibconfig;

import com.google.common.collect.ImmutableList;
import com.jackyblackson.bindings.BindingNode;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

import java.util.List;

public class Hotkeys
{
    private static final KeybindSettings GUI_RELAXED = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, false);
    private static final KeybindSettings GUI_RELAXED_CANCEL = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, true, false, false, true);
    private static final KeybindSettings GUI_NO_ORDER = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.PRESS, false, false, false, true);
    private static final KeybindSettings INGAME_RELAXED = KeybindSettings.create(KeybindSettings.Context.INGAME, KeyAction.PRESS, true, false, false, false);
    private static final KeybindSettings INGAME_RELEASE = KeybindSettings.create(KeybindSettings.Context.INGAME, KeyAction.RELEASE, true, false, false, false);

    private static final KeybindSettings INGAME_MENU = KeybindSettings.create(KeybindSettings.Context.INGAME, KeyAction.BOTH, true, false, false, false);
    private static final KeybindSettings GUI_RELEASE = KeybindSettings.create(KeybindSettings.Context.GUI, KeyAction.RELEASE, true, false, false, false);


    public static List<ConfigHotkey> getHotkeyList() {

        HOTKEY_LIST = Configs.BINDING_NODES.stream().map(BindingNode::getHotkey).toList();
        return HOTKEY_LIST;
    }

    public static List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
            Configs.Toggles.OPEN_CONFIG_GUI,
            Configs.Toggles.OPEN_TEST_GUI
    );


}
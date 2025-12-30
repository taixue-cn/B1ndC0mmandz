package com.jackyblackson.config.malilibconfig;

import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;

public class CustomConfigHotKey extends ConfigHotkey {
    public CustomConfigHotKey(String name, String defaultStorageString, String comment) {
        super(name, defaultStorageString, comment);
    }

    public CustomConfigHotKey(String name, String defaultStorageString, KeybindSettings settings, String comment) {
        super(name, defaultStorageString, settings, comment);
    }

    public CustomConfigHotKey(String name, String defaultStorageString, String comment, String prettyName) {
        super(name, defaultStorageString, comment, prettyName);
    }

    public CustomConfigHotKey(String name, String defaultStorageString, KeybindSettings settings, String comment, String prettyName) {
        super(name, defaultStorageString, settings, comment, prettyName);
    }


}

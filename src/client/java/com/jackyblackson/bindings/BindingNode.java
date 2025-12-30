package com.jackyblackson.bindings;

import com.jackyblackson.config.malilibconfig.Configs;
import fi.dy.masa.malilib.config.options.ConfigHotkey;

import java.util.ArrayList;
import java.util.List;

public class BindingNode {
    private transient ConfigHotkey hotkey;
//    private ConfigHotkey hotkey;
    private String hotkeyStorageString;
    private String name;
    private String comment;
    private List<CommandNode> commandNodes;

    public BindingNode(String name, String hotkeyStorageString, String comment) {
        this.hotkey = new ConfigHotkey(
                name,
                hotkeyStorageString,
                comment
        );
        this.hotkeyStorageString = hotkeyStorageString;
        this.name = name;
        this.comment = comment;
        this.commandNodes = new ArrayList<>();
    }

    public ConfigHotkey getHotkey() {
        var keybind = new ConfigHotkey(
                name,
                hotkeyStorageString,
                "(" + this.commandNodes.size() + ") " + this.comment
        );
        keybind.setValueChangeCallback(hotkey -> {
            System.out.println("KEY: " + hotkey.getStringValue());
            this.hotkeyStorageString = hotkey.getStringValue();
            Configs.saveToFile();
        });
        return keybind;
//        return this.hotkey;
    }

    public void setHotkey(ConfigHotkey hotkey) {
        this.hotkey = hotkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<CommandNode> getCommandNodes() {
        return commandNodes;
    }

    public void setCommandNodes(List<CommandNode> commandNodes) {
        this.commandNodes = commandNodes;
    }

    public String getHotkeyStorageString() {
        return hotkeyStorageString;
    }

    public void setHotkeyStorageString(String hotkeyStorageString) {
        this.hotkeyStorageString = hotkeyStorageString;
    }

    //
    // METHODS
    //

    public boolean executes() {
        System.out.println(name + " Executes!");
        this.commandNodes.forEach(CommandNode::execute);
        return true;
    }
}

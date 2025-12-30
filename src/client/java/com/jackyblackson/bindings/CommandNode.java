package com.jackyblackson.bindings;

import com.jackyblackson.placeholders.VariableParser;
import com.jackyblackson.utils.MessageUtils;
import net.minecraft.client.MinecraftClient;

public class CommandNode {
    private String name;
    private String command;
    private String comment;
    private transient String responseMessage = null;

    private transient Boolean success = null;


    public CommandNode(String name, String command, String comment) {
        this.name = name;
        this.command = command;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    //
    // METHODS
    //

    public boolean execute() {
        var player = MinecraftClient.getInstance().player;
        assert player != null;
        String variableFinished = VariableParser.parse(this.command);
        MessageUtils.executeCommand(player, variableFinished);
        return true;
    }
}

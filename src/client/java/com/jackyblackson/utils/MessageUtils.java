package com.jackyblackson.utils;

import net.minecraft.client.network.ClientPlayerEntity;

public class MessageUtils {
    public static void executeCommand(ClientPlayerEntity player, String command){
//        if (command.startsWith("/")) {
//            command = command.substring(1);
//            sendChat(player, command);
//        } else {
        player.networkHandler.sendChatCommand(command);
//        }
    }
}

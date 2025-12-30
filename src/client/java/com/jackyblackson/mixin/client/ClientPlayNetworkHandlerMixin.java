package com.jackyblackson.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @ModifyVariable(
            method = "sendChatCommand",
            at = @At("HEAD"),
            ordinal = 0, // 指定目标局部变量的序号，0 表示第一个局部变量
            argsOnly = true)
    private String modifyContentVariable(String content) {
//        var expanded = TailwindMaterialAdapter.toExpandForm(B1ndC0mmandzClient.tailwindConfigs.TAILWIND_ITEM_LIST(), content);
//        System.out.println("[Tailwind] " + content + " -> " + expanded);
        return content;
    }
}

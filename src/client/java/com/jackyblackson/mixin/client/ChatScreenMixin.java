package com.jackyblackson.mixin.client;

import com.jackyblackson.config.malilibconfig.Configs;
import com.jackyblackson.placeholders.VariableParser;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {

    @Unique
    SliderWidget SliderWidget;

    @Shadow
    protected TextFieldWidget chatField;


    protected ChatScreenMixin(Text title) {
        super(title);
    }

    // 使用 @Redirect 拦截并修改 normalize 方法对 chatText 的处理
    @Redirect(method = "sendMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;normalize(Ljava/lang/String;)Ljava/lang/String;"))
    private String redirectNormalize(ChatScreen instance, String chatText) {
        // 调用自定义的 modifyChatText 方法，返回修改后的文本
        return modifyChatText(chatText);
    }

    // 修改 chatText 的逻辑
    @Unique
    private String modifyChatText(String originalText) {
        // System.out.println("HI: " + originalText);
//        if(Configs.Toggles.USE_PLACEHOLDER_SYSTEM.getBooleanValue()){
//            chatText = PlaceholderParser.parse(chatText);
//        }
        if(Configs.Toggles.USE_VARIABLE_SYSTEM.getBooleanValue()) {
            originalText = VariableParser.parse(originalText);
        }
        System.out.println("Parsed: " + originalText);
        // 例如：在消息前加上一个自定义前缀 "[Custom]"
        return originalText;
    }
}


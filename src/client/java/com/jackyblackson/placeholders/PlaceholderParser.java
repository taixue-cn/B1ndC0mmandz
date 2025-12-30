package com.jackyblackson.placeholders;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Objects;

public class PlaceholderParser {
    public static String parse(String text) {
        assert MinecraftClient.getInstance().player != null;
        text = String.valueOf(Placeholders.parseText(Text.of(text), PlaceholderContext.of(
                Objects.requireNonNull(MinecraftClient.getInstance().getServer())
        )));
        text = String.valueOf(Placeholders.parseText(Text.of(text), PlaceholderContext.of(
                MinecraftClient.getInstance().player.getGameProfile(),
                Objects.requireNonNull(MinecraftClient.getInstance().getServer())
        )));
        return text;
    }
}

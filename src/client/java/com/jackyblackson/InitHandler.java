package com.jackyblackson;

import com.jackyblackson.config.malilibconfig.Configs;
import com.jackyblackson.event.InputHandler;
import com.jackyblackson.event.KeybindCallbacks;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.TickHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;

public class InitHandler implements IInitializationHandler
{
    public static InputHandler handler;
    @Override
    public void registerModHandlers()
    {
        ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());

        handler = new InputHandler();
        InputEventHandler.getKeybindManager().registerKeybindProvider(handler);
        InputEventHandler.getInputManager().registerKeyboardInputHandler(handler);
        InputEventHandler.getInputManager().registerMouseInputHandler(handler);

        TickHandler.getInstance().registerClientTickHandler(KeybindCallbacks.getInstance());

        KeybindCallbacks.getInstance().setCallbacks();
    }
}

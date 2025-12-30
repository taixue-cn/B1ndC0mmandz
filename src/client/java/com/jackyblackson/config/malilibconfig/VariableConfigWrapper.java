package com.jackyblackson.config.malilibconfig;

import com.google.gson.JsonElement;
import com.jackyblackson.placeholders.Variable;
import fi.dy.masa.malilib.config.ConfigType;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigString;

public class VariableConfigWrapper implements IConfigBase {
    private final String name;
    private final ConfigString configValue;
    private final ConfigBoolean configOn;

    public VariableConfigWrapper(String name, Variable variable) {
        this.name = name;
        this.configValue = new ConfigString(name + "_value", variable.value(), name + "'s value");
        this.configOn = new ConfigBoolean(name + "_on", variable.on(), name + "_on");
    }

    @Override
    public ConfigType getType() {
        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getComment() {
        return name;
    }

    @Override
    public void setValueFromJsonElement(JsonElement element) {
        if (element.isJsonObject()) {
            var jsonObject = element.getAsJsonObject();

            if (jsonObject.has("value")) {
                this.configValue.setValueFromString(jsonObject.get("value").getAsString());
            }

            if (jsonObject.has("on")) {
                this.configOn.setBooleanValue(jsonObject.get("on").getAsBoolean());
            }
        }
    }

    @Override
    public JsonElement getAsJsonElement() {
        var gson = new com.google.gson.JsonObject();
        gson.addProperty("value", this.configValue.getStringValue());
        gson.addProperty("on", this.configOn.getBooleanValue());
        return gson;
    }

    public Variable toVariable() {
        return new Variable(configValue.getStringValue(), configOn.getBooleanValue());
    }
}
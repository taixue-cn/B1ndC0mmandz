package com.jackyblackson.config.malilibconfig;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jackyblackson.Reference;
import com.jackyblackson.bindings.BindingNode;
import com.jackyblackson.bindings.CommandNode;
import com.jackyblackson.placeholders.Variable;
import net.fabricmc.loader.api.FabricLoader;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.config.options.*;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.StringUtils;

import java.io.File;
import java.util.*;

public class Configs implements IConfigHandler
{
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    private static Configs INSTANCE;

    public Configs() {
        INSTANCE = this;
    }

    public static Configs getInstance() {
        return INSTANCE;
    }

    private ImmutableList<IConfigValue> variableOptions = null;

    public static class Toggles
    {
        public static final ConfigHotkey OPEN_CONFIG_GUI = new ConfigHotkey(
                "Open config gui",
                "B, C",
                "Open the in-game config GUI"
        );


        public static final ConfigHotkey OPEN_TEST_GUI = new ConfigHotkey(
                "Open test gui",
                "G, T",
                "Open the in-game test GUI"
        );

        public static final ConfigBoolean USE_PLACEHOLDER_SYSTEM = new ConfigBoolean(
                "Use Placeholder System",
                true,
                "Should the placeholder system replace what it find in the commands and chats"
        );

        public static final ConfigBoolean USE_VARIABLE_SYSTEM = new ConfigBoolean(
                "Use Variable System",
                true,
                "Should the Variable system replace what it find in the commands and chats"
        );

        public static final ImmutableList<IConfigValue> OPTIONS = ImmutableList.of(
                OPEN_CONFIG_GUI,
                OPEN_TEST_GUI,
                USE_PLACEHOLDER_SYSTEM,
                USE_VARIABLE_SYSTEM
        );
    }

    public static ImmutableList<IConfigValue> createVariableConfigList() {
        var configs = new ArrayList<IConfigValue>();
        configs.add(Toggles.USE_PLACEHOLDER_SYSTEM);
        configs.add(Toggles.USE_VARIABLE_SYSTEM);
        VARIABLE_MAPS.forEach((name, properties) -> {
            configs.add(new ConfigString(
                    name,
                    name,
                    name
            ));
            configs.add(new ConfigString(
                    "    " + StringUtils.translate("b1ndc0mmandz.gui.button.config_gui.variables.value", name),
                    properties.value(),
                    StringUtils.translate("b1ndc0mmandz.gui.button.config_gui.variables.value.comment", name)
            ));
            configs.add(new ConfigBoolean(
                    "    " + StringUtils.translate("b1ndc0mmandz.gui.button.config_gui.variables.on", name),
                    properties.on(),
                    StringUtils.translate("b1ndc0mmandz.gui.button.config_gui.variables.on.comment", name)
            ));
        });
        Configs.getInstance().variableOptions = ImmutableList.copyOf(configs);
        return ImmutableList.copyOf(configs);
    }

    public static final Set<String> GUI_BLACKLIST = new HashSet<>();
    public static final Set<String> SLOT_BLACKLIST = new HashSet<>();

    public static final List<BindingNode> BINDING_NODES = new ArrayList<>();

    public static final Map<String, Variable> VARIABLE_MAPS = new HashMap<>();

//    public static final List<TailwindItem> TAILWIND_ITEM_LIST = TailwindMaterialAdapter.getExampleItemLists();

    public static List<VariableConfigWrapper> getVariableConfigs() {
        List<VariableConfigWrapper> configList = new ArrayList<>();
        for (Map.Entry<String, Variable> entry : VARIABLE_MAPS.entrySet()) {
            configList.add(new VariableConfigWrapper(entry.getKey(), entry.getValue()));
        }
        return configList;
    }

    public static void updateVariablesFromConfig(List<VariableConfigWrapper> configWrappers) {
        for (VariableConfigWrapper wrapper : configWrappers) {
            VARIABLE_MAPS.put(wrapper.getName(), wrapper.toVariable());
        }
    }

    public static void loadFromFile()
    {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead())
        {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject())
            {
                JsonObject root = element.getAsJsonObject();
                ConfigUtils.readConfigBase(root, "Toggles", Toggles.OPTIONS);

                getStrings(root, GUI_BLACKLIST, "guiBlacklist");
                getStrings(root, SLOT_BLACKLIST, "slotBlacklist");
                getList(root, BINDING_NODES, "bindingNodes",  BindingNode.class);
                getMap(root, VARIABLE_MAPS, "variables", String.class, Variable.class);

            }
        }

        if(BINDING_NODES.isEmpty()) {
            var node = new BindingNode(
                    "Example",
                    "LEFT_CONTROL,C",
                    "This is an example of how to configure the file"
            );
            node.getCommandNodes().add(new CommandNode(
                    "Example Command",
                    "say Hello,world!",
                    "Say hello world to all other players in the server!"
            ));
            node.getCommandNodes().add(new CommandNode(
                    "Next Command",
                    "say End!",
                    "You can add more to this list, and will be executed in sequence!"
            ));
            BINDING_NODES.add(node);
        }

        if(VARIABLE_MAPS.isEmpty()) {
            VARIABLE_MAPS.put("ExampleVariable", new Variable("114514", true));
        }
    }

    public static void saveToFile()
    {
        File dir = FabricLoader.getInstance().getConfigDir().toFile();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs())
        {
            JsonObject root = new JsonObject();

            if(Hotkeys.HOTKEY_LIST.size() == BINDING_NODES.size()) {
                for(int i = 0; i < BINDING_NODES.size(); i++) {
                    var node = BINDING_NODES.get(i);
                    var key = Hotkeys.HOTKEY_LIST.get(i);
                    node.setHotkey(key);
                    node.setHotkeyStorageString(key.getStringValue());
                    System.out.println("Set Node " + node.getName() + " with Key " + key.getStringValue());
                }
            } else {
                System.out.println("ERROR: HOTKEY LIST SIZE NOT EQUALS WITH BINDING NODE LIST SIZE!!!!!");
            }

            if(GuiConfigs.variableConfigs != null && GuiConfigs.variableConfigs.size() == VARIABLE_MAPS.size() * 3 + 2) {
                for(int i = 0; i < VARIABLE_MAPS.size(); i++) {
                    var name = GuiConfigs.variableConfigs.get(3 * i + 2 + 0);
                    var value = GuiConfigs.variableConfigs.get(3 * i + 2 + 1);
                    var on = GuiConfigs.variableConfigs.get(3 * i + 2 + 2);

                    assert name instanceof ConfigString;
                    assert value instanceof ConfigString;
                    assert on instanceof ConfigBoolean;

                    var variableName = name.getName();
                    var newName = ((ConfigString) name).getStringValue();

                    var variableValue = ((ConfigString) value).getStringValue();
                    var variableOn = ((ConfigBoolean) on).getBooleanValue();
                    VARIABLE_MAPS.remove(variableName);
                    VARIABLE_MAPS.put(newName, new Variable(variableValue, variableOn));
                }
                System.out.println(VARIABLE_MAPS.toString());
            } else {
                System.out.println("ERROR: VARIABLE MAP SIZE NOT EQUALS WITH BINDING NODE LIST SIZE!!!!!");
            }

            ConfigUtils.writeConfigBase(root, "Toggles", Toggles.OPTIONS);

            writeStrings(root, GUI_BLACKLIST, "guiBlacklist");
            writeStrings(root, SLOT_BLACKLIST, "slotBlacklist");
            writeList(root, BINDING_NODES, "bindingNodes");
            writeMap(root, VARIABLE_MAPS, "variables");

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));

            System.out.println("CONFIG SAVED!");
        }
    }

    @Override
    public void load()
    {
        loadFromFile();
    }

    @Override
    public void save()
    {
        saveToFile();
    }

    private static void getStrings(JsonObject obj, Set<String> outputSet, String arrayName)
    {
        outputSet.clear();

        if (JsonUtils.hasArray(obj, arrayName))
        {
            JsonArray arr = obj.getAsJsonArray(arrayName);
            final int size = arr.size();

            for (int i = 0; i < size; i++)
            {
                outputSet.add(arr.get(i).getAsString());
            }
        }
    }

    private static <T> void getList(JsonObject obj, List<T> outputList, String arrayName, Class<T> clazz) {
        outputList.clear(); // 清空输出列表
        Gson gson = new Gson();
        if (JsonUtils.hasArray(obj, arrayName)) {
            JsonArray arr = obj.getAsJsonArray(arrayName);
            final int size = arr.size();

            for (int i = 0; i < size; i++) {
                // 使用 Gson 将 JsonElement 转换为 T 类型的对象
                T element = gson.fromJson(arr.get(i), clazz);
                outputList.add(element);
            }
        }
    }


    private static <T> void writeList(JsonObject obj, List<T> inputList, String arrayName) {
        if (!inputList.isEmpty()) {
            JsonArray arr = new JsonArray();
            Gson gson = new Gson();
            for (T item : inputList) {
                // 使用 Gson 序列化泛型对象到 JsonElement
                JsonElement element = gson.toJsonTree(item);
                arr.add(element);
            }

            obj.add(arrayName, arr);
        }
    }

    private static void writeStrings(JsonObject obj, Set<String> inputSet, String arrayName)
    {
        if (inputSet.isEmpty() == false)
        {
            JsonArray arr = new JsonArray();

            for (String str : inputSet)
            {
                arr.add(str);
            }

            obj.add(arrayName, arr);
        }
    }

    private static <K, V> void getMap(JsonObject obj, Map<K, V> outputMap, String mapName, Class<K> keyClass, Class<V> valueClass) {
        outputMap.clear(); // 清空输出 Map
        Gson gson = new Gson();
        if (JsonUtils.hasObject(obj, mapName)) {
            JsonObject mapObj = obj.getAsJsonObject(mapName);

            for (Map.Entry<String, JsonElement> entry : mapObj.entrySet()) {
                // 使用 Gson 将 JsonElement 转换为 K 类型的键 和 V 类型的值
                K key = gson.fromJson(entry.getKey(), keyClass);
                V value = gson.fromJson(entry.getValue(), valueClass);
                outputMap.put(key, value);
            }
        }
    }

    private static <K, V> void writeMap(JsonObject obj, Map<K, V> inputMap, String mapName) {
        if (!inputMap.isEmpty()) {
            JsonObject mapObj = new JsonObject();
            Gson gson = new Gson();
            for (Map.Entry<K, V> entry : inputMap.entrySet()) {
                // 使用 Gson 将键和值分别序列化为 JsonElement
                JsonElement keyElement = gson.toJsonTree(entry.getKey());
                JsonElement valueElement = gson.toJsonTree(entry.getValue());
                mapObj.add(keyElement.getAsString(), valueElement);
            }

            obj.add(mapName, mapObj);
        }
    }

}
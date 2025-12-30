package com.jackyblackson.placeholders;

import com.jackyblackson.config.malilibconfig.Configs;

public class VariableParser {
    public static String parse(String text) {
//        var variableArray = Configs.VARIABLE_MAPS.entrySet().stream().map(variable -> {
//            if(variable.getValue().on()) {
//                return Map.entry(variable.getKey(), variable.getValue().value());
//            } else {
//                return null;
//            }
//        }).toList();
//        Map<String, Text> variableMap = new HashMap<>();
//        variableArray.forEach(entry -> {
//            variableMap.put(entry.getKey(), Text.literal(entry.getValue()));
//        });
//        return  String.valueOf(Placeholders.parseText(Text.literal(text),
//                Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
//                variableMap)); // parse the inputText
        StringBuilder textBuilder = new StringBuilder(text);
        Configs.VARIABLE_MAPS.forEach((name, properties) -> {
            if(properties.on()) {
                String value = properties.value();
                String namePattern1 = "%" + name + "%";
                String namePattern2 = "${" + name + "}";
                String namePattern3 = "{" + name + "}";
                int start = 0;

                while ((start = textBuilder.indexOf(namePattern1, start)) != -1) {
                    textBuilder.replace(start, start + namePattern1.length(), value);
                    start += value.length();
                }
                start = 0;
                while ((start = textBuilder.indexOf(namePattern2, start)) != -1) {
                    textBuilder.replace(start, start + namePattern2.length(), value);
                    start += value.length();
                }
                start = 0;
                while ((start = textBuilder.indexOf(namePattern3, start)) != -1) {
                    textBuilder.replace(start, start + namePattern3.length(), value);
                    start += value.length();
                }
            }
        });

        text = textBuilder.toString();

        return text;
    }
}

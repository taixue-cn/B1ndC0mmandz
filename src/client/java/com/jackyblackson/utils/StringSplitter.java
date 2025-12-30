package com.jackyblackson.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplitter {
    private final Pattern pattern; // 缓存编译后的正则表达式
    private final List<String> delimiters; // 缓存分隔符列表

    // 构造函数，初始化分隔符列表并编译正则表达式
    public StringSplitter(List<String> delimiters) {
        this.delimiters = new ArrayList<>(delimiters);
        this.pattern = compilePattern(delimiters);
    }

    // 动态构建并编译正则表达式
    private Pattern compilePattern(List<String> delimiters) {
        StringBuilder regexBuilder = new StringBuilder();
        regexBuilder.append("(");
        for (String delimiter : delimiters) {
            // 对每个分隔符进行转义
            regexBuilder.append(Pattern.quote(delimiter));
            regexBuilder.append("|");
        }
        // 删除最后一个多余的 "|"
        regexBuilder.deleteCharAt(regexBuilder.length() - 1);
        regexBuilder.append(")");

        // 编译正则表达式
        return Pattern.compile(regexBuilder.toString());
    }

    // 分割字符串并返回两个列表：非分隔符节点和分隔符节点
    public List<List<String>> splitIntoTwoLists(String input) {
        Matcher matcher = pattern.matcher(input);
        List<String> nonDelimiters = new ArrayList<>(); // 非分隔符节点
        List<String> delimitersList = new ArrayList<>(); // 分隔符节点
        int lastEnd = 0;

        // 遍历匹配的分隔符
        while (matcher.find()) {
            // 添加分隔符之前的部分（非分隔符节点）
            if (matcher.start() > lastEnd) {
                nonDelimiters.add(input.substring(lastEnd, matcher.start()));
                delimitersList.add(null); // 对应位置的分隔符节点为 null
            }
            // 添加分隔符本身（分隔符节点）
            nonDelimiters.add(null); // 对应位置的非分隔符节点为 null
            delimitersList.add(matcher.group());
            lastEnd = matcher.end();
        }

        // 添加最后一个分隔符之后的部分（非分隔符节点）
        if (lastEnd < input.length()) {
            nonDelimiters.add(input.substring(lastEnd));
            delimitersList.add(null); // 对应位置的分隔符节点为 null
        }

        // 返回两个列表
        List<List<String>> result = new ArrayList<>();
        result.add(nonDelimiters);
        result.add(delimitersList);
        return result;
    }

    // 将分离后的两个列表重新连接回字符串
    public String join(List<String> nonDelimiters, List<String> delimitersList) {
        if (nonDelimiters.size() != delimitersList.size()) {
            throw new IllegalArgumentException("两个列表的长度必须相同");
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < nonDelimiters.size(); i++) {
            String nonDelimiter = nonDelimiters.get(i);
            String delimiter = delimitersList.get(i);

            // 如果非分隔符节点不为 null，则添加到结果中
            if (nonDelimiter != null) {
                result.append(nonDelimiter);
            }
            // 如果分隔符节点不为 null，则添加到结果中
            if (delimiter != null) {
                result.append(delimiter);
            }
        }

        return result.toString();
    }

    // 获取当前使用的分隔符列表
    public List<String> getDelimiters() {
        return new ArrayList<>(delimiters);
    }

    // 示例用法
    public static void main(String[] args) {
        // 定义分隔符列表
        List<String> delimiters = List.of("/", "#");
        // 初始化 StringSplitter
        StringSplitter splitter = new StringSplitter(delimiters);

        // 测试字符串
        String input = "I/love#you";
        List<List<String>> result = splitter.splitIntoTwoLists(input);

        // 输出结果
        List<String> nonDelimiters = result.get(0);
        List<String> delimitersList = result.get(1);

        System.out.println("非分隔符节点: " + nonDelimiters);
        System.out.println("分隔符节点:   " + delimitersList);
    }
}

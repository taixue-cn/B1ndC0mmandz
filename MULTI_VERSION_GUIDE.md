# Minecraft 多版本模组开发架构指南

为了同时维护 Minecraft 1.21.1 和 1.21.4 (或未来的 1.21.11/1.22) 版本，并保证代码库的整洁与可维护性，强烈建议采用 **Gradle Multi-Project（多模块）架构**。

这种架构的核心思想是：**将绝大多数（80%-95%）通用的业务逻辑代码（如 UI、配置、通用事件处理）放在一个共享模块中，只有极少数涉及 Minecraft 内部 API 变动的代码放在各个版本专用的模块中。**

## 1. 推荐的项目结构

你需要将目前的扁平结构重构为多模块结构。

```text
b1nd-c0mmandz/
├── common/                  <-- 【核心】存放所有通用代码、资源、UI (Owo-Lib/Malilib)
│   ├── src/main/java
│   └── src/main/resources
├── 1.21.1/                  <-- 1.21.1 专用模块
│   ├── src/main/java        <-- 仅存放该版本特有的 Mixin 或实现
│   └── build.gradle         <-- 指定 Minecraft 1.21.1 依赖
├── 1.21.4/                  <-- 1.21.4 (或更高版本) 专用模块
│   ├── src/main/java
│   └── build.gradle         <-- 指定 Minecraft 1.21.4 依赖
├── build.gradle             <-- 根构建文件，管理公共依赖
├── settings.gradle          <-- 定义包含哪些模块 (include 'common', '1.21.1', '1.21.4')
└── gradle.properties        <-- 存放各个版本的版本号变量
```

## 2. Git 分支管理策略

**最佳实践：单分支策略 (Single Branch Strategy)**

不要为每个 Minecraft 版本创建一个 Git 分支（例如不要创建 `branch-1.21.1` 和 `branch-1.21.4`）。

*   **主分支 (`main` / `master`)**：永远包含所有支持版本的代码。
*   **开发方式**：当你修改 `common` 里的一个功能时，Gradle 会同时构建 1.21.1 和 1.21.4 的 jar 包。这意味着你一次提交就能修复所有版本的 Bug。
*   **发布**：打 Tag 发布时，CI/CD 脚本会一次性产出 `mod-1.21.1.jar` 和 `mod-1.21.4.jar`。

**为什么不推荐按版本分分支？**
如果你将 1.21.1 和 1.21.4 分在不同分支，当你修复一个通用的 UI Bug 时，你需要 `cherry-pick` 代码到另一个分支。久而久之，分支差异会越来越大，维护成本会指数级上升。

## 3. 实现细节与抽象层

### A. 通用代码 (`common`)
你的 `BindingNode`、`Configs`、`GuiConfigs`、`BindingConfigScreen` 等逻辑代码几乎完全不依赖特定版本的 NMS (net.minecraft.server) 混淆变化。这些应该全部移入 `common` 模块。

### B. 处理版本差异 (Abstraction)
当遇到 Minecraft API 变动时（例如 1.21.1 到 1.21.4 若某些方法签名变了），不要在 `common` 里写 `if (version == "1.21.1")`。

**做法：**
1.  在 `common` 中定义一个接口或抽象类。
    ```java
    // 在 common 模块
    public interface IVersionPlatform {
        void sendChatMessage(PlayerEntity player, String message);
    }
    ```
2.  在 `1.21.1` 和 `1.21.4` 模块中分别实现它。
    ```java
    // 在 1.21.1 模块
    public class VersionImpl implements IVersionPlatform {
        public void sendChatMessage(PlayerEntity player, String message) {
            // 1.21.1 的具体写法
        }
    }
    ```
3.  使用 `ServiceLoader` 或简单的静态注入在模组加载时注入实现。

## 4. Gradle 配置关键点 (build.gradle)

根目录的 `build.gradle` 负责配置所有子项目的通用部分（比如仓库、Java 版本）。

**settings.gradle:**
```groovy
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven { url "https://maven.fabricmc.net/" }
    }
}

include 'common'
include '1.21.1'
include '1.21.4'

rootProject.name = 'b1nd-c0mmandz'
```

**子模块 (例如 1.21.1/build.gradle):**
```groovy
dependencies {
    // 依赖 common 项目
    implementation project(path: ":common", configuration: "namedElements")
    
    // 特定版本的 Minecraft
    minecraft "com.mojang:minecraft:1.21.1"
    mappings "net.fabricmc:yarn:1.21.1+build.3:v2"
    modImplementation "net.fabricmc:fabric-loader:0.16.5"
    
    // 其他特定版本的库
}
```

## 5. 迁移现有项目的步骤

1.  **备份**：在开始前，提交当前所有更改。
2.  **创建目录**：新建 `common`、`1.21.1` 文件夹。
3.  **移动代码**：
    *   将 `src/main` 和 `src/client` 移动到 `common/src/main` 和 `common/src/client`。
    *   这是最“激进”但最有效的方法。先假设所有代码都是通用的。
4.  **设置 Gradle**：修改 `settings.gradle` 包含新模块。
5.  **修复报错**：尝试构建。如果发现某些类在 `common` 里找不到（因为依赖了特定版本的 Minecraft 方法且该方法在另一版本不存在），将该类提取出来，使用“抽象接口”模式，分别放入 `1.21.1` 文件夹中。
6.  **添加新版本**：复制 `1.21.1` 文件夹为 `1.21.4`，修改其 `build.gradle` 中的版本号，然后修复编译错误（适配新 API）。

## 6. 工具推荐

*   **Stonecutter**: 这是一个更轻量级的多版本管理工具。它允许你在同一个源码树中使用类似 `#if version >= 1.21.4` 的注释来控制代码。如果你的模组版本差异非常小（只是 mapping 名字不同，逻辑没变），Stonecutter 比多模块更简单。
*   **Architectury**: 如果你不仅要支持多版本，还要支持 NeoForge/Fabric 跨平台，这是标准解决方案。

**总结建议：**
鉴于你目前是在 Fabric 上开发，且 1.21.1 到 1.21.4 变化可能涉及组件或协议更新，**标准的 Gradle 多模块架构** 是最稳健的选择。它能迫使你分离业务逻辑和底层实现，长期来看代码质量最高。

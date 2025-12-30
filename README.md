# b1nd-c0mmandz
![Logo](design/logo.svg)

**b1nd-c0mmandz** is a powerful Minecraft Fabric mod that allows you to bind complex command macros to keyboard shortcuts. Whether you need to automate chat messages, quickly execute server commands, or manage complex workflows, this mod provides a user-friendly and highly configurable solution.

## ✨ Features

*   **Unlimited Keybindings:** Bind any key on your keyboard to trigger commands.
*   **Command Macros:** Execute multiple commands in sequence with a single keypress.
*   **Modern UI:** A clean, user-friendly configuration interface built with [Owo-Lib](https://github.com/wisp-forest/owo-lib), accessible directly in-game.
*   **Legacy Support:** Fully integrated with [MaLiLib](https://github.com/maruohon/malilib) for robust hotkey handling.
*   **Variables & Placeholders:**
    *   Define custom variables to reuse values across different commands.
    *   Full support for [Placeholder API](https://placeholders.pb4.eu/), allowing dynamic command content (e.g., player names, coordinates, server stats).
*   **Configurable via JSON:** Advanced users can edit the `b1ndc0mmandz.json` file directly for bulk management.

## 📥 Installation

This mod requires the following dependencies:
1.  **[Fabric API](https://modrinth.com/mod/fabric-api)**
2.  **[MaLiLib](https://www.curseforge.com/minecraft/mc-mods/malilib)**
3.  **[Owo-Lib](https://modrinth.com/mod/owo-lib)**
4.  **[Placeholder API](https://modrinth.com/mod/placeholder-api)** (Fabric)

Download the latest release of **b1nd-c0mmandz** and place it in your `mods` folder along with the dependencies.

## 🎮 Usage

### Opening the Configuration
By default, you can open the main configuration menu by pressing **`B` + `C`**.

### Managing Bindings
1.  **Open the Menu:** Press `B` + `C`.
2.  **Enter Editor:** Click the **"Edit Bindings (GUI)"** button.
3.  **Add Binding:** Click "Add New Binding".
4.  **Edit Details:**
    *   Click "Edit" on your new binding.
    *   Click **"Edit Name / Hotkey / Comment"** to set the key trigger (e.g., `LEFT_CONTROL, P`) and give it a recognizable name.
    *   **Add Commands:** In the main edit screen, add one or more commands to the list (e.g., `/say Hello World`).
5.  **Save:** Click "Save" and close the menu.

### Using Variables
You can define variables in the config to easily update shared values (like a teammate's name or a coordinate) across multiple bindings without editing each macro individually.

## 🛠️ Project Structure

This project uses a **multi-module Gradle architecture** to support multiple Minecraft versions:

*   `common/`: Contains the source code and resources.
*   `1.21.1/`: Specific configuration for Minecraft 1.21.1.

## 📄 License

[MIT License](LICENSE) (Assuming standard open source, please verify)
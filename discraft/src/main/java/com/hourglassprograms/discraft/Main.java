package com.hourglassprograms.discraft;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Startup
        // Reloads
        // Plugin reloads

    }

    @Override
    public void onDisable() {
        // Shutdown
        // Reloads
        // Plugin reloads
    }

    // /hello --> "Welcome!"

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("hello")) {
            if (sender instanceof Player) { // Sender is player
                Player player = (Player) sender;
                if (player.hasPermission("hello.use")) {
                    player.sendMessage(ChatColor.BOLD + "Hello and welcome :P");

                } else {
                    player.sendMessage(ChatColor.BOLD + "You do not have the perms to do this");
                }

                // player.sendMessage(ChatColor.translatealternateColorcodes("&","&2Hello
                // &3World"));
                return true;
            } else {
                // Console
                sender.sendMessage(ChatColor.BOLD + "Hey Console!");
                return true;
            }
        }
        return false;
    }
}
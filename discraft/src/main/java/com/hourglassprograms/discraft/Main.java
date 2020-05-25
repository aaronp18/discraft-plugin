package com.hourglassprograms.discraft;

import java.util.function.Function;

import javax.sound.sampled.Port;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import express.Express;
import express.ExpressListener;

public class Main extends JavaPlugin {
    // Use to get jar with all dependencies
    // mvn clean compile assembly:single

    @Override
    public void onEnable() {
        // Startup
        // Reloads
        // Plugin reloads
        getLogger().info("DisCraft has loaded");
        loadExpress();
    }

    @Override
    public void onDisable() {
        // Shutdown
        // Reloads
        // Plugin reloads
    }

    public void loadExpress() {
        // ? Gets port from config
        int port = 9000;
        Express app = new Express() {
            {
                // Define Root Greeting
                get("/", (req, res) -> res.send("Welcome to DisCraft"));
                get("/run/:command/:hash", (req, res) -> {
                    runCommand();
                    res.send(req.getParam("command") + " ran successfully");
                });

                // Start server
                listen(port);
            }
        };
        getLogger().info("Discraft server running on port: " + port);

    }

    public void runCommand() {
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                getLogger().info("Discraft command run...");
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "say hello");
            }
        });

    }

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
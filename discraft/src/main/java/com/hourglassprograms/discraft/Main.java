package com.hourglassprograms.discraft;

import java.util.function.Function;

import javax.sound.sampled.Port;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
        loadConfig();
        loadExpress();
    }

    @Override
    public void onDisable() {
        // Shutdown
        // Reloads
        // Plugin reloads
        this.saveDefaultConfig();
    }

    public void loadConfig() {
        FileConfiguration config = this.getConfig();
        config.addDefault("port", 9000);
        config.addDefault("authkey", "");
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void loadExpress() {
        // ? Gets port from config
        FileConfiguration config = this.getConfig();
        int port = config.getInt("port");
        Express app = new Express() {
            {
                // Define Root Greeting
                get("/", (req, res) -> res.send("Welcome to DisCraft"));
                get("/run/:command/:hash", (req, res) -> {
                    runCommand(req.getParam("command"));
                    res.send(req.getParam("command") + " ran successfully");
                });

                // Start server
                listen(port);
            }
        };
        getLogger().info("Discraft server running on port: " + port);

    }

    public void runCommand(String cmd) {

        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {

                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                getLogger().info("Ran: " + cmd);
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
        if (label.equalsIgnoreCase("discraft")) {
            getLogger().info("Discraft command ran: " + label + " " + cmd.getLabel());
            if (args.length == 0) {
                // Display help for discraft

                return false;
            } else if (args[0].equalsIgnoreCase("port")) {
                if (sender instanceof Player) { // Sender is player
                    Player player = (Player) sender;
                    if (player.hasPermission("port.get")) {
                        player.sendMessage(ChatColor.BOLD + "The Port that discraft is running on is: " + ChatColor.RED
                                + this.getConfig().getInt("port"));

                    } else {
                        player.sendMessage(ChatColor.BOLD + "You do not have the permission to do this");
                    }

                    // player.sendMessage(ChatColor.translatealternateColorcodes("&","&2Hello
                    // &3World"));
                    return true;
                } else {
                    // Console
                    sender.sendMessage(ChatColor.BOLD + "The Port that discraft is running on is: " + ChatColor.RED
                            + this.getConfig().getInt("port"));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("auth")) {
                if (sender instanceof Player) { // Sender is player
                    Player player = (Player) sender;
                    if (player.hasPermission("auth.get")) {
                        player.sendMessage(ChatColor.BOLD + "The authkey for this server for Discraft is: "
                                + ChatColor.RED + this.getConfig().getString("authkey"));

                    } else {
                        player.sendMessage(ChatColor.BOLD + "You do not have the permission to do this");
                    }

                    // player.sendMessage(ChatColor.translatealternateColorcodes("&","&2Hello
                    // &3World"));
                    return true;
                } else {
                    // Console
                    sender.sendMessage(ChatColor.BOLD + "The authkey for this server for Discraft is: " + ChatColor.RED
                            + this.getConfig().getString("authkey"));
                    return true;
                }
            }

        }
        return false;
    }
}
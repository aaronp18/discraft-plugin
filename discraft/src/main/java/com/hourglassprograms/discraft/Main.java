package com.hourglassprograms.discraft;



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
                    if(checkHash(req.getParam("command"), req.getParam("hash"))){
                        runCommand(req.getParam("command"));
                        res.send("200 - " + req.getParam("command") + " was ran successfully");
                    }
                    else {
                        res.send("401 - Hash was wrong. Perhaps the auth key is incorrect?");
                    }
                });
                // get("/*", (req, res) -> res.send("404 - Wrong address"));

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
    public boolean checkHash(String command, String hash) {
        // String authkey = getConfig().getString("authkey");
        // String message = command + authkey;
        // getLogger().info("Message: " + message);
        // byte[] bytesOfMessage = message.getBytes("UTF-8");

        // MessageDigest md = MessageDigest.getInstance("MD5");
        // byte[] thedigest = md.digest(bytesOfMessage);
        // getLogger().info(thedigest.toString());

        return true;
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
            if (args.length == 0) {
                // Display help for discraft

                return false;
            } else if (args[0].equalsIgnoreCase("port")) {
                String message = ChatColor.BOLD + "The Port that discraft is running on is: " + ChatColor.RED
                        + this.getConfig().getInt("port");
                if (sender instanceof Player) { // Sender is player
                    Player player = (Player) sender;
                    if (player.hasPermission("port.get")) {
                        player.sendMessage(message);

                    } else {
                        player.sendMessage(ChatColor.BOLD + "You do not have the permission to do this");
                    }

                    return true;
                } else {
                    // Console
                    sender.sendMessage(message);
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("auth")) {
                String message = ChatColor.BOLD + "The Authkey for this server for Discraft is: " + ChatColor.RED
                        + this.getConfig().getString("authkey");
                if (sender instanceof Player) { // Sender is player
                    Player player = (Player) sender;
                    if (player.hasPermission("auth.get")) {
                        player.sendMessage(message);

                    } else {
                        player.sendMessage(ChatColor.BOLD + "You do not have the permission to do this");
                    }

                    return true;
                } else {
                    // Console
                    sender.sendMessage(message);
                    return true;
                }
            }

        }
        return false;
    }
}
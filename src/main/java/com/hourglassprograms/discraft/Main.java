package com.hourglassprograms.discraft;

import java.security.MessageDigest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;

import java.net.URL;
import java.security.NoSuchAlgorithmException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import express.Express;

public class Main extends JavaPlugin {
    // Use to get jar with all dependencies
    // ! mvn clean compile assembly:single
    private HttpURLConnection con;

    @Override
    public void onEnable() {
        // Startup
        // Reloads
        // Plugin reloads
        getLogger().info("DisCraft has loaded");
        loadConfig();
        loadExpress();

        if (this.getConfig().getString("authkey").equals("")) {
            getLogger().info(
                    "The Authkey has not been set yet. Simply use d!auth in the discord to get the authkey and place that in the config.yml");
        } else {
            getLogger().info("Linking to Discraft server...");
            LinkDiscraft();
        }

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
                    if (checkHash(req.getParam("command"), req.getParam("hash"))) {
                        runCommand(req.getParam("command"), false);
                        res.send("200 - " + req.getParam("command") + " was executed successfully");
                    } else {
                        res.send("401 - Hash doesn't match. Perhaps the auth key is incorrect?");
                    }
                });
                get("*", (req, res) -> res.send("404 - Page not found"));

                // Start server
                listen(port);
            }
        };
        getLogger().info("Discraft server running on port: " + port);

    }

    public void runCommand(String cmd, boolean silent) {

        Bukkit.getScheduler().runTask(this, new Runnable() {
            @Override
            public void run() {

                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
                if (!silent) {
                    getLogger().info("Executed " + cmd + " from Discord");
                }

            }
        });

    }

    // Checks the hash sent from the get request and compares it locally to make
    // sure nothging has been changed
    public boolean checkHash(String command, String hash) {
        if (this.getConfig().getString("authkey").equals("")) {
            getLogger().info(
                    "The Authkey has not been set yet. Simply use d!auth in the discord to get the authkey and place that in the config.yml");
            return false;
        } else {
            String authkey = this.getConfig().getString("authkey");
            String message = command + authkey;
            String hashtext = hash(message);
            if (hashtext.equals(hash)) {
                return true;
            } else {
                return false;
            }
        }

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
        if (label.equalsIgnoreCase("discraft") || label.equalsIgnoreCase("d")) {
            if (args.length == 0) {
                // Display help for discraft

                return false;
            } else if (args[0].equalsIgnoreCase("port")) {
                if (args.length == 2) {
                    // Therfore to set port
                    if (isNumeric(args[1])) {

                        String message = ChatColor.BOLD + "Port set to: " + ChatColor.RED + args[1];
                        if (sender instanceof Player) { // Sender is player
                            Player player = (Player) sender;
                            if (player.hasPermission("port.set")) {
                                player.sendMessage(message);
                                this.getConfig().set("port", Integer.parseInt(args[1]));
                                saveConfig();

                            } else {
                                player.sendMessage(ChatColor.BOLD + "You do not have the permission to do this");
                            }

                            return true;
                        } else {
                            // Console
                            sender.sendMessage(message);
                            this.getConfig().set("port", Integer.parseInt(args[1]));
                            saveConfig();
                            return true;
                        }
                    } else {
                        String message = ChatColor.BOLD + "Port setting failed, please only use an integer";
                        if (sender instanceof Player) { // Sender is player
                            Player player = (Player) sender;
                            player.sendMessage(message);
                            return true;
                        } else {
                            // Console
                            sender.sendMessage(message);
                            return true;
                        }
                    }

                } else if (args.length == 1) {
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
                }

            } else if (args[0].equalsIgnoreCase("auth")) {

                if (args.length > 1) {
                    // Then is setting

                    String message = ChatColor.BOLD + "The Authkey for this server has been updated";

                    if (sender instanceof Player) { // Sender is player
                        Player player = (Player) sender;
                        if (player.hasPermission("auth.set")) {
                            player.sendMessage(message);
                            getConfig().set("authkey", args[1]);
                            saveConfig();
                        } else {
                            player.sendMessage(ChatColor.BOLD + "You do not have the permission to do this");
                        }

                        return true;
                    } else {
                        // Console
                        getConfig().set("authkey", args[1]);
                        saveConfig();
                        sender.sendMessage(message);

                        return true;
                    }
                } else {
                    String message = ChatColor.BOLD + "The Authkey for this server for Discraft is: " + ChatColor.RED
                            + this.getConfig().getString("authkey");
                    if (this.getConfig().getString("authkey").equals("")) {
                        message = ChatColor.BOLD
                                + "The authkey has not been set yet. Simply use d!auth in the discord to get the authkey and place that in the config.yml";
                    }
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
            } else if (args[0].equalsIgnoreCase("link")) {
                String message = ChatColor.BOLD + "Linking to Discraft server...";

                if (sender instanceof Player) { // Sender is player
                    Player player = (Player) sender;
                    if (player.hasPermission("link.use")) {
                        player.sendMessage(message);
                        LinkDiscraft();

                    } else {
                        player.sendMessage(ChatColor.BOLD + "You do not have the permission to do this");
                    }

                    return true;
                } else {
                    // Console
                    sender.sendMessage(message);
                    LinkDiscraft();
                    return true;
                }
            }

        }
        return false;
    }

    public boolean LinkDiscraft() {
        try {
            String authkey = this.getConfig().getString("authkey");
            if (this.getConfig().getString("authkey").equals("")) {
                getLogger().info(
                        "The Authkey has not been set yet. Simply use d!auth in the discord to get the authkey and place that in the config.yml");
                runCommand(
                        "say The Authkey has not been set yet. Simply use d!auth in the discord to get the authkey and place that in the config.yml",
                        true);
                return false;
            }
            String ip = Bukkit.getServer().getIp();
            if (ip.equals("")) {
                // IP is empty, so must be localhost
                ip = "localhost";
            }

            String message = ip + authkey;
            getLogger().info("Current IP: " + ip);
            String hash = hash(message);
            URL url = new URL("http://discraft.hourglassprograms.com/link/" + ip + "/" + hash);
            try {
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                StringBuilder content;

                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {

                    String line;
                    content = new StringBuilder();

                    while ((line = in.readLine()) != null) {

                        content.append(line);
                        content.append(System.lineSeparator());
                    }
                }

                if (content.toString().contains("200")) {
                    // Then success
                    runCommand("say Discraft has successfully been linked", true);
                } else {
                    runCommand("say An error has occured - " + content.toString(), true);
                    getLogger().info("Error: " + (content.toString()));
                }
            } catch (Exception err) {
                runCommand("say An error has occured: " + err.toString(), true);
                getLogger().info("Error: " + (err.toString()));

            } finally {

                con.disconnect();
                return true;
            }
        } catch (Exception err) {
            runCommand("say An error has occured - " + err.toString(), true);
            getLogger().info("Outside Error: " + (err.toString()));
            return false;
        }

    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public String hash(String inputText) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(inputText.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;

        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

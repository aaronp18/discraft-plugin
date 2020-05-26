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
    // mvn clean compile assembly:single
    private HttpURLConnection con;

    @Override
    public void onEnable() {
        // Startup
        // Reloads
        // Plugin reloads
        getLogger().info("DisCraft has loaded");
        loadConfig();
        loadExpress();

        getLogger().info("Linking to Discraft server...");
        LinkDiscraft();

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
                        res.send("200 - " + req.getParam("command") + " was ran successfully");
                    } else {
                        res.send("401 - Hash  didnt match. Perhaps the auth key is incorrect?");
                    }
                });
                get("/*", (req, res) -> res.send("404 - Wrong address"));

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
                    getLogger().info("Ran: " + cmd);
                }

            }
        });

    }

    // Checks the hash sent from the get request and compares it locally to make
    // sure nothging has been changed
    public boolean checkHash(String command, String hash) {
        String authkey = getConfig().getString("authkey");
        String message = command + authkey;
        String hashtext = hash(message);
        if (hashtext.equals(hash)) {
            return true;
        } else {
            return false;
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
            String authkey = getConfig().getString("authkey");
            String ip = Bukkit.getServer().getIp();
            if (ip.equals("")) {
                // IP is empty, so must be localhost
                ip = "localhost";
            }

            String message = ip + authkey;
            getLogger().info("Current IP: " + ip);
            String hash = hash(message);
            URL url = new URL("http://192.168.0.23:5000/link/" + ip + "/" + hash); // TODO Change to new server IP
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

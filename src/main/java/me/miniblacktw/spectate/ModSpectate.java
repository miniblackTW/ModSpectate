package me.miniblacktw.spectate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class ModSpectate extends JavaPlugin implements CommandExecutor {

    private HashMap<UUID, Location> specLocations = new HashMap<>();

    @Override
    public void onEnable() {
        this.getCommand("mspec").setExecutor(this);
        Bukkit.getLogger().info("Loaded ModSpectate");
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("Unloaded ModSpectate");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You can't use this command from console");
            return true;
        }

        Player player = (Player) sender;
        UUID playerId = player.getUniqueId();

        if (command.getName().equalsIgnoreCase("mspec")) {
            if (!player.hasPermission("modspec.*")) {
                player.sendMessage(colorize(getConfig().getString("no-permission", "&cYou don't have permission")));
                return true;
            }

            GameMode spectatorMode = GameMode.valueOf(getConfig().getString("enter-gamemode", "SPECTATOR"));
            GameMode survivalMode = GameMode.valueOf(getConfig().getString("exit-gamemode", "SURVIVAL"));

            if (player.getGameMode() == survivalMode) {
                specLocations.put(playerId, player.getLocation());
                player.setGameMode(spectatorMode);
                player.sendMessage(colorize(getConfig().getString("enter-spectator", "&8[&bYourServer&8] &bEnabled your Spectate mode, &c/mspec &bagain to exit")));
            } else if (player.getGameMode() == spectatorMode) {
                if (specLocations.containsKey(playerId)) {
                    player.teleport(specLocations.get(playerId));
                    specLocations.remove(playerId);
                }
                player.setGameMode(survivalMode);
                player.sendMessage(colorize(getConfig().getString("exit-spectator", "&8[&bYourServer&8] &bTeleported you to the last location")));
            }
        }
        return true;
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
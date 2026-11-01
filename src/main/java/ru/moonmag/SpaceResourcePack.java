package ru.moonmag;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpaceResourcePack extends JavaPlugin implements Listener, TabExecutor {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("ssrp").setExecutor(this);
        getCommand("ssrp").setTabCompleter(this);

        new ru.moonmag.Metrics(this, 25598);
        new UpdateChecker(this).checkForUpdates();

        console("#FF7C00╔");
        console("#FF7C00║ &fЗапуск плагина...");
        console("#FF7C00║ #00FF17Плагин запустился! &fКодер: #FF6E00m0onmag");
        console("#FF7C00║ #00F5FFhttps://t.me/spacestudiomc");
        console("#FF7C00╚");
    }

    @Override
    public void onDisable() {
        console("#FF7C00╔");
        console("#FF7C00║ &fОтключение плагина...");
        console("#FF7C00║ #FF0000Плагин отключился! &fКодер: #FF6E00m0onmag");
        console("#FF7C00║ #00F5FFhttps://t.me/spacestudiomc");
        console("#FF7C00╚");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("spaceresourcepack")) return false;

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("ssrp.admin")) {
                reloadConfig();
                sender.sendMessage(Colorizer.colorize(getConfig().getString("reload")));
            } else {
                sender.sendMessage(Colorizer.colorize(getConfig().getString("nopermission")));
            }
            return true;
        }

        sender.sendMessage(Colorizer.colorize(getConfig().getString("usage")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("spaceresourcepack")) {
            if (args.length == 1 && sender.hasPermission("ssrp.admin")) {
                return Collections.singletonList("reload");
            }
        }
        return Collections.emptyList();
    }

    private void console(String message) {
        getServer().getConsoleSender().sendMessage(Colorizer.colorize(message));
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED) {

            List<String> lines = getConfig().getStringList("KickMessageLines");

            String fullMessage = lines.stream()
                    .map(line -> Colorizer.colorize(line.replace("%player%", player.getName())))
                    .collect(Collectors.joining("\n"));

            player.kickPlayer(fullMessage);

            String notifyMessage = getConfig().getString("KickNotify")
                    .replace("%player%", player.getName());
            notifyMessage = Colorizer.colorize(notifyMessage);

            for (Player p : getServer().getOnlinePlayers()) {
                if (p.hasPermission("ssrp.admin")) {
                    p.sendMessage(notifyMessage);
                }
            }
        }
    }
}
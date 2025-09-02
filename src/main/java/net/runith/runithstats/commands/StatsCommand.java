package net.runith.runithstats.commands;

import net.runith.runithstats.managers.StatsManager;
import net.runith.runithstats.database.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class StatsCommand implements CommandExecutor, TabCompleter {

    private final StatsManager statsManager;

    public StatsCommand(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cEste comando solo puede ser ejecutado por jugadores.");
                return true;
            }

            PlayerStats stats = statsManager.getPlayerStats(player.getUniqueId());

            if (stats == null) {
                sender.sendMessage("§cTus estadísticas no están cargadas. Vuelve a conectarte.");
                return true;
            }

            sender.sendMessage("§6§lTUS ESTADÍSTICAS");
            sender.sendMessage("§eKills: §f" + stats.getKills());
            sender.sendMessage("§eDeaths: §f" + stats.getDeaths());
            sender.sendMessage("§eKDR: §f" + String.format("%.2f", stats.getKDR()));
            sender.sendMessage("§eTiempo jugado: §f" + stats.getPlaytimeHours() + " horas");
            return true;
        }

        if (!sender.hasPermission("runithstats.admin")) {
            sender.sendMessage("§cNo tienes permiso para usar este comando.");
            return true;
        }

        if (args.length >= 4 && args[0].equalsIgnoreCase("set")) {
            String statType = args[1].toLowerCase();
            String playerName = args[2];
            int value;

            try {
                value = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cEl valor debe ser un número entero.");
                return true;
            }

            Player target = Bukkit.getPlayer(playerName);
            UUID uuid;
            String displayName;

            if (target != null) {
                uuid = target.getUniqueId();
                displayName = target.getName();
            } else {
                Optional<PlayerStats> offlineStats = statsManager.getOfflinePlayerStats(playerName).join();
                if (offlineStats.isPresent()) {
                    uuid = offlineStats.get().getUuid();
                    displayName = offlineStats.get().getName();
                } else {
                    sender.sendMessage("§cJugador no encontrado.");
                    return true;
                }
            }

            PlayerStats stats = statsManager.getPlayerStats(uuid);
            if (stats == null) {
                sender.sendMessage("§cNo se pudieron cargar las estadísticas del jugador.");
                return true;
            }

            switch (statType) {
                case "kills":
                    stats.setKills(value);
                    sender.sendMessage("§aKills de " + displayName + " establecidas a " + value);
                    break;
                case "deaths":
                    stats.setDeaths(value);
                    sender.sendMessage("§aDeaths de " + displayName + " establecidas a " + value);
                    break;
                default:
                    sender.sendMessage("§cTipo de estadística no válido. Usa 'kills' o 'deaths'.");
                    return true;
            }

            statsManager.savePlayerStats(Bukkit.getPlayer(uuid));
            return true;
        }

        sender.sendMessage("§cUso: /runithstats [set <kills|deaths> <jugador> <valor>]");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("runithstats.admin")) {
                return StringUtil.copyPartialMatches(args[0], List.of("set"), new ArrayList<>());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            if (sender.hasPermission("runithstats.admin")) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("kills", "deaths"), new ArrayList<>());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            if (sender.hasPermission("runithstats.admin")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (StringUtil.startsWithIgnoreCase(player.getName(), args[2])) {
                        completions.add(player.getName());
                    }
                }
                return completions;
            }
        }

        return completions;
    }
}
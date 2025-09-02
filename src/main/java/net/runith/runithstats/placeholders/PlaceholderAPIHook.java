package net.runith.runithstats.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.runith.runithstats.managers.StatsManager;
import net.runith.runithstats.database.PlayerStats;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final StatsManager statsManager;

    public PlaceholderAPIHook(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "runithstats";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Runith";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "0";

        PlayerStats stats = statsManager.getPlayerStats(player.getUniqueId());
        if (stats == null) return "0";

        switch (identifier) {
            case "deaths":
                return String.valueOf(stats.getDeaths());
            case "kills":
                return String.valueOf(stats.getKills());
            case "playtime":
                return String.valueOf(stats.getPlaytimeHours());
            case "kdr":
                return String.format("%.2f", stats.getKDR());
            default:
                return null;
        }
    }
}
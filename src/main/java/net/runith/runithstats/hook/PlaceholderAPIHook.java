package net.runith.runithstats.hook;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.runith.runithstats.storage.PlayersStatsStorage;
import net.runith.runithstats.database.PlayerStats;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final PlayersStatsStorage playersStatsStorage;

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
        if (player == null) {
            return "0";
        }

        final PlayerStats stats = playersStatsStorage.getPlayerStats(player.getUniqueId());
        if (stats == null) {
            return "0";
        }

        return switch (identifier) {
            case "deaths" -> String.valueOf(stats.getDeaths());
            case "kills" -> String.valueOf(stats.getKills());
            case "playtime" -> String.valueOf(TimeUnit.MILLISECONDS.toHours(stats.getPlayTime()));
            case "kdr" -> String.format("%.2f", stats.getKDR());
            default -> null;
        };
    }
}
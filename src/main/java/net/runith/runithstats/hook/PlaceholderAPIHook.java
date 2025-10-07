package net.runith.runithstats.hook;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import net.runith.runithstats.database.PlayerStats;
import net.runith.runithstats.leaderboard.Leaderboards;
import net.runith.runithstats.storage.PlayersStatsStorage;
import net.runith.runithstats.util.TimeFormatter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
public final class PlaceholderAPIHook extends PlaceholderExpansion {

    private static final String NOT_UPDATED = ChatColor.YELLOW + "Not updated yet";
    private final PlayersStatsStorage playersStatsStorage;
    private final Leaderboards leaderboards;

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
        return "1.1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (identifier.startsWith("leaderboard_")) {
            return handleLeaderboardTop(identifier);
        }

        if (identifier.startsWith("leaderboardpos_")) {
            return handleLeaderboardPosition(player, identifier);
        }

        return handlePlayerStats(player, identifier);
    }

    private @NotNull String handleLeaderboardTop(final String identifier) {
        String[] parts = identifier.split("_");
        if (parts.length != 3) return "Invalid format";

        String type = parts[1];
        int index;
        try {
            index = Integer.parseInt(parts[2]) - 1;
        } catch (NumberFormatException e) {
            return "";
        }

        List<? extends java.util.Map.Entry<String, ? extends Number>> top = getLeaderboardEntries(type, index + 1);
        if (top == null || index >= top.size()) return "";

        var entry = top.get(index);
        return entry.getKey() + ChatColor.DARK_GRAY + " ► " + ChatColor.AQUA + (type.equalsIgnoreCase("playtime")
            ? TimeFormatter.format(entry.getValue().longValue() / 1000)
            : entry.getValue());
    }

    private @NotNull String handleLeaderboardPosition(Player player, final String identifier) {
        if (player == null) return "0";

        String type = identifier.substring("leaderboardpos_".length());
        var lbEntries = getLeaderboardEntries(type, Integer.MAX_VALUE);
        if (lbEntries == null) return "";

        for (int i = 0; i < lbEntries.size(); i++) {
            if (lbEntries.get(i).getKey().equalsIgnoreCase(player.getName())) {
                return ChatColor.GREEN + "#" + (i + 1);
            }
        }
        return NOT_UPDATED;
    }

    private @Nullable String handlePlayerStats(Player player, final String identifier) {
        if (player == null) {
            return "Player can't be null";
        }

        final PlayerStats stats = playersStatsStorage.getPlayerStats(player.getUniqueId());
        if (stats == null) {
            return "Not loaded yet";
        }

        return switch (identifier) {
            case "kills" -> String.valueOf(stats.getKills());
            case "deaths" -> String.valueOf(stats.getDeaths());
            case "playtime" -> TimeFormatter.format(stats.getPlayTime() / 1000);
            case "sessionplaytime" -> TimeFormatter.format(stats.getSessionTime() / 1000);
            case "kdr" -> String.format("%.2f", stats.getKDR());
            default -> null;
        };
    }

    private @Nullable List<? extends java.util.Map.Entry<String, ? extends Number>> getLeaderboardEntries(String type, int limit) {
        return switch (type.toLowerCase()) {
            case "kills" -> leaderboards.getKills().getTopPlayers(limit);
            case "deaths" -> leaderboards.getDeaths().getTopPlayers(limit);
            case "playtime" -> leaderboards.getPlayTime().getTopPlayers(limit);
            default -> null;
        };
    }
}

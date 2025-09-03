package net.runith.runithstats.hook;

import lombok.Getter;
import me.NoChance.PvPManager.PvPlayer;
import net.runith.runithstats.database.PlayerStats;
import net.runith.runithstats.storage.PlayersStatsStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PvPManagerHook {

    private final PlayersStatsStorage playersStatsStorage;

    @Getter
    private final boolean pvpManagerEnabled;

    public PvPManagerHook(PlayersStatsStorage playersStatsStorage) {
        this.playersStatsStorage = playersStatsStorage;
        this.pvpManagerEnabled = Bukkit.getPluginManager().getPlugin("PvPManager") != null;
    }

    public void handleCombatLog(final Player player) {
        if (!pvpManagerEnabled) {
            return;
        }

        final PvPlayer pvPlayer = PvPlayer.get(player);
        if (pvPlayer == null || !pvPlayer.isInCombat()) {
            return;
        }

        final PlayerStats playerStats = playersStatsStorage.getPlayerStats(player.getUniqueId());
        if (playerStats != null) {
            playerStats.addDeaths(1);
        }

        if (pvPlayer.getEnemy() == null) {
            return;
        }

        final PlayerStats enemyStats = playersStatsStorage.getPlayerStats(pvPlayer.getEnemy().getUUID());
        if (enemyStats != null) {
            enemyStats.addKills(1);
        }
    }
}
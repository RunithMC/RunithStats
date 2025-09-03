package net.runith.runithstats.listeners;

import lombok.RequiredArgsConstructor;
import net.runith.runithstats.database.PlayerStats;
import net.runith.runithstats.storage.PlayersStatsStorage;
import net.runith.runithstats.hook.PvPManagerHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class PvPListener implements Listener {

    private final PlayersStatsStorage playersStatsStorage;
    private final PvPManagerHook pvpManagerHook;

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player victim = event.getEntity();
        final Player killer = victim.getKiller();

        final PlayerStats victimStats = playersStatsStorage.getPlayerStats(victim.getUniqueId());

        if (victimStats != null) {
            victimStats.addDeaths(1);
        }

        if (killer != null && killer != victim) {
            final PlayerStats killerStats = playersStatsStorage.getPlayerStats(killer.getUniqueId());
            if (killerStats != null) {
                killerStats.addKills(1);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        pvpManagerHook.handleCombatLog(event.getPlayer());
    }
}
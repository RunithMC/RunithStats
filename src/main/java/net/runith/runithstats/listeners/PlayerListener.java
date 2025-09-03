package net.runith.runithstats.listeners;

import lombok.RequiredArgsConstructor;
import net.runith.runithstats.storage.PlayersStatsStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final PlayersStatsStorage playersStatsStorage;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playersStatsStorage.loadPlayerStats(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playersStatsStorage.savePlayerStats(event.getPlayer());
    }
}
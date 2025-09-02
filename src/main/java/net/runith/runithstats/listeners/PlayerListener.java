package net.runith.runithstats.listeners;

import net.runith.runithstats.managers.StatsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final StatsManager statsManager;

    public PlayerListener(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        statsManager.loadPlayerStats(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        statsManager.savePlayerStats(event.getPlayer());
    }
}
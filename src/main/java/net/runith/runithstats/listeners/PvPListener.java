package net.runith.runithstats.listeners;

import net.runith.runithstats.managers.StatsManager;
import net.runith.runithstats.managers.PvPManagerHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PvPListener implements Listener {

    private final PvPManagerHook pvpManagerHook;

    public PvPListener(StatsManager statsManager, PvPManagerHook pvpManagerHook) {
        this.pvpManagerHook = pvpManagerHook;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        pvpManagerHook.handlePlayerDeath(event);
    }
}
package net.runith.runithstats.managers;

import me.NoChance.PvPManager.PvPlayer;
import net.runith.runithstats.RunithStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PvPManagerHook {

    private final RunithStats plugin;
    private final StatsManager statsManager;
    private final boolean pvpManagerEnabled;

    public PvPManagerHook(RunithStats plugin, StatsManager statsManager) {
        this.plugin = plugin;
        this.statsManager = statsManager;
        this.pvpManagerEnabled = Bukkit.getPluginManager().getPlugin("PvPManager") != null;
    }

    public void handlePlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        if (killer != null && killer != player) {
            statsManager.addKill(killer.getUniqueId());
            statsManager.addDeath(player.getUniqueId());
        } else {
            statsManager.addDeath(player.getUniqueId());
        }
    }

    public void handleCombatLog(Player player) {
        if (pvpManagerEnabled) {
            try {
                PvPlayer pvPlayer = PvPlayer.get(player);
                if (pvPlayer != null && pvPlayer.isInCombat()) {
                    statsManager.addDeath(player.getUniqueId());

                    if (pvPlayer.getEnemy() != null) {
                        Player enemy = Bukkit.getPlayer(pvPlayer.getEnemy().getUUID());
                        if (enemy != null) {
                            statsManager.addKill(enemy.getUniqueId());
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Error al interactuar con PvPManager: " + e.getMessage());
            }
        }
    }

    public boolean isPvPManagerEnabled() {
        return pvpManagerEnabled;
    }
}
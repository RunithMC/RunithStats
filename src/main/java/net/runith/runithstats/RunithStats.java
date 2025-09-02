package net.runith.runithstats;

import lombok.Getter;
import net.runith.runithstats.commands.StatsCommand;
import net.runith.runithstats.database.DatabaseManager;
import net.runith.runithstats.database.MongoDBInitializer;
import net.runith.runithstats.listeners.PlayerListener;
import net.runith.runithstats.listeners.PvPListener;
import net.runith.runithstats.managers.PvPManagerHook;
import net.runith.runithstats.managers.StatsManager;
import net.runith.runithstats.placeholders.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RunithStats extends JavaPlugin {

    private DatabaseManager databaseManager;
    @Getter
    private StatsManager statsManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            this.databaseManager = MongoDBInitializer.initialize(this);
            getLogger().info("Conexión a MongoDB establecida correctamente");
        } catch (Exception e) {
            getLogger().severe("Error al conectar con MongoDB: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.statsManager = new StatsManager(this, databaseManager);
        PvPManagerHook pvpManagerHook = new PvPManagerHook(this, statsManager);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(statsManager), this);
        Bukkit.getPluginManager().registerEvents(new PvPListener(statsManager, pvpManagerHook), this);

        getCommand("runithstats").setExecutor(new StatsCommand(statsManager));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(statsManager).register();
        }
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

}
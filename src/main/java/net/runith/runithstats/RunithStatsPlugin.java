package net.runith.runithstats;

import net.runith.runithstats.commands.StatsCommand;
import net.runith.runithstats.database.MongoDatabase;
import net.runith.runithstats.database.MongoDBInitializer;
import net.runith.runithstats.listeners.PlayerListener;
import net.runith.runithstats.listeners.PvPListener;
import net.runith.runithstats.hook.PvPManagerHook;
import net.runith.runithstats.storage.PlayersStatsStorage;
import net.runith.runithstats.hook.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RunithStatsPlugin extends JavaPlugin {

    private MongoDatabase mongoDatabase;
    private PlayersStatsStorage playersStatsStorage;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            this.mongoDatabase = MongoDBInitializer.initialize(this);
            getLogger().info("Conexión a MongoDB establecida correctamente");
        } catch (Exception e) {
            getLogger().severe("Error al conectar con MongoDB: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.playersStatsStorage = new PlayersStatsStorage(mongoDatabase);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, playersStatsStorage::saveAllStatsAsync, 6000, 6000);

        final PvPManagerHook pvpManagerHook = new PvPManagerHook(playersStatsStorage);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(playersStatsStorage), this);
        Bukkit.getPluginManager().registerEvents(new PvPListener(playersStatsStorage, pvpManagerHook), this);

        getCommand("runithstats").setExecutor(new StatsCommand(playersStatsStorage));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(playersStatsStorage).register();
        }
    }

    @Override
    public void onDisable() {
        if (mongoDatabase != null) {
            playersStatsStorage.saveAllStats();
            mongoDatabase.close();
        }
    }
}
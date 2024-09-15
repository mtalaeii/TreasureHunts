package org.mahdi.testmode.treasurehunts;

import org.bukkit.plugin.java.JavaPlugin;
import org.mahdi.testmode.treasurehunts.commands.TreasureCommand;
import org.mahdi.testmode.treasurehunts.handlers.TreasureSpawnHandler;
import org.mahdi.testmode.treasurehunts.listeners.TreasureListener;

public final class TreasureHunts extends JavaPlugin {
    public TreasureSpawnHandler handler;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new TreasureListener(this), this);
        this.getCommand("treasures").setExecutor(new TreasureCommand(this));
        this.handler = new TreasureSpawnHandler(this);
        this.handler.loadConfig();
        this.handler.startTreasureSpawner();
    }

    @Override
    public void onDisable() {
        this.handler.cleanupActiveChests();
    }

}

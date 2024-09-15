package org.mahdi.testmode.treasurehunts.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mahdi.testmode.treasurehunts.TreasureHunts;

import java.util.concurrent.atomic.AtomicInteger;

public class TreasureCommand implements CommandExecutor {
    private final TreasureHunts plugin;

    public TreasureCommand(TreasureHunts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NonNull CommandSender sender,
            @NonNull Command command,
            @NonNull String label,
            @NonNull String[] args
    ) {
        if (!(sender instanceof Player player)) return false;

        Inventory treasureInventory = Bukkit.createInventory(null, 27, "Active Treasures");
        plugin.handler.getActiveTreasures().entrySet().stream().map(treasure -> {
            ItemStack treasureItem = new ItemStack(Material.CHEST);
            ItemMeta meta = treasureItem.getItemMeta();
            meta.setDisplayName(treasure.getValue() + " at " + treasure.getKey());
            treasureItem.setItemMeta(meta);
            return treasureItem;
        }).forEach(treasureInventory::addItem);
        player.openInventory(treasureInventory);
        return true;
    }
}

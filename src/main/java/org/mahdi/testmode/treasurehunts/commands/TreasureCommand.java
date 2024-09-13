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
import org.mahdi.testmode.treasurehunts.TreasureHunts;

public class TreasureCommand implements CommandExecutor {
    private final TreasureHunts plugin;

    public TreasureCommand(TreasureHunts plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Inventory treasureInventory = Bukkit.createInventory(null, 27, "Active Treasures");
        int counter = 1;
        for (Location location : plugin.handler.getActiveTreasures().keySet()) {
            ItemStack treasureItem = new ItemStack(Material.CHEST);
            ItemMeta meta = treasureItem.getItemMeta();
            meta.setDisplayName("#Treasure " + counter + " at " + location);
            treasureItem.setItemMeta(meta);
            treasureInventory.addItem(treasureItem);
        }

        player.openInventory(treasureInventory);
        return true;
    }
}

package org.mahdi.testmode.treasurehunts.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;

import org.mahdi.testmode.treasurehunts.TreasureHunts;
import org.mahdi.testmode.treasurehunts.handlers.MessageHandler;

import java.util.Objects;


public class TreasureListener implements Listener {
    private final TreasureHunts plugin;

    public TreasureListener(TreasureHunts plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CHEST) return;

        Location chestLocation = event.getClickedBlock().getLocation();
        Player player = event.getPlayer();

        if (plugin.handler.getActiveTreasures().containsKey(chestLocation)) {
            Inventory chestInventory = ((org.bukkit.block.Chest) event.getClickedBlock().getState()).getBlockInventory();
            chestInventory.forEach(itemStack -> {
                if (itemStack != null) {
                    if (player.getInventory().addItem(itemStack).size() != 0) {
                        player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                    }
                }
            });
            event.getClickedBlock().setType(Material.AIR);
            plugin.handler.removeTreasure(chestLocation);
            plugin.handler.removePlayerTrace(player);
            Bukkit.broadcastMessage(MessageHandler.setMessageColor("&2" + player.getName() + " looted a treasure chest at " + chestLocation));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Active Treasures")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.CHEST) {
                Player player = (Player) event.getWhoClicked();
                Location treasureLocation = plugin.handler.getActiveTreasures().keySet().stream()
                        .findFirst().filter(key -> Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName().contains(key.toString()))
                        .orElse(null);
                if (treasureLocation != null) {
                    if (plugin.handler.getPlayerTraces().containsKey(player) && plugin.handler.getPlayerTraces().get(player).equals(treasureLocation)) {
                        plugin.handler.removePlayerTrace(player);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
                    } else {
                        plugin.handler.getPlayerTraces().put(player, treasureLocation);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MessageHandler.setMessageColor("&9Distance to treasure&r: " + (int) player.getLocation().distance(treasureLocation) + " &b blocks&r")));
                    }
                    player.closeInventory(); // Close the inventory after clicking
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.handler.getPlayerTraces().containsKey(player)) {
            Location treasureLocation = plugin.handler.getPlayerTraces().get(player);
            double distance = player.getLocation().distance(treasureLocation);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MessageHandler.setMessageColor("&9Distance to treasure&r: " + (int) distance + "&b blocks&r")));
        }
    }
}

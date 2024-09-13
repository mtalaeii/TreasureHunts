package org.mahdi.testmode.treasurehunts.handlers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mahdi.testmode.treasurehunts.TreasureHunts;

import java.util.*;

public class TreasureSpawnHandler {
    private TreasureHunts plugin;
    private Map<Material, Integer> itemConfig;
    private int maxItemsPerChest;

    private Map<Location, String> activeTreasures = new HashMap<>();
    private Map<Player, Location> playerTraces = new HashMap<>();

    private static int chestCounter = 0;

    public TreasureSpawnHandler(TreasureHunts plugin){
        this.plugin = plugin;
    }


    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();

        itemConfig = new HashMap<>();
        for (String key : config.getConfigurationSection("treasure-chest.items").getKeys(false)) {
            try {
                Material material = Material.valueOf(key);
                int quantity = config.getInt("treasure-chest.items." + key);
                itemConfig.put(material, quantity);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material specified in config: " + key);
            }
        }

        maxItemsPerChest = config.getInt("treasure-chest.max-items-per-chest", 1);
        if (maxItemsPerChest < 1) maxItemsPerChest = 1;
        if (maxItemsPerChest > 27) maxItemsPerChest = 27;
    }

    public Map<Material, Integer> getItemConfig() {
        return itemConfig;
    }

    public int getMaxItemsPerChest() {
        return maxItemsPerChest;
    }

    public void startTreasureSpawner() {
        int spawnTime = plugin.getConfig().getInt("treasure-chest.spawntime") < 0 ? 6000 : plugin.getConfig().getInt("treasure-chest.spawntime");
        new BukkitRunnable() {
            @Override
            public void run() {
                spawnTreasure();
            }
        }.runTaskTimer(plugin, 0,spawnTime);
    }

    private void spawnTreasure() {
        // Check if the number of active chests exceeds the limit
        if (this.getActiveTreasures().size() >= 27) {
            cleanupActiveChests();
            Bukkit.broadcastMessage(MessageHandler.setMessageColor("&4All Treasures have been cleaned up!"));
        }

        // Get a random online player
        Player randomPlayer = getRandomOnlinePlayer();
        if (randomPlayer == null) return;

        // Get a random location near the player
        Location spawnLocation = getRandomLocationNearPlayer(randomPlayer);

        // Set the block at the location to a chest
        spawnLocation.getBlock().setType(Material.CHEST);

        // Broadcast message about the treasure spawn
        Bukkit.broadcastMessage(MessageHandler.setMessageColor("&1A treasure chest has spawned!"));

        // Fill the chest with loot
        fillChestWithLoot(spawnLocation);
    }

    private Player getRandomOnlinePlayer() {
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        if (players.length == 0) return null;
        return players[new Random().nextInt(players.length)];
    }

    private Location getRandomLocationNearPlayer(Player player) {
        Random random = new Random();
        Location playerLocation = player.getLocation();
        World world = player.getWorld();
        Location spawnLocation = null;

        for (int i = 0; i < 10; i++) { // Try up to 10 times to find a valid location
            int x = playerLocation.getBlockX() + random.nextInt(100) - 50;
            int z = playerLocation.getBlockZ() + random.nextInt(100) - 50;
            int y = world.getHighestBlockYAt(x, z) - 1;

            Location potentialLocation = new Location(world, x, y, z);
            if (isValidSpawnLocation(potentialLocation)) {
                spawnLocation = potentialLocation;
                break;
            }
        }

        return spawnLocation != null ? spawnLocation : playerLocation; // Fallback to player location if no valid location found
    }

    private boolean isValidSpawnLocation(Location location) {
        Material blockType = location.getBlock().getType();
        return blockType.isSolid() && blockType != Material.LEGACY_LEAVES && blockType != Material.WATER;
    }

    private void fillChestWithLoot(Location location) {
        if (location.getWorld() == null) return;

        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) location.getBlock().getState();
        Inventory chestInventory = chest.getBlockInventory();

        Map<Material, Integer> itemConfig = this.getItemConfig();
        int maxItemsPerChest = this.getMaxItemsPerChest();

        List<ItemStack> items = new ArrayList<>();
        for (Map.Entry<Material, Integer> entry : itemConfig.entrySet()) {
            Material material = entry.getKey();
            int quantity = entry.getValue();
            items.add(new ItemStack(material, quantity));
        }

        Collections.shuffle(items);

        int itemCount = Math.min(maxItemsPerChest, items.size());
        for (int i = 0; i < itemCount; i++) {
            ItemStack item = items.get(i);
            if (chestInventory.firstEmpty() == -1) {
                break;
            }
            chestInventory.addItem(item);
        }

        // Assign a unique number for this chest
        chestCounter++;
        String traceText = "Treasure #" + chestCounter;

        // Save chest location and trace text
        this.getActiveTreasures().put(location, traceText);
    }

    public Map<Location, String> getActiveTreasures() {
        return activeTreasures;
    }

    public void removeTreasure(Location location) {
        activeTreasures.remove(location);
    }

    public Map<Player, Location> getPlayerTraces() {
        return playerTraces;
    }

    public void removePlayerTrace(Player player) {
        playerTraces.remove(player);
    }

    public void cleanupActiveChests() {
        // Remove all active chests
        for (Map.Entry<Location, String> chests : this.getActiveTreasures().entrySet()) {
            chests.getKey().getBlock().setType(Material.AIR);
        }
        this.getActiveTreasures().clear();

        // Remove all player trace texts
        this.getPlayerTraces().clear();
    }
}

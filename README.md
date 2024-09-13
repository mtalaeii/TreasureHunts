# TreasureHunts

A plugin that create event for give online player some random treasure hunts!


## Installation
for clone and build library in your system type :
```bash
git clone https://github.com/mtalaeii/TreasureHunts.git && cd TreasureHunts && gradlew build

```
Then pick up .jar file from build/libs and put it in your server plugins directory!

## Configuration
```yaml
treasure-chest:
  items:
    DIAMOND: 10
    IRON_INGOT: 10
    GOLD_INGOT: 10
    NETHERITE_INGOT: 10
  max-items-per-chest: 2
  spawntime : 6000 #its ticks 6000 ticks = every 5 minutes

```
This is default config you can modify items(supported all vanilla items) + count
max-items-per-chest parameter allow the max items can be placed in the chest (for example here is 2 it means 2 item per chest maximum is 27)
and spawtime is the ticks need for every chest spawns (here is 5 minutes that take 6000 ticks set)
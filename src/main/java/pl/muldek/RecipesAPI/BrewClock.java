package pl.muldek.RecipesAPI;

import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.muldek.RecipesAPI.listeners.BrewingRecipeListener;
import pl.muldek.RecipesAPI.recipes.BrewingRecipe;

public class BrewClock extends BukkitRunnable {
    
    public BrewerInventory inventory;
    private BrewingRecipe recipe;
    private ItemStack[] before;
    private BrewingStand stand;
    private int current;
    
    public BrewClock(BrewingRecipe recipe, BrewerInventory inventory, int time) {
        this.recipe = recipe;
        this.inventory = inventory;
        this.stand = inventory.getHolder();
        this.before = inventory.getContents();
        this.current = time;
        BrewingRecipeListener.clocks.add(this);
        runTaskTimer(Main.getPlugin(Main.class),0L,1L);
    }
    
    @Override
    public void run() {
        stand = inventory.getHolder();
        if (current == 0) {
            stand.setFuelLevel(stand.getFuelLevel() - recipe.getFuelConsumption());
            stand.update(true);
            
            if (inventory.getIngredient().getAmount() > 1) {
                ItemStack is = inventory.getIngredient();
                is.setAmount(inventory.getIngredient().getAmount() - 1);
                inventory.setIngredient(is);
            } else
                inventory.setIngredient(new ItemStack(Material.AIR));
            
            if (recipe.getFuel() != null && recipe.getFuel().getType() != Material.AIR) {
                ItemStack fuel = inventory.getFuel();
                fuel.setAmount(fuel.getAmount() - 1);
                inventory.setFuel(fuel);
            }

            for (int i = 0; i < 3; i++) {
                if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) continue;
                if (recipe.getFunction() != null) inventory.setItem(i, recipe.getFunction().apply(new Pair(inventory, inventory.getItem(i))));
            }
            
            BrewingRecipeListener.clocks.remove(this);
            cancel();
            return;
        }
        
        if (searchChanged(inventory.getContents())) {
            BrewingRecipeListener.clocks.remove(this);
            cancel();
            return;
        }
        
        current--;
        stand.setBrewingTime(current);
        stand.update(true);
    }
    
//    @EventHandler(priority = EventPriority.HIGH)
//    public void onItemChange(InventoryClickEvent event) {
//        if (event.getView().getTopInventory() != inventory) return;
//        Do some stuff
//    }
    
    public boolean searchChanged(ItemStack[] after) {
        if ((after[0] == null || after[0].getType() == Material.AIR)
                && (after[1] == null || after[1].getType() == Material.AIR)
                && (after[2] == null || after[2].getType() == Material.AIR))
            return true;
        for (int i = 0; i < before.length; i++) {
            if (i == 4 && (recipe.getFuel() == null || recipe.getFuel().getType() == Material.AIR)) continue;
            if (i < 3 && (after[i] == null || after[i].getType() == Material.AIR)) {
                before[i] = after[i];
                continue;
            }
            if ((before[i] != null && after[i] == null) || (before[i] == null && after[i] != null))
                return true;
            else {
                if (before[i] == null) continue;
                if (!before[i].isSimilar(after[i])) return true;
            }
        }
        return false;
    }
}
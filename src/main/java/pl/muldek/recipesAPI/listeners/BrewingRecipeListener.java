package pl.muldek.recipesAPI.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.view.BrewingStandView;
import org.bukkit.scheduler.BukkitRunnable;
import pl.muldek.recipesAPI.BrewClock;
import pl.muldek.recipesAPI.BrewingFuel;
import pl.muldek.recipesAPI.Main;
import pl.muldek.recipesAPI.recipes.BrewingRecipe;

import java.util.*;

public class BrewingRecipeListener implements Listener {
    
    private static Queue<BrewingRecipe> recipes = new ArrayDeque<>();
    public static List<BrewClock> clocks = new ArrayList<>();
    
    private static List<Material> ingredients = new ArrayList<>();
    private static List<Material> items = new ArrayList<>();
    private static List<BrewingFuel> fuels = new ArrayList<>();
    
    
    public static Queue<BrewingRecipe> getRecipes() {
        return recipes;
    }
    public static void addRecipe(BrewingRecipe recipe) {
        recipes.add(recipe);
        
        if (!ingredients.contains(recipe.getIngredient().getType()))
            ingredients.add(recipe.getIngredient().getType());
        for (ItemStack i : recipe.getItems()) {
            if (!items.contains(i.getType())) items.add(i.getType());
        }
        if (recipe.getFuel() != null && recipe.getFuel().getType() != Material.AIR)
            addBrewingFuel(new BrewingFuel(recipe.getFuel(), recipe.isExactFuel(),null));
    }
    
    public static void addBrewingFuel(BrewingFuel brewingFuel) { fuels.add(brewingFuel); }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!(event.getView().getTopInventory() instanceof BrewerInventory inventory)) return;
                for (BrewClock c : clocks) {
                    if (c.inventory == inventory) return;
                }
                if (inventory.getIngredient() == null) return;
                if (inventory.getItem(0) == null && inventory.getItem(1) == null && inventory.getItem(2) == null) return;
                
                BrewingRecipe recipe = null;
                for (BrewingRecipe r : getRecipes()) {
                    if (r.isExactIngredient() && inventory.getIngredient().isSimilar(r.getIngredient()))
                        recipe = r;
                    if (!r.isExactIngredient() && inventory.getIngredient().getType() == r.getIngredient().getType())
                        recipe = r;
                }
                if (recipe == null) return;
                
                boolean slot0 = false;
                boolean slot1 = false;
                boolean slot2 = false;
                for (ItemStack i : recipe.getItems()) {
                    if (recipe.isExactItems()){
                        if (inventory.getItem(0) == null || inventory.getItem(0).getType() == Material.AIR
                                || inventory.getItem(0).isSimilar(i)) slot0 = true;
                        if (inventory.getItem(1) == null || inventory.getItem(1).getType() == Material.AIR
                                || inventory.getItem(1).isSimilar(i)) slot1 = true;
                        if (inventory.getItem(2) == null || inventory.getItem(2).getType() == Material.AIR
                                || inventory.getItem(2).isSimilar(i)) slot2 = true;
                    } else {
                        if (inventory.getItem(0) == null || inventory.getItem(0).getType() == Material.AIR
                                || inventory.getItem(0).getType() == i.getType()) slot0 = true;
                        if (inventory.getItem(1) == null || inventory.getItem(1).getType() == Material.AIR
                                || inventory.getItem(1).getType() == i.getType()) slot1 = true;
                        if (inventory.getItem(2) == null || inventory.getItem(2).getType() == Material.AIR
                                || inventory.getItem(2).getType() == i.getType()) slot2 = true;
                    }
                }
                if (!slot0 || !slot1 || !slot2) return;
                
                boolean fuelCheck = false;
                if (recipe.getFuel() != null && recipe.getFuel().getType() != Material.AIR) {
                    if (inventory.getFuel() != null) {
                        if (recipe.isExactFuel() && inventory.getFuel().isSimilar(recipe.getFuel()))
                            fuelCheck = true;
                        if (!recipe.isExactFuel() && inventory.getFuel().getType() == recipe.getFuel().getType())
                            fuelCheck = true;
                    }
                } else fuelCheck = true;
                if (!fuelCheck) return;
                
                BrewingStandView view = (BrewingStandView) event.getView();
                if (recipe.getFuelConsumption() > view.getFuelLevel()) {
                    chargeFuel(inventory, view);
//                    for (BrewingFuel f : fuels) {
//                        if (inventory.getFuel() == null || inventory.getFuel().getType() == Material.AIR) break;
//                        if ((f.exactFuel() && f.fuel().isSimilar(inventory.getFuel()))
//                                || (!f.exactFuel() && f.fuel().getType() == inventory.getFuel().getType())) {
//                            ItemStack fuel = inventory.getFuel();
//                            fuel.setAmount(fuel.getAmount() - 1);
//                            inventory.setFuel(fuel);
//                            view.setFuelLevel(view.getFuelLevel() + f.fuelLoad());
//                            break;
//                        }
//                    }
                }
                if (recipe.getFuelConsumption() > view.getFuelLevel()) return;
                
                recipe.startBrewing(inventory);
                cancel();
            }
        }.runTaskLater(Main.getPlugin(Main.class),1l);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onIllegalItemClick(InventoryClickEvent event) {
        if (event.getView().getType() != InventoryType.BREWING) return;
        BrewerInventory brewingInv = (BrewerInventory) event.getView().getTopInventory();
        BrewingStandView view = (BrewingStandView) event.getView();
        
        if (event.getClickedInventory() instanceof PlayerInventory playerInv) {
            Inventory inventory = event.getInventory();
            ItemStack current = playerInv.getItem(event.getSlot());
            if (current == null) current = new ItemStack(Material.AIR);
            
            if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                if (ingredients.contains(current.getType())) {
                    ItemStack slot3 = inventory.getItem(3);
                    
                    if (slot3 != null && slot3.isSimilar(current)) {
                        int total = slot3.getAmount() + current.getAmount();
                        if (total <= slot3.getMaxStackSize()) {
                            slot3.setAmount(total);
                            current = new ItemStack(Material.AIR);
                        } else {
                            slot3.setAmount(current.getMaxStackSize());
                            current.setAmount(total - current.getMaxStackSize());
                        }
                        inventory.setItem(3, slot3);
                        playerInv.setItem(event.getSlot(), current);
                        event.setCancelled(true);
                        return;
                    }
                    
                    if (inventory.getItem(3) == null) {
                        inventory.setItem(3, current);
                        playerInv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                        event.setCancelled(true);
                        return;
                    }
                }
                for (BrewingFuel f : fuels) {
                    if ((f.exactFuel() && f.fuel().isSimilar(current))
                            || (!f.exactFuel() && f.fuel().getType() == current.getType())) {
                        ItemStack slot4 = inventory.getItem(4);
                        
                        if (slot4 != null && slot4.isSimilar(current)) {
                            int total = slot4.getAmount() + current.getAmount();
                            if (total <= slot4.getMaxStackSize()) {
                                slot4.setAmount(total);
                                current = new ItemStack(Material.AIR);
                            } else {
                                slot4.setAmount(current.getMaxStackSize());
                                current.setAmount(total - current.getMaxStackSize());
                            }
                            inventory.setItem(4, slot4);
                            playerInv.setItem(event.getSlot(), current);
                            event.setCancelled(true);
                            if (view.getFuelLevel() == 0) chargeFuel(brewingInv, view);
                            return;
                        }
                        
                        if (inventory.getItem(4) == null) {
                            inventory.setItem(4, current);
                            playerInv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                            event.setCancelled(true);
                            if (view.getFuelLevel() == 0) chargeFuel(brewingInv, view);
                            return;
                        }
                    }
                }
                if (items.contains(current.getType())) {
                    ItemStack slot0 = inventory.getItem(0);
                    ItemStack slot1 = inventory.getItem(1);
                    ItemStack slot2 = inventory.getItem(2);
                    
                    if (slot0 != null && slot0.isSimilar(current)) {
                        int total = slot0.getAmount() + current.getAmount();
                        if (total <= slot0.getMaxStackSize()) {
                            slot0.setAmount(total);
                            current = new ItemStack(Material.AIR);
                        } else {
                            slot0.setAmount(current.getMaxStackSize());
                            current.setAmount(total - current.getMaxStackSize());
                        }
                        inventory.setItem(0, slot0);
                        playerInv.setItem(event.getSlot(), current);
                        event.setCancelled(true);
                        if (current.getType() == Material.AIR) return;
                    }
                    
                    if (slot1 != null && slot1.isSimilar(current)) {
                        int total = slot1.getAmount() + current.getAmount();
                        if (total <= slot1.getMaxStackSize()) {
                            slot1.setAmount(total);
                            current = new ItemStack(Material.AIR);
                        } else {
                            slot1.setAmount(current.getMaxStackSize());
                            current.setAmount(total - current.getMaxStackSize());
                        }
                        inventory.setItem(1, slot1);
                        playerInv.setItem(event.getSlot(), current);
                        event.setCancelled(true);
                        if (current.getType() == Material.AIR) return;
                    }
                    
                    if (slot2 != null && slot2.isSimilar(current)) {
                        int total = slot2.getAmount() + current.getAmount();
                        if (total <= slot2.getMaxStackSize()) {
                            slot2.setAmount(total);
                            current = new ItemStack(Material.AIR);
                        } else {
                            slot2.setAmount(current.getMaxStackSize());
                            current.setAmount(total - current.getMaxStackSize());
                        }
                        inventory.setItem(2, slot2);
                        playerInv.setItem(event.getSlot(), current);
                        event.setCancelled(true);
                        if (current.getType() == Material.AIR) return;
                    }
                    
                    if (inventory.getItem(0) == null) {
                        inventory.setItem(0, current);
                        playerInv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                    } else if (inventory.getItem(1) == null) {
                        inventory.setItem(1, current);
                        playerInv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                    } else if (inventory.getItem(2) == null) {
                        inventory.setItem(2, current);
                        playerInv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                    }
                    event.setCancelled(true);
                }
            }
        } else if (event.getClickedInventory() instanceof BrewerInventory inventory) {
            ItemStack cursor = event.getWhoClicked().getItemOnCursor();
            ItemStack slot = inventory.getItem(event.getSlot());
            
            if (cursor == null) cursor = new ItemStack(Material.AIR);
            else cursor = cursor.clone();
            if (slot == null) slot = new ItemStack(Material.AIR);
            else slot = slot.clone();
            
            if (event.getSlot() == 0 || event.getSlot() == 1 || event.getSlot() == 2) {
                if (!items.contains(cursor.getType())) return;
                
                if (event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.LEFT) {
                    if (slot.isSimilar(cursor)) {
                        int total = slot.getAmount() + cursor.getAmount();
                        if (total <= slot.getMaxStackSize()) {
                            slot.setAmount(total);
                            cursor = new ItemStack(Material.AIR);
                        } else {
                            slot.setAmount(slot.getMaxStackSize());
                            cursor.setAmount(total - slot.getMaxStackSize());
                        }
                        inventory.setItem(event.getSlot(), slot);
                        event.setCursor(cursor);
                        event.setCancelled(true);
                    } else {
                        event.setCursor(slot);
                        inventory.setItem(event.getSlot(), cursor);
                        event.setCancelled(true);
                    }
                }
                
                if (event.getClick() == ClickType.RIGHT) {
                    if (cursor == null || cursor.getType() == Material.AIR) return;
                    if (slot.getType() != Material.AIR) {
                        if (!slot.isSimilar(cursor)) {
                            event.setCursor(slot);
                            inventory.setItem(event.getSlot(), cursor);
                            event.setCancelled(true);
                        } else {
                            if (slot.getAmount() >= slot.getMaxStackSize()) return;
                            cursor.setAmount(cursor.getAmount() - 1);
                            event.setCursor(cursor);
                            slot.setAmount(slot.getAmount() + 1);
                            inventory.setItem(event.getSlot(), slot);
                            event.setCancelled(true);
                        }
                    } else {
                        cursor.setAmount(cursor.getAmount() - 1);
                        event.setCursor(cursor);
                        cursor.setAmount(1);
                        inventory.setItem(event.getSlot(), cursor);
                        event.setCancelled(true);
                    }
                }
            }
            
            if (event.getSlot() == 4) {
                for (BrewingFuel f : fuels) {
                    if ((f.exactFuel() && f.fuel().isSimilar(cursor))
                            || (!f.exactFuel() && f.fuel().getType() == cursor.getType())) {
                        if (event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.LEFT) {
                            if (slot.isSimilar(cursor)) {
                                int total = slot.getAmount() + cursor.getAmount();
                                if (total <= slot.getMaxStackSize()) {
                                    slot.setAmount(total);
                                    cursor = new ItemStack(Material.AIR);
                                } else {
                                    slot.setAmount(slot.getMaxStackSize());
                                    cursor.setAmount(total - slot.getMaxStackSize());
                                }
                                inventory.setItem(event.getSlot(), slot);
                                event.setCursor(cursor);
                                event.setCancelled(true);
                                if (inventory.getHolder().getFuelLevel() == 0) chargeFuel(inventory, view);
                                return;
                            } else {
                                event.setCursor(slot);
                                inventory.setItem(event.getSlot(), cursor);
                                event.setCancelled(true);
                                if (inventory.getHolder().getFuelLevel() == 0) chargeFuel(inventory, view);
                                return;
                            }
                        }
        
                        if (event.getClick() == ClickType.RIGHT) {
                            if (cursor == null || cursor.getType() == Material.AIR) return;
                            if (slot.getType() != Material.AIR) {
                                if (!slot.isSimilar(cursor)) {
                                    event.setCursor(slot);
                                    inventory.setItem(event.getSlot(), cursor);
                                    event.setCancelled(true);
                                    if (inventory.getHolder().getFuelLevel() == 0) chargeFuel(inventory, view);
                                    return;
                                } else {
                                    if (slot.getAmount() >= slot.getMaxStackSize()) return;
                                    cursor.setAmount(cursor.getAmount() - 1);
                                    event.setCursor(cursor);
                                    slot.setAmount(slot.getAmount() + 1);
                                    inventory.setItem(event.getSlot(), slot);
                                    event.setCancelled(true);
                                    if (inventory.getHolder().getFuelLevel() == 0) chargeFuel(inventory, view);
                                    return;
                                }
                            } else {
                                cursor.setAmount(cursor.getAmount() - 1);
                                event.setCursor(cursor);
                                cursor.setAmount(1);
                                inventory.setItem(event.getSlot(), cursor);
                                event.setCancelled(true);
                                if (inventory.getHolder().getFuelLevel() == 0) chargeFuel(inventory, view);
                                return;
                            }
                        }
                    }
                }
            }
            
            if (event.getSlot() == 3) {
                if (!ingredients.contains(cursor.getType())) return;
                
                if (event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.LEFT) {
                    if (slot.isSimilar(cursor)) {
                        int total = slot.getAmount() + cursor.getAmount();
                        if (total <= slot.getMaxStackSize()) {
                            slot.setAmount(total);
                            cursor = new ItemStack(Material.AIR);
                        } else {
                            slot.setAmount(slot.getMaxStackSize());
                            cursor.setAmount(total - slot.getMaxStackSize());
                        }
                        inventory.setItem(event.getSlot(), slot);
                        event.setCursor(cursor);
                        event.setCancelled(true);
                    } else {
                        event.setCursor(slot);
                        inventory.setItem(event.getSlot(), cursor);
                        event.setCancelled(true);
                    }
                }
                
                if (event.getClick() == ClickType.RIGHT) {
                    if (cursor == null || cursor.getType() == Material.AIR) return;
                    if (slot.getType() != Material.AIR) {
                        if (!slot.isSimilar(cursor)) {
                            event.setCursor(slot);
                            inventory.setItem(event.getSlot(), cursor);
                            event.setCancelled(true);
                        } else {
                            if (slot.getAmount() >= slot.getMaxStackSize()) return;
                            cursor.setAmount(cursor.getAmount() - 1);
                            event.setCursor(cursor);
                            slot.setAmount(slot.getAmount() + 1);
                            inventory.setItem(event.getSlot(), slot);
                            event.setCancelled(true);
                        }
                    } else {
                        cursor.setAmount(cursor.getAmount() - 1);
                        event.setCursor(cursor);
                        cursor.setAmount(1);
                        inventory.setItem(event.getSlot(), cursor);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
    private void chargeFuel(BrewerInventory inventory, BrewingStandView view) {
        for (BrewingFuel f : fuels) {
            if (inventory.getFuel() == null || inventory.getFuel().getType() == Material.AIR) break;
            if ((f.exactFuel() && f.fuel().isSimilar(inventory.getFuel()))
                    || (!f.exactFuel() && f.fuel().getType() == inventory.getFuel().getType())) {
                ItemStack fuel = inventory.getFuel();
                fuel.setAmount(fuel.getAmount() - 1);
                inventory.setFuel(fuel);
                view.setFuelLevel(view.getFuelLevel() + f.fuelLoad());
                break;
            }
        }
    }
}

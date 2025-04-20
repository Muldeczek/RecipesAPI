package pl.muldek.recipesAPI.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import pl.muldek.recipesAPI.recipes.GrindstoneRecipe;

import java.util.*;

public class GrindstoneRecipeListener implements Listener {
    
    private static Queue<GrindstoneRecipe> recipes = new ArrayDeque<>();
    private Set<GrindstoneInventory> inventories = new HashSet<>();
    private static List<Material> materials = new ArrayList<>();
    
    public static void addRecipe(GrindstoneRecipe recipe) {
        recipes.add(recipe);
        
        if (!materials.contains(recipe.getItem().getType())) materials.add(recipe.getItem().getType());
        for (ItemStack i : recipe.getIngredients()) {
            if (!materials.contains(i.getType())) materials.add(i.getType());
        }
    }
    
    @EventHandler
    public void onPrepareAnvil(PrepareGrindstoneEvent event) {
        GrindstoneInventory inventory = event.getInventory();
        if (inventory.getItem(0) == null || inventory.getItem(1) == null) return;
        ItemStack item0 = inventory.getItem(0);
        ItemStack item1 = inventory.getItem(1);
        
        for (GrindstoneRecipe recipe : recipes) {
            if (recipe.isExactItem())
                if (!recipe.getItem().isSimilar(item0)) continue;
                else
                if (recipe.getItem().getType() != item0.getType()) continue;
            
            if (recipe.isExactIngredients()) {
                for (ItemStack tempItem : recipe.getIngredients()) {
                    if (item1.isSimilar(tempItem)) {
                        ItemStack result = recipe.getResult();
                        if (recipe.getFunction() != null) result = recipe.getFunction().apply(event);
                        
                        event.setResult(result);
                        inventories.add(inventory);
                        return;
                    }
                }
            } else {
                for (ItemStack tempItem : recipe.getIngredients()) {
                    if (tempItem.getType() == item1.getType()) {
                        ItemStack result = recipe.getResult();
                        if (recipe.getFunction() != null) result = recipe.getFunction().apply(event);
                        
                        event.setResult(result);
                        inventories.add(inventory);
                        return;
                    }
                }
            }
        }
        
        for (GrindstoneRecipe recipe : recipes) {
            if (recipe.isExactItem())
                if (!recipe.getItem().isSimilar(item1)) continue;
            else
                if (recipe.getItem().getType() != item1.getType()) continue;
            
            if (recipe.isExactIngredients()) {
                for (ItemStack tempItem : recipe.getIngredients()) {
                    if (item0.isSimilar(tempItem)) {
                        ItemStack result = recipe.getResult();
                        if (recipe.getFunction() != null) result = recipe.getFunction().apply(event);
                        
                        event.setResult(result);
                        inventories.add(inventory);
                        return;
                    }
                }
            } else {
                for (ItemStack tempItem : recipe.getIngredients()) {
                    if (tempItem.getType() == item0.getType()) {
                        ItemStack result = recipe.getResult();
                        if (recipe.getFunction() != null) result = recipe.getFunction().apply(event);
                        
                        event.setResult(result);
                        inventories.add(inventory);
                        return;
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (event.getWhoClicked().getItemOnCursor().getType() == Material.AIR
                && event.getClickedInventory() != null && inventories.remove(event.getClickedInventory())
                && event.getRawSlot() == 2) {
            GrindstoneInventory inventory = (GrindstoneInventory) event.getClickedInventory();
            ItemStack result = inventory.getItem(2);
            event.getWhoClicked().setItemOnCursor(result);
            
            for (ItemStack item : inventory.getContents()) {
                item.setAmount(item.getAmount() - 1);
            }
            
            inventory.setItem(2, new ItemStack(Material.AIR));
            Block block = inventory.getLocation().getBlock();
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS,1,1);
        }
    }
    
    @EventHandler
    public void onIllegalItemClick(InventoryClickEvent event) {
        if (event.getView().getType() != InventoryType.GRINDSTONE) return;
        
        if (event.getClickedInventory() instanceof PlayerInventory playerInv) {
            Inventory inventory = event.getInventory();
            ItemStack current = playerInv.getItem(event.getSlot());
            if (current == null) current = new ItemStack(Material.AIR);
            
            if (materials.contains(current.getType())) {
                if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
                    ItemStack slot0 = inventory.getItem(0);
                    ItemStack slot1 = inventory.getItem(1);
                    
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
                    
                    if (inventory.getItem(0) == null) {
                        inventory.setItem(0, current);
                        playerInv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                    } else if (inventory.getItem(1) == null) {
                        inventory.setItem(1, current);
                        playerInv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                    }
                    event.setCancelled(true);
                }
            }
        } else if (event.getClickedInventory() instanceof GrindstoneInventory inventory) {
            ItemStack cursor = event.getWhoClicked().getItemOnCursor();
            ItemStack slot = inventory.getItem(event.getSlot());
            
            if (cursor == null) cursor = new ItemStack(Material.AIR);
            else cursor = cursor.clone();
            if (slot == null) slot = new ItemStack(Material.AIR);
            else slot = slot.clone();
            
            if (event.getSlot() == 0 || event.getSlot() == 1) {
                if (!materials.contains(cursor.getType())) return;
                
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
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) { inventories.remove(event.getInventory()); }
}
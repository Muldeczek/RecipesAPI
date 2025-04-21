package pl.muldek.RecipesAPI.listeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.muldek.RecipesAPI.recipes.AnvilRecipe;

import java.util.*;

public class AnvilRecipeListener implements Listener {
    
    private static Queue<AnvilRecipe> recipes = new ArrayDeque<>();
    private Set<AnvilInventory> anvils = new HashSet<>();
    
    public static void addRecipe(AnvilRecipe recipe) {
        recipes.add(recipe);
    }
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        if (inventory.getItem(0) == null || inventory.getItem(1) == null) return;
        ItemStack material = inventory.getItem(0);
        ItemStack ingredient = inventory.getItem(1);
        
        for (AnvilRecipe recipe : recipes) {
            if (recipe.isExactItem())
                if (!recipe.getItem().isSimilar(material)) continue;
            else
                if (recipe.getItem().getType() != material.getType()) continue;
            
            if (recipe.isExactIngredients()) {
                for (ItemStack tempItem : recipe.getIngredients()) {
                    if (ingredient.isSimilar(tempItem)) {
                        ItemStack result = recipe.getResult();
                        if (recipe.getFunction() != null) result = recipe.getFunction().apply(event);
                        
                        if (!event.getView().getRenameText().equals(ChatColor.stripColor(
                                material.hasItemMeta() ? material.getItemMeta().getDisplayName() : ""))) {
                            ItemMeta meta = result.getItemMeta();
                            meta.setDisplayName(event.getView().getRenameText());
                            result.setItemMeta(meta);
                        }
                        event.setResult(result);
                        event.getView().setRepairCost(recipe.getCost());
                        anvils.add(inventory);
                        return;
                    }
                }
            } else {
                for (ItemStack tempItem : recipe.getIngredients()) {
                    if (tempItem.getType() == ingredient.getType()) {
                        ItemStack result = recipe.getResult();
                        if (recipe.getFunction() != null) result = recipe.getFunction().apply(event);
                        
                        if (!event.getView().getRenameText().equals(ChatColor.stripColor(
                                material.hasItemMeta() ? material.getItemMeta().getDisplayName() : ""))) {
                            ItemMeta meta = result.getItemMeta();
                            meta.setDisplayName(event.getView().getRenameText());
                            result.setItemMeta(meta);
                        }
                        event.setResult(result);
                        event.getView().setRepairCost(recipe.getCost());
                        anvils.add(inventory);
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
                && event.getClickedInventory() != null && anvils.remove(event.getClickedInventory())
                && event.getRawSlot() == 2) {
            AnvilInventory inventory = (AnvilInventory) event.getClickedInventory();
            ItemStack result = inventory.getItem(2);
            event.getWhoClicked().setItemOnCursor(result);
            
            for (ItemStack item : inventory.getContents()) {
                item.setAmount(item.getAmount() - 1);
            }
            
            inventory.setItem(2, new ItemStack(Material.AIR));
            Block block = inventory.getLocation().getBlock();
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.BLOCKS,1,1);
            
            if (new Random().nextInt(3) == 0 && !isPlayerImmune((Player) event.getWhoClicked())) {
                String data = block.getBlockData().getAsString();
                switch (block.getType()) {
                    case ANVIL:
                        block.setBlockData(Bukkit.createBlockData(data.replace("anvil","chipped_anvil")));
                        break;
                    case CHIPPED_ANVIL:
                        block.setBlockData(Bukkit.createBlockData(data.replace("chipped_anvil","damaged_anvil")));
                        break;
                    case DAMAGED_ANVIL:
                        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS,1,1);
                        block.breakNaturally(new ItemStack(Material.AIR));
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) { anvils.remove(event.getInventory()); }
    
    private boolean isPlayerImmune(Player player) {
        GameMode mode = player.getGameMode();
        return mode == GameMode.CREATIVE || mode == GameMode.SPECTATOR;
    }
}

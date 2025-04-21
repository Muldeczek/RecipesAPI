package pl.muldek.RecipesAPI;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import pl.muldek.RecipesAPI.listeners.AnvilRecipeListener;
import pl.muldek.RecipesAPI.listeners.BrewingRecipeListener;
import pl.muldek.RecipesAPI.listeners.GrindstoneRecipeListener;
import pl.muldek.RecipesAPI.recipes.BrewingRecipe;
import pl.muldek.RecipesAPI.recipes.GrindstoneRecipe;

import java.util.Arrays;
import java.util.List;

public final class Main extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new AnvilRecipeListener(),this);
        getServer().getPluginManager().registerEvents(new GrindstoneRecipeListener(),this);
        getServer().getPluginManager().registerEvents(new BrewingRecipeListener(),this);
        
        
        List<ItemStack> items = Arrays.asList(new ItemStack(Material.LEATHER_HELMET),
                new ItemStack(Material.LEATHER_CHESTPLATE),
                new ItemStack(Material.LEATHER_LEGGINGS),
                new ItemStack(Material.LEATHER_BOOTS));

        BrewingRecipe recipe = new BrewingRecipe(new ItemStack(Material.BLACK_DYE),false,
                items,false,11,400);
        recipe.setFunction(pair -> {
            ItemStack item = pair.item();
            if (item.getItemMeta() instanceof LeatherArmorMeta meta) {
                meta.setColor(Color.BLACK);
                item.setItemMeta(meta);
            }
            return item;
        });
        RecipesManager.addBrewingRecipe(recipe);
        RecipesManager.addBrewingFuel(new ItemStack(Material.COAL),false,10);
        
        RecipesManager.addBrewingFuel(new ItemStack(Material.BLAZE_POWDER),false,20);
        
        getServer().getConsoleSender().sendMessage(
                ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "RecipesAPI" + ChatColor.DARK_GRAY +  "]"
                        + ChatColor.GREEN + " Enabled!");
    }
    
    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(
                ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "RecipesAPI" + ChatColor.DARK_GRAY +  "]"
                        + ChatColor.DARK_RED + " Disabled!");
    }
}

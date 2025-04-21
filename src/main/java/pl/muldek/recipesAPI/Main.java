package pl.muldek.recipesAPI;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import pl.muldek.recipesAPI.listeners.AnvilRecipeListener;
import pl.muldek.recipesAPI.listeners.BrewingRecipeListener;
import pl.muldek.recipesAPI.listeners.GrindstoneRecipeListener;

public final class Main extends JavaPlugin {
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new AnvilRecipeListener(),this);
        getServer().getPluginManager().registerEvents(new GrindstoneRecipeListener(),this);
        getServer().getPluginManager().registerEvents(new BrewingRecipeListener(),this);
        
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

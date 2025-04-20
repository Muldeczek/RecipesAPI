package pl.muldek.recipesAPI;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import pl.muldek.recipesAPI.listeners.AnvilRecipeListener;
import pl.muldek.recipesAPI.listeners.BrewingRecipeListener;
import pl.muldek.recipesAPI.listeners.GrindstoneRecipeListener;
import pl.muldek.recipesAPI.recipes.AnvilRecipe;
import pl.muldek.recipesAPI.recipes.BrewingRecipe;
import pl.muldek.recipesAPI.recipes.GrindstoneRecipe;

import java.util.Iterator;

import static org.bukkit.Bukkit.getServer;

public class RecipesManager {
    
    public static void addAnvilRecipe(AnvilRecipe recipe) { AnvilRecipeListener.addRecipe(recipe); }
    public static void addGrindstoneRecipe(GrindstoneRecipe recipe) { GrindstoneRecipeListener.addRecipe(recipe); }
    public static void addBrewingRecipe(BrewingRecipe recipe) { BrewingRecipeListener.addRecipe(recipe); }
    public static void addBrewingFuel(ItemStack fuel, boolean exactFuel, int amount) { BrewingRecipeListener.addBrewingFuel(new BrewingFuel(fuel, exactFuel, amount)); }
//    public static void addBrewingFuel(BrewingFuel brewingFuel) { BrewingRecipeListener.addBrewingFuel(brewingFuel); }
    
    public static void removeCraftingRecipe(Material material) {
        Iterator<Recipe> it = getServer().recipeIterator();
        Recipe recipe;
        while(it.hasNext()) {
            recipe = it.next();
            if (recipe != null && recipe.getResult().getType() == material)
                it.remove();
        }
    }
    public static void removeCraftingRecipe(ItemStack item) {
        Iterator<Recipe> it = getServer().recipeIterator();
        Recipe recipe;
        while(it.hasNext()) {
            recipe = it.next();
            if (recipe != null && recipe.getResult() == item)
                it.remove();
        }
    }
}

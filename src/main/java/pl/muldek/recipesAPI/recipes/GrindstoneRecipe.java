package pl.muldek.recipesAPI.recipes;

import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GrindstoneRecipe {
    
    private Function<PrepareGrindstoneEvent, ItemStack> function;
    
    private ItemStack item;
    private boolean exactItem;
    
    private Queue<ItemStack> ingredients;
    private boolean exactIngredients;
    
    private ItemStack result;
    
    public GrindstoneRecipe(ItemStack item, List<ItemStack> ingredients, ItemStack result) {
        this(item,false, ingredients,false, result);
    }
    public GrindstoneRecipe(ItemStack item, boolean exactItem, List<ItemStack> ingredients, boolean exactIngredients, ItemStack result) {
        this.item = item.clone();
        this.exactIngredients = exactItem;
        this.ingredients = new ArrayDeque<>(ingredients.stream().map(k -> k.clone()).collect(Collectors.toList()));
        this.exactIngredients = exactIngredients;
        this.result = result.clone();
    }
    
    public Function<PrepareGrindstoneEvent, ItemStack> getFunction() { return function; }
    public void setFunction(Function<PrepareGrindstoneEvent, ItemStack> function) { this.function = function; }
    
    public ItemStack getItem() { return item.clone(); }
    public void setItem(ItemStack item) { this.item = item.clone(); }
    
    public boolean isExactItem() { return exactItem; }
    public void setExactItem(boolean exactItem) { this.exactItem = exactItem; }
    
    public Queue<ItemStack> getIngredients() { return ingredients; }
    
    public boolean isExactIngredients() { return exactIngredients; }
    public void setExactIngredients(boolean exactIngredients) { this.exactIngredients = exactIngredients; }
    
    public void setIngredients(Queue<ItemStack> ingredients) { this.ingredients = ingredients; }
    
    public ItemStack getResult() { return result.clone(); }
    public void setResult(ItemStack result) { this.result = result; }
}
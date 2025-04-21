package pl.muldek.recipesAPI.recipes;

import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AnvilRecipe {
    
    private Function<PrepareAnvilEvent, ItemStack> function;
    
    private ItemStack item;
    private boolean exactItem;
    
    private Queue<ItemStack> ingredients;
    private boolean exactIngredients;
    
    private ItemStack result;
    private int cost;
    
    public AnvilRecipe(ItemStack item, List<ItemStack> ingredients, ItemStack result) {
        this(item,false, ingredients,false, result,0);
    }
    public AnvilRecipe(ItemStack item, boolean exactItem, List<ItemStack> ingredients, boolean exactIngredients, ItemStack result, int cost) {
        this.item = item.clone();
        this.exactItem = exactItem;
        this.ingredients = new ArrayDeque<>(ingredients.stream().map(k -> k.clone()).collect(Collectors.toList()));
        this.exactIngredients = exactIngredients;
        this.result = result.clone();
        this.cost = cost;
    }
    
    public Function<PrepareAnvilEvent, ItemStack> getFunction() { return function; }
    public void setFunction(Function<PrepareAnvilEvent, ItemStack> function) { this.function = function; }
    
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
    
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
}

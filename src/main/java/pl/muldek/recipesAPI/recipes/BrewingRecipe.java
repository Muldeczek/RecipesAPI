package pl.muldek.recipesAPI.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import pl.muldek.recipesAPI.BrewClock;
import pl.muldek.recipesAPI.Pair;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BrewingRecipe {
    
    private Function<Pair, ItemStack> function;
    private BrewClock clock;
    
    private ItemStack ingredient;
    private boolean exactIngredient;
    
    private Queue<ItemStack> items;
    private boolean exactItems;
    
    private ItemStack fuel;
    private boolean exactFuel;
    
    private int fuelConsumption;
    private int brewingTime;
    
    public BrewingRecipe(ItemStack ingredient, List<ItemStack> items) {
        this(ingredient,false, items,false,null,false,1,400);
    }
    public BrewingRecipe(ItemStack ingredient, boolean exactIngredient,
                         List<ItemStack> items, boolean exactItems,
                         int fuelConsumption) {
        this(ingredient, exactIngredient, items, exactItems,null,false, fuelConsumption,400);
    }
    public BrewingRecipe(ItemStack ingredient, boolean exactIngredient,
                         List<ItemStack> items, boolean exactItems,
                         int fuelConsumption, int brewingTime) {
        this(ingredient, exactIngredient, items, exactItems,null,false, fuelConsumption, brewingTime);
    }
    
    public BrewingRecipe(ItemStack ingredient, boolean exactIngredient,
                         List<ItemStack> items, boolean exactItems,
                         ItemStack fuel, boolean exactFuel) {
        this(ingredient, exactIngredient, items, exactItems, fuel, exactFuel,0,0);
    }
    
    public BrewingRecipe(ItemStack ingredient, boolean exactIngredient,
                         List<ItemStack> items, boolean exactItems,
                         ItemStack fuel, boolean exactFuel, int brewingTime) {
        this(ingredient, exactIngredient, items, exactItems, fuel, exactFuel,0, brewingTime);
    }
    
    public BrewingRecipe(ItemStack ingredient, boolean exactIngredient,
                         List<ItemStack> items, boolean exactItems,
                         ItemStack fuel, boolean exactFuel,
                         int fuelConsumption, int brewingTime) {
        this.ingredient = ingredient;
        this.exactIngredient = exactIngredient;
        this.items = new ArrayDeque<>(items.stream().map(k -> k.clone()).collect(Collectors.toList()));
        this.exactItems = exactItems;
        this.fuel = (fuel == null ? new ItemStack(Material.AIR) : fuel);
        this.exactFuel = exactFuel;
        this.fuelConsumption = fuelConsumption;
        this.brewingTime = brewingTime;
    }
    
    public Function<Pair, ItemStack> getFunction() { return function; }
    public void setFunction(Function<Pair, ItemStack> function) { this.function = function; }
    
    public BrewClock getClock() { return clock; }
    public void setClock(BrewClock clock) { this.clock = clock; }
    
    public ItemStack getIngredient() { return ingredient; }
    public void setIngredient(ItemStack ingredient) { this.ingredient = ingredient.clone(); }
    
    public boolean isExactIngredient() { return exactIngredient; }
    public void setExactIngredient(boolean exactIngredient) { this.exactIngredient = exactIngredient; }
    
    public Queue<ItemStack> getItems() { return items; }
    
    public boolean isExactItems() { return exactItems; }
    public void setExactItems(boolean exactItems) { this.exactItems = exactItems; }
    
    public ItemStack getFuel() { return fuel; }
    public void setFuel(ItemStack fuel) { this.fuel = fuel.clone(); }
    
    public boolean isExactFuel() { return exactFuel; }
    public void setExactFuel(boolean exactFuel) { this.exactFuel = exactFuel; }
    
    public int getFuelConsumption() { return fuelConsumption; }
    public void setFuelConsumption(int fuelConsumption) { this.fuelConsumption = fuelConsumption; }
    
    public int getBrewingTime() { return brewingTime; }
    public void setBrewingTime(int brewingTime) { this.brewingTime = brewingTime; }
    
    public void startBrewing(BrewerInventory inventory) { clock = new BrewClock(this, inventory, brewingTime); }
}
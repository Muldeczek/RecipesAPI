package pl.muldek.RecipesAPI;

import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public record Pair(BrewerInventory inventory, ItemStack item) {}

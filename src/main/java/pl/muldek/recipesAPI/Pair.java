package pl.muldek.recipesAPI;

import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public record Pair(BrewerInventory inventory, ItemStack item) {}

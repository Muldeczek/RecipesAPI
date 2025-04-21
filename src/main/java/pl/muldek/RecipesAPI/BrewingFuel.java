package pl.muldek.RecipesAPI;

import org.bukkit.inventory.ItemStack;

public record BrewingFuel(ItemStack fuel, boolean exactFuel, Integer fuelLoad) {}

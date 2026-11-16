package com.iranituber.registry;

import com.iranituber.IraniTuberMod;
import com.iranituber.item.CameraItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItems {
	private ModItems() {}

	public static final Item CAMERA = register("camera", new CameraItem(new Item.Settings().maxCount(1)));

	public static final Item TUBER_SPAWN_EGG = register("tuber_spawn_egg",
			new SpawnEggItem(ModEntities.TUBER, 0xC62828, 0xFFD54F, new Item.Settings()));

	public static final ItemGroup GROUP = Registry.register(
			Registries.ITEM_GROUP,
			Identifier.of(IraniTuberMod.MOD_ID, "main"),
			FabricItemGroup.builder()
					.icon(() -> new ItemStack(CAMERA))
					.displayName(Text.translatable("itemGroup.irani_tuber.main"))
					.entries((context, entries) -> {
						entries.add(CAMERA);
						entries.add(TUBER_SPAWN_EGG);
					})
					.build());

	private static Item register(String name, Item item) {
		return Registry.register(Registries.ITEM, Identifier.of(IraniTuberMod.MOD_ID, name), item);
	}

	public static void register() {}
}

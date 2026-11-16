package com.iranituber;

import com.iranituber.channel.ChannelCommand;
import com.iranituber.channel.RecordingManager;
import com.iranituber.registry.ModEntities;
import com.iranituber.registry.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.BiomeKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IraniTuberMod implements ModInitializer {
	public static final String MOD_ID = "irani_tuber";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModEntities.register();
		ModItems.register();

		// طبیعی‌ترین جاها برای پیدا کردن یوتوبرها: دشت و جنگل
		BiomeModifications.addSpawn(
				BiomeSelectors.includeByKey(
						BiomeKeys.PLAINS,
						BiomeKeys.SUNFLOWER_PLAINS,
						BiomeKeys.MEADOW,
						BiomeKeys.FOREST,
						BiomeKeys.FLOWER_FOREST),
				SpawnGroup.CREATURE,
				ModEntities.TUBER,
				6, 1, 3);

		ChannelCommand.register();

		ServerTickEvents.END_SERVER_TICK.register(RecordingManager::tick);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> RecordingManager.forget(handler.player));

		LOGGER.info("Irani Youtuber loaded. Camera, lights, action!");
	}
}

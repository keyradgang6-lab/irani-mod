package com.iranituber.registry;

import com.iranituber.IraniTuberMod;
import com.iranituber.entity.TuberEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public final class ModEntities {
	private ModEntities() {}

	public static final EntityType<TuberEntity> TUBER = Registry.register(
			Registries.ENTITY_TYPE,
			Identifier.of(IraniTuberMod.MOD_ID, "tuber"),
			FabricEntityTypeBuilder.<TuberEntity>createMob()
					.spawnGroup(SpawnGroup.CREATURE)
					.entityFactory(TuberEntity::new)
					.spawnRestriction(SpawnLocationTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MobEntity::canMobSpawn)
					.dimensions(EntityDimensions.fixed(0.6f, 1.8f))
					.defaultAttributes(TuberEntity::createTuberAttributes)
					.build());

	/** فقط برای اینکه کلاس لود بشه و ثبت انجام بشه. */
	public static void register() {}
}

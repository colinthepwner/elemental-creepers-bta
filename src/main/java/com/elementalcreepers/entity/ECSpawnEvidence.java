package com.elementalcreepers.entity;

import com.elementalcreepers.ElementalCreepers;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.pos.TilePos;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class ECSpawnEvidence {

	private ECSpawnEvidence() {}

	private static final Set<String> REPORTED = Collections.synchronizedSet(new HashSet<>());

	static void record(MobElementalCreeper creeper, String id) {
		if (!REPORTED.add(id)) {
			return;
		}
		Biome biome = creeper.world.getBlockBiome(
			new TilePos((int) creeper.x, (int) creeper.y, (int) creeper.z));
		ElementalCreepers.LOGGER.info(
			"First natural spawn: {}:{} at ({}, {}, {}) in biome '{}'.",
			ElementalCreepers.MOD_ID, id,
			(int) creeper.x, (int) creeper.y, (int) creeper.z,
			biome == null ? "unknown" : String.valueOf(biome.getRegistryKey()));
	}
}

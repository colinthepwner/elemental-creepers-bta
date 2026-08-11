package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import com.elementalcreepers.ElementalCreepers;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.enums.MobCategory;
import net.minecraft.core.entity.factories.EntityFactory;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.biome.Biome;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.elementalcreepers.ElementalCreepers.MOD_ID;

public final class ECEntities {
	private ECEntities() {}

	public static final List<String> REGISTERED_IDS = new ArrayList<>();

	public static String nameKeyFor(String name) {
		return "guidebook.section.mob." + name + ".name";
	}

	private static <T extends Entity> void register(Class<T> entityClass, String name,
	                                                EntityFactory<T> factory) {
		String nameKey = nameKeyFor(name);
		EntityDispatcher.getInstance().addMapping(
			entityClass,

			new NamespaceID(MOD_ID, name),
			factory,
			nameKey
		);
		REGISTERED_IDS.add(name);
	}

	public static void initEntities() {

		register(MobWaterCreeper.class, "watercreeper", MobWaterCreeper::new);
		register(MobFireCreeper.class, "firecreeper", MobFireCreeper::new);
		register(MobIceCreeper.class, "icecreeper", MobIceCreeper::new);
		register(MobElectricCreeper.class, "electriccreeper", MobElectricCreeper::new);
		register(MobEarthCreeper.class, "earthcreeper", MobEarthCreeper::new);
		register(MobPsychicCreeper.class, "psychiccreeper", MobPsychicCreeper::new);
		register(MobCookieCreeper.class, "cookiecreeper", MobCookieCreeper::new);
		register(MobMagmaCreeper.class, "magmacreeper", MobMagmaCreeper::new);
		register(MobGhostCreeper.class, "ghostcreeper", MobGhostCreeper::new);
	}

	public static void registerSpawns() {
		List<String> netherKeys = new ArrayList<>();
		int allBiomes = 0;

		for (Biome biome : Registries.BIOMES.values()) {
			List<SpawnListEntry> monsters = biome.getSpawnableList(MobCategory.MONSTER);
			++allBiomes;

			if (isNether(biome)) {

				monsters.add(new SpawnListEntry(MobMagmaCreeper.class, ECConfig.MAGMA_CREEPER_SPAWN));
				netherKeys.add(String.valueOf(biome.getRegistryKey()));
			}

			monsters.add(new SpawnListEntry(MobWaterCreeper.class, ECConfig.WATER_CREEPER_SPAWN));
			monsters.add(new SpawnListEntry(MobFireCreeper.class, ECConfig.FIRE_CREEPER_SPAWN));
			monsters.add(new SpawnListEntry(MobIceCreeper.class, ECConfig.ICE_CREEPER_SPAWN));
			monsters.add(new SpawnListEntry(MobElectricCreeper.class, ECConfig.ELECTRIC_CREEPER_SPAWN));
			monsters.add(new SpawnListEntry(MobEarthCreeper.class, ECConfig.EARTH_CREEPER_SPAWN));
			monsters.add(new SpawnListEntry(MobPsychicCreeper.class, ECConfig.PSYCHIC_CREEPER_SPAWN));
			monsters.add(new SpawnListEntry(MobCookieCreeper.class, ECConfig.COOKIE_CREEPER_SPAWN));

			monsters.add(new SpawnListEntry(MobGhostCreeper.class, 12));
		}

		ElementalCreepers.LOGGER.info(
			"Added elemental creeper spawns to {} biomes ({} of them nether).",
			allBiomes, netherKeys.size());

		ElementalCreepers.LOGGER.info("Magma creeper nether biomes: {}", String.join(", ", netherKeys));
	}

	private static boolean isNether(Biome biome) {
		String key = biome.getRegistryKey();
		if (key != null) {
			int colon = key.indexOf(':');
			String path = colon >= 0 ? key.substring(colon + 1) : key;
			for (String segment : path.toLowerCase(Locale.ROOT).split("[._-]")) {
				if (segment.equals("nether")) {
					return true;
				}
			}
		}
		return biome.getClass().getName().toLowerCase(Locale.ROOT).contains("nether");
	}
}

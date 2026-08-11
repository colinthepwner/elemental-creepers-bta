package com.elementalcreepers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.IOException;

import static com.elementalcreepers.ElementalCreepers.MOD_ID;

@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
public final class ECConfig {
	private ECConfig() {}

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static TomlConfigHandler cfg;

	public static final String SPAWN_CATEGORY = "Spawning";
	public static final String EFFECT_CATEGORY = "Effects";

	public static int WATER_CREEPER_SPAWN = 6;
	public static int FIRE_CREEPER_SPAWN = 6;
	public static int ICE_CREEPER_SPAWN = 6;
	public static int ELECTRIC_CREEPER_SPAWN = 6;
	public static int EARTH_CREEPER_SPAWN = 6;
	public static int PSYCHIC_CREEPER_SPAWN = 6;
	public static int COOKIE_CREEPER_SPAWN = 1;

	public static int MAGMA_CREEPER_SPAWN = 8;

	public static int WATER_CREEPER_RADIUS = 5;
	public static int FIRE_CREEPER_RADIUS = 6;
	public static int ICE_CREEPER_RADIUS = 12;
	public static int ELECTRIC_CREEPER_RADIUS = 5;
	public static int EARTH_CREEPER_RADIUS = 8;
	public static int PSYCHIC_CREEPER_POWER = 6;
	public static int COOKIE_CREEPER_AMOUNT = 25;
	public static int MAGMA_CREEPER_RADIUS = 5;

	public static int GHOST_CREEPER_RADIUS = 5;

	public static int GHOST_CREEPER_CHANCE = 35;

	@SuppressWarnings({"java:S899", "ResultOfMethodCallIgnored"})
	static void init() {
		LOGGER.info("Initializing config..");

		Toml props = new Toml("Elemental Creepers.toml");
		assembleProperties(props);

		cfg = new TomlConfigHandler(MOD_ID, props);

		if (cfg.getConfigFile().exists()) {
			cfg.loadConfig();
		} else {
			try {
				cfg.getConfigFile().createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			cfg.writeConfig();
		}

		loadProperties();
	}

	private static void assembleProperties(Toml properties) {
		properties.addCategory(SPAWN_CATEGORY)
			.addEntry("cfgVersion", 1)
			.addEntry("WATER_CREEPER_SPAWN", WATER_CREEPER_SPAWN)
			.addEntry("FIRE_CREEPER_SPAWN", FIRE_CREEPER_SPAWN)
			.addEntry("ICE_CREEPER_SPAWN", ICE_CREEPER_SPAWN)
			.addEntry("ELECTRIC_CREEPER_SPAWN", ELECTRIC_CREEPER_SPAWN)
			.addEntry("EARTH_CREEPER_SPAWN", EARTH_CREEPER_SPAWN)
			.addEntry("PSYCHIC_CREEPER_SPAWN", PSYCHIC_CREEPER_SPAWN)
			.addEntry("COOKIE_CREEPER_SPAWN", COOKIE_CREEPER_SPAWN)
			.addEntry("MAGMA_CREEPER_SPAWN", MAGMA_CREEPER_SPAWN);

		properties.addCategory(EFFECT_CATEGORY)
			.addEntry("WATER_CREEPER_RADIUS", WATER_CREEPER_RADIUS)
			.addEntry("FIRE_CREEPER_RADIUS", FIRE_CREEPER_RADIUS)
			.addEntry("ICE_CREEPER_RADIUS", ICE_CREEPER_RADIUS)
			.addEntry("ELECTRIC_CREEPER_RADIUS", ELECTRIC_CREEPER_RADIUS)
			.addEntry("EARTH_CREEPER_RADIUS", EARTH_CREEPER_RADIUS)
			.addEntry("PSYCHIC_CREEPER_POWER", PSYCHIC_CREEPER_POWER)
			.addEntry("COOKIE_CREEPER_AMOUNT", COOKIE_CREEPER_AMOUNT)
			.addEntry("MAGMA_CREEPER_RADIUS", MAGMA_CREEPER_RADIUS)
			.addEntry("GHOST_CREEPER_RADIUS", GHOST_CREEPER_RADIUS)
			.addEntry("GHOST_CREEPER_CHANCE", GHOST_CREEPER_CHANCE);
	}

	private static void loadProperties() {
		WATER_CREEPER_SPAWN = cfgGetValueOrDefault(SPAWN_CATEGORY + ".WATER_CREEPER_SPAWN", WATER_CREEPER_SPAWN);
		FIRE_CREEPER_SPAWN = cfgGetValueOrDefault(SPAWN_CATEGORY + ".FIRE_CREEPER_SPAWN", FIRE_CREEPER_SPAWN);
		ICE_CREEPER_SPAWN = cfgGetValueOrDefault(SPAWN_CATEGORY + ".ICE_CREEPER_SPAWN", ICE_CREEPER_SPAWN);
		ELECTRIC_CREEPER_SPAWN = cfgGetValueOrDefault(SPAWN_CATEGORY + ".ELECTRIC_CREEPER_SPAWN", ELECTRIC_CREEPER_SPAWN);
		EARTH_CREEPER_SPAWN = cfgGetValueOrDefault(SPAWN_CATEGORY + ".EARTH_CREEPER_SPAWN", EARTH_CREEPER_SPAWN);
		PSYCHIC_CREEPER_SPAWN = cfgGetValueOrDefault(SPAWN_CATEGORY + ".PSYCHIC_CREEPER_SPAWN", PSYCHIC_CREEPER_SPAWN);
		COOKIE_CREEPER_SPAWN = cfgGetValueOrDefault(SPAWN_CATEGORY + ".COOKIE_CREEPER_SPAWN", COOKIE_CREEPER_SPAWN);
		MAGMA_CREEPER_SPAWN = cfgGetValueOrDefault(SPAWN_CATEGORY + ".MAGMA_CREEPER_SPAWN", MAGMA_CREEPER_SPAWN);

		WATER_CREEPER_RADIUS = cfgGetValueOrDefault(EFFECT_CATEGORY + ".WATER_CREEPER_RADIUS", WATER_CREEPER_RADIUS);
		FIRE_CREEPER_RADIUS = cfgGetValueOrDefault(EFFECT_CATEGORY + ".FIRE_CREEPER_RADIUS", FIRE_CREEPER_RADIUS);
		ICE_CREEPER_RADIUS = cfgGetValueOrDefault(EFFECT_CATEGORY + ".ICE_CREEPER_RADIUS", ICE_CREEPER_RADIUS);
		ELECTRIC_CREEPER_RADIUS = cfgGetValueOrDefault(EFFECT_CATEGORY + ".ELECTRIC_CREEPER_RADIUS", ELECTRIC_CREEPER_RADIUS);
		EARTH_CREEPER_RADIUS = cfgGetValueOrDefault(EFFECT_CATEGORY + ".EARTH_CREEPER_RADIUS", EARTH_CREEPER_RADIUS);
		PSYCHIC_CREEPER_POWER = cfgGetValueOrDefault(EFFECT_CATEGORY + ".PSYCHIC_CREEPER_POWER", PSYCHIC_CREEPER_POWER);
		COOKIE_CREEPER_AMOUNT = cfgGetValueOrDefault(EFFECT_CATEGORY + ".COOKIE_CREEPER_AMOUNT", COOKIE_CREEPER_AMOUNT);
		MAGMA_CREEPER_RADIUS = cfgGetValueOrDefault(EFFECT_CATEGORY + ".MAGMA_CREEPER_RADIUS", MAGMA_CREEPER_RADIUS);
		GHOST_CREEPER_RADIUS = cfgGetValueOrDefault(EFFECT_CATEGORY + ".GHOST_CREEPER_RADIUS", GHOST_CREEPER_RADIUS);
		GHOST_CREEPER_CHANCE = cfgGetValueOrDefault(EFFECT_CATEGORY + ".GHOST_CREEPER_CHANCE", GHOST_CREEPER_CHANCE);
	}

	@SuppressWarnings("unchecked")
	static <T> T cfgGetValueOrDefault(String key, T def) {
		T res = null;

		try {
			if (def instanceof String) {
				res = (T) cfg.getString(key);
			} else if (def instanceof Integer) {
				res = (T) Integer.valueOf(cfg.getInt(key));
			} else if (def instanceof Long) {
				res = (T) Long.valueOf(cfg.getLong(key));
			} else if (def instanceof Boolean) {
				res = (T) Boolean.valueOf(cfg.getBoolean(key));
			} else if (def instanceof Double || def instanceof Float) {
				double raw = cfg.getDouble(key);
				if (def instanceof Float) {
					res = (T) Float.valueOf((float) raw);
				} else {
					res = (T) Double.valueOf(raw);
				}
			} else {
				throw new RuntimeException("Invalid value type!");
			}
		} catch (NullPointerException ignored) {  }

		if (res == null) {
			LOGGER.warn("Failed to load \"{}\"! Assuming default...", key);
			return def;
		}

		return res;
	}
}

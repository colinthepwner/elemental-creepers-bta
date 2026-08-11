package com.elementalcreepers;

import com.elementalcreepers.entity.ECEntities;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.util.dependency.Key;

public class ElementalCreepers implements ModInitializer {
	public static final String MOD_ID = HalpLibe.registerMod("elementalcreepers", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ECConfig.init();
		CommonEvents.BEFORE_GAME_START.listen(Key.of(MOD_ID), this::beforeGameStart);
		CommonEvents.AFTER_GAME_START.listen(Key.of(MOD_ID), this::afterGameStart);
		LOGGER.info("Elemental Creepers initialized.");
	}

	private void beforeGameStart() {

		ECEntities.initEntities();
	}

	private void afterGameStart() {

		ECEntities.registerSpawns();

		ECAudit.run();
	}
}

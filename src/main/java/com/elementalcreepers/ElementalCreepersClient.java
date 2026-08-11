package com.elementalcreepers;

import com.elementalcreepers.asset.ECAssetSidecar;
import com.elementalcreepers.client.render.ECEntityRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.sound.SoundRepository;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

public class ElementalCreepersClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientEvents.AFTER_CLIENT_START.listen(Key.of(ElementalCreepers.MOD_ID), this::afterClientStart);
		ClientEvents.ENTITY_RENDERER_RELOAD.listen(Key.of(ElementalCreepers.MOD_ID),
			ECEntityRenderers::registerRenderers);
	}

	private void afterClientStart() {
		SoundRepository.namespaceAdded(ElementalCreepers.MOD_ID);

		ECAssetSidecar.run();
	}
}

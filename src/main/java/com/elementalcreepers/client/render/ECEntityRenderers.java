package com.elementalcreepers.client.render;

import com.elementalcreepers.entity.MobCookieCreeper;
import com.elementalcreepers.entity.MobEarthCreeper;
import com.elementalcreepers.entity.MobElectricCreeper;
import com.elementalcreepers.entity.MobFireCreeper;
import com.elementalcreepers.entity.MobGhostCreeper;
import com.elementalcreepers.entity.MobIceCreeper;
import com.elementalcreepers.entity.MobMagmaCreeper;
import com.elementalcreepers.entity.MobPsychicCreeper;
import com.elementalcreepers.entity.MobWaterCreeper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.EntityRendererDispatcher;

@Environment(EnvType.CLIENT)
public final class ECEntityRenderers {
	private ECEntityRenderers() {}

	public static void registerRenderers(EntityRendererDispatcher dispatcher) {
		dispatcher.assignRenderer(MobWaterCreeper.class, new MobRendererElementalCreeper());
		dispatcher.assignRenderer(MobFireCreeper.class, new MobRendererElementalCreeper());
		dispatcher.assignRenderer(MobIceCreeper.class, new MobRendererElementalCreeper());
		dispatcher.assignRenderer(MobElectricCreeper.class, new MobRendererElementalCreeper());
		dispatcher.assignRenderer(MobEarthCreeper.class, new MobRendererElementalCreeper());
		dispatcher.assignRenderer(MobPsychicCreeper.class, new MobRendererElementalCreeper());
		dispatcher.assignRenderer(MobCookieCreeper.class, new MobRendererElementalCreeper());
		dispatcher.assignRenderer(MobMagmaCreeper.class, new MobRendererElementalCreeper());

		dispatcher.assignRenderer(MobGhostCreeper.class, new MobRendererGhostCreeper());
	}
}

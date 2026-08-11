package com.elementalcreepers.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.entity.monster.MobCreeper;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class MobRendererGhostCreeper extends MobRendererElementalCreeper {

	private static final float GHOST_ALPHA = 0.5F;

	@Override
	protected float getRenderAlpha(@NotNull MobCreeper entity, float partialTick) {
		return GHOST_ALPHA;
	}
}

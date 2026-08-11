package com.elementalcreepers.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.MobRendererCreeper;

@Environment(EnvType.CLIENT)
public class MobRendererElementalCreeper extends MobRendererCreeper {

	public MobRendererElementalCreeper() {
		super(0.5F);

		this.setModel("main", "geometry.creeper", 0.0D);
		this.setModel("charged", "geometry.creeper.charged", 0.0D);
	}
}

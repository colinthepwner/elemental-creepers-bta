package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobWaterCreeper extends MobElementalCreeper {

	public MobWaterCreeper(@NotNull World world) {
		super(world, "watercreeper");
	}

	@Override
	protected void detonate() {
		int radius = this.powerScaled(ECConfig.WATER_CREEPER_RADIUS);
		Block<?> water = Blocks.FLUID_WATER_STILL;
		int ox = this.blockX();
		int oy = this.blockY();
		int oz = this.blockZ();

		for (int dx = -radius; dx <= radius; ++dx) {
			for (int dy = -radius; dy <= radius; ++dy) {
				for (int dz = -radius; dz <= radius; ++dz) {
					if (this.isSurfaceSpot(water, ox + dx, oy + dy, oz + dz)) {
						this.place(water, ox + dx, oy + dy, oz + dz);
					}
				}
			}
		}
		this.playDetonationEffects();
	}
}

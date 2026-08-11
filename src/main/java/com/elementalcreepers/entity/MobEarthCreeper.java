package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobEarthCreeper extends MobElementalCreeper {

	public MobEarthCreeper(@NotNull World world) {
		super(world, "earthcreeper");
	}

	@Override
	protected void detonate() {
		int radius = this.powerScaled(ECConfig.EARTH_CREEPER_RADIUS);
		Block<?> dirt = Blocks.DIRT;
		int ox = this.blockX();
		int oy = this.blockY();
		int oz = this.blockZ();

		for (int dx = -radius; dx <= radius; ++dx) {
			for (int dy = -radius; dy <= radius; ++dy) {
				for (int dz = -radius; dz <= radius; ++dz) {

					if (this.canPlaceAt(dirt, ox + dx, oy + dy, oz + dz) && this.random.nextBoolean()) {
						this.place(dirt, ox + dx, oy + dy, oz + dz);
					}
				}
			}
		}
		this.playDetonationEffects();
	}
}

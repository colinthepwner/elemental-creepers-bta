package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobFireCreeper extends MobElementalCreeper {

	public MobFireCreeper(@NotNull World world) {
		super(world, "firecreeper");
		this.fireImmune = true;
	}

	@Override
	protected void detonate() {
		int radius = this.powerScaled(ECConfig.FIRE_CREEPER_RADIUS);

		Block<?> probe = Blocks.FLUID_WATER_STILL;
		Block<?> fire = Blocks.FIRE;
		int ox = this.blockX();
		int oy = this.blockY();
		int oz = this.blockZ();

		for (int dx = -radius; dx <= radius; ++dx) {
			for (int dy = -radius; dy <= radius; ++dy) {
				for (int dz = -radius; dz <= radius; ++dz) {
					if (this.isSurfaceSpot(probe, ox + dx, oy + dy, oz + dz)
						&& this.random.nextBoolean()) {
						this.place(fire, ox + dx, oy + dy, oz + dz);
					}
				}
			}
		}
		this.playDetonationEffects();
	}
}

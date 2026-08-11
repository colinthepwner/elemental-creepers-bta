package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobMagmaCreeper extends MobElementalCreeper {

	public MobMagmaCreeper(@NotNull World world) {
		super(world, "magmacreeper");
		this.fireImmune = true;
	}

	@Override
	public void tick() {
		if (!this.world.isClientSide
			&& (Math.round(this.x) != Math.round(this.xo)
			|| Math.round(this.y) != Math.round(this.yo)
			|| Math.round(this.z) != Math.round(this.zo))) {
			this.place(Blocks.FIRE,
				(int) Math.round(this.xo), (int) Math.round(this.yo), (int) Math.round(this.zo));
		}
		super.tick();
	}

	@Override
	protected void detonate() {
		int radius = this.powerScaled(ECConfig.MAGMA_CREEPER_RADIUS);
		Block<?> lava = Blocks.FLUID_LAVA_STILL;
		int ox = this.blockX();
		int oy = this.blockY();
		int oz = this.blockZ();

		for (int dx = -radius; dx <= radius; ++dx) {
			for (int dy = -radius; dy <= radius; ++dy) {
				for (int dz = -radius; dz <= radius; ++dz) {
					if (this.isSurfaceSpot(lava, ox + dx, oy + dy, oz + dz)) {
						this.place(lava, ox + dx, oy + dy, oz + dz);
					}
				}
			}
		}
		this.playDetonationEffects();
	}
}

package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobIceCreeper extends MobElementalCreeper {

	public MobIceCreeper(@NotNull World world) {
		super(world, "icecreeper");
	}

	@Override
	protected void detonate() {
		int radius = this.powerScaled(ECConfig.ICE_CREEPER_RADIUS);
		Block<?> snow = Blocks.LAYER_SNOW;
		int ox = this.blockX();
		int oy = this.blockY();
		int oz = this.blockZ();

		for (int dx = -radius; dx <= radius; ++dx) {
			for (int dy = -radius; dy <= radius; ++dy) {
				for (int dz = -radius; dz <= radius; ++dz) {
					int x = ox + dx;
					int y = oy + dy;
					int z = oz + dz;

					Material material = this.materialAt(x, y, z);

					boolean isSource = this.metadataAt(x, y, z) == 0;

					if (material == Materials.WATER && isSource) {
						this.place(Blocks.ICE, x, y, z);
					} else if (material == Materials.LAVA && isSource) {
						this.place(Blocks.OBSIDIAN, x, y, z);
					} else if (this.isSurfaceSpot(snow, x, y, z)) {
						this.place(snow, x, y, z);
					}
				}
			}
		}
		this.playDetonationEffects();
	}
}

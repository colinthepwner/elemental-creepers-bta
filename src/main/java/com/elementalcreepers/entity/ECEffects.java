package com.elementalcreepers.entity;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

final class ECEffects {

	private ECEffects() {}

	static void explosionSound(@NotNull World world, double x, double y, double z) {
		world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x, y, z, "random.explode", 4.0F,
			(1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
	}

	static void explosionPoof(@NotNull World world, @NotNull Entity entity) {
		for (int i = 0; i < 20; ++i) {
			double mx = world.rand.nextGaussian() * 0.02D;
			double my = world.rand.nextGaussian() * 0.02D;
			double mz = world.rand.nextGaussian() * 0.02D;
			double spread = 10.0D;
			world.spawnParticle("explode",
				entity.x + world.rand.nextFloat() * entity.bbWidth * 2.0F - entity.bbWidth - mx * spread,
				entity.y + world.rand.nextFloat() * entity.bbHeight - my * spread,
				entity.z + world.rand.nextFloat() * entity.bbWidth * 2.0F - entity.bbWidth - mz * spread,
				mx, my, mz, 0,  true);
		}
	}
}

package com.elementalcreepers.entity;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.primitives.AABBd;

import java.util.List;

public final class PsychicShockwave {

	private PsychicShockwave() {}

	public static void launch(@NotNull World world, @Nullable Entity exploder,
	                          double x, double y, double z, float power) {
		float size = power * 2.0F;

		int x1 = MathHelper.floor(x - size - 1.0D);
		int x2 = MathHelper.floor(x + size + 1.0D);
		int y1 = MathHelper.floor(y - size - 1.0D);
		int y2 = MathHelper.floor(y + size + 1.0D);
		int z1 = MathHelper.floor(z - size - 1.0D);
		int z2 = MathHelper.floor(z + size + 1.0D);

		List<Entity> targets =
			world.getEntitiesWithinAABBExcludingEntity(exploder, new AABBd(x1, y1, z1, x2, y2, z2));
		Vector3d centre = new Vector3d(x, y, z);

		for (Entity target : targets) {
			double falloff = target.distanceTo(x, y, z) / size;
			if (falloff > 1.0D) {
				continue;
			}

			double dirX = target.x - x;
			double dirY = target.y - y;
			double dirZ = target.z - z;
			double length = MathHelper.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);

			dirX /= length;
			dirY /= length;
			dirZ /= length;

			double seen = world.getSeenPercent(centre, target.bb);
			double force = (1.0D - falloff) * seen;

			target.xd += dirX * force * 2.0D;

			target.yd += 1.5D - Math.sqrt(dirX * dirX + dirZ * dirZ) * 0.75D;
			target.zd += dirZ * force * 2.0D;
		}
	}
}

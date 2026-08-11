package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import net.minecraft.core.entity.EntityLightning;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBd;

import java.util.List;

public class MobElectricCreeper extends MobElementalCreeper {

	public MobElectricCreeper(@NotNull World world) {
		super(world, "electriccreeper");
	}

	@Override
	protected void detonate() {
		int radius = this.powerScaled(ECConfig.ELECTRIC_CREEPER_RADIUS);

		List<Mob> targets = this.world.getEntitiesWithinAABB(
			Mob.class,
			new AABBd(this.x - radius, this.y - radius, this.z - radius,
				this.x + 1.0D + radius, this.y + 1.0D + radius, this.z + 1.0D + radius));

		for (Mob target : targets) {

			if (target == null || target instanceof MobCreeper) {
				continue;
			}
			this.world.entityJoinedWorld(new EntityLightning(this.world, target.x, target.y, target.z));
		}

		this.playDetonationEffects();
	}
}

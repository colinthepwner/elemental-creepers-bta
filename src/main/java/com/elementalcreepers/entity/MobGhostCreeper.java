package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobGhostCreeper extends MobElementalCreeper {

	private static final float BLAST_POWER = 6.0F;

	public MobGhostCreeper(@NotNull World world) {

		super(world);
	}

	@Override
	protected boolean spawnsGhostOnDeath() {
		return false;
	}

	@Override
	protected void detonate() {

		GhostShockwave.detonate(this.world, this, this.x, this.y, this.z, BLAST_POWER);

	}
}

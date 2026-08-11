package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobPsychicCreeper extends MobElementalCreeper {

	public MobPsychicCreeper(@NotNull World world) {
		super(world, "psychiccreeper");
	}

	@Override
	protected void detonate() {

		float power = this.getPowered()
			? ECConfig.PSYCHIC_CREEPER_POWER * 1.5F
			: ECConfig.PSYCHIC_CREEPER_POWER;

		PsychicShockwave.launch(this.world, this, this.x, this.y, this.z, power);

	}
}

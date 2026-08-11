package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobCookieCreeper extends MobElementalCreeper {

	public MobCookieCreeper(@NotNull World world) {
		super(world, "cookiecreeper");
	}

	@Override
	protected boolean spawnsGhostOnDeath() {
		return false;
	}

	@Override
	protected void detonate() {
		int amount = this.powerScaled(ECConfig.COOKIE_CREEPER_AMOUNT);

		for (int i = 0; i < amount; ++i) {

			float spread = 0.7F;
			double ox = this.world.rand.nextFloat() * spread + (1.0F + spread) * 0.5D;
			double oy = this.world.rand.nextFloat() * spread + (1.0F + spread) * 0.5D;
			double oz = this.world.rand.nextFloat() * spread + (1.0F + spread) * 0.5D;

			EntityItem cookie = new EntityItem(
				this.world, this.x + ox, this.y + oy, this.z + oz,
				new ItemStack(Items.FOOD_COOKIE, 1));

			cookie.pickupDelay = 10;
			this.world.entityJoinedWorld(cookie);
		}

		this.playDetonationEffects();
	}
}

package com.elementalcreepers.entity;

import com.elementalcreepers.ECConfig;
import com.elementalcreepers.ElementalCreepers;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;

public abstract class MobElementalCreeper extends MobCreeper {

	protected int fuse;

	private final String spawnId;

	private boolean fromSpawner;

	protected MobElementalCreeper(@NotNull World world, @NotNull String textureName) {
		super(world);

		this.setTextureIdentifier(ElementalCreepers.MOD_ID, textureName);
		this.spawnId = textureName;
	}

	protected MobElementalCreeper(@NotNull World world) {
		super(world);
		this.spawnId = "ghostcreeper";
	}

	@Override
	public boolean canSpawnHere() {
		boolean allowed = super.canSpawnHere();
		if (allowed) {
			this.fromSpawner = true;
		}
		return allowed;
	}

	@Override
	public void spawnInit() {
		super.spawnInit();
		if (this.fromSpawner) {
			ECSpawnEvidence.record(this, this.spawnId);
		}
	}

	protected abstract void detonate();

	protected boolean spawnsGhostOnDeath() {
		return true;
	}

	protected int powerScaled(int base) {
		return this.getPowered() ? (int) (base * 1.5F) : base;
	}

	protected int getFuseState() {
		return this.entityData.getByte(DATA_CREEPER_STATE);
	}

	protected void setFuseState(int state) {
		this.entityData.set(DATA_CREEPER_STATE, (byte) state);
	}

	@Override
	protected void attackEntity(@NotNull Entity entity, float distance) {
		if (this.world.isClientSide) {
			return;
		}

		int state = this.getFuseState();
		if (state <= 0 && distance < 3.0F || state > 0 && distance < 7.0F) {
			if (this.fuse == 0) {
				this.world.playSoundAtEntity(null, this, "mob.creeper.fuse", 1.0F, 1.0F);
			}
			this.setFuseState(1);
			++this.fuse;
			if (this.fuse >= 30) {
				this.detonate();
				this.remove();
			}
			this.hasAttacked = true;
		} else {
			this.setFuseState(-1);
			if (--this.fuse < 0) {
				this.fuse = 0;
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClientSide && this.target == null && this.fuse > 0) {
			this.setFuseState(-1);
			if (--this.fuse < 0) {
				this.fuse = 0;
			}
		}
	}

	@Override
	public void onDeath(Entity entityKilledBy) {
		super.onDeath(entityKilledBy);
		if (this.world.isClientSide || !this.spawnsGhostOnDeath()) {
			return;
		}
		if (this.random.nextInt(100) < ECConfig.GHOST_CREEPER_CHANCE) {
			MobGhostCreeper ghost = new MobGhostCreeper(this.world);
			ghost.setPos(this.x, this.y, this.z);
			this.world.entityJoinedWorld(ghost);
		}
	}

	private final TilePos scratch = new TilePos();

	protected boolean canPlaceAt(Block<?> block, int x, int y, int z) {
		return this.world.canBlockIdBePlacedAt(block.id(), this.scratch.set(x, y, z), false, Side.BOTTOM);
	}

	protected boolean isSurfaceSpot(Block<?> block, int x, int y, int z) {
		return this.canPlaceAt(block, x, y, z) && !this.canPlaceAt(block, x, y - 1, z);
	}

	protected void place(Block<?> block, int x, int y, int z) {
		this.world.setBlockTypeNotify(this.scratch.set(x, y, z), block);
	}

	protected Material materialAt(int x, int y, int z) {
		return this.world.getBlockMaterial(this.scratch.set(x, y, z));
	}

	protected int metadataAt(int x, int y, int z) {
		return this.world.getBlockData(this.scratch.set(x, y, z));
	}

	protected int blockX() { return (int) this.x; }

	protected int blockY() { return (int) this.y; }

	protected int blockZ() { return (int) this.z; }

	protected void playDetonationEffects() {
		ECEffects.explosionSound(this.world, this.x, this.y, this.z);
		ECEffects.explosionPoof(this.world, this);
	}
}

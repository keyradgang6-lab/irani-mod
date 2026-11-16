package com.iranituber.entity;

import com.iranituber.registry.ModItems;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;

/** وقتی دوربین دستته، یوتوبرها میان دنبالت تا تو ویدیو باشن. */
public class FollowCameraGoal extends Goal {
	private final TuberEntity mob;
	private PlayerEntity target;
	private int recalcTicks;

	public FollowCameraGoal(TuberEntity mob) {
		this.mob = mob;
		this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
	}

	@Override
	public boolean canStart() {
		PlayerEntity player = mob.getWorld().getClosestPlayer(mob, 12.0);
		if (player == null || !player.isHolding(ModItems.CAMERA)) {
			return false;
		}
		if (mob.squaredDistanceTo(player) <= 9.0) {
			return false;
		}
		this.target = player;
		return true;
	}

	@Override
	public boolean shouldContinue() {
		return target != null
				&& target.isAlive()
				&& target.isHolding(ModItems.CAMERA)
				&& mob.squaredDistanceTo(target) > 4.0
				&& mob.squaredDistanceTo(target) < 256.0;
	}

	@Override
	public void start() {
		recalcTicks = 0;
	}

	@Override
	public void tick() {
		mob.getLookControl().lookAt(target, 30.0f, mob.getMaxLookPitchChange());
		if (--recalcTicks <= 0) {
			recalcTicks = 10;
			mob.getNavigation().startMovingTo(target, 1.0);
		}
	}

	@Override
	public void stop() {
		target = null;
		mob.getNavigation().stop();
	}
}

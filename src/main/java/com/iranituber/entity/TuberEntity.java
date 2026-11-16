package com.iranituber.entity;

import com.iranituber.registry.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * یوتوبر ایرانی ما. سه تا شخصیت اورجینال داره (آرسام، پگاه، بردیا) که
 * ظاهر و تکیه‌کلام خودشون رو دارن. فارسی حرف می‌زنن و دوربین دستشونه.
 */
public class TuberEntity extends PathAwareEntity {
	public static final int VARIANT_COUNT = 3;

	private static final TrackedData<Integer> VARIANT =
			DataTracker.registerData(TuberEntity.class, TrackedDataHandlerRegistry.INTEGER);

	private int chatCooldown;
	private int hurtCooldown;

	public TuberEntity(EntityType<? extends PathAwareEntity> type, World world) {
		super(type, world);
		this.dataTracker.set(VARIANT, this.random.nextInt(VARIANT_COUNT));
		this.chatCooldown = 300 + this.random.nextInt(600);
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.CAMERA));
		this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
	}

	public static DefaultAttributeContainer.Builder createTuberAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
				.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
				.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
		this.goalSelector.add(3, new FollowCameraGoal(this));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
		this.goalSelector.add(7, new LookAroundGoal(this));
	}

	// ---------- متغیر (کدوم شخصیت) ----------

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(VARIANT, 0);
	}

	public int getVariant() {
		return this.dataTracker.get(VARIANT);
	}

	public void setVariant(int variant) {
		this.dataTracker.set(VARIANT, Math.floorMod(variant, VARIANT_COUNT));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Variant", getVariant());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		setVariant(nbt.getInt("Variant"));
	}

	@Override
	protected Text getDefaultName() {
		return Text.translatable("entity.irani_tuber.tuber." + getVariant());
	}

	// ---------- حرف زدن ----------

	/** یه جمله از دسته‌ی داده‌شده برای بازیکن می‌فرسته. گاهی به‌جاش تکیه‌کلام خودش رو می‌گه. */
	public void say(ServerPlayerEntity player, String category, int count) {
		Text line;
		if (this.random.nextInt(4) == 0 && (category.equals("idle") || category.equals("interact"))) {
			line = Text.translatable("dialogue.irani_tuber.catch." + getVariant());
		} else {
			line = Text.translatable("dialogue.irani_tuber." + category + "." + (1 + this.random.nextInt(count)));
		}
		player.sendMessage(
				Text.empty()
						.append(Text.literal("[").formatted(Formatting.GRAY))
						.append(getName().copy().formatted(Formatting.GOLD))
						.append(Text.literal("] ").formatted(Formatting.GRAY))
						.append(line),
				false);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld().isClient) {
			return;
		}
		if (hurtCooldown > 0) {
			hurtCooldown--;
		}
		if (--chatCooldown <= 0) {
			chatCooldown = 600 + this.random.nextInt(900);
			PlayerEntity nearest = this.getWorld().getClosestPlayer(this, 10.0);
			if (nearest instanceof ServerPlayerEntity player) {
				say(player, "idle", Dialogue.IDLE);
			}
		}
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		if (player.getStackInHand(hand).isOf(ModItems.CAMERA)) {
			// با دوربین، راست‌کلیک رو بده به خود دوربین (شروع ضبط)
			return ActionResult.PASS;
		}
		if (!this.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
			say(serverPlayer, "interact", Dialogue.INTERACT);
		}
		return ActionResult.success(this.getWorld().isClient);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		boolean hurt = super.damage(source, amount);
		if (hurt && !this.getWorld().isClient && hurtCooldown <= 0
				&& source.getAttacker() instanceof ServerPlayerEntity attacker) {
			hurtCooldown = 40;
			say(attacker, "hurt", Dialogue.HURT);
		}
		return hurt;
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}

	// ---------- صدا ----------

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_VILLAGER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_VILLAGER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_VILLAGER_DEATH;
	}
}

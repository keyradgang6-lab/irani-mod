package com.iranituber.channel;

import com.iranituber.entity.Dialogue;
import com.iranituber.entity.TuberEntity;
import com.iranituber.registry.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** ضبط ویدیو: شروع، شمارش معکوس، پایان و پاداش. */
public final class RecordingManager {
	private RecordingManager() {}

	public static final int RECORD_TICKS = 100; // ۵ ثانیه
	private static final double GUEST_RANGE = 16.0;
	private static final int[] MILESTONES = {10, 50, 100, 500, 1000, 10000, 100000};
	private static final int[] MILESTONE_EMERALDS = {2, 5, 10, 20, 40, 64, 64};

	private static final Map<UUID, Integer> ACTIVE = new HashMap<>();

	public static boolean isRecording(ServerPlayerEntity player) {
		return ACTIVE.containsKey(player.getUuid());
	}

	public static void forget(ServerPlayerEntity player) {
		ACTIVE.remove(player.getUuid());
	}

	public static void start(ServerPlayerEntity player) {
		ServerWorld world = (ServerWorld) player.getWorld();
		ACTIVE.put(player.getUuid(), RECORD_TICKS);
		world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 1.0f, 0.6f);
		player.sendMessage(Text.translatable("message.irani_tuber.rec_start").formatted(Formatting.RED), true);

		// یکی از دوستای نزدیک واکنش نشون می‌ده
		List<TuberEntity> guests = nearbyTubers(world, player);
		if (!guests.isEmpty()) {
			guests.get(world.random.nextInt(guests.size())).say(player, "camera_start", Dialogue.CAMERA_START);
		}
	}

	public static void cancel(ServerPlayerEntity player, boolean playSound) {
		ACTIVE.remove(player.getUuid());
		if (playSound) {
			player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 1.0f, 1.2f);
		}
		player.sendMessage(Text.translatable("message.irani_tuber.rec_cancel").formatted(Formatting.GRAY), true);
	}

	public static void tick(MinecraftServer server) {
		if (ACTIVE.isEmpty()) {
			return;
		}
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			Integer left = ACTIVE.get(player.getUuid());
			if (left == null) {
				continue;
			}
			if (!player.isHolding(ModItems.CAMERA)) {
				cancel(player, true);
				continue;
			}
			left--;
			if (left <= 0) {
				ACTIVE.remove(player.getUuid());
				finish(player);
			} else {
				ACTIVE.put(player.getUuid(), left);
				if (left % 20 == 0) {
					int seconds = left / 20;
					player.sendMessage(Text.translatable("message.irani_tuber.rec_progress", seconds).formatted(Formatting.RED), true);
				}
			}
		}
	}

	private static List<TuberEntity> nearbyTubers(ServerWorld world, ServerPlayerEntity player) {
		return world.getEntitiesByClass(TuberEntity.class, player.getBoundingBox().expand(GUEST_RANGE), e -> e.isAlive());
	}

	private static void finish(ServerPlayerEntity player) {
		ServerWorld world = (ServerWorld) player.getWorld();
		MinecraftServer server = world.getServer();
		Random random = world.random;

		List<TuberEntity> guests = nearbyTubers(world, player);
		int guestCount = Math.min(guests.size(), 5);

		long views = 40 + random.nextInt(120) + guestCount * 90L;
		int subsGained = 1 + random.nextInt(3) + guestCount * 2;

		ChannelState.Channel channel = ChannelState.of(server).forPlayer(player.getUuid());
		int before = channel.subs;
		channel.videos++;
		channel.views += views;
		channel.subs += subsGained;

		world.playSound(null, player.getBlockPos(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.PLAYERS, 1.0f, 1.0f);

		int titleIndex = 1 + random.nextInt(Dialogue.VIDEO_TITLES);
		player.sendMessage(Text.translatable("message.irani_tuber.rec_done").formatted(Formatting.GREEN, Formatting.BOLD), true);
		player.sendMessage(Text.translatable(
				"message.irani_tuber.uploaded",
				Text.translatable("video.irani_tuber.title." + titleIndex),
				views, subsGained, channel.subs), false);

		if (!guests.isEmpty()) {
			guests.get(random.nextInt(guests.size())).say(player, "camera_done", Dialogue.CAMERA_DONE);
		}

		for (int i = 0; i < MILESTONES.length; i++) {
			if (before < MILESTONES[i] && channel.subs >= MILESTONES[i]) {
				player.sendMessage(Text.translatable("message.irani_tuber.milestone", MILESTONES[i]).formatted(Formatting.GOLD, Formatting.BOLD), false);
				player.giveItemStack(new ItemStack(Items.EMERALD, MILESTONE_EMERALDS[i]));
			}
		}

		player.getItemCooldownManager().set(ModItems.CAMERA, 40);
	}
}

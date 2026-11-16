package com.iranituber.item;

import com.iranituber.channel.RecordingManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * دوربین فیلمبرداری. راست‌کلیک = شروع ضبط (۵ ثانیه). دوباره راست‌کلیک = لغو.
 * هرچی دوستای بیشتری دور و برت باشن، ویدیو ویو و سابسکرایبر بیشتری می‌گیره.
 */
public class CameraItem extends Item {
	public CameraItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (!world.isClient && user instanceof ServerPlayerEntity player) {
			if (RecordingManager.isRecording(player)) {
				RecordingManager.cancel(player, true);
			} else {
				RecordingManager.start(player);
			}
		}
		return TypedActionResult.success(stack, world.isClient);
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		tooltip.add(Text.translatable("item.irani_tuber.camera.tooltip.1").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("item.irani_tuber.camera.tooltip.2").formatted(Formatting.DARK_GRAY));
	}
}

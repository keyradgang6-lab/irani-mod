package com.iranituber.channel;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/** /channel  ->  آمار کانال خودت رو نشون می‌ده. */
public final class ChannelCommand {
	private ChannelCommand() {}

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("channel").executes(context -> {
					ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
					ChannelState.Channel c = ChannelState.of(context.getSource().getServer()).forPlayer(player.getUuid());
					context.getSource().sendFeedback(() -> Text.translatable(
							"command.irani_tuber.channel",
							player.getName(), c.subs, c.views, c.videos), false);
					return 1;
				})));
	}
}

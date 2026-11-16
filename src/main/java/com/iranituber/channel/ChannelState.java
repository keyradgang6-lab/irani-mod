package com.iranituber.channel;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** آمار کانال هر بازیکن (سابسکرایبر، ویو، تعداد ویدیو). توی ذخیره‌ی ورلد ذخیره می‌شه. */
public class ChannelState extends PersistentState {
	public static class Channel {
		public int subs;
		public long views;
		public int videos;
	}

	private final Map<UUID, Channel> channels = new HashMap<>();

	public Channel forPlayer(UUID id) {
		return channels.computeIfAbsent(id, k -> new Channel());
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		NbtCompound all = new NbtCompound();
		channels.forEach((id, c) -> {
			NbtCompound n = new NbtCompound();
			n.putInt("subs", c.subs);
			n.putLong("views", c.views);
			n.putInt("videos", c.videos);
			all.put(id.toString(), n);
		});
		nbt.put("channels", all);
		return nbt;
	}

	public static ChannelState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		ChannelState state = new ChannelState();
		NbtCompound all = nbt.getCompound("channels");
		for (String key : all.getKeys()) {
			try {
				NbtCompound n = all.getCompound(key);
				Channel c = new Channel();
				c.subs = n.getInt("subs");
				c.views = n.getLong("views");
				c.videos = n.getInt("videos");
				state.channels.put(UUID.fromString(key), c);
			} catch (IllegalArgumentException ignored) {
				// کلید خراب، رد می‌شیم
			}
		}
		return state;
	}

	private static final Type<ChannelState> TYPE = new Type<>(ChannelState::new, ChannelState::fromNbt, null);

	public static ChannelState of(MinecraftServer server) {
		PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
		ChannelState state = manager.getOrCreate(TYPE, "irani_tuber_channels");
		state.markDirty();
		return state;
	}
}

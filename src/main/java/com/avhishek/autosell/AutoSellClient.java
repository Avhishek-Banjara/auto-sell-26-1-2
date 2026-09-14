package com.avhishek.autosell;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;

import java.util.concurrent.ThreadLocalRandom;

public final class AutoSellClient implements ClientModInitializer {
    private static final long MIN_DELAY_MS = 80_000L;
    private static final long MAX_DELAY_MS = 90_000L;
    private static long nextSellAt = Long.MAX_VALUE;
    private static boolean connected;

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            connected = true;
            scheduleNext();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            connected = false;
            nextSellAt = Long.MAX_VALUE;
        });

        ClientTickEvents.END_CLIENT_TICK.register(AutoSellClient::onEndTick);
    }

    private static void onEndTick(Minecraft client) {
        if (!connected || client.player == null || client.getConnection() == null) return;
        if (System.currentTimeMillis() < nextSellAt) return;

        // Commands sent to the server must not include the leading slash.
        client.getConnection().sendCommand("sell all");
        scheduleNext();
    }

    private static void scheduleNext() {
        long delay = ThreadLocalRandom.current().nextLong(MIN_DELAY_MS, MAX_DELAY_MS + 1L);
        nextSellAt = System.currentTimeMillis() + delay;
    }
}

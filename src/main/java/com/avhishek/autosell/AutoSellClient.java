package com.avhishek.autosell;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ThreadLocalRandom;

public final class AutoSellClient implements ClientModInitializer {
    private static final long MIN_DELAY_MS = 80_000L;
    private static final long MAX_DELAY_MS = 90_000L;

    private static long nextSellAt = Long.MAX_VALUE;
    private static boolean connected;
    private static boolean enabled = true;

    private final KeyMapping.Category category =
            KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath("autosell", "controls")
            );

    private final KeyMapping toggleKey =
            KeyMappingHelper.registerKeyMapping(
                    new KeyMapping(
                            "key.autosell.toggle",
                            InputConstants.Type.KEYSYM,
                            GLFW.GLFW_KEY_UNKNOWN,
                            category
                    )
            );

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            connected = true;

            if (enabled) {
                scheduleNext();
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            connected = false;
            nextSellAt = Long.MAX_VALUE;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                enabled = !enabled;

                if (enabled) {
                    scheduleNext();
                } else {
                    nextSellAt = Long.MAX_VALUE;
                }

                if (client.player != null) {
                    client.player.sendSystemMessage(
                            Component.literal("Auto Sell: " + (enabled ? "ON" : "OFF"))
                    );
                }
            }

            runAutoSell(client);
        });
    }

    private static void runAutoSell(Minecraft client) {
        if (!enabled || !connected) return;
        if (client.player == null || client.getConnection() == null) return;
        if (System.currentTimeMillis() < nextSellAt) return;

        client.getConnection().sendCommand("sell all");
        scheduleNext();
    }

    private static void scheduleNext() {
        long delay = ThreadLocalRandom.current()
                .nextLong(MIN_DELAY_MS, MAX_DELAY_MS + 1L);

        nextSellAt = System.currentTimeMillis() + delay;
    }
}

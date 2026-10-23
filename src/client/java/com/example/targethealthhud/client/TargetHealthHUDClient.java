
package com.example.targethealthhud.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class TargetHealthHUDClient implements ClientModInitializer {

    private static final Identifier FULL_HEART =
        Identifier.fromNamespaceAndPath("minecraft", "hud/heart/full");

    private static final Identifier HALF_HEART =
        Identifier.fromNamespaceAndPath("minecraft", "hud/heart/half");

    private static final Identifier EMPTY_HEART =
        Identifier.fromNamespaceAndPath("minecraft", "hud/heart/container");

    private static final int HEART_SIZE = 9;
    private static final int HEART_SPACING = 9;
    private static final int MAX_VISIBLE_HEARTS = 20;

    @Override
    public void onInitializeClient() {
        // Load saved settings when Minecraft starts.
        TargetHealthHUDConfig.load();

        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath(
                "targethealthhud",
                "target_hud"
            ),
            TargetHealthHUDClient::render
        );
    }

    private static void render(
        GuiGraphicsExtractor graphics,
        DeltaTracker deltaTracker
    ) {
        Minecraft client = Minecraft.getInstance();

        if (client.player == null || client.level == null) {
            return;
        }

        // Stop rendering when the HUD is disabled.
        if (!TargetHealthHUDConfig.enabled) {
            return;
        }

        // Only display health when aiming at an entity.
        if (!(client.hitResult instanceof EntityHitResult hit)) {
            return;
        }

        Entity entity = hit.getEntity();

        if (!(entity instanceof LivingEntity target)
                || !target.isAlive()) {
            return;
        }

        int health = (int) Math.ceil(target.getHealth());
        int maxHealth = (int) Math.ceil(target.getMaxHealth());

        if (maxHealth <= 0) {
            return;
        }

        // Get the target's interpolated position above its head.
        float tickDelta =
            deltaTracker.getGameTimeDeltaPartialTick(false);

        Vec3 targetPosition = target
            .getPosition(tickDelta)
            .add(0, target.getBbHeight() + 0.35, 0);

        // Convert the target's world position to screen coordinates.
        Vec3 screenPosition =
            client.gameRenderer.projectPointToScreen(targetPosition);

        // Ignore positions outside the visible screen area.
        if (screenPosition.z < -1.0
                || screenPosition.z > 1.0
                || screenPosition.x < -1.0
                || screenPosition.x > 1.0
                || screenPosition.y < -1.0
                || screenPosition.y > 1.0) {
            return;
        }

        int centerX = (int) (
            (screenPosition.x + 1.0)
                * 0.5 * graphics.guiWidth()
        );

        int centerY = (int) (
            (1.0 - screenPosition.y)
                * 0.5 * graphics.guiHeight()
        );

        // NUMBERS MODE
        if ("Numbers".equals(TargetHealthHUDConfig.displayMode)) {
            String text = health + "/" + maxHealth;

            int textX = centerX - client.font.width(text) / 2;
            int textY = centerY - 5;

            graphics.text(
                client.font,
                text,
                textX,
                textY,
                0xFFFFFFFF,
                true
            );

            return;
        }

        // HEARTS MODE
        int totalHearts = (maxHealth + 1) / 2;

        int visibleHearts = Math.min(
            totalHearts,
            MAX_VISIBLE_HEARTS
        );

        int rowWidth = visibleHearts * HEART_SPACING;
        int startX = centerX - rowWidth / 2;

        // Position hearts just above the target's head.
        int startY = centerY - HEART_SIZE / 2;

        for (int i = 0; i < visibleHearts; i++) {
            int heartX = startX + i * HEART_SPACING;

            // Draw the empty heart background first.
            graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                EMPTY_HEART,
                heartX,
                startY,
                HEART_SIZE,
                HEART_SIZE
            );

            int remainingHealth = health - i * 2;

            if (remainingHealth >= 2) {
                graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    FULL_HEART,
                    heartX,
                    startY,
                    HEART_SIZE,
                    HEART_SIZE
                );
            } else if (remainingHealth == 1) {
                graphics.blitSprite(
                    RenderPipelines.GUI_TEXTURED,
                    HALF_HEART,
                    heartX,
                    startY,
                    HEART_SIZE,
                    HEART_SIZE
                );
            }
        }
    }
}

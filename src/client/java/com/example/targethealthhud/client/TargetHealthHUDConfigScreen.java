
package com.example.targethealthhud.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TargetHealthHUDConfigScreen extends Screen {

    private final Screen parent;

    public TargetHealthHUDConfigScreen(Screen parent) {
        super(Component.literal("Target Health HUD Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 40;

        addRenderableWidget(
            Button.builder(
                getEnabledText(),
                button -> {
                    TargetHealthHUDConfig.enabled =
                        !TargetHealthHUDConfig.enabled;

                    TargetHealthHUDConfig.save();
                    button.setMessage(getEnabledText());
                }
            ).bounds(centerX - 100, startY, 200, 20).build()
        );

        addRenderableWidget(
            Button.builder(
                getDisplayModeText(),
                button -> {
                    TargetHealthHUDConfig.displayMode =
                        TargetHealthHUDConfig.displayMode.equals("Hearts")
                            ? "Numbers"
                            : "Hearts";

                    TargetHealthHUDConfig.save();
                    button.setMessage(getDisplayModeText());
                }
            ).bounds(centerX - 100, startY + 28, 200, 20).build()
        );

        addRenderableWidget(
            Button.builder(
                Component.literal("Done"),
                button -> this.minecraft.gui.setScreen(parent)
            ).bounds(centerX - 100, startY + 64, 200, 20).build()
        );
    }

    private Component getEnabledText() {
        return Component.literal(
            "HUD: " + (TargetHealthHUDConfig.enabled ? "ON" : "OFF")
        );
    }

    private Component getDisplayModeText() {
        return Component.literal(
            "Display: " + TargetHealthHUDConfig.displayMode
        );
    }
}

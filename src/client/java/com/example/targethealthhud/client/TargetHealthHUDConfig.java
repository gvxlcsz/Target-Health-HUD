
package com.example.targethealthhud.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TargetHealthHUDConfig {

    private static final Gson GSON =
        new GsonBuilder().setPrettyPrinting().create();

    private static final Path CONFIG_PATH =
        FabricLoader.getInstance()
            .getConfigDir()
            .resolve("targethealthhud.json");

    public static boolean enabled = true;
    public static String displayMode = "Hearts";

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }

        try {
            String json = Files.readString(CONFIG_PATH);
            Settings settings = GSON.fromJson(json, Settings.class);

            if (settings != null) {
                enabled = settings.enabled;
                displayMode = settings.displayMode == null
                    ? "Hearts"
                    : settings.displayMode;

                if (!displayMode.equals("Hearts")
                        && !displayMode.equals("Numbers")) {
                    displayMode = "Hearts";
                }
            }
        } catch (Exception e) {
            System.err.println(
                "Could not load Target Health HUD settings: "
                    + e.getMessage()
            );
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            Settings settings = new Settings();
            settings.enabled = enabled;
            settings.displayMode = displayMode;

            Files.writeString(CONFIG_PATH, GSON.toJson(settings));
        } catch (IOException e) {
            System.err.println(
                "Could not save Target Health HUD settings: "
                    + e.getMessage()
            );
        }
    }

    private static class Settings {
        boolean enabled = true;
        String displayMode = "Hearts";
    }
}

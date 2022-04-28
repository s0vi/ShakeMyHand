package dev.s0vi.shakemyhand.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.s0vi.shakemyhand.ShakeMyHand;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class ConfigManager {
    private static final Logger LOGGER = ShakeMyHand.LOGGER;

    private final Path configPath;
    private final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    private ClientConfig config = new ClientConfig();

    public ConfigManager(Path configPath) {
        this.configPath = configPath;
        this.config = getOrCreateConfig();
        writeConfig();
    }

    private ClientConfig getOrCreateConfig() {
        LOGGER.info("Config exists: {}", configPath.toFile().exists());

        ClientConfig tempConfig = null;

        try {
            tempConfig = GSON.fromJson(new FileReader(configPath.toFile()), ClientConfig.class);
        } catch (FileNotFoundException e) {
            LOGGER.warn("Config could not be loaded. Creating a new one!");
        }

        if(tempConfig != null) return tempConfig;

        return new ClientConfig();
    }

    private void writeConfig() {
        CompletableFuture.runAsync(() -> {
            try {
                FileWriter fw = new FileWriter(configPath.toFile());
                fw.write(GSON.toJson(config));
                fw.close();
                LOGGER.info("Successfully wrote config.");
            } catch (IOException e) {
                LOGGER.info("Unable to write config! Changes not saved!");
            }
        });
    }

    public boolean shouldAlwaysHardRestart() {
        return config.alwaysHardRestart;
    }

    public boolean shouldTryToRejoin() {
        return config.tryToRejoin;
    }

    public boolean shouldTryToResolveUnknownMods() {
        return config.tryToResolveUnknownMods;
    }

    public String getModListToLoad() {
        return config.modListToLoad;
    }

    public boolean needsReload() {
        return config.needsReload;
    }

    public void setAlwaysHardRestart(boolean bool) {
        config.alwaysHardRestart = bool;
        writeConfig();
    }

    public void setTryToRejoin(boolean bool) {
        config.tryToRejoin = bool;
        writeConfig();
    }

    public void setTryToResolveUnknownMods(boolean bool) {
        config.tryToResolveUnknownMods = bool;
        writeConfig();
    }

    public void setModLostToLoad(String str) {
        config.modListToLoad = str;
        config.needsReload = false;
        writeConfig();
    }

    public void setNeedsReload(boolean bool) {
        config.needsReload = bool;
        writeConfig();
    }

}
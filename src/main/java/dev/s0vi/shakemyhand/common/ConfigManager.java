package dev.s0vi.shakemyhand.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import dev.s0vi.shakemyhand.ShakeMyHand;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

public class ConfigManager<T extends Config> {
    private final Class<T> configType;
    private final Supplier<T> defaultSupplier;
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private final Logger logger = ShakeMyHand.LOGGER;
    private final Path configPath = FabricLoaderImpl.InitHelper.get().getConfigDir().resolve("shakemyhand.json");

    public ConfigManager(Class<T> configType, Supplier<T> defaultSupplier) {
        this.configType = configType;
        this.defaultSupplier = defaultSupplier;
    }

    public T getOrCreateConfig() {
        T config;

        try{
            config = GSON.fromJson(new FileReader(configPath.toFile()), configType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("Unable to find or read config at \"{}\"! Is that a protected folder?", configPath);
            logger.error("Creating default config... this may mean certain settings aren't what you intended.");
            config = defaultSupplier.get();
        }

        return config;
    }

    public void writeConfig(T config) {
        String json = GSON.toJson(config, configType);
        logger.info("Saving config... sending to thread pool");

        ForkJoinPool.commonPool().submit(() -> {
            try {
                FileWriter writer = new FileWriter(configPath.toFile());

                logger.info("Writing config to \"{}\"", configPath);
                writer.write(json);
                logger.info("Successfully wrote config!");
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Unable to save config to \"{}\"! Is that a protected folder?", configPath);
                logger.error("Config not saved! Values may not be correct upon next config read.");
            }
        });
    }
}
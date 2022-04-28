package dev.s0vi.shakemyhand.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.s0vi.shakemyhand.ShakeMyHand;
import dev.s0vi.shakemyhand.config.ClientConfig;
import dev.s0vi.shakemyhand.config.ConfigManager;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * An intermediary in the FabricLoader process that handles injecting SMH mods and removing
 * non-SMH mods from the classpath.
 */
public class ShakeMyHandModInjector {

    /**
     * Local Mod Repo Structure
     *  MC Version
     *      modid
     *          mcversion-modid-modversion.jar
      */

    public static final Path LOCAL_MOD_REPO = FabricLoader.getInstance().getGameDir()
            .resolve("shakemyhand")
            .resolve("mods");
    private static final Logger LOGGER = ShakeMyHand.LOGGER;

//    private final ConfigManager<ClientConfig> configManager;

    public ShakeMyHandModInjector() {
        validateDirectories();
    }

    private void validateDirectories(){
        if(!LOCAL_MOD_REPO.toFile().exists()){
            LOGGER.info("Local Mod Repository not found... creating it");

            if(LOCAL_MOD_REPO.toFile().mkdirs()) { //try to make dirs
                LOGGER.info("Created Local Mod Repo!");
            } else {
                LOGGER.error("Cannot make Local Mod Repo! Is this a protected folder?");
                LOGGER.error("Folder path: {}", LOCAL_MOD_REPO.toAbsolutePath().toString());
                throw new IllegalStateException();
            }
        }
    }

    public Set<Path> resolveModPaths(JsonObject modList) {
        Set<Path> paths = new HashSet<>();

        String name = modList.get("name").getAsString();
        String minecraftVersion = modList.get("minecraftVersion").getAsString();
        JsonArray array = modList.getAsJsonArray("mods");

        for (int i = 0; i < array.size(); i++) {
            JsonElement jsonElement = array.get(i);
            if(jsonElement instanceof JsonObject mod) {
                String modid = mod.get("id").getAsString();
                String modVersion = mod.get("version").getAsString();

                Path modPath = LOCAL_MOD_REPO
                        .resolve(minecraftVersion)
                        .resolve(modid)
                        .resolve(minecraftVersion + modid + modVersion + ".jar");
                paths.add(modPath);
            } else {
                LOGGER.warn("Found non-JsonObject in modList {} at index {}", name, i);
            }
        }

        return paths;
    }

    public Set<Path> checkForMissingMods(Set<Path> mods) {
        Set<Path> missingPaths = new HashSet<>();

        mods.forEach(path -> {
            if (!path.toFile().exists())
                LOGGER.warn("Required mod at {} is missing!", path);
                missingPaths.add(path);
        });

        return missingPaths;
    }


}

package dev.s0vi.shakemyhand.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.s0vi.shakemyhand.ShakeMyHand;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.discovery.*;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

public class ModManager {
    private final File smhModsDir;
    private final File activeModsDir;
    private final ModDiscoverer modDiscoverer = new ModDiscoverer();
    private final FabricLoader fabricLoader = FabricLoaderImpl.InitHelper.get();
    private final Boolean isDevEnv = fabricLoader.isDevelopmentEnvironment();
    private final Logger logger = ShakeMyHand.LOGGER;
    private final Path workingDir = FabricLoaderImpl.InitHelper.get().getGameDir();
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public ModManager(File smhModsDir, File activeModsDir) {
        //check that SMH_mods exists, and is a directory
        if(!smhModsDir.exists()) {
            //.mkdirs() returns false if it wasn't able to create the folders. We kinda need that.
            if (!smhModsDir.mkdirs()) {
                throw new IllegalStateException("Unable to make the folder \"" + smhModsDir.getAbsolutePath() + "\"! Is it in a does the JVM have write access?");
            }
        } else if (!smhModsDir.isDirectory()) {
            throw new IllegalStateException("SMH_mods is a file! Maybe you have a file in your minecraft instance with the same name?");
        }

        //this literally *is* a mod, we don't need to check if the active mods dir is real

        this.smhModsDir = smhModsDir;
        this.activeModsDir = activeModsDir;
    }

//    @SuppressWarnings("ConstantConditions")
    public List<ModContainerImpl> getMods(String serverIP) throws ModResolutionException {
        File[] smhModDirFiles = smhModsDir.listFiles();

        if(smhModDirFiles == null) {
            return List.of();
        } else {
            File modsDir = Arrays.stream(smhModDirFiles)
                    .filter(file -> file.getName().equals(serverIP))
                    .filter(file -> file.getName().equals("mods"))
                    .filter(File::isDirectory)
                    .collect(Collectors.toList()).get(0);
            ModDiscoverer discoverer = new ModDiscoverer();

            discoverer.addCandidateFinder(new DirectoryModCandidateFinder(modsDir.toPath(), isDevEnv));
            Map<String, Set<ModCandidate>> envDisabledMods = new HashMap<>();

            List<ModCandidate> modCandidates = discoverer.discoverMods((FabricLoaderImpl) fabricLoader, envDisabledMods);
            logger.info("Discovered {} mod candidates", modCandidates.size());

            modCandidates = ModResolver.resolve(modCandidates, FabricLauncherBase.getLauncher().getEnvironmentType(), envDisabledMods);
            logger.info("Resolved {} mod candidates", modCandidates.size());

            return modCandidates.stream().map(ModContainerImpl::new).collect(Collectors.toList());
        }
    }

}

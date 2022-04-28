package dev.s0vi.shakemyhand;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.s0vi.shakemyhand.config.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.discovery.*;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.fabricmc.loader.impl.launch.knot.Knot;
import net.fabricmc.loader.impl.metadata.DependencyOverrides;
import net.fabricmc.loader.impl.metadata.VersionOverrides;
import net.fabricmc.loader.impl.util.SystemProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joor.Reflect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ShakeMyHand implements ModInitializer, PreLaunchEntrypoint {
    public static final String MOD_ID = "shakemyhand";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final Path GAME_DIR = FabricLoaderImpl.InitHelper.get().getGameDir();
    public static final Path SMH_DIR = GAME_DIR.resolve("shakemyhand");
    public static final Path LRM_DIR = SMH_DIR.resolve("mods");
    public static final Path MODLISTS_DIR = SMH_DIR.resolve("modlists");
    public static final Path CONFIGS_DIR = SMH_DIR.resolve("configs");


    private final FabricLoaderImpl LOADER = FabricLoaderImpl.InitHelper.get();
    private final ConfigManager clientConfigManager = new ConfigManager(GAME_DIR.resolve("shakemyhand.json"));

    private Knot knot;


    @Override
    public void onInitialize() {

    }

    @Override
    public void onPreLaunch() {
        if(LOADER.getEnvironmentType() == EnvType.CLIENT) {
            fuckWithFabricLoader();
        }
    }

    private void fuckWithFabricLoader() {
        List<ModContainerImpl> oldMods = getMods(LOADER); // get old modlist
        List<ModContainerImpl> defaultMods = getDefaultMods(LOADER); //get a list of only the default mods
        String minecraftVersion = oldMods.stream().filter(mod -> mod.getMetadata().getId().equals("minecraft")).toList().get(0).getMetadata().getVersion().getFriendlyString();
        JsonObject modList = getModList();


        //re-discover mods
        List<Path> modPaths = resolveModPaths(minecraftVersion, modList);
        Map<String, Set<ModCandidate>> envDisabledMods = new HashMap<>();
        List<ModCandidate> modCandidates = getModCandidates(modPaths, envDisabledMods);

        //Check versions, dependencies
        modCandidates = resolveMods(modCandidates, envDisabledMods, modList);

        //shuffle, lateload
        reorderMods(modCandidates);

        //re-assign modlist and map to FabricLoader
        Reflect.on(LOADER).set("mods", new ArrayList<>());
        Reflect.on(LOADER).set("modMap", new HashMap<>());

        modCandidates.forEach(mod -> {
            if(!mod.hasPath() && !mod.isBuiltin()) {
                try {
                    Path outputDir = GAME_DIR.resolve(".fabric").resolve("procesedMods");
                    mod.setPaths(Collections.singletonList(mod.copyToDir(outputDir, false)));
                } catch (IOException e) {
                    LOGGER.warn("Somehow mod with id \"{}\" didn't exist by now", mod.getId());
                }
            }

            Reflect.on(LOADER).call("addMod", mod);
        });
        //get jvm args

        String[] args = getJVMArgs();
        //re-init Knot
        Reflect.onClass(FabricLauncherBase.class).set("launcher", null);
        Reflect.onClass(FabricLauncherBase.class).set("properties", null);
        Reflect.on(LOADER).set("frozen", false);

        this.knot = new Knot(EnvType.CLIENT);
        ClassLoader cl = this.getClass().getClassLoader(); //this class was loaded by Knot, must be same cl

    }

    private String[] getJVMArgs() {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        List<String> listArgs = bean.getInputArguments();
        String[] args = new String[listArgs.size()];
        return listArgs.toArray(args);
    }

    private void reorderMods(List<ModCandidate> modCandidates) {
        if (LOADER.isDevelopmentEnvironment() && System.getProperty(SystemProperties.DEBUG_DISABLE_MOD_SHUFFLE) == null) {
            Collections.shuffle(modCandidates);
        }

        String modsToLoadLate = System.getProperty(SystemProperties.DEBUG_LOAD_LATE);
        if (modsToLoadLate != null) {
            for (String modId : modsToLoadLate.split(",")) {
                for (Iterator<ModCandidate> it = modCandidates.iterator(); it.hasNext(); ) {
                    ModCandidate mod = it.next();

                    if (mod.getId().equals(modId)) {
                        it.remove();
                        modCandidates.add(mod);
                        break;
                    }
                }
            }
        }

    }

    private List<ModCandidate> resolveMods(List<ModCandidate> modCandidates, Map<String, Set<ModCandidate>> envDisabledMods, JsonObject modList) {
        VersionOverrides versionOverrides = new VersionOverrides();
        versionOverrides.apply(modCandidates);

        DependencyOverrides depOverrides = new DependencyOverrides(CONFIGS_DIR.resolve(modList.get("name").getAsString() + ".json"));
        depOverrides.apply(modCandidates);

        if (!versionOverrides.getAffectedModIds().isEmpty()) {
            LOGGER.info("Versions overridden for {}", String.join(", ", versionOverrides.getAffectedModIds()));
        }

        if (!depOverrides.getAffectedModIds().isEmpty()) {
            LOGGER.info("Dependencies overridden for {}", String.join(", ", depOverrides.getAffectedModIds()));
        }

        try {
            return ModResolver.resolve(modCandidates, EnvType.CLIENT, envDisabledMods);
        } catch (ModResolutionException e) {
            LOGGER.warn("Failed to resolve mods! Something is terribly wrong!");
            return new ArrayList<>();
        }
    }

    private List<ModCandidate> getModCandidates(List<Path> modPaths, Map<String, Set<ModCandidate>> envDisabledMods) {
        ModDiscoverer discoverer = new ModDiscoverer();

        //for ever mod, add a directory finder for LRM/minecraftVers/id/version/minecraftvers-id-version.jar
        modPaths.forEach(path -> {
            LOGGER.info(path.subpath(0, path.getNameCount() - 1 ));
            discoverer.addCandidateFinder(new DirectoryModCandidateFinder(path.subpath(0, path.getNameCount() - 1).toAbsolutePath().resolve(String.valueOf(File.separatorChar)), false));
        });

        List<ModCandidate> candidates = new ArrayList<>();
        try {
            candidates = discoverer.discoverMods(LOADER, envDisabledMods);
        } catch (ModResolutionException e) {
            e.printStackTrace();
        }

        return candidates;
    }

    private JsonObject getModList() {
        Path modListPath = MODLISTS_DIR
                .resolve(clientConfigManager.getModListToLoad());
        JsonObject modListObject = null;

        try {
            modListObject = (JsonObject) JsonParser.parseReader(new FileReader(modListPath.toFile()));
        } catch (FileNotFoundException e) {
            LOGGER.warn("Modlist not found at expected path \"{}\". Bad things will probably happen.", modListPath.toAbsolutePath());
        }
        return modListObject;
    }

//    private getCandidateFinder() {
//
//    }

    private List<Path> resolveModPaths(String minecraftVersion, JsonObject modListObject) {
        Path modListPath = MODLISTS_DIR
                .resolve(clientConfigManager.getModListToLoad());
//        JsonObject modListObject = null;
        try {
            modListObject = (JsonObject) JsonParser.parseReader(new FileReader(modListPath.toFile()));
            JsonArray modListArray = (JsonArray) modListObject.get("mods");

            List<Path> modPaths = new ArrayList<>();
            modListArray.forEach(mod -> {
                JsonObject obj = (JsonObject)mod;

                String id = obj.get("id").getAsString();
                String version = obj.get("version").getAsString();

                Path modPath = LRM_DIR.resolve(minecraftVersion)
                        .resolve(id)
                        .resolve(version)
                        .resolve("%s-%s-%s.jar".formatted(minecraftVersion, id, version));
                modPaths.add(modPath.toAbsolutePath());
            });

            LOGGER.info(modPaths);

            return modPaths;
        } catch (FileNotFoundException e) {
            LOGGER.warn("Unable to find modlist at \"{}\". Bad things will probably happen.", modListPath.toAbsolutePath());
        }

        return new ArrayList<>();
    }

    private List<ModContainerImpl> getMods(FabricLoaderImpl fabricLoader) {
        return Reflect.on(LOADER).get("mods");
    }

    private List<ModContainerImpl> getDefaultMods(FabricLoaderImpl fabricLoader) {
        return getMods(fabricLoader).stream()
                .filter(mod -> {
                    //mod is made by "FabricMC"
                    if(mod.getMetadata().getAuthors().stream().anyMatch(person -> Objects.equals(person.getName(), "FabricMC"))) {
                        return true;
                    }
                    //mod has no author, and is named "java"
                    if(mod.getMetadata().getAuthors().isEmpty() && Objects.equals(mod.getMetadata().getId(), "java")) {
                        return true;
                    }
                    //mod is named "minecraft"
                    if(mod.getMetadata().getId().equals("minecraft")) {
                        return true;
                    }
                    //mod is this "shakemyhand"
                    if(mod.getMetadata().getId().equals("shakemyhand")) {
                        return true;
                    }

                    return false;
                }).collect(Collectors.toList());
    }
}

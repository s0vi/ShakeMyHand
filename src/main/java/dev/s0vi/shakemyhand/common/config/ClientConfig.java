package dev.s0vi.shakemyhand.common.config;

public class ClientConfig extends Config {
    public boolean alwaysHardRestart = false;
    public boolean tryToRejoin = true;
    public boolean tryToResolveUnknownMods = true;

    private String modListToLoad = "";
    public boolean needsReload = false;

    public String getModListToLoad() {
        return modListToLoad;
    }

    public void setModListToLoad(String path) {
        this.modListToLoad = path;
        this.needsReload = true;
    }

    public ClientConfig() {

    }
}

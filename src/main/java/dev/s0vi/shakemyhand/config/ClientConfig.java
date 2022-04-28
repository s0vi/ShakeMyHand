package dev.s0vi.shakemyhand.config;

public class ClientConfig {
    public boolean alwaysHardRestart = false;
    public boolean tryToRejoin = true;
    public boolean tryToResolveUnknownMods = true;

    public String modListToLoad = "";
    public boolean needsReload = false;

    public ClientConfig() {
    }
}

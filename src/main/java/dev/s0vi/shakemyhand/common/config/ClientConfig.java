package dev.s0vi.shakemyhand.common.config;

public class ClientConfig extends Config {
    public boolean alwaysHardRestart = false;
    public boolean instantlyTryToRejoin = true;
    public boolean tryToResolveUnknownMods = true;
    public boolean alwaysUserConfirmUnknownMods = false;

    public ClientConfig() {

    }
}

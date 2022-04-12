public class ConfigManager<T extends Config> {
    private final Class<T> configType;
    private final Path configPath;

    public ConfigManager(Class<T> configType) {
        this.configType = configType;
    }

    public T getOrCreateConfig() {

    }
}
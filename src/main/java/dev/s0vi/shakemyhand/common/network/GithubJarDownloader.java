package dev.s0vi.shakemyhand.common.network;

import dev.s0vi.shakemyhand.ShakeMyHand;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GithubJarDownloader implements JarDownloader{
    private final Logger logger = ShakeMyHand.LOGGER;

    @Override
    public Optional<String> getFileName(URL url) {
        String[] urlChunks = url.toExternalForm().substring(7).split("/");
        String user = urlChunks[1];
        String repo = urlChunks[2];
        String version = urlChunks[5];

        return Optional.of("%s-%s-%s.jar".formatted(user, repo, version));
    }

    @Override
    public CompletableFuture<Path> searchAndDownload(String repo, String versionTag, String githubUser) {
        return downloadJar("https://github.com/%s/%s/releases/tag/%s".formatted(githubUser, repo, versionTag));
    }
}

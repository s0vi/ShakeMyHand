package dev.s0vi.shakemyhand.common.network;

import dev.s0vi.shakemyhand.ShakeMyHand;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GithubModDownloader implements ModDownloader {
    private final Logger logger = ShakeMyHand.LOGGER;

    @Override
    public Optional<String> getName(URL url) {
        String[] urlChunks = url.toExternalForm().substring(7).split("/");
        String repo = urlChunks[2];

        return Optional.of(repo);
    }

    @Override
    public Optional<String> getFileName(URL url) {
        String[] urlChunks = url.toExternalForm().substring(7).split("/");
        String user = urlChunks[1];
        String repo = urlChunks[2];
        String version = urlChunks[5];

        return Optional.of("%s-%s-%s.jar".formatted(user, repo, version));
    }

    @Override
    public CompletableFuture<Path> downloadJar(URL url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());

                Path path = FabricLoaderImpl.InitHelper.get().getGameDir().resolve("SMH_mods");
                Optional<String> fileNameOpt = getFileName(url);

                if(fileNameOpt.isPresent()) {
                    path = path.resolve(fileNameOpt.get());

                    FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
                    FileChannel fileChannel = fileOutputStream.getChannel();

                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

                    return path;
                } else {
                    throw new IllegalArgumentException("File name not present!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                ShakeMyHand.LOGGER.error("Error downloading jar at url \"{}\"", url);
                return null;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                ShakeMyHand.LOGGER.error("Failed to parse filename for jar at url \"{}\"", url);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<List<URL>> searchApiForMatch(String name) {
        return null;
    }

    //    @Override
//    public Optional<String> getFileName(URL url) {
//        String[] urlChunks = url.toExternalForm().substring(7).split("/");
//        String user = urlChunks[1];
//        String repo = urlChunks[2];
//        String version = urlChunks[5];
//
//        return Optional.of("%s-%s-%s.jar".formatted(user, repo, version));
//    }
//
//
//    public CompletableFuture<Path> searchAndDownload(String repo, String versionTag, String githubUser) {
//
//
//        return downloadJar("https://github.com/%s/%s/releases/tag/%s".formatted(githubUser, repo, versionTag));
//    }
}

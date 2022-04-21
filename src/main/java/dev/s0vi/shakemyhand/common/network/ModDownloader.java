package dev.s0vi.shakemyhand.common.network;

import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ModDownloader {
    Optional<String> getName(URL url);
    Optional<String> getFileName(URL url);
    CompletableFuture<Path> downloadJar(URL url);
    CompletableFuture<List<URL>> searchApiForMatch(String name);

//    default CompletableFuture<Path> downloadJar(URL url) {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
//                byte[] databuffer = new byte[1024];
//                int bytesRead;
//
//                Path path = FabricLoaderImpl.InitHelper.get().getGameDir().resolve("SMH_mods");
//                Optional<String> fileNameOpt = getFileName(url);
//
//                if(fileNameOpt.isPresent()) {
//                    path = path.resolve(fileNameOpt.get());
//
//                    FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
//                    FileChannel fileChannel = fileOutputStream.getChannel();
//
//                    fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
//
//                    return path;
//                } else {
//                    throw new IllegalArgumentException("File name not present!");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                ShakeMyHand.LOGGER.error("Error downloading jar at url \"{}\"", url);
//                return null;
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//                ShakeMyHand.LOGGER.error("Failed to parse filename for jar at url \"{}\"", url);
//                return null;
//            }
//        });
//    }
//
//
//
//    default CompletableFuture<Path> downloadJar(String urlString) {
//        try {
//            URL url = new URL(urlString);
//            return downloadJar(url);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            return CompletableFuture.failedFuture(e);
//        }
//    }
//
//    Optional<String> getFileName(URL url);
//
//    /***
//     * This is the **LEAST** reliable way to get a mod. In the future, this feature may be removed entirely.
//     *
//     * @param name The name of the mod. Usually, this is the name of the jar file.
//     * @param version The version of the mod
//     * @param authorName The name of the author.
//     * @return
//     */
//    CompletableFuture<Path> searchAndDownload(String name, String version, String authorName);
}

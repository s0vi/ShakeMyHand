package dev.s0vi.shakemyhand.common.network;

import com.google.gson.*;
import dev.s0vi.shakemyhand.ShakeMyHand;
import dev.s0vi.shakemyhand.common.network.request.GETRequestHandler;
import dev.s0vi.shakemyhand.common.network.request.GETResponse;
import org.apache.logging.log4j.Logger;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModrinthModDownloader implements ModDownloader {
    private final Logger logger = ShakeMyHand.LOGGER;
    private final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final Charset UTF_8 = Charset.forName(StandardCharsets.UTF_8.name());
    private static final GETRequestHandler REQUEST_HANDLER = GETRequestHandler.getInstance();
    private static final String API_URL = "https://api.modrinth.com/v2";

//    Optional<JsonObject> getResponse(URL url) {
//        try {
//            InputStream response = url.openStream();
//
//            JsonObject json = (JsonObject) JsonParser.parseReader(
//                    new InputStreamReader(response, UTF_8)
//            );
//
//            return Optional.of(json);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            logger.error("Couldn't download a jar from Modrinth! Is it a real project?");
//        } catch (IOException e) {
//            e.printStackTrace();
//            logger.error("Couldn't make a connection!");
//        }
//
//        return Optional.empty();
//    }

    Optional<String> getProjectID(String name) {
        GETResponse response = REQUEST_HANDLER.sendGETRequest(API_URL, "/project/%s".formatted(name));
        if(response.json().isPresent()) {
            return Optional.of(
                    ((JsonObject) response.json().get())
                            .get("id").getAsString()
            );
        } else {
            return Optional.empty();
        }
    }

    Optional<JsonObject> getVersionsResponse(String id) {
        try {
            return getResponse(new URL("https://api.modrinth.com/v2/project/%s/version".formatted(id)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getName(URL url) {

    }

    @Override
    public Optional<String> getFileName(URL url) {
        return null;
    }

    @Override
    public CompletableFuture<Path> downloadJar(URL url) {
        return null;
    }

    @Override
    public CompletableFuture<List<URL>> searchApiForMatch(String name) {
        return null;
    }
}

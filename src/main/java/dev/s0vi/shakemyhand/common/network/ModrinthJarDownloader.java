package dev.s0vi.shakemyhand.common.network;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import dev.s0vi.shakemyhand.ShakeMyHand;
import org.apache.logging.log4j.Logger;

import javax.swing.text.html.Option;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ModrinthJarDownloader implements JarDownloader {
    private final Logger logger = ShakeMyHand.LOGGER;
    private final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final Charset UTF_8 = Charset.forName(StandardCharsets.UTF_8.name());

//    @Override
//    public CompletableFuture<Path> downloadJar(URL url) {
//        return null;
//    }

    Optional<JsonObject> getResponse(URL url) {
        try {
            InputStream response = url.openStream();

            JsonObject json = (JsonObject) JsonParser.parseReader(
                    new InputStreamReader(response, UTF_8)
            );

            return Optional.of(json);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            logger.error("Couldn't download a jar from Modrinth! Is it a real project?");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Couldn't make a connection!");
        }

        return Optional.empty();
    }

    Optional<String> getProjectID(String name) {
        Optional<JsonObject> responseOpt = getAPIResponse(name);
        if(responseOpt.isPresent()){
            JsonObject response = responseOpt.get();
            String id = response.get("id").getAsString();

            if (id == null || id.equals("")) {
                return Optional.empty();
            } else {
                return Optional.of(id);
            }
        }

        return Optional.empty();
    }

    Optional<JsonObject> getAPIResponse(String projectName) {
        try {
            return getResponse(new URL("https://api.modrinth.com/v2/project/%s".formatted(projectName)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
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
    public Optional<String> getFileName(URL url) {
        return null;
    }

    @Override
    public CompletableFuture<Path> searchAndDownload(String name, String version, String authorName) {
        CompletableFuture.supplyAsync(() -> {
            Optional<JsonObject> responseOpt = getAPIResponse(name);
            if(responseOpt.isPresent()) {

            }
        })
    }
}

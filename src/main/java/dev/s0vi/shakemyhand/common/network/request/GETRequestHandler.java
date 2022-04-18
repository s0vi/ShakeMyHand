package dev.s0vi.shakemyhand.common.network.request;

import com.google.gson.*;
import dev.s0vi.shakemyhand.ShakeMyHand;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GETRequestHandler {
    //singleton
    private static GETRequestHandler INSTANCE;
    public static GETRequestHandler getInstance() {
        if(INSTANCE == null) {
            //noinspection InstantiationOfUtilityClass
            INSTANCE = new GETRequestHandler();
        }

        return INSTANCE;
    }

//    private Gson GSON = new Gson();
    private final Logger LOGGER = ShakeMyHand.LOGGER;

    public CompletableFuture<GETResponse> sendGETRequest(URL url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                InputStream stream = url.openStream();
                JsonElement json = JsonParser.parseReader(new InputStreamReader(stream));

                return new GETResponse(Optional.ofNullable(json), this, url);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("A network error occurred!");
            } catch (JsonParseException e) {
                e.printStackTrace();
                LOGGER.error("Cannot parse json from \"{}\"!", url.toExternalForm());
            }

            return new GETResponse(Optional.empty(), this, url);
        });
    }

}

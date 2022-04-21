package dev.s0vi.shakemyhand.common.network.request;

import com.google.gson.*;
import dev.s0vi.shakemyhand.ShakeMyHand;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GETRequestHandler {
    //singleton
    private static GETRequestHandler INSTANCE;
    public static GETRequestHandler getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new GETRequestHandler();
        }

        return INSTANCE;
    }

    private final Logger LOGGER = ShakeMyHand.LOGGER;

    public GETResponse sendGETRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            InputStream stream = url.openStream();
            JsonElement json = JsonParser.parseReader(new InputStreamReader(stream));

            return new GETResponse(Optional.ofNullable(json), this, url);
        } catch (MalformedURLException e){
            e.printStackTrace();
            LOGGER.error("The url \"{}\" is not valid!", urlString);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("A network error occurred!");
        } catch (JsonParseException e) {
            e.printStackTrace();
            LOGGER.error("Cannot parse json from \"{}\"!", urlString);
        }

        return new GETResponse(Optional.empty(), this, null);
    }

    public GETResponse sendGETRequest(String baseURL, String domain) {
        return sendGETRequest(baseURL + domain);
    }

}

package dev.s0vi.shakemyhand.common.network.request;

import com.google.gson.JsonElement;

import java.net.URL;
import java.util.Optional;

public record GETResponse(Optional<JsonElement> json,
                          GETRequestHandler handler,
                          URL url) {

}
package org.example.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Page implements HttpHandler {
    private final String IndexPath;

    public Page(String indexPath) {
        IndexPath = indexPath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = Files.readString(Path.of(IndexPath));

        byte[] utf8Bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(200, utf8Bytes.length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.getResponseBody().close();
    }
}

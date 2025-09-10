package org.example.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class Post implements HttpHandler {

    Function<JSONObject, JSONObject> postHandler;

    public Post(Function<JSONObject, JSONObject> postHandler) {
        this.postHandler = postHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if (method.equalsIgnoreCase("POST")) {
            handlePostRequest(exchange);
        }
        else {
            exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
            exchange.getResponseBody().close();
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        InputStream requestBodyStream = exchange.getRequestBody();
        String requestBody = new String(requestBodyStream.readAllBytes(), StandardCharsets.UTF_8);

        // Process the request data (for example, parse JSON)
        // Here we assume the request body is a JSON object
        JSONObject requestJson = new JSONObject(requestBody);

        // Process the request and generate a response
        JSONObject responseJson = postHandler.apply(requestJson);

        // Convert response JSON object to byte array
        byte[] responseBytes = responseJson.toString().getBytes(StandardCharsets.UTF_8);

        // Set response headers
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseBytes.length);

        // Send response body
        OutputStream responseBodyStream = exchange.getResponseBody();
        responseBodyStream.write(responseBytes);
        responseBodyStream.flush();
        responseBodyStream.close();
    }
}
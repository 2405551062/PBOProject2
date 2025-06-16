package Tugas2;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Response {

    private final HttpExchange httpExchange;
    private final StringBuilder stringBuilder;
    private boolean isSent;

    public Response(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
        this.stringBuilder = new StringBuilder();
        this.isSent = false;
    }

    public void setBody(String body) {
        stringBuilder.setLength(0);
        stringBuilder.append(body);
    }

    public void send(int status) {
        try {
            String body = stringBuilder.toString();
            byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            httpExchange.sendResponseHeaders(status, bodyBytes.length);

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(bodyBytes);
                os.flush();
            }

            this.isSent = true;
        } catch (IOException e) {
            System.err.println("Error sending response: " + e.getMessage());
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    public boolean isSent() {
        return this.isSent;
    }
}

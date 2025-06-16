package Tugas2;

import Tugas2.Controller.Controller;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

public class Server {
    private final HttpServer server;

    public Server(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 128);
        registerRoutes();
        server.start();
        System.out.println("Server started on port: " + port);
    }

    private void registerRoutes() {
        server.createContext("/villas", (exchange) -> {
            String path = exchange.getRequestURI().getPath();
            System.out.println("path: " + path);

            if ("/favicon.ico".equals(path)) {
                sendNotFound(exchange);
                return;
            }

            Controller villaController = new Controller();
            switch (exchange.getRequestMethod()) {
                case "GET":
                    villaController.GetAllVillas(exchange);
                    break;
                case "POST":
                    villaController.handleCreate(exchange);
                    break;
                case "PUT":
                    villaController.handleUpdate(exchange);
                    break;
                case "DELETE":
                    villaController.handleDelete(exchange);
                    break;
                default:
                    Response res = new Response(exchange);
                    res.setBody("{\"error\":\"Method Not Allowed\"}");
                    res.send(405);
            }
        });

        // You can add more endpoints here (e.g., server.createContext("/other", ...))
    }

    private void sendNotFound(HttpExchange exchange) {
        Response res = new Response(exchange);
        res.setBody("{\"error\": \"Not Found\"}");
        res.send(HttpURLConnection.HTTP_NOT_FOUND);
    }
}

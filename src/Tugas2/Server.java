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
        Controller controller = new Controller();

        server.createContext("/villas", (exchange) -> {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String[] parts = path.split("/");

            try {
                if (path.matches("^/villas/\\d+/reviews$")) {
                    int villaId = Integer.parseInt(parts[2]);
                    if ("GET".equals(method)) {
                        controller.getReviewsByVillaId(exchange, villaId);
                    } else {
                        sendNotFound(exchange);
                    }
                    return;
                }

                if (path.matches("^/villas/\\d+/bookings$")) {
                    int villaId = Integer.parseInt(parts[2]);
                    if ("GET".equals(method)) {
                        controller.getBookingsByVillaId(exchange, villaId);
                    } else {
                        sendNotFound(exchange);
                    }
                    return;
                }

                if (parts.length >= 4 && parts[1].equals("villas") && parts[3].equals("rooms")) {
                    int villaId = Integer.parseInt(parts[2]);

                    if (parts.length == 4) {
                        if ("GET".equals(method)) {
                            controller.getRoomsByVillaId(exchange, villaId);
                        } else if ("POST".equals(method)) {
                            controller.createRoom(exchange, villaId);
                        } else {
                            sendNotFound(exchange);
                        }
                        return;
                    }

                    if (parts.length == 5) {
                        int roomId = Integer.parseInt(parts[4]);

                        if ("PUT".equals(method)) {
                            controller.updateRoom(exchange, villaId, roomId);
                        } else if ("DELETE".equals(method)) {
                            controller.deleteRoom(exchange, villaId, roomId);
                        } else {
                            sendNotFound(exchange);
                        }
                        return;
                    }

                    sendNotFound(exchange);
                    return;
                }

                switch (method) {
                    case "GET":
                        controller.GetAllVillas(exchange);
                        break;
                    case "POST":
                        controller.handleCreate(exchange);
                        break;
                    case "PUT":
                        controller.handleUpdate(exchange);
                        break;
                    case "DELETE":
                        controller.handleDelete(exchange);
                        break;
                    default:
                        Response res = new Response(exchange);
                        res.setBody("{\"error\":\"Method Not Allowed\"}");
                        res.send(405);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendNotFound(exchange);
            }
        });

        // /customers/{id}/bookings/{id}/reviews
        server.createContext("/customers", (exchange) -> {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String[] parts = path.split("/");

            try {
                if (path.matches("^/customers/\\d+/bookings/\\d+/reviews$") && "POST".equals(method)) {
                    int bookingId = Integer.parseInt(parts[4]);
                    controller.createReview(exchange, bookingId);
                } else {
                    sendNotFound(exchange);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendNotFound(exchange);
            }
        });
    }


    private void sendNotFound(HttpExchange exchange) {
        Response res = new Response(exchange);
        res.setBody("{\"error\": \"Not Found\"}");
        res.send(HttpURLConnection.HTTP_NOT_FOUND);
    }
}

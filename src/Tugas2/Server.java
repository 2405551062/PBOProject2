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
                // POST /customers/{id}/bookings/{id}/reviews
                if (parts.length == 5 && "POST".equals(method) && parts[3].equals("bookings") && parts[4].equals("reviews")) {
                    int bookingId = Integer.parseInt(parts[2]);
                    controller.createReview(exchange, bookingId);
                    return;
                }

                // GET /customers/{id}/bookings
                if (parts.length == 4 && "bookings".equals(parts[3]) && "GET".equals(method)) {
                    int customerId = Integer.parseInt(parts[2]);
                    controller.getBookingsByCustomerId(exchange, customerId);
                    return;
                }

                // POST /customers/{id}/bookings
                if (parts.length == 4 && "bookings".equals(parts[3]) && "POST".equals(method)) {
                    controller.createBooking(exchange); // could also pass customerId if needed
                    return;
                }

                // GET /customers
                if (parts.length == 2 && "GET".equals(method)) {
                    controller.getAllCustomers(exchange);
                    return;
                }

                // GET /customers/{id}
                if (parts.length == 3 && "GET".equals(method)) {
                    int id = Integer.parseInt(parts[2]);
                    controller.getCustomerById(exchange, id);
                    return;
                }

                // POST /customers
                if (parts.length == 2 && "POST".equals(method)) {
                    controller.createCustomer(exchange);
                    return;
                }

                // PUT /customers/{id}
                if (parts.length == 3 && "PUT".equals(method)) {
                    int id = Integer.parseInt(parts[2]);
                    controller.updateCustomer(exchange, id);
                    return;
                }

                // GET /customers/{id}/reviews
                if (parts.length == 4 && "reviews".equals(parts[3]) && "GET".equals(method)) {
                    int customerId = Integer.parseInt(parts[2]);
                    controller.getReviewsByCustomerId(exchange, customerId);
                    return;
                }

                sendNotFound(exchange);
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

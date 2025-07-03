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
            String query = exchange.getRequestURI().getQuery();

            try {
                // /villas/{id}/reviews
                if (parts.length == 4 && "villas".equals(parts[1]) && "reviews".equals(parts[3])) {
                    int villaId = Integer.parseInt(parts[2]);
                    if ("GET".equals(method)) {
                        controller.getReviewsByVillaId(exchange, villaId);
                    } else {
                        sendNotFound(exchange);
                    }
                    return;
                }

                // /villas/{id}/bookings
                if (parts.length == 4 && "villas".equals(parts[1]) && "bookings".equals(parts[3])) {
                    int villaId = Integer.parseInt(parts[2]);
                    if ("GET".equals(method)) {
                        controller.getBookingsByVillaId(exchange, villaId);
                    } else {
                        sendNotFound(exchange);
                    }
                    return;
                }

                // /villas/{id}/rooms and /villas/{id}/rooms/{roomId}
                if (parts.length >= 4 && "villas".equals(parts[1]) && "rooms".equals(parts[3])) {
                    int villaId = Integer.parseInt(parts[2]);

                    if (parts.length == 4) {
                        switch (method) {
                            case "GET":
                                controller.getRoomsByVillaId(exchange, villaId);
                                break;
                            case "POST":
                                controller.createRoom(exchange, villaId);
                                break;
                            default:
                                sendNotFound(exchange);
                        }
                        return;
                    }

                    if (parts.length == 5) {
                        int roomId = Integer.parseInt(parts[4]);
                        switch (method) {
                            case "PUT":
                                controller.updateRoom(exchange, villaId, roomId);
                                break;
                            case "DELETE":
                                controller.deleteRoom(exchange, villaId, roomId);
                                break;
                            default:
                                sendNotFound(exchange);
                        }
                        return;
                    }

                    sendNotFound(exchange);
                    return;
                }

                // /villas/{id}
                if (parts.length == 3 && "villas".equals(parts[1])) {
                    int villaId;
                    try {
                        villaId = Integer.parseInt(parts[2]);
                    } catch (NumberFormatException e) {
                        Response res = new Response(exchange);
                        res.setBody("{\"error\":\"Invalid ID format\"}");
                        res.send(400);
                        return;
                    }

                    switch (method) {
                        case "GET":
                            controller.getVillaById(exchange);
                            break;
                        case "DELETE":
                            controller.handleDelete(exchange, villaId);
                            break;
                        default:
                            sendNotFound(exchange);
                    }
                    return;
                }

                // /villas?ci_date=...&co_date=...
                if ("GET".equals(method) && query != null && query.contains("ci_date") && query.contains("co_date")) {
                    controller.getAvailableVillasByDate(exchange);
                    return;
                }

                // /villas (base endpoint)
                if (parts.length == 2 && "villas".equals(parts[1])) {
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
                        default:
                            Response res = new Response(exchange);
                            res.setBody("{\"error\":\"Method Not Allowed\"}");
                            res.send(405);
                    }
                    return;
                }

                sendNotFound(exchange); // fallback if none matched
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
                // POST /customers/{customerId}/bookings/{bookingId}/reviews
                if (parts.length == 6 &&
                        "POST".equals(method) &&
                        "customers".equals(parts[1]) &&
                        "bookings".equals(parts[3]) &&
                        "reviews".equals(parts[5])) {

                    int bookingId = Integer.parseInt(parts[4]);
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

        server.createContext("/vouchers", (exchange) -> {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            String[] parts = path.split("/");


            try {
                // GET /vouchers (daftar semua voucher)
                if (parts.length == 2 && "GET".equals(method)) {
                    controller.getAllVouchers(exchange);
                    return;
                }

                // GET /vouchers/{id}
                if (parts.length == 3 && "GET".equals(method)) {
                    int id = Integer.parseInt(parts[2]);
                    controller.getVoucherById(exchange, id);
                    return;
                }

                // POST /vouchers
                if (parts.length == 2 && "POST".equals(method)) {
                    controller.createVoucher(exchange);
                    return;
                }

                // PUT /vouchers/{id}
                if (parts.length == 3 && "PUT".equals(method)) {
                    int id = Integer.parseInt(parts[2]);
                    controller.updateVoucher(exchange, id);
                    return;
                }

                // DELETE /vouchers/{id}
                if (parts.length == 3 && "DELETE".equals(method)) {
                    int id = Integer.parseInt(parts[2]);
                    controller.deleteVoucher(exchange, id);
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

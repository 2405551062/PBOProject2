package Tugas2.Controller;

import Tugas2.DAO.*;
import Tugas2.Model.*;
import Tugas2.Request;
import Tugas2.Response;
import Tugas2.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Controller {
    private VillaDAO villaDAO;
    private ReviewsDAO reviewsDAO;
    private BookingsDAO bookingsDAO;
    private RoomsDAO roomsDAO;

    public Controller() {
        try {
            this.villaDAO = new VillaDAO(DB.getConnection());
            this.reviewsDAO = new ReviewsDAO(DB.getConnection());
            this.bookingsDAO = new BookingsDAO(DB.getConnection());
            this.roomsDAO = new RoomsDAO(DB.getConnection());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void GetAllVillas(HttpExchange exchange) {
        Response res = new Response(exchange);
        try {
            List<Villas> villas = villaDAO.getAllVillas();

            if (villas == null) {
                villas = List.of();
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(villas);
            res.setBody(json);
            res.send(200);
        } catch (Exception e) {
            e.printStackTrace(); // Debug log
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(500);
        }
    }

    public void handleCreate(HttpExchange exchange) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            Map<String, Object> reqBody = req.getJSON();
            if (reqBody == null) {
                res.setBody("{\"error\": \"Invalid or missing JSON\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            String name = (String) reqBody.get("name");
            String description = (String) reqBody.get("description");
            String address = (String) reqBody.get("address");

            if (name == null || description == null || address == null) {
                res.setBody("{\"error\": \"Missing required fields: name, description, address\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Villas villa = new Villas(name, description, address);
            villaDAO.insertVilla(villa);

            res.setBody("{\"message\": \"Villa created successfully\"}");
            res.send(HttpURLConnection.HTTP_CREATED);

        } catch (Exception e) {
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void handleUpdate(HttpExchange exchange) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            Map<String, Object> reqBody = req.getJSON();
            if (reqBody == null) {
                res.setBody("{\"error\": \"Invalid or missing JSON\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Integer id = (Integer) reqBody.get("id");
            String name = (String) reqBody.get("name");
            String description = (String) reqBody.get("description");
            String address = (String) reqBody.get("address");

            if (id == null || name == null || description == null || address == null) {
                res.setBody("{\"error\": \"Missing required fields: id, name, description, address\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Villas villa = villaDAO.getVillaById(id);
            if (villa == null) {
                res.setBody("{\"error\": \"Villa not found\"}");
                res.send(HttpURLConnection.HTTP_NOT_FOUND);
                return;
            }

            villa.setName(name);
            villa.setDescription(description);
            villa.setAddress(address);
            villaDAO.updateVilla(villa);

            res.setBody("{\"message\": \"Villa updated successfully\"}");
            res.send(HttpURLConnection.HTTP_OK);

        } catch (Exception e) {
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void handleDelete(HttpExchange exchange) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            Map<String, Object> reqBody = req.getJSON();
            if (reqBody == null) {
                res.setBody("{\"error\": \"Invalid or missing JSON\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Integer id = (Integer) reqBody.get("id");
            if (id == null) {
                res.setBody("{\"error\": \"Missing required field: id\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Villas villa = villaDAO.getVillaById(id);
            if (villa == null) {
                res.setBody("{\"error\": \"Villa not found\"}");
                res.send(HttpURLConnection.HTTP_NOT_FOUND);
                return;
            }

            villaDAO.deleteVilla(id);
            res.setBody("{\"message\": \"Villa deleted successfully\"}");
            res.send(HttpURLConnection.HTTP_OK);

        } catch (Exception e) {
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getReviewsByVillaId(HttpExchange exchange, int villaId) {
        Response res = new Response(exchange);
        try {
            List<Reviews> reviews = reviewsDAO.getReviewsByVillaId(villaId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(reviews);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void createReview(HttpExchange exchange, int bookingId) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            Map<String, Object> reqBody = req.getJSON();
            if (reqBody == null) {
                res.setBody("{\"error\": \"Invalid or missing JSON\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Integer star = (Integer) reqBody.get("star");
            String title = (String) reqBody.get("title");
            String content = (String) reqBody.get("content");

            if (star == null || title == null || content == null) {
                res.setBody("{\"error\": \"Missing required fields: star, title, content\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Reviews review = new Reviews();
            review.setBooking(bookingId);
            review.setStar(star);
            review.setTitle(title);
            review.setContent(content);

            boolean success = reviewsDAO.insertReview(review);
            if (success) {
                res.setBody("{\"message\": \"Review submitted successfully\"}");
                res.send(HttpURLConnection.HTTP_CREATED);
            } else {
                res.setBody("{\"error\": \"Failed to submit review\"}");
                res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getBookingsByVillaId(HttpExchange exchange, int villaId) {
        Response res = new Response(exchange);
        try {
            List<Bookings> bookings = bookingsDAO.getBookingsByVillaId(villaId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(bookings);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }
    public void getBookingsByCustomerId(HttpExchange exchange, int customerId) {
        Response res = new Response(exchange);
        try {
            List<Bookings> bookings = bookingsDAO.getBookingsByCustomerId(customerId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(bookings);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getRoomsByVillaId(HttpExchange exchange, int villaId) {
        Response res = new Response(exchange);
        try {
            List<Rooms> rooms = roomsDAO.getRoomsByVillaId(villaId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(rooms);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void createRoom(HttpExchange exchange, int villaId) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            Map<String, Object> body = req.getJSON();
            if (body == null) {
                res.setBody("{\"error\": \"Invalid JSON\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            String name = (String) body.get("name");
            String bedSize = (String) body.get("bed_size");
            Integer price = (Integer) body.get("price");
            Integer quantity = (Integer) body.getOrDefault("quantity", 1);
            Integer capacity = (Integer) body.getOrDefault("capacity", 1);

            if (name == null || bedSize == null || price == null) {
                res.setBody("{\"error\": \"Missing required fields: name, bed_size, price\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Rooms room = new Rooms();
            room.setVilla(villaId);
            room.setName(name);
            room.setBedSize(bedSize);
            room.setPrice(price);
            room.setQuantity(quantity);
            room.setCapacity(capacity);
            room.setHasDesk((Integer) body.getOrDefault("has_desk", 0));
            room.setHasAc((Integer) body.getOrDefault("has_ac", 0));
            room.setHasTv((Integer) body.getOrDefault("has_tv", 0));
            room.setHasWifi((Integer) body.getOrDefault("has_wifi", 0));
            room.setHasShower((Integer) body.getOrDefault("has_shower", 0));
            room.setHasHotwater((Integer) body.getOrDefault("has_hotwater", 0));
            room.setHasFridge((Integer) body.getOrDefault("has_fridge", 0));

            boolean success = roomsDAO.insertRoom(room);
            if (success) {
                res.setBody("{\"message\": \"Room created successfully\"}");
                res.send(HttpURLConnection.HTTP_CREATED);
            } else {
                res.setBody("{\"error\": \"Failed to insert room\"}");
                res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void updateRoom(HttpExchange exchange, int villaId, int roomId) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            Map<String, Object> body = req.getJSON();
            if (body == null) {
                res.setBody("{\"error\": \"Invalid JSON\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Rooms room = new Rooms();
            room.setId(roomId);
            room.setVilla(villaId);
            room.setName((String) body.get("name"));
            room.setQuantity((Integer) body.getOrDefault("quantity", 1));
            room.setCapacity((Integer) body.getOrDefault("capacity", 1));
            room.setPrice((Integer) body.get("price"));
            room.setBedSize((String) body.get("bed_size"));
            room.setHasDesk((Integer) body.getOrDefault("has_desk", 0));
            room.setHasAc((Integer) body.getOrDefault("has_ac", 0));
            room.setHasTv((Integer) body.getOrDefault("has_tv", 0));
            room.setHasWifi((Integer) body.getOrDefault("has_wifi", 0));
            room.setHasShower((Integer) body.getOrDefault("has_shower", 0));
            room.setHasHotwater((Integer) body.getOrDefault("has_hotwater", 0));
            room.setHasFridge((Integer) body.getOrDefault("has_fridge", 0));

            boolean updated = roomsDAO.updateRoom(room);
            if (updated) {
                res.setBody("{\"message\": \"Room updated successfully\"}");
                res.send(HttpURLConnection.HTTP_OK);
            } else {
                res.setBody("{\"error\": \"Failed to update room\"}");
                res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void deleteRoom(HttpExchange exchange, int villaId, int roomId) {
        Response res = new Response(exchange);

        try {
            boolean deleted = roomsDAO.deleteRoom(roomId);
            if (deleted) {
                res.setBody("{\"message\": \"Room deleted successfully\"}");
                res.send(HttpURLConnection.HTTP_OK);
            } else {
                res.setBody("{\"error\": \"Failed to delete room\"}");
                res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

}


package Tugas2.Controller;

import Tugas2.DAO.*;
import Tugas2.Model.*;
import Tugas2.Request;
import Tugas2.Response;
import Tugas2.util.*;
import Tugas2.Exception.UnauthorizedException;
import static Tugas2.util.AuthUtil.isAuthorized;

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
    private CustomersDAO customersDAO;
    private VouchersDAO vouchersDAO;

    public Controller() {
        try {
            this.villaDAO = new VillaDAO(DB.getConnection());
            this.reviewsDAO = new ReviewsDAO(DB.getConnection());
            this.bookingsDAO = new BookingsDAO(DB.getConnection());
            this.roomsDAO = new RoomsDAO(DB.getConnection());
            this.customersDAO = new CustomersDAO(DB.getConnection());
            this.vouchersDAO = new VouchersDAO(DB.getConnection());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void GetAllVillas(HttpExchange exchange) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            List<Villas> villas = villaDAO.getAllVillas();

            if (villas == null) {
                villas = List.of();
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(villas);
            res.setBody(json);
            res.send(200);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
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
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

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

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void handleUpdate(HttpExchange exchange) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

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

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void handleDelete(HttpExchange exchange) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

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

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getAvailableVillasByDate(HttpExchange exchange) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            Map<String, String> queryParams = QueryParser.parseQueryParams(exchange.getRequestURI().getQuery());

            String ciDate = queryParams.get("ci_date");
            String coDate = queryParams.get("co_date");

            if (ciDate == null || coDate == null) {
                res.setBody("{\"error\": \"Missing ci_date or co_date\"}");
                res.send(400);
                return;
            }

            List<Villas> availableVillas = villaDAO.getAvailableVillas(ciDate, coDate);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(availableVillas);
            res.setBody(json);
            res.send(200);
        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(500);
        }
    }

    public void getReviewsByVillaId(HttpExchange exchange, int villaId) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            List<Reviews> reviews = reviewsDAO.getReviewsByVillaId(villaId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(reviews);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getReviewsByCustomerId(HttpExchange exchange, int customerId) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            List<Reviews> reviews = reviewsDAO.getReviewsByCustomerId(customerId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(reviews);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
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
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

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

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getBookingsByVillaId(HttpExchange exchange, int villaId) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            List<Bookings> bookings = bookingsDAO.getBookingsByVillaId(villaId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(bookings);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getBookingsByCustomerId(HttpExchange exchange, int customerId) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            List<Bookings> bookings = bookingsDAO.getBookingsByCustomerId(customerId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(bookings);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void createBooking(HttpExchange exchange) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            Map<String, Object> body = req.getJSON();
            if (body == null) {
                res.setBody("{\"error\": \"Invalid JSON\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Integer customer = (Integer) body.get("customer");
            Integer roomType = (Integer) body.get("room_type");
            String checkinDate = (String) body.get("checkin_date");
            String checkoutDate = (String) body.get("checkout_date");
            Integer price = (Integer) body.get("price");
            Integer voucher = body.get("voucher") != null ? (Integer) body.get("voucher") : null;
            Integer finalPrice = (Integer) body.get("final_price");
            String paymentStatus = (String) body.get("payment_status");
            Integer hasCheckedIn = (Integer) body.getOrDefault("has_checkedin", 0);
            Integer hasCheckedOut = (Integer) body.getOrDefault("has_checkedout", 0);

            if (customer == null || roomType == null || checkinDate == null || checkoutDate == null
                    || price == null || finalPrice == null || paymentStatus == null) {
                res.setBody("{\"error\": \"Missing required fields\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Bookings booking = new Bookings();
            booking.setCustomer(customer);
            booking.setRoomType(roomType);
            booking.setCheckinDate(checkinDate);
            booking.setCheckoutDate(checkoutDate);
            booking.setPrice(price);
            booking.setVoucher(voucher);
            booking.setFinalPrice(finalPrice);
            booking.setPaymentStatus(paymentStatus);
            booking.setHasCheckedIn(hasCheckedIn);
            booking.setHasCheckedOut(hasCheckedOut);

            boolean success = bookingsDAO.insertBooking(booking);
            if (success) {
                res.setBody("{\"message\": \"Booking created successfully\"}");
                res.send(HttpURLConnection.HTTP_CREATED);
            } else {
                res.setBody("{\"error\": \"Failed to create booking\"}");
                res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getRoomsByVillaId(HttpExchange exchange, int villaId) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            List<Rooms> rooms = roomsDAO.getRoomsByVillaId(villaId);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(rooms);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
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
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

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

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
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
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

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

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void deleteRoom(HttpExchange exchange, int villaId, int roomId) {
        Response res = new Response(exchange);

        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            boolean deleted = roomsDAO.deleteRoom(roomId);
            if (deleted) {
                res.setBody("{\"message\": \"Room deleted successfully\"}");
                res.send(HttpURLConnection.HTTP_OK);
            } else {
                res.setBody("{\"error\": \"Failed to delete room\"}");
                res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getAllCustomers(HttpExchange exchange) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            List<Customers> customers = customersDAO.getAllCustomers();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(customers);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getCustomerById(HttpExchange exchange, int id) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            Customers customer = customersDAO.getCustomerById(id);
            if (customer == null) {
                res.setBody("{\"error\": \"Customer not found\"}");
                res.send(HttpURLConnection.HTTP_NOT_FOUND);
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(customer);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void createCustomer(HttpExchange exchange) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            Map<String, Object> body = req.getJSON();
            if (body == null) {
                res.setBody("{\"error\": \"Invalid JSON\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            String name = (String) body.get("name");
            String email = (String) body.get("email");
            String phone = (String) body.get("phone");

            if (name == null || email == null || phone == null) {
                res.setBody("{\"error\": \"Missing required fields: name, email, phone\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Customers customer = new Customers();
            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone);

            boolean success = customersDAO.insertCustomer(customer);
            if (success) {
                res.setBody("{\"message\": \"Customer created successfully\"}");
                res.send(HttpURLConnection.HTTP_CREATED);
            } else {
                res.setBody("{\"error\": \"Failed to create customer\"}");
                res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void updateCustomer(HttpExchange exchange, int id) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            Map<String, Object> body = req.getJSON();
            if (body == null) {
                res.setBody("{\"error\": \"Invalid JSON\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            String name = (String) body.get("name");
            String email = (String) body.get("email");
            String phone = (String) body.get("phone");

            if (name == null || email == null || phone == null) {
                res.setBody("{\"error\": \"Missing required fields: name, email, phone\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Customers customer = customersDAO.getCustomerById(id);
            if (customer == null) {
                res.setBody("{\"error\": \"Customer not found\"}");
                res.send(HttpURLConnection.HTTP_NOT_FOUND);
                return;
            }

            customer.setName(name);
            customer.setEmail(email);
            customer.setPhone(phone);

            boolean updated = customersDAO.updateCustomer(customer);
            if (updated) {
                res.setBody("{\"message\": \"Customer updated successfully\"}");
                res.send(HttpURLConnection.HTTP_OK);
            } else {
                res.setBody("{\"error\": \"Failed to update customer\"}");
                res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getAllVouchers(HttpExchange exchange) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            List<Vouchers> vouchers = vouchersDAO.getAllVouchers();
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(vouchers);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void getVoucherById(HttpExchange exchange, int id) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            Vouchers voucher = vouchersDAO.getVoucherById(id);
            if (voucher == null) {
                res.setBody("{\"error\": \"Voucher not found\"}");
                res.send(HttpURLConnection.HTTP_NOT_FOUND);
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(voucher);
            res.setBody(json);
            res.send(HttpURLConnection.HTTP_OK);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void createVoucher(HttpExchange exchange) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            Map<String, Object> reqBody = req.getJSON();
            if (reqBody == null) {
                res.setBody("{\"error\": \"Invalid or missing JSON\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            String code = (String) reqBody.get("code");
            String description = (String) reqBody.get("description");
            Double discount = (Double) reqBody.get("discount");
            String startDate = (String) reqBody.get("start_date");
            String endDate = (String) reqBody.get("end_date");

            if (code == null || description == null || discount == null || startDate == null || endDate == null) {
                res.setBody("{\"error\": \"Missing required fields\"}");
                res.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return;
            }

            Vouchers voucher = new Vouchers(code, description, discount, startDate, endDate);
            vouchersDAO.insertVoucher(voucher);

            res.setBody("{\"message\": \"Voucher created successfully\"}");
            res.send(HttpURLConnection.HTTP_CREATED);

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    public void updateVoucher(HttpExchange exchange, int id) {
        Response res = new Response(exchange);
        Request req = new Request(exchange);

        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            Map<String, Object> reqBody = req.getJSON();

            String code = (String) reqBody.get("code");
            String description = (String) reqBody.get("description");
            Double discount = (Double) reqBody.get("discount");
            String startDate = (String) reqBody.get("start_date");
            String endDate = (String) reqBody.get("end_date");

            if (code == null || description == null || discount == null || startDate == null || endDate == null) {
                res.setBody("{\"error\": \"Missing required fields\"}");
                res.send(400);
                return;
            }

            Vouchers existing = vouchersDAO.getVoucherById(id);
            if (existing == null) {
                res.setBody("{\"error\": \"Voucher not found\"}");
                res.send(404);
                return;
            }

            Vouchers updated = new Vouchers(id, code, description, discount, startDate, endDate);
            boolean success = vouchersDAO.updateVoucher(id, updated);

            if (success) {
                res.setBody("{\"message\": \"Voucher updated successfully\"}");
                res.send(200);
            } else {
                res.setBody("{\"error\": \"Failed to update voucher\"}");
                res.send(500);
            }

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(500);
        }
    }

    public void deleteVoucher(HttpExchange exchange, int id) {
        Response res = new Response(exchange);
        try {
            if (!isAuthorized(exchange)) {
                throw new UnauthorizedException();
            }

            Vouchers existing = vouchersDAO.getVoucherById(id);
            if (existing == null) {
                res.setBody("{\"error\": \"Voucher not found\"}");
                res.send(404);
                return;
            }

            boolean success = vouchersDAO.deleteVoucher(id);
            if (success) {
                res.setBody("{\"message\": \"Voucher deleted successfully\"}");
                res.send(200);
            } else {
                res.setBody("{\"error\": \"Failed to delete voucher\"}");
                res.send(500);
            }

        } catch (UnauthorizedException e) {
            res.setBody("{\"error\": \"Unauthorized\"}");
            res.send(401);
        } catch (Exception e) {
            e.printStackTrace();
            res.setBody("{\"error\": \"Internal Server Error\"}");
            res.send(500);
        }
    }

}
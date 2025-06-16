package Tugas2.Controller;

import Tugas2.DAO.VillaDAO;
import Tugas2.Model.Villas;
import Tugas2.Request;
import Tugas2.Response;

import Tugas2.util.DB;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Controller {
    private VillaDAO villaDAO;

    public Controller() {
        try {
            this.villaDAO = new VillaDAO(DB.getConnection());
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
}

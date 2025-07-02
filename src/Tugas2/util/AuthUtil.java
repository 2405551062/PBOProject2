package Tugas2.util;

import Tugas2.Main;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class AuthUtil {

    public static boolean isAuthorized(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();
        String key = headers.getFirst("Authorization");
        return Main.API_KEY.equals(key);
    }
}

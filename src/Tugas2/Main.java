package Tugas2;

public class Main {

    public static final String API_KEY = "12345";

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        System.out.printf("Listening on port: %s...\n", port);
        new Server(port);
    }
}

// Cara menjalankan
// java -cp ".;src;lib\sqlite-jdbc-3.42.0.0.jar;lib\jackson-annotations-2.13.3.jar;lib\jackson-core-2.13.3.jar;lib\jackson-databind-2.13.3.jar" Tugas2.Main
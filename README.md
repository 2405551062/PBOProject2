Project 2
-
**Pembuatan API Pemesanan Vila Sederhana Berbasis Java**
-
Proyek ini adalah simulasi sederhana REST API menggunakan Java. Proyek ini dibuat untuk memahami konsep dasar OOP (Object-Oriented Programming), khususnya penggunaan class, object, dan bagaimana alur request-response di server.
Simulasi ini terdiri dari beberapa class utama yang memiliki peran masing-masing, mulai dari menerima request, memproses, hingga mengirim response.

Main classes
-
**Main.java**
-
File Main.java adalah entry point program. Di sini, kita membuat instance dari Server, lalu mensimulasikan request yang akan dikirim ke server.

Fungsi utama:
- Menjalankan server.
- Membuat objek request dan mengirim ke server.
- Menampilkan response yang diterima.

Highlight logika:
- Program bisa menerima argumen port saat dijalankan (args[0]), sehingga lebih fleksibel.
- Ada variabel API_KEY sebagai kunci API yang bisa digunakan di file lain jika dibutuhkan (misalnya untuk autentikasi sederhana).

Contoh alur:
Jika kita jalankan java Main 9090, server akan berjalan di port 9090. Jika tidak diberi argumen, akan default ke 8080.

**Request.java**
-
Class Request digunakan untuk membungkus informasi HTTP request yang diterima server. Menggunakan library Jackson (ObjectMapper) untuk parsing JSON dan sudah aman terhadap body kosong (ada validasi error).

Fungsi utama: 
- Mengakses method request (GET, POST, dll.).
- Membaca dan menyimpan request body (hanya di-load sekali).
- Mendapatkan header tertentu, termasuk Content-Type.
- Mengubah body JSON menjadi Map<String, Object> (menggunakan Jackson).

Highlight logika:
- Saat pertama kali dipanggil, getBody() membaca isi request body, lalu menyimpannya ke rawBody agar tidak perlu dibaca ulang.
- getJSON() memvalidasi header Content-Type harus application/json, lalu parsing body menjadi map (struktur key-value).
- Mendukung pengecekan header dengan mudah melalui getHeader().

Contoh alur, ketika client mengirim request POST dengan JSON, class ini akan:
1. Mengecek apakah content-type sudah benar.
2.  Membaca body (misal: { "name": "Budi" }).
3.  Mengembalikannya sebagai map agar server mudah memproses.

**Response.java**
-
Class Response digunakan untuk membungkus data yang akan dikembalikan ke client.

Fungsi utama: 
- Menyimpan dan mengatur body response sebelum dikirim.
- Mengirim response dengan status code tertentu (misalnya 200, 404).
- Mengatur header (secara default JSON, application/json; charset=utf-8).

Highlight logika:
- setBody(String body): Menentukan isi response yang akan dikirim.
- send(int status): Mengirim response ke client, sekaligus menuliskan body ke output stream dan menutup koneksi.
- isSent(): Mengecek apakah response sudah dikirim (menghindari pengiriman ulang).

Contoh alur:
1. Server memproses request dan membuat response.
2. Server menentukan body (misalnya: {"message":"Success"}) lewat setBody().
3. Server memanggil send(200) untuk mengirim response dengan status 200.

Note: Body dikirim dalam format UTF-8. Setelah dikirim, koneksi akan langsung ditutup (httpExchange.close()).

**Server.java**
-
Class Server berfungsi sebagai inti server HTTP yang menangani semua endpoint REST API.

Fungsi utama:
- Membuat dan memulai instance HttpServer.
- Mendefinisikan berbagai endpoint (context) dengan routing masing-masing.
- Mendistribusikan request ke Controller sesuai path dan method.

Highlight logika:
- Port: Bisa diatur (default 8080), diinisiasi saat pembuatan server.
- Routing utama:
  - /villas: Mendukung GET (list semua villa), POST (create villa), PUT (update), DELETE (delete), serta filtering villa berdasarkan tanggal (check-in, check-out).
  - /villas/{id}/reviews: GET review berdasarkan ID villa.
  - /villas/{id}/bookings: GET booking berdasarkan ID villa.
  - /villas/{id}/rooms: GET & POST rooms untuk villa tertentu, PUT & DELETE untuk room tertentu.
  - /customers: Mendukung GET (list semua customer), POST (create), GET by ID, PUT update, serta akses bookings & reviews.
  - /customers/{id}/bookings/{id}/reviews: POST untuk membuat review pada booking tertentu.
  - /vouchers: Mendukung GET (list semua voucher atau by ID), POST, PUT, DELETE.
- Fallback: Jika route tidak ditemukan atau method tidak sesuai, server mengembalikan response 404 Not Found.

Contoh alur:
1. Client mengirim request GET /villas → Server mengarahkan ke controller.GetAllVillas().
2. Client mengirim request POST /vouchers → Server memanggil controller.createVoucher().
3. Client mengirim request yang tidak valid → Server balas {"error": "Not Found"} dengan status 404.

Note: 
- Setiap handler (context) menggunakan HttpExchange.
- Menggunakan class Response untuk konsisten dalam mengirim response JSON.
- Penanganan error sudah ditangani dengan fallback sendNotFound().

Controller
-
**Controller.java**

DAO (Data Access Object)
-
**BookingsDAO.java**

**CustomersDAO.java**

**ReviewsDAO.java**

**RoomsDAO.java**

**VillaDAO.java**

**VouchersDAO.java**

Exception
-
**UnauthorizedException.java**

Model
-
**Bookings.java**

**Customers.java**

**Reviews.java**

**Rooms.java**

**Villas.java**

**Vouchers**

util
-
**DB.java**

**QueryParser.java**

**AuthUtil.java**

API Demonstration
-
isi endpoint.. (http://localhost:8080/villas.. dst..)

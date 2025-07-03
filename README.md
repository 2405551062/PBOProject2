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
  
| Entitas  | Metode  | Endpoint                                      | Deskripsi                                                                 |
|----------|---------|-----------------------------------------------|---------------------------------------------------------------------------|
| Villa    | GET     | /villas                                       | Daftar semua vila                                                         |
| Villa    | GET     | /villas/{id}                                  | Informasi detail suatu vila                                               |
| Villa    | GET     | /villas/{id}/rooms                            | Informasi kamar suatu vila, lengkap dengan fasilitas dan harga            |
| Villa    | GET     | /villas/{id}/bookings                         | Daftar semua booking pada suatu vila                                      |
| Villa    | GET     | /villas/{id}/reviews                          | Daftar semua review pada suatu vila                                       |
| Villa    | GET     | /villas?ci_date={checkin_date}&co_date={checkout_date} | Pencarian ketersediaan vila berdasarkan tanggal check-in dan checkout     |
| Villa    | POST    | /villas                                       | Menambahkan data vila                                                     |
| Villa    | POST    | /villas/{id}/rooms                            | Menambahkan tipe kamar pada vila                                          |
| Villa    | PUT     | /villas/{id}                                  | Mengubah data suatu vila                                                  |
| Villa    | PUT     | /villas/{id}/rooms/{id}                       | Mengubah informasi kamar suatu vila                                       |
| Villa    | DELETE  | /villas/{id}/rooms/{id}                       | Menghapus kamar suatu vila                                                |
| Villa    | DELETE  | /villas/{id}                                  | Menghapus data suatu vila                                                 |
| Customer | GET     | /customers                                    | Daftar semua customer                                                     |
| Customer | GET     | /customers/{id}                               | Informasi detail seorang customer                                         |
| Customer | GET     | /customers/{id}/bookings                      | Daftar booking oleh seorang customer                                      |
| Customer | GET     | /customers/{id}/reviews                       | Daftar ulasan oleh customer                                               |
| Customer | POST    | /customers                                    | Menambahkan customer baru (registrasi)                                    |
| Customer | POST    | /customers/{id}/bookings                      | Customer melakukan pemesanan vila                                         |
| Customer | POST    | /customers/{id}/bookings/{id}/reviews        | Customer memberikan ulasan berdasarkan booking                            |
| Customer | PUT     | /customers/{id}                               | Mengubah data customer                                                    |
| Voucher  | GET     | /vouchers                                     | Daftar semua voucher                                                      |
| Voucher  | GET     | /vouchers/{id}                                | Informasi detail suatu voucher                                            |
| Voucher  | POST    | /vouchers                                     | Membuat voucher baru                                                      |
| Voucher  | PUT     | /vouchers/{id}                                | Mengubah data voucher                                                     |
| Voucher  | DELETE  | /vouchers/{id}                                | Menghapus data voucher                                                    |
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
-
Class Controller berfungsi sebagai central handler atau "pengatur logika" untuk setiap endpoint dalam server. File ini menjadi jembatan antara request yang diterima server dan data/logic yang akan dijalankan.

Fungsi utama :
- Menyediakan method-method yang menangani setiap path endpoint (seperti /villas, /customers, /vouchers, dll.).
- Menyusun logika bisnis (business logic) sesuai permintaan client, seperti mengambil data villa, menambahkan booking, memberikan review, dan sebagainya.
- Berkomunikasi dengan class lain (misalnya Request, Response, atau model data) untuk memproses request dan membentuk response.

Highlight logika :
- Setiap method di dalam Controller mewakili satu aksi, misalnya:
  - getAllVillas(): mengambil daftar semua villa.
  - createVilla(): membuat villa baru.
  - updateVilla(): memperbarui data villa.
  - deleteVilla(): menghapus villa.
  - Dan method serupa untuk customer, booking, review, serta voucher.
- Tidak menggunakan framework seperti Spring Boot, melainkan menggunakan Java HTTP Server native (com.sun.net.httpserver.HttpServer).
- Mengandalkan class Request dan Response untuk membaca HTTP request dan mengirimkan HTTP response.
- Mengembalikan data dalam bentuk JSON (format text), menggunakan Response.setBody() dan Response.send(statusCode).

Contoh alur :
- Client mengirim request ke server, misalnya GET /villas.
- Server mendeteksi path dan method, lalu memanggil controller.getAllVillas(request, response).
- Method getAllVillas() membaca request (jika perlu), memproses logika (misalnya membaca data villa dari list/array), lalu mengembalikan response JSON.

Note :
- Class Controller dibuat supaya kode server tetap terstruktur (tidak semua logic langsung di Server.java).
- Setiap handler endpoint hanya memanggil satu method controller agar lebih modular dan mudah diperluas.
- Bisa dikembangkan untuk validasi tambahan (misalnya cek parameter, cek header, cek token API_KEY).
  
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
**Run Program**
Gunakan perintah berikut untuk mengkompilasi semua file Java dalam proyek, termasuk DAO, pengontrol, model, utilitas, dan pengecualian khusus

1. javac -cp ".;lib\sqlite-jdbc-3.42.0.0.jar;lib\jackson-annotations-2.13.3.jar;lib\jackson-core-2.13.3.jar;lib\jackson-databind-2.13.3.jar" src\Tugas2\*.java src\Tugas2\DAO\*.java src\Tugas2\Controller\*.java src\Tugas2\Model\*.java src\Tugas2\util\*.java src\Tugas2\Exception\*.java

2. java -cp ".;src;lib\sqlite-jdbc-3.42.0.0.jar;lib\jackson-annotations-2.13.3.jar;lib\jackson-core-2.13.3.jar;lib\jackson-databind-2.13.3.jar" Tugas2.Main

![RunProgram](https://github.com/user-attachments/assets/67ed5c50-91ac-4c1f-ad40-3b3ce6b3a77a)

Pesan yang ditampilkan di terminal (Mendengarkan di port: 8080..., Server dimulai di port: 8080) menunjukkan bahwa server API Java telah berhasil dikompilasi dan sekarang sedang berjalan. Output ini dihasilkan dari kelas Main.java Anda, khususnya dari bagian kode yang menyiapkan server HTTP menggunakan HttpServer bawaan Java. Ini berarti server secara aktif mendengarkan permintaan HTTP yang masuk di port 8080, yang dapat diakses melalui http://localhost:8080 dari browser atau alat API Anda. Pada titik ini, kita dapat mulai menguji titik akhir API Anda menggunakan alat seperti Postman.

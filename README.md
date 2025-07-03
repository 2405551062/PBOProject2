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

**API Key Authorization**

![APIkey](https://github.com/user-attachments/assets/cf4e3282-966d-433a-aa3f-4c3c160cee4c)

Untuk mengakses API, kita harus memasukan dulu API key di bagian Headers dimana Key = Authorization dan Values = 12345

Villa
-
**GET /villas**

![GETvillas](https://github.com/user-attachments/assets/a8931337-f4e5-497a-8ec3-60b7922a1492)

GET /villas adalah endpoint yang digunakan untuk mengambil daftar semua vila yang tersedia di sistem. Saat permintaan ini dikirim, server akan merespons dengan data dalam format JSON yang berisi informasi setiap vila, seperti ID, nama, deskripsi, dan alamat. Endpoint ini berguna ketika pengguna atau sistem ingin menampilkan seluruh koleksi vila yang terdaftar, misalnya untuk ditampilkan di halaman utama aplikasi atau sebagai referensi dalam proses pemesanan.

**GET /villas/{id}**

![GETvillasbyID](https://github.com/user-attachments/assets/819c5643-c1de-4ee1-8062-70531ff4375a)

GET /villas/{id} adalah endpoint yang digunakan untuk mengambil detail dari satu vila tertentu berdasarkan ID-nya. Saat pengguna mengirim permintaan ini dengan mengganti {id} menggunakan angka ID vila yang diinginkan, server akan mengembalikan data vila tersebut dalam format JSON. Informasi yang dikembalikan biasanya mencakup ID, nama vila, deskripsi, dan alamat. Endpoint ini berguna ketika pengguna ingin melihat detail dari satu vila secara spesifik, misalnya saat akan melakukan pemesanan atau melihat fasilitas yang ditawarkan.

**GET /villas/{id}/rooms**

![GETroomsbyvillaID](https://github.com/user-attachments/assets/f5e398df-43b5-4af4-945e-b5ec8f0a6982)

GET /villas/{id}/rooms adalah endpoint yang digunakan untuk mengambil daftar seluruh kamar yang tersedia dalam satu vila tertentu. {id} di bagian URL harus diganti dengan ID dari vila yang ingin dilihat kamarnya. Saat permintaan dikirim, server akan mengembalikan data dalam format JSON yang berisi informasi setiap kamar, seperti nama kamar, ukuran tempat tidur, kapasitas, harga, serta fasilitas yang disediakan (seperti AC, Wi-Fi, TV, shower, dan lainnya). Endpoint ini sangat berguna ketika pengguna ingin melihat pilihan kamar sebelum melakukan pemesanan pada vila tertentu.

**GET /villas/{id}/bookings**

![GETbookingsbyvillaID](https://github.com/user-attachments/assets/c2449e6a-5a29-47e1-8867-ed5628664585)

GET /villas/{id}/bookings adalah endpoint yang digunakan untuk mengambil daftar semua pemesanan (bookings) yang terkait dengan vila tertentu. {id} pada URL harus diganti dengan ID vila yang ingin dilihat riwayat atau data pemesanannya. Saat permintaan ini dikirim, server akan merespons dengan data dalam format JSON yang mencakup informasi setiap pemesanan, seperti ID booking, nama pelanggan, tanggal check-in dan check-out, status pembayaran, dan informasi tambahan lainnya. Endpoint ini berguna bagi admin atau sistem yang ingin memantau aktivitas pemesanan pada vila tertentu.

**GET /villas/{id}/reviews**

![GETreviewsbyvilladID](https://github.com/user-attachments/assets/436aa5f2-3e4d-4401-9a75-41ec5354a3b5)

GET /villas/{id}/reviews adalah endpoint yang digunakan untuk mengambil semua ulasan (reviews) yang diberikan oleh pelanggan terhadap vila tertentu. Bagian {id} dalam URL harus diganti dengan ID vila yang ingin dilihat ulasannya. Ketika permintaan ini dikirim, server akan merespons dengan data dalam format JSON yang berisi daftar ulasan, termasuk rating bintang, judul ulasan, dan isi komentar dari pelanggan yang pernah memesan vila tersebut. Endpoint ini berguna bagi pengguna yang ingin mengetahui pengalaman orang lain sebelum melakukan pemesanan.

**GET /villas?ci_date={checkin_date}&co_date={checkout_date}**

![villasci_date=2025-07-11 co_date=2025-07-14](https://github.com/user-attachments/assets/936b41b0-b2fa-4b67-bf54-9861f9cd60c8)

GET /villas?ci_date={checkin_date}&co_date={checkout_date} adalah endpoint yang digunakan untuk menampilkan daftar vila yang tersedia untuk disewa berdasarkan rentang tanggal check-in dan check-out yang ditentukan. Parameter ci_date mewakili tanggal check-in, sedangkan co_date adalah tanggal check-out, dan keduanya harus dituliskan dalam format tanggal yang valid (misalnya: 2025-07-05). Saat permintaan ini dikirim, server akan memproses data pemesanan yang sudah ada dan mengembalikan hanya vila-vila yang belum dipesan pada rentang tanggal tersebut. Hasilnya akan dikirim dalam format JSON, memudahkan pengguna untuk melihat opsi vila yang masih bisa dipesan.

**POST /villas**

![POSTvillas](https://github.com/user-attachments/assets/67024c2c-797b-463a-89e1-1cb7debc43f9)

POST /villas adalah endpoint yang digunakan untuk menambahkan data vila baru ke dalam sistem. Saat mengakses endpoint ini, pengguna perlu mengirimkan data vila dalam format JSON melalui body request. Data yang dikirim biasanya mencakup nama vila, deskripsi, dan alamat. Setelah permintaan diterima dan diproses, server akan menyimpan data tersebut ke dalam database dan memberikan respons bahwa vila berhasil ditambahkan. Endpoint ini sangat berguna bagi admin atau pemilik sistem untuk menambah daftar vila yang tersedia. 

**POST /villas/{id}/rooms**

![createROOM](https://github.com/user-attachments/assets/fc7ea9ef-8a5e-4616-b06f-2e09eaa971cd)

POST /villas/{id}/rooms adalah endpoint yang digunakan untuk menambahkan kamar baru ke dalam vila tertentu. Bagian {id} pada URL harus diganti dengan ID vila yang ingin ditambahkan kamar. Permintaan ini harus disertai dengan data kamar dalam format JSON melalui body request. Data tersebut biasanya mencakup nama kamar, ukuran tempat tidur (bed_size), harga, kapasitas, jumlah unit (quantity), serta berbagai fasilitas seperti AC, Wi-Fi, TV, shower, air panas, dan lainnya. Endpoint ini sangat berguna bagi admin sistem untuk memperbarui atau memperluas jenis kamar yang tersedia dalam sebuah vila.

**PUT /villas/{id}**

![PUTvilla](https://github.com/user-attachments/assets/2384c88c-f3de-4252-be21-f7d28d93e484)

![AFTERPUTvilla](https://github.com/user-attachments/assets/a536e661-7dc8-4b11-a569-ca8463b6d653)

PUT /villas/{id} adalah endpoint yang digunakan untuk memperbarui data sebuah vila yang sudah ada. Bagian {id} pada URL harus diganti dengan ID dari vila yang ingin diubah. Permintaan ini harus dikirim dengan format JSON melalui body request, yang berisi data baru seperti name (nama vila), description (deskripsi vila), dan address (alamat vila). Endpoint ini sangat penting bagi admin sistem untuk memperbarui informasi vila agar tetap akurat dan relevan, misalnya saat terjadi perubahan nama, deskripsi layanan, atau lokasi.

**PUT /villas/{id}/rooms/{id}**

![PUTrooms](https://github.com/user-attachments/assets/5b073f5e-fb8d-4a5c-bac2-47dd1b110030)

![AFTERPUTrooms](https://github.com/user-attachments/assets/e1ad105f-87c2-4926-b0e8-0bf7dba9918f)

PUT /villas/{villaId}/rooms/{roomId} adalah endpoint yang digunakan untuk memperbarui data kamar tertentu dalam sebuah vila. Bagian {villaId} adalah ID vila tempat kamar berada, dan {roomId} adalah ID kamar yang ingin diperbarui. Permintaan ini dikirim dalam format JSON melalui body request, berisi data baru seperti name (nama kamar), bed_size (ukuran tempat tidur), price (harga per malam), quantity (jumlah kamar tersedia), capacity (kapasitas orang), serta informasi fasilitas seperti has_ac, has_wifi, has_tv, dan lainnya. Endpoint ini penting bagi pengelola vila untuk memperbarui data kamar sesuai kondisi terbaru, misalnya perubahan harga, fasilitas tambahan, atau jumlah ketersediaan kamar.

**DELETE /villas/{id}/rooms/{id}**

![DELETErooms](https://github.com/user-attachments/assets/b3665615-d115-407f-b921-a43a08204086)

![AFTERDELETErooms](https://github.com/user-attachments/assets/9828abc8-0ff3-4cc6-b387-d68f0b27f39c)

DELETE /villas/{villaId}/rooms/{roomId} adalah endpoint yang digunakan untuk menghapus data kamar tertentu dari sebuah vila. Parameter {villaId} menunjukkan ID vila tempat kamar tersebut berada, dan {roomId} adalah ID kamar yang ingin dihapus. Permintaan ini biasanya digunakan oleh admin atau pengelola sistem untuk menghapus kamar yang sudah tidak tersedia atau tidak lagi digunakan. 

**DELETE /villas/{id}**

![DELETEvillas](https://github.com/user-attachments/assets/e0236bd9-319e-42d5-acc1-5c09d58d80f5)

![AFTERDELETEvillas](https://github.com/user-attachments/assets/12b52d76-e91a-4eff-bdf1-78dda6fedba3)

DELETE /villas/{id} adalah endpoint yang digunakan untuk menghapus data vila berdasarkan ID tertentu. Bagian {id} dalam URL harus diisi dengan ID vila yang ingin dihapus. Permintaan ini menggunakan metode DELETE dan harus dikirim oleh pengguna yang memiliki otorisasi, biasanya admin sistem. Penggunaan endpoint ini berguna jika sebuah vila sudah tidak lagi tersedia atau perlu dihapus dari sistem karena alasan tertentu.

Customer
-
**GET /customers**

![GETallcustomers](https://github.com/user-attachments/assets/878673ec-1837-4eda-b876-ebfc5a305ef9)

GET /customers adalah endpoint yang digunakan untuk mengambil daftar seluruh pelanggan (customers) yang terdaftar di sistem. Ketika endpoint ini diakses, server akan merespons dengan data dalam format JSON berisi informasi setiap customer, seperti nama, email, dan nomor telepon. Endpoint ini berguna untuk keperluan administratif atau pengelolaan data pelanggan, misalnya saat admin ingin melihat siapa saja yang sudah pernah melakukan pemesanan.

**GET /customers/{id}**

![GETcustomersbyID](https://github.com/user-attachments/assets/d261dd2a-44a5-4cdc-b41c-44bdc55261fb)

GET /customers/{id} adalah endpoint yang digunakan untuk mengambil data detail dari satu pelanggan berdasarkan ID tertentu. Bagian {id} pada URL harus diganti dengan ID pelanggan yang ingin dicari. Saat endpoint ini diakses, server akan mengembalikan informasi lengkap tentang pelanggan tersebut dalam format JSON, seperti nama, email, dan nomor telepon. Endpoint ini berguna untuk menampilkan data individual pelanggan, misalnya saat admin ingin memverifikasi informasi atau melakukan pembaruan data secara manual.

**GET /customers/{id}/bookings**

![GETreviewsbycustomerID](https://github.com/user-attachments/assets/ef2de9f3-3a62-4562-b90f-c4ed4249e80e)

GET /customers/{id}/bookings adalah endpoint yang digunakan untuk mengambil daftar seluruh pemesanan (bookings) yang dilakukan oleh seorang pelanggan tertentu. Bagian {id} pada URL harus diisi dengan ID pelanggan yang ingin dicari riwayat pemesanannya. Ketika endpoint ini diakses, server akan mengembalikan data dalam format JSON yang berisi daftar semua booking yang pernah dibuat oleh pelanggan tersebut, termasuk informasi seperti tipe kamar, tanggal check-in dan check-out, status pembayaran, serta harga. Endpoint ini bermanfaat bagi admin sistem atau pelanggan itu sendiri untuk melihat riwayat transaksi yang sudah dilakukan.

**GET /customers/{id}/reviews**

![GETreviewsbycustomerID](https://github.com/user-attachments/assets/42754200-82cb-4d13-983c-afec2de60992)

GET /customers/{id}/reviews adalah endpoint yang digunakan untuk mengambil semua ulasan (reviews) yang pernah dibuat oleh seorang pelanggan berdasarkan ID-nya. Bagian {id} pada URL harus diisi dengan ID pelanggan yang ingin dicari ulasannya. Saat endpoint ini dipanggil, server akan mengembalikan data dalam format JSON berisi daftar review yang pernah ditulis oleh pelanggan tersebut. Data ini mencakup informasi seperti rating bintang (star), judul ulasan, isi ulasan, dan ID booking terkait. Endpoint ini berguna untuk menampilkan riwayat kontribusi pelanggan terhadap penilaian layanan, yang dapat bermanfaat bagi admin dalam mengevaluasi kepuasan pengguna.

**POST /customers**

![POSTcustomers](https://github.com/user-attachments/assets/250c0663-9cc9-434c-a195-0ffbcddbbd37)

POST /customers adalah endpoint yang digunakan untuk menambahkan data pelanggan baru ke dalam sistem. Permintaan ini harus dikirim menggunakan metode POST dan berisi data dalam format JSON yang mencakup informasi pelanggan, seperti name (nama pelanggan), email, dan phone (nomor telepon). Saat endpoint ini dipanggil dengan data yang valid, server akan menyimpan informasi pelanggan ke dalam database dan mengembalikan respons dalam format JSON yang menunjukkan bahwa proses berhasil dilakukan. Respon ini biasanya berisi pesan sukses dan status 201 Created. Endpoint ini berguna saat proses pendaftaran pelanggan baru dilakukan, baik secara manual oleh admin maupun secara otomatis oleh sistem.

**POST /customers/{id}/bookings**

![POSTbookings](https://github.com/user-attachments/assets/1bc3e181-a5e6-4b28-a460-c79a11b66f60)

GET /customers/{id}/reviews adalah endpoint yang digunakan untuk mengambil semua ulasan (reviews) yang pernah dibuat oleh seorang pelanggan berdasarkan ID-nya. Bagian {id} pada URL harus diisi dengan ID pelanggan yang ingin dicari ulasannya. Saat endpoint ini diakses, server akan membalas dengan data dalam format JSON yang berisi daftar review milik pelanggan tersebut. Setiap review umumnya memuat informasi seperti jumlah bintang (star), judul (title), isi ulasan (content), dan ID pemesanan (booking) yang berkaitan. Endpoint ini sangat bermanfaat untuk menampilkan riwayat penilaian atau feedback pelanggan terhadap layanan yang pernah mereka gunakan. Admin bisa menggunakan data ini untuk mengevaluasi tingkat kepuasan pelanggan dan mengidentifikasi area layanan yang perlu ditingkatkan.

**POST /customers/{id}/bookings/{id}/reviews**

![POSTreview](https://github.com/user-attachments/assets/ffe97e9c-ada2-40de-b5c2-e556691c3efd)

GET /customers/{id}/reviews adalah endpoint yang digunakan untuk mengambil semua ulasan (review) yang dibuat oleh seorang pelanggan berdasarkan ID-nya. Bagian {id} pada URL harus diganti dengan ID dari pelanggan yang ingin dicari ulasannya. Saat endpoint ini diakses, server akan mengembalikan data dalam format JSON yang berisi daftar review yang pernah ditulis oleh pelanggan tersebut. Setiap data review biasanya memuat informasi seperti jumlah bintang (star), judul (title), isi ulasan (content), dan ID pemesanan (booking) yang terkait. Endpoint ini berguna untuk menampilkan riwayat kontribusi pelanggan terhadap penilaian layanan. Informasi ini dapat dimanfaatkan oleh admin untuk mengevaluasi tingkat kepuasan pelanggan dan kualitas layanan yang diberikan.

**PUT /customers/{id}**

![PUTcustomers](https://github.com/user-attachments/assets/2d1f23e7-4c23-4713-ac3f-72015be907e3)

![AFTERPUTcustomers](https://github.com/user-attachments/assets/84c545e4-80e4-4260-931c-c0b00766e33e)

POST /customers/{id}/bookings adalah endpoint yang digunakan untuk membuat pemesanan (booking) baru untuk pelanggan tertentu berdasarkan ID-nya. Bagian {id} pada URL harus diisi dengan ID pelanggan yang akan melakukan pemesanan. Permintaan ini dikirim menggunakan metode HTTP POST, dengan body dalam format JSON yang berisi informasi detail pemesanan. Data yang perlu disediakan antara lain room_type (ID tipe kamar), checkin_date, checkout_date, price, final_price, payment_status, serta opsi tambahan seperti voucher, has_checkedin, dan has_checkedout.

Voucher
-
**GET /vouchers**

![GETvouchers](https://github.com/user-attachments/assets/7fc9f71a-d322-43d3-85a0-04bca9ce4c0e)

GET /vouchers adalah endpoint yang digunakan untuk mengambil seluruh daftar voucher yang tersedia di sistem. Endpoint ini tidak memerlukan parameter khusus di URL. Saat dipanggil, server akan merespons dengan data dalam format JSON yang berisi kumpulan voucher aktif maupun yang sudah kedaluwarsa, tergantung implementasinya. Setiap entri voucher yang dikembalikan biasanya mencakup informasi seperti id, code (kode voucher), description (deskripsi), discount (nilai diskon), start_date, dan end_date.

**GET /vouchers/{id}**

![GETvouchersbyID](https://github.com/user-attachments/assets/0fdcd4e9-7a61-416b-9f91-40cbceed9997)

GET /vouchers/{id} adalah endpoint yang digunakan untuk mengambil detail dari satu voucher tertentu berdasarkan ID-nya. Dalam penggunaannya, bagian {id} pada URL harus diganti dengan ID voucher yang ingin dicari. Setelah dipanggil, server akan mengembalikan informasi voucher dalam format JSON, termasuk data seperti kode voucher (code), deskripsi (description), besar diskon (discount), tanggal mulai berlaku (start_date), dan tanggal berakhir (end_date). Endpoint ini bermanfaat ketika pengguna atau admin ingin melihat informasi lengkap mengenai suatu voucher, misalnya untuk memverifikasi masa berlaku atau nilai diskon.

**POST /vouchers**

![POSTvouchers](https://github.com/user-attachments/assets/6d5fa505-1c52-4de7-8752-d6eccd9418c0)

POST /vouchers adalah endpoint yang digunakan untuk menambahkan voucher baru ke dalam sistem. Permintaan ini harus dikirim dalam format JSON melalui body request, yang berisi data-data penting seperti code (kode unik voucher), description (deskripsi voucher), discount (besar diskon dalam persen), start_date (tanggal mulai berlakunya voucher), dan end_date (tanggal berakhirnya masa berlaku voucher). Endpoint ini biasanya digunakan oleh admin untuk membuat voucher promosi baru yang bisa digunakan pelanggan saat melakukan pemesanan.

**PUT /vouchers/{id}**

![PUTvouchers](https://github.com/user-attachments/assets/a4f241d9-5e8b-4472-8517-cf22776eb755)

![AFTERPUTvouchers](https://github.com/user-attachments/assets/40eb2134-0f6e-4878-b5a8-fb601b522c8e)

PUT /vouchers/{id} adalah endpoint yang digunakan untuk memperbarui data dari sebuah voucher yang sudah ada. Bagian {id} pada URL harus diisi dengan ID voucher yang ingin diperbarui. Permintaan ini dikirim dengan format JSON melalui body request, yang harus mencakup informasi baru seperti code (kode voucher), description (deskripsi), discount (persentase diskon), start_date (tanggal mulai berlaku), dan end_date (tanggal berakhir). Endpoint ini berguna bagi admin yang ingin memperbarui informasi voucher yang sebelumnya sudah dibuat, seperti mengganti nilai diskon atau memperpanjang masa berlakunya.

**DELETE /vouchers/{id}**

![DELETEVouchers](https://github.com/user-attachments/assets/eb8b33bd-2eba-47c6-ab37-779fc5da57d0)

![AFTERDELETEvouchers](https://github.com/user-attachments/assets/cd5baa5d-a76a-4d4f-b13f-42373b1e4f24)

DELETE /vouchers/{id} adalah endpoint yang digunakan untuk menghapus sebuah voucher dari sistem berdasarkan ID-nya. Bagian {id} pada URL harus diisi dengan ID voucher yang ingin dihapus. Permintaan ini umumnya digunakan oleh admin untuk menghapus voucher yang sudah tidak berlaku atau tidak lagi dibutuhkan.

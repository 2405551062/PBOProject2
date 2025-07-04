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
-
BookingsDAO adalah class yang digunakan untuk mengatur data pemesanan vila di database. Class ini dipakai untuk mengambil data booking yang sudah ada atau menyimpan data booking baru yang dikirim oleh client. Class ini bekerja dengan database melalui koneksi Connection

Fungsi utama:
- getBookingsByVillaId(int villaId)
Mengambil semua data pemesanan yang berkaitan dengan satu vila tertentu. Caranya dengan mencari pemesanan berdasarkan tipe kamar (room_type) yang terhubung ke  vila tersebut. Hasilnya akan diurutkan dari tanggal check-in paling awal.
- getBookingsByCustomerId(int customerId)
  Mengambil semua pemesanan berdasarkan ID customer. Hasilnya juga diurutkan dari yang paling awal check-in.
- insertBooking(Bookings booking)
 Menyimpan data pemesanan baru ke dalam tabel bookings di database. Termasuk data customer, tipe kamar, tanggal check-in dan check-out, harga, voucher (jika ada), dan status pembayaran

Highlight logika:
- Pada method getBookingsByVillaId(), digunakan SQL JOIN antara tabel bookings dan room_types supaya bisa tahu pemesanan mana yang termasuk dalam vila tertentu.
- Method insertBooking() memeriksa apakah ada voucher. Jika tidak ada (null), maka diisi NULL ke database supaya tidak error.
- Di bagian bawah class, ada method bantu mapResultSetToBooking() yang berfungsi untuk mengubah data dari database (ResultSet) menjadi object Bookings. Ini memudahkan pemrosesan di Java.

Contoh alur:
 
Client ingin memesan vila, lalu mengirim data booking baru lewat POST (misalnya POST /bookings).
Server akan:
  -  Menerima data dari client (seperti tanggal check-in, tipe kamar, dll).
  -  Mengubah data tersebut menjadi objek Bookings.
  -  Memanggil method insertBooking() untuk menyimpan data ke databas
  -  jika berhasil, server akan mengirim response ke client bahwa booking berhasil dibuat.

**CustomersDAO.java**
-
CustomersDAO adalah class yang digunakan untuk mengatur data pelanggan (customer) di database. Dengan class ini, server bisa mengambil semua data pelanggan, mengambil data berdasarkan ID, menambahkan pelanggan baru, atau mengubah data pelanggan yang sudah ada.

Fungsi utama:
- getAllCustomers()  
  Mengambil semua data customer dari tabel customers. Data diurutkan berdasarkan ID dari yang paling awal.
- getCustomerById(int id)  
  Mengambil satu data customer berdasarkan ID yang diberikan.
- insertCustomer(Customers customer)  
  Menyimpan data customer baru ke database. Termasuk nama, email, dan nomor telepon.
- updateCustomer(Customers customer)  
  Mengubah data customer yang sudah ada berdasarkan ID. Data seperti nama, email, dan nomor telepon akan diperbarui.

Highlight logika:
- Setiap method menggunakan PreparedStatement supaya lebih aman dari serangan SQL Injection.
- Di bagian bawah, ada method mapResultSetToCustomer() yang berfungsi mengubah hasil query (ResultSet) menjadi objek Customers agar bisa diproses di Java.
- Saat insert atau update, data dimasukkan langsung dari object Customers yang sudah terbentuk dari request.
  
Contoh alur:

Client mengirim permintaan POST /customers untuk menambahkan pelanggan baru.
Server akan:
- Menerima data seperti nama, email, dan nomor HP dari client.  
- Mengubah data tersebut menjadi objek Customers.  
- Memanggil method insertCustomer() untuk menyimpan data ke database.  
- Jika berhasil, server mengirimkan response sukses ke client bahwa data pelanggan berhasil ditambahkan.

**ReviewsDAO.java**
-
ReviewsDAO adalah class yang digunakan untuk mengatur data ulasan atau review dari pelanggan terhadap vila yang mereka sewa. Class ini bisa mengambil review berdasarkan vila atau customer, dan juga bisa menambahkan review baru ke database.

Fungsi utama:
- getReviewsByVillaId(int villaId)  
  Mengambil semua review berdasarkan ID vila. Method ini menggunakan JOIN dengan tabel booking dan room_types agar bisa menemukan review mana yang termasuk ke dalam vila tersebut.
- getReviewsByCustomerId(int customerId)  
  Mengambil semua review berdasarkan ID customer. Data diambil dari tabel reviews yang dikaitkan dengan bookings.
- insertReview(Reviews review)  
  Menambahkan review baru ke dalam database. Data yang disimpan adalah ID booking, rating bintang (star), judul, dan isi ulasan (content).

Highlight logika:
- Semua query ditulis menggunakan PreparedStatement agar lebih aman dari serangan SQL Injection.
- Untuk mendapatkan data berdasarkan vila, digunakan query dengan JOIN 3 tabel: reviews → bookings → room_types.
- Objek Reviews dibuat langsung dari hasil query (ResultSet) untuk memudahkan pemrosesan di Java.


**RoomsDAO.java**
-
RoomsDAO adalah class yang digunakan untuk mengatur data tipe kamar (room types) dalam sebuah vila. Dengan class ini, server bisa mengambil daftar kamar dari suatu vila, menambahkan kamar baru, mengubah data kamar yang ada, atau menghapus kamar dari database.
Class ini menggunakan objek Connection untuk berkomunikasi langsung dengan database.

Fungsi utama:
- getRoomsByVillaId(int villaId)  
  Mengambil semua tipe kamar berdasarkan ID vila. Misalnya, kita ingin tahu kamar apa saja yang dimiliki oleh vila nomor 3.
- insertRoom(Rooms room)  
  Menyimpan tipe kamar baru ke database. Data kamar seperti nama, jumlah kamar, kapasitas orang, fasilitas (AC, wifi, TV, dll.) akan disimpan.
- updateRoom(Rooms room)  
  Mengubah data kamar tertentu. Kamar yang dimaksud dicari berdasarkan ID kamar dan ID vila.
- deleteRoom(int roomId)  
  Menghapus satu kamar dari database berdasarkan ID kamar.

Highlight logika:
- Data fasilitas kamar seperti AC, TV, Wi-Fi, shower, kulkas, dan lain-lain disimpan sebagai nilai angka (biasanya 1 = ada, 0 = tidak ada).
- Semua SQL dijalankan menggunakan PreparedStatement agar aman dari serangan SQL Injection.
- Method getRoomsByVillaId() akan membaca semua kolom dari tabel room_types dan mengubahnya menjadi object Rooms.

Contoh alur:
Client ingin menambahkan tipe kamar baru ke vila dengan ID 2.  
Client mengirim data lewat POST /rooms dengan informasi seperti nama kamar, kapasitas, harga, dan fasilitas.Server akan:
- Membaca data JSON dan membuat objek Rooms.  
- Memanggil insertRoom() untuk menyimpan data ke database.  
- Jika berhasil, server mengirimkan respons bahwa kamar berhasil ditambahkan.


**VillaDAO.java**
-
VillaDAO adalah class yang digunakan untuk mengelola data vila yang tersimpan di dalam database. Class ini bertugas untuk mengambil daftar vila, mengambil vila berdasarkan ID, menambahkan vila baru, memperbarui vila, menghapus vila, dan mengecek vila yang tersedia berdasarkan tanggal check-in dan check-out.

Fungsi utama:
- getAllVillas()  
  Mengambil semua data vila dari database. Data yang diambil termasuk ID, nama vila, deskripsi, dan alamat.
- getVillaById(int id)  
  Mengambil data satu vila berdasarkan ID tertentu.
- getAvailableVillas(String checkinDate, String checkoutDate)  
  Mengambil daftar vila yang masih tersedia pada rentang tanggal yang diberikan. Digunakan saat ingin melihat vila mana yang belum dipesan.
- insertVilla(Villas villa)  
  Menyimpan data vila baru ke database, seperti nama, deskripsi, dan alamat.
- updateVilla(Villas villa)  
  Mengubah data vila yang sudah ada berdasarkan ID vila tersebut.
- deleteVilla(int id)  
  Menghapus data vila dari database berdasarkan ID.

Highlight logika:
- Method getAvailableVillas() menggunakan query SQL dengan kondisi NOT IN, artinya akan mencari vila yang tidak sedang dibooking dalam tanggal yang ditentukan.  
- Semua pengolahan data menggunakan PreparedStatement agar lebih aman dan mencegah SQL Injection.  
- Untuk membaca data dari ResultSet, data langsung dimasukkan ke objek Villas agar mudah diproses di dalam program.

Contoh alur:

Client ingin mencari vila yang tersedia untuk disewa dari tanggal 10 Juli sampai 12 Juli.  
Client mengirim request GET ke /villas/available?checkin=2025-07-10&checkout=2025-07-12.Server akan:
- Membaca parameter tanggal dari request.  
- Memanggil method getAvailableVillas(checkin, checkout).  
- Method ini akan mencari vila yang tidak sedang dibooking pada rentang tanggal tersebut.  
- Hasilnya dikirim kembali ke client dalam bentuk JSON.

**VouchersDAO.java**
-
VouchersDAO adalah class yang digunakan untuk mengelola data voucher diskon yang ada di dalam database. Dengan class ini, server bisa menampilkan semua voucher, mencari voucher tertentu, menambah voucher baru, memperbarui voucher yang sudah ada, atau menghapus voucher berdasarkan ID.

Fungsi utama:
- getAllVouchers()  
  Mengambil semua data voucher dari database dan mengurutkannya berdasarkan ID.
- getVoucherById(int id)  
  Mengambil satu voucher berdasarkan ID tertentu.
- insertVoucher(Vouchers voucher)  
  Menambahkan voucher baru ke database. Data yang dimasukkan seperti kode voucher, deskripsi, diskon, tanggal mulai, dan tanggal berakhir.
- updateVoucher(int id, Vouchers voucher)  
  Memperbarui isi voucher berdasarkan ID. Misalnya, kita ingin mengganti besar diskon atau mengubah tanggal berlakunya.
- deleteVoucher(int id)  
  Menghapus voucher dari database berdasarkan ID.

Highlight logika:
- Data voucher seperti diskon disimpan dalam format desimal (double), sehingga bisa 10%, 25.5%, dll.  
- Digunakan helper method mapResultSetToVoucher() untuk mengubah hasil query ke bentuk objek Vouchers.  
- Seluruh query dijalankan dengan PreparedStatement agar lebih aman dari SQL Injection.

Contoh alur:

Seorang admin ingin menambahkan voucher diskon baru untuk promosi liburan.  
Admin mengisi data seperti kode voucher, besar diskon, serta tanggal mulai dan akhir.  
Setelah data dikirim ke server, sistem menyimpannya ke database sebagai voucher baru.  
Ketika pelanggan melakukan pemesanan, mereka bisa menggunakan kode voucher ini untuk mendapatkan potongan harga selama periode berlaku.

Exception
-
**UnauthorizedException.java**
-
Class UnauthorizedException adalah exception khusus yang digunakan untuk menangani kasus autorisasi yang gagal, yaitu ketika client tidak memiliki hak akses terhadap resource tertentu di API. Biasanya ini digunakan dalam proses autentikasi menggunakan API key atau token.

Fungsi utama:
- Menandai bahwa client tidak memiliki hak akses ke resource tertentu.
- Menghasilkan response dengan status 401 Unauthorized.
  
Highlight logika:
- Merupakan turunan dari class Exception.
- Menerima parameter pesan error (String message) saat exception dibuat.
- Umumnya digunakan untuk autentikasi API sederhana (misalnya validasi API key di header).

Contoh alur:
- Client mengirim request tanpa menyertakan header X-API-KEY.
- Server memeriksa dan menemukan bahwa tidak ada API key atau key-nya tidak valid.
- Server melempar throw new UnauthorizedException("API key tidak valid").
- Server mengirim response dengan status 401 dan pesan error ke client.

Note:
- Class UnauthorizedException dibuat untuk menangani autentikasi yang gagal agar sistem lebih aman.
- Memisahkan logika error 401 dari error lainnya sehingga response lebih spesifik dan mudah dipahami client.
- Dapat dikembangkan untuk sistem autentikasi lanjutan seperti token, session, atau OAuth di masa depan.

Model
-
**Bookings.java**
-
Class Bookings digunakan untuk merepresentasikan data pemesanan vila oleh pelanggan. Class ini menyimpan seluruh informasi yang dibutuhkan dalam satu transaksi booking, termasuk data pelanggan, kamar, tanggal menginap, harga, status pembayaran, hingga status check-in dan check-out.

Fungsi utama:
- Menyimpan data pemesanan dalam bentuk objek Java.
- Digunakan untuk membuat, membaca, atau mengupdate data booking di dalam sistem.
- Menyediakan getter dan setter untuk setiap field agar bisa diakses dan dimodifikasi dengan aman.

Highlight logika:
- Memiliki dua konstruktor: satu konstruktor kosong (no-args constructor) untuk kebutuhan default dan deserialisasi JSON, serta satu konstruktor lengkap untuk inisialisasi semua data booking.
- Field voucher menggunakan tipe Integer (bukan int) agar bisa menyimpan nilai null jika tidak ada voucher yang digunakan.
- Field hasCheckedIn dan hasCheckedOut menggunakan tipe int sebagai indikator status (misalnya 0 = belum, 1 = sudah).
- Data yang disimpan di class ini biasanya berasal dari request client atau hasil query dari database.

Contoh alur:
- Client mengirim data pemesanan melalui request POST /bookings.
- Server mengubah data JSON dari client menjadi objek Bookings.
- Objek ini digunakan untuk menyimpan data ke database atau mengembalikannya sebagai response JSON.

**Customers.java**
-
Class Customers digunakan untuk merepresentasikan data pelanggan dalam sistem pemesanan vila. Class ini menyimpan informasi dasar pelanggan seperti nama, email, dan nomor telepon yang diperlukan saat melakukan pemesanan.

Fungsi utama:
- Menyimpan dan mengelola data pelanggan dalam bentuk objek Java.
- Digunakan dalam proses input data pelanggan baru, pengambilan data pelanggan berdasarkan ID, atau pembaruan data pelanggan.
- Menyediakan getter dan setter untuk tiap atribut agar data bisa diakses dan dimodifikasi dengan aman.

Highlight logika:
- Memiliki dua konstruktor: satu konstruktor kosong (no-args constructor) untuk kebutuhan deserialisasi JSON, dan satu konstruktor lengkap (full-args) untuk inisialisasi data.
- Field seperti name, email, dan phone bertipe String untuk menyimpan informasi pelanggan dengan fleksibel.
- Class ini umumnya digunakan dalam proses pemetaan antara database dan objek Java melalui DAO.

Contoh alur:
- Client mengirim data pelanggan baru melalui request POST /customers.
- Server menerima data tersebut dan membentuk objek Customers.
- Objek ini kemudian dikirim ke CustomersDAO untuk disimpan ke dalam database.
- Saat dibutuhkan, server juga dapat mengembalikan data ini ke client dalam format JSON.

**Reviews.java**
-
Class Reviews digunakan untuk merepresentasikan data ulasan (review) dari pelanggan terhadap vila yang telah mereka pesan. Informasi yang disimpan dalam class ini mencakup ID booking, jumlah bintang (rating), judul ulasan, dan isi ulasan.

Fungsi utama:
- Menyimpan data review dalam bentuk objek Java.
- Digunakan saat pelanggan mengirim ulasan melalui API, maupun saat mengambil data review dari database.
- Menyediakan getter dan setter untuk setiap atribut agar data bisa diproses lebih mudah.

Highlight logika:
Terdapat tiga konstruktor:
- Konstruktor lengkap (full constructor) yang menyertakan semua data termasuk booking ID.
- Konstruktor tanpa booking, digunakan jika ID booking dikelola di tempat lain.
- Konstruktor kosong (no-args constructor) dibutuhkan untuk deserialisasi otomatis, seperti saat parsing JSON.
Field booking menandakan review ini berasal dari pemesanan mana, star digunakan untuk rating 1–5. Sedangkan title dan content menyimpan ulasan pelanggan secara ringkas dan detail.

Contoh alur:
- Pelanggan menyelesaikan pemesanan dan mengirim ulasan melalui request POST /reviews.
- Server membentuk objek Reviews dari data tersebut.
- Objek digunakan untuk disimpan ke database atau dikembalikan ke client

**Rooms.java**
-
Class Rooms berfungsi sebagai representasi dari tipe kamar yang tersedia di dalam sebuah vila. Informasi yang disimpan mencakup detail lengkap mulai dari jumlah unit kamar, kapasitas tamu, harga, ukuran tempat tidur, hingga fasilitas tambahan seperti meja, AC, TV, Wi-Fi, dan lainnya.

Fungsi utama:
- Menyimpan informasi detail tentang tipe kamar yang dimiliki oleh vila.
- Menyediakan data kamar yang bisa ditampilkan ke pengguna saat browsing vila.
- Digunakan sebagai referensi dalam proses booking kamar oleh pelanggan.
- Menyediakan data pendukung untuk pencarian dan filter berdasarkan fasilitas kamar.

Highlight logika:
- Atribut villa menunjukkan relasi bahwa kamar ini milik vila tertentu.
- Properti seperti hasDesk, hasAc, hasTv, dan lainnya menunjukkan keberadaan fasilitas tertentu. Nilainya biasanya berupa angka 1 (ada) atau 0 (tidak ada).
- Getter dan setter disediakan lengkap untuk setiap field agar data bisa dimanipulasi atau ditampilkan dengan mudah.
- Data bedSize, capacity, dan price sangat penting untuk memberikan gambaran detail tentang kamar kepada pengguna.

Contoh alur:
Misalnya, pengguna membuka aplikasi dan ingin melihat tipe kamar yang tersedia di salah satu vila.
- Client mengirim request ke server (misalnya GET /rooms?villa=1).
- Server mengambil daftar tipe kamar dari database yang sesuai dengan ID vila tersebut.
- Data kamar dikonversi ke dalam bentuk objek Rooms, lalu diubah ke JSON.
- Client menampilkan daftar kamar dengan fasilitas dan harga kepada pengguna.

**Villas.java**
-
Class Villas merupakan representasi dari entitas vila yang tersedia dalam sistem. Setiap vila memiliki informasi dasar seperti nama, deskripsi singkat, dan alamat lokasi. Data ini menjadi fondasi utama yang digunakan dalam menampilkan daftar vila kepada pelanggan serta menghubungkannya dengan kamar (Rooms) yang dimiliki.

Fungsi utama:
- Menyimpan informasi identitas dan lokasi vila dalam bentuk objek Java.
- Digunakan untuk menampilkan daftar vila yang tersedia kepada pengguna.
- Menjadi referensi utama dalam pemetaan tipe kamar dan pemesanan.
- Mempermudah proses insert, update, dan fetch data vila dari database.

Highlight logika:
Class ini memiliki tiga jenis konstruktor:
- Full constructor digunakan saat semua data vila sudah diketahui (biasanya saat pengambilan data dari database).
- Constructor tanpa ID dipakai saat membuat data vila baru (karena ID akan di-generate otomatis oleh database).
- No-args constructor penting untuk proses deserialisasi otomatis (misalnya saat parsing JSON atau mapping JDBC).
Setiap properti seperti name, description, dan address memiliki getter dan setter untuk fleksibilitas pemrosesan.

Contoh alur:
Misalnya, admin ingin menambahkan vila baru ke dalam sistem:
- Admin mengisi formulir berisi nama, deskripsi, dan alamat vila.
- Data dikirim ke server dalam format JSON.
- Server membentuk objek Villas dari request tersebut, lalu mengirimkannya ke DAO untuk disimpan di database.
- Jika berhasil, sistem akan menampilkan daftar vila baru tersebut kepada pengguna.

**Vouchers**

util
-
**DB.java**
-
DB adalah class utilitas yang bertanggung jawab untuk mengatur koneksi ke database SQLite yang digunakan oleh aplikasi. Class ini memastikan hanya ada satu koneksi aktif yang digunakan bersama oleh semua bagian sistem yang membutuhkan akses database.

Fungsi utama:
- getConnection()
  Metode ini akan mengembalikan objek Connection yang bisa digunakan untuk menjalankan query SQL. Jika belum ada koneksi yang terbuka, atau koneksi sebelumnya telah    ditutup, maka sistem akan membuat koneksi baru ke file database vbook.db.

Highlight logika:
- Digunakan SQLite sebagai sistem basis data, dengan path file database: vbook.db.
- Pola yang digunakan adalah lazy initialization—koneksi baru hanya dibuat saat dibutuhkan (saat belum ada koneksi aktif).
- Koneksi disimpan sebagai variabel statis (conn), sehingga bisa digunakan kembali tanpa membuka koneksi baru setiap kali.
- Menggunakan DriverManager.getConnection() untuk membuat koneksi ke database SQLite.

Contoh alur:
- Sistem perlu menjalankan query SQL, misalnya untuk mengambil data voucher.
- Class seperti VouchersDAO memanggil DB.getConnection() untuk mendapatkan objek Connection.
- Jika koneksi belum ada atau sudah tertutup, class DB membuat koneksi baru ke database vbook.db.
- Query dijalankan menggunakan koneksi tersebut, lalu hasilnya diproses dan dikembalikan ke bagian yang membutuhkan.

**QueryParser.java**
-
QueryParser adalah class utilitas yang digunakan untuk memproses query string dari URL, dan mengubahnya menjadi pasangan key-value dalam bentuk Map. Ini sangat berguna untuk mengambil parameter dari URL saat server menerima permintaan GET atau POST yang mengandung data dalam format query.

Fungsi utama:
- parseQueryParams(String query)
  Metode ini menerima query string (contohnya: name=Adi&umur=21) dan mengubahnya menjadi objek Map<String, String> yang berisi pasangan nama-parameter dan nilainya.    Jika query kosong atau null, akan mengembalikan map kosong.

Highlight logika:
- Query string dipisah berdasarkan simbol & untuk mendapatkan tiap parameter.
- Setiap parameter kemudian dipecah menjadi key dan value menggunakan simbol =.
- URLDecoder.decode() digunakan untuk memastikan bahwa karakter yang di-encode di URL (seperti %20 untuk spasi) dikembalikan ke bentuk aslinya.
- Encoding yang digunakan adalah UTF-8, sesuai standar web modern.

Contoh alur:
- Sebuah request datang ke server dengan query string seperti: ?kode=DISC10&kategori=makanan%20ringan
- Server memanggil QueryParser.parseQueryParams(query).
- Fungsi akan mengembalikan map berisi:
  * "kode" → "DISC10"
  * "kategori" → "makanan ringan"
- Data ini bisa digunakan untuk mencari data dalam database atau menjalankan logika lainnya.

**AuthUtil.java**
-
AuthUtil adalah class utilitas yang digunakan untuk memeriksa apakah permintaan HTTP (request) yang masuk memiliki hak akses (otorisasi) yang sesuai. Class ini berfungsi sebagai bagian dari sistem keamanan API, memastikan bahwa hanya pengguna yang memiliki API key yang benar yang dapat mengakses endpoint tertentu.

Fungsi utama:
- isAuthorized(HttpExchange exchange)
  Fungsi ini memeriksa header dari permintaan HTTP untuk melihat apakah terdapat header Authorization. Jika ada, nilainya dibandingkan dengan API_KEY yang telah        ditentukan dalam class Main. Jika cocok, maka permintaan dianggap sah (authorized), dan fungsi mengembalikan true; jika tidak cocok atau tidak ada, maka false.

Highlight logika:
- Header Authorization dibaca langsung dari objek HttpExchange, yang merupakan bagian dari framework HttpServer bawaan Java.
- Perbandingan dilakukan dengan nilai Main.API_KEY, yang diasumsikan telah ditetapkan sebelumnya secara statis.
- Metode ini digunakan untuk melindungi endpoint dari akses yang tidak sah, tanpa perlu sistem login yang kompleks.

Contoh alur:
- Klien (misalnya aplikasi mobile atau web frontend) mengirim request ke server untuk mengambil data voucher.
- Dalam request tersebut, klien menyertakan API key pada header Authorization.
- Server menerima request dan memanggil AuthUtil.isAuthorized(exchange) untuk memverifikasi apakah API key-nya valid.
- Jika API key cocok, request diproses. Jika tidak cocok, server dapat mengembalikan respons error seperti 401 Unauthorized.

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

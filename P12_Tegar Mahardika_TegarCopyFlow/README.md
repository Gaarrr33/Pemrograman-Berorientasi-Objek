# Laporan Praktikum Pemrograman Berorientasi Objek — Modul 12
**Topik:** Penyimpanan dan Pembacaan Data ke File (File I/O & Repository Pattern)  
**Judul Project:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Penyusun:** Tegar Mahardika  
**Dosen Pengampu:** Nirwana Haidar Hari, S.Pd., M.Kom.  
**Institusi:** Politeknik Elektronika Negeri Surabaya (PENS) — PSDKU Sumenep  
**Program Studi:** D3 Teknik Informatika  
**Tahun Ajaran:** 2026  

---

## 1. Tujuan Praktikum

Setelah menyelesaikan praktikum Modul 12 ini, mahasiswa mampu:
1. **Memahami Konsep Dasar Persistensi Data:** Menjelaskan perbedaan fundamental antara memori volatil (*RAM*) dan media penyimpanan persisten (*Non-Volatile Storage / Disk*).
2. **Menerapkan Operasi File I/O Berbasis Teks:** Menggunakan class `BufferedReader`, `BufferedWriter`, `FileReader`, dan `FileWriter` dari Java I/O untuk membaca dan menulis data teks.
3. **Menerapkan Pola Try-With-Resources:** Menjamin penutupan *stream* I/O secara otomatis (*automatic resource management*) demi mencegah *resource leak* dan *file lock* pada sistem operasi.
4. **Melakukan Serialisasi dan Deserialisasi Objek:** Mengonversi representasi *state* objek domain ke dalam baris data terstruktur berbasis delimitator (*CSV-style formatting*) dan mengonstruksinya kembali menjadi objek utuh.
5. **Mengimplementasikan Repository Pattern:** Memisahkan logika manipulasi data bisnis di memori dari mekanisme persistensi fisik ke dalam komponen khusus (`ProdukFileRepository`).
6. **Menerapkan Pertahanan Menyeluruh (*Defensive Programming*):** Memvalidasi integritas data berkas dari anomali format, baris kosong, kerusakan tipe data numerik, dan duplikasi kode sebelum diterapkan ke dalam katalog memori.
7. **Menjamin Integritas State Data (*Atomic Writing & Staging Isolation*):** Mencegah korupsi data saat penulisan melalui berkas sementara (*atomic move*) serta melindungi katalog aktif agar tidak hilang saat proses deserialisasi berkas korup mengalami kegagalan.

---

## 2. Dasar Teori Singkat

### 2.1 Memori Volatil vs Penyimpanan Persisten
Data yang disimpan di dalam struktur data memori (seperti `ArrayList` pada Modul 11) bersifat **volatil**. Ketika aplikasi ditutup atau komputer dimatikan, seluruh state objek di memori RAM akan musnah. Persistensi data ke berkas fisik (*file persistence*) memungkinkan aplikasi menyimpan status terakhir ke media penyimpanan permanen sehingga data dapat dimuat kembali pada sesi berikutnya.

### 2.2 Character Stream & Buffering
Java menyediakan dua hierarki utama I/O: *Byte Stream* (untuk data biner) dan *Character Stream* (untuk data teks). Dalam modul ini digunakan Character Stream:
- `FileWriter` & `FileReader`: Menghubungkan aplikasi langsung dengan berkas di media penyimpanan karakter per karakter.
- `BufferedWriter` & `BufferedReader`: Membungkus *stream* dasar dengan penyangga memori (*buffer*). Buffering secara dramatis mengurangi frekuensi pemanggilan *system call* ke disk, sehingga meningkatkan performa I/O secara signifikan.

```java
// Contoh Pola Penulisan Aman dengan Try-With-Resources
try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
    for (Buku buku : daftarBuku) {
        writer.write(buku.toFileLine());
        writer.newLine();
    }
}
```

### 2.3 Try-With-Resources (Java 7+)
Konstruksi `try-with-resources` menjamin bahwa setiap objek yang mengimplementasikan antarmuka `java.lang.AutoCloseable` akan ditutup secara deterministik pada saat blok `try` berakhir, baik proses berjalan sukses maupun terinterupsi oleh *exception*. Hal ini meniadakan kebutuhan penutupan manual pada blok `finally` yang rawan galat.

### 2.4 Repository Pattern, Atomic Writing, dan Staging Isolation
Dalam rekayasa perangkat lunak modern:
- **Repository Pattern:** Memisahkan *Domain Logic* dari *Persistence Logic*. Domain class (`Produk`, `KatalogProduk`) tidak perlu mengetahui apakah data disimpan di berkas teks, database SQL, atau layanan cloud.
- **Atomic Writing:** Penulisan data langsung ke berkas utama sangat berisiko: jika proses terputus di tengah jalan (misalnya listrik padam atau out-of-disk), berkas lama akan terpotong (*truncated*) dan rusak permanen. Pola *Atomic Writing* menulis data ke berkas sementara (`.tmp`), lalu mengganti berkas utama menggunakan operasi atomik `Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE)`.
- **Staging Isolation (Katalog Sementara):** Saat membaca berkas data masuk, repositori mengumpulkan seluruh objek yang lolos validasi ke dalam *staging list* terlebih dahulu. Jika salah satu baris korup di tengah pembacaan, proses dihentikan seketika (*fail-fast*) tanpa merusak katalog aktif yang sudah ada di memori.

---

## 3. Langkah Praktikum 1 s.d. 5 (Studi Kasus Buku Perpustakaan)

Pada implementasi dasar (`Modul12Dasar.java`), class `BukuModul` dan `PerpustakaanModul` diintegrasikan dengan metode persistensi `simpanKeFile(String namaFile)` dan `muatDariFile(String namaFile)`. Pengujian dilakukan secara menyeluruh mencakup skenario sukses dan skenario defensif (*fault injection*).

### Tabel Hasil Pengujian Studi Kasus Modul 12
| No | Skenario Pengujian | Input / Operasi | Hasil yang Diharapkan | Hasil Aktual Program | Evaluasi / Status |
|:---:|:---|:---|:---|:---|:---:|
| 1 | **Simpan Data Valid** | Tambah `B001` & `B002`, lalu `simpanKeFile("data/buku.txt")` | Berkas `buku.txt` terbentuk dan terisi 2 baris | Berhasil menyimpan 2 buku ke file: data/buku.txt | **VALID (100%)** |
| 2 | **Muat Data Valid** | `perpustakaanBaru.muatDariFile("data/buku.txt")` | Data 2 buku dipulihkan ke memori, koleksi terisi | Berhasil memuat 2 buku. List terisi B001 & B002 | **VALID (100%)** |
| 3 | **Berkas Tidak Ada** | `muatDariFile("data/buku_hilang.txt")` | Menangani `IOException` tanpa *crash* | Tertangkap IOException: File tidak ditemukan | **VALID (Defensif)** |
| 4 | **Format Baris Rusak** | Muat `buku_format_salah.txt` (tanpa koma) | Ditolak `IllegalArgumentException`, baris rusak diidentifikasi | STATUS: Ditolak! Format baris tidak valid pada baris 1 | **VALID (Defensif)** |
| 5 | **Judul Buku Kosong** | Muat `buku_judul_kosong.txt` (`B003,`) | Ditolak validasi entitas `Buku` | STATUS: Ditolak! Judul buku tidak boleh kosong | **VALID (Defensif)** |
| 6 | **Kode Duplikat** | Muat `buku_duplikat.txt` (`B001` muncul 2x) | Ditolak aturan keunikan repositori perpustakaan | STATUS: Ditolak! Buku dengan kode B001 sudah terdaftar | **VALID (Defensif)** |
| 7 | **Berkas Kosong (0 B)** | Muat `buku_kosong.txt` | Koleksi tetap kosong, sistem melaporkan 0 data | Selesai membaca file kosong: 0 buku dimuat | **VALID (Defensif)** |
| 8 | **Uji Konsistensi State** | Muat file rusak saat repositori sudah berisi data awal | Data awal perpustakaan tetap utuh (Staging Isolation) | Berhasil! Koleksi awal tetap utuh 2 buku (Atomic Rollback) | **VALID (Robust)** |

Bukti tangkapan layar eksekusi:  
🖼️ [`Screenshot/Screenshot_1_Modul12_StudiKasus.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P12_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_1_Modul12_StudiKasus.png)

---

## 4. Tugas Proyek Kelompok — Sistem Tegar CopyFlow

Sistem **Tegar CopyFlow** mengelola inventaris toko alat tulis dan fotokopi yang menuntut persistensi data aman antar-sesi kerja kasir.

### 4.1 Format Baris Persistensi Data Produk (10 Kolom)
Setiap objek `Produk` diserialisasi ke dalam baris teks dengan 10 atribut yang dipisahkan oleh tanda koma (*comma delimiter*):
```text
kode,id,nama,tipe,stok,satuan,hargaBeli,hargaJual,minStok,status
```

Contoh baris aktual pada berkas `data/produk.txt`:
```text
PRD-001,101,Kertas HVS A4 70gsm,Barang Fisik,25,rim,45000.00,55000.00,5,Aktif
PRD-002,102,Bolpoin Standard AE7,Barang Fisik,120,pcs,1800.00,2500.00,20,Aktif
PRD-003,103,Spidol Snowman Whiteboard,Barang Fisik,36,lusin,8500.00,10500.00,10,Aktif
SRV-001,201,Jasa Fotokopi Hitam Putih A4,Layanan Jasa,999,lembar,150.00,300.00,0,Aktif
SRV-002,202,Jasa Jilid Spiral Kawat,Layanan Jasa,999,buku,8000.00,15000.00,0,Aktif
```

> **Catatan Teknis Enkoding Angka:**  
> Untuk mencegah kesalahan penguraian (`NumberFormatException`) akibat perbedaan *Locale* sistem operasi (seperti tanda koma desimal `55000,00` pada regional Indonesia), serialisasi atribut numerik `hargaBeli` dan `hargaJual` diwajibkan menggunakan `java.util.Locale.US` (`String.format(Locale.US, "%.2f", nilai)`), serta didukung mekanisme normalisasi `.replace(',', '.')` pada saat deserialisasi.

### 4.2 Class Repository: `ProdukFileRepository`
Implementasi *Repository Pattern* terletak pada class `repository.ProdukFileRepository` dengan dua tanggung jawab utama:

```java
public class ProdukFileRepository {
    private final Path filePath;

    public void simpan(List<Produk> daftarProduk) throws IOException { ... }
    public List<Produk> muat() throws IOException { ... }
}
```

1. **`simpan(List<Produk> daftarProduk)` — Atomic Writing:**
   - Direktori target dipastikan keberadaannya melalui `Files.createDirectories()`.
   - Data ditulis ke berkas sementara dengan ekstensi `.tmp`.
   - Setelah penulisan selesai dan buffer ter-flush sempurna, berkas `.tmp` diganti secara atomik ke berkas utama via `Files.move(..., StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)`.
2. **`muat()` — Staging Isolation & Strict Parsing:**
   - Berkas dibaca baris per baris menggunakan `BufferedReader`.
   - Setiap baris divalidasi memiliki tepat 10 komponen kolom.
   - Dilakukan validasi tipe data numerik (`Integer.parseInt`, `Double.parseDouble`), validasi aturan bisnis (`stok >= 0`, `hargaJual >= 0`), dan verifikasi keunikan kode.
   - Objek yang valid dimasukkan ke `stagingList`. Jika seluruh baris berhasil diproses, `stagingList` dikembalikan untuk menggantikan katalog aktif melalui `katalog.gantiSemua(stagingList)`. Jika ada satu baris korup, method melempar exception dan katalog aktif tetap aman.

---

## 5. Tabel Hasil Pengujian Komprehensif Proyek

Pengujian sistem Tegar CopyFlow dieksekusi melalui class `Main.java` yang terstruktur ke dalam 6 fase komprehensif:

| Fase / Skenario Pengujian | Input / Operasi yang Diuji | Hasil yang Diharapkan | Hasil Aktual Program | Status |
|---|---|---|---|:---:|
| **Tahap 1: Inisialisasi & Simpan** | Inisialisasi 5 master produk, simpan ke `data/produk.txt` via Atomic Write | File `produk.txt` terbentuk tanpa galat, data 5 produk tersimpan | Berhasil menyimpan 5 produk ke file 'data/produk.txt' secara atomik | **SUKSES** |
| **Tahap 2: Sesi Baru & Muat Data** | Instansiasi katalog baru, muat dari `data/produk.txt` | Seluruh 5 produk ter-deserialize lengkap beserta seluruh atribut | Berhasil memuat 5 produk ke memori. Total Nilai Inventaris: Rp 1.646.000 | **SUKSES** |
| **Tahap 3a: File Tidak Ditemukan** | Muat `data/produk_hilang.txt` | `FileNotFoundException` tertangkap, sistem tidak crash | Tertangkap IOException: File tidak ditemukan: data/produk_hilang.txt | **SUKSES** |
| **Tahap 3b: Baris Rusak (< 10 Kolom)**| Muat `data/produk_format_salah.txt` (hanya 3 kolom) | Ditolak `IllegalArgumentException` dengan keterangan baris | STATUS: Ditolak! Format kolom tidak lengkap (ditemukan 3, wajib 10) | **SUKSES** |
| **Tahap 3c: Nilai Angka Rusak** | Muat `data/produk_angka_rusak.txt` (`"seratus"` pada stok)| Ditolak `IllegalArgumentException` pembungkus `NumberFormatException` | STATUS: Ditolak! Format numerik tidak valid pada baris 1 | **SUKSES** |
| **Tahap 3d: Nilai Negatif** | Muat `data/produk_harga_negatif.txt` (`harga = -5000`) | Ditolak aturan invariant `Produk` | STATUS: Ditolak! Nilai numerik tidak boleh bernilai negatif | **SUKSES** |
| **Tahap 3e: Kode Duplikat** | Muat `data/produk_duplikat.txt` (`PRD-001` muncul 2x) | Ditolak aturan keunikan repositori | STATUS: Ditolak! Kode produk duplikat 'PRD-001' ditemukan | **SUKSES** |
| **Tahap 3f: Berkas Kosong** | Muat `data/produk_kosong.txt` (0 byte) | Berhasil dimuat, mengembalikan list kosong (0 produk) | Selesai membaca file kosong: 0 produk dimuat | **SUKSES** |
| **Tahap 4: Uji Konsistensi State** | Muat file corrupt ke katalog aktif yang sudah berisi 5 produk | Deserialisasi gagal, 5 produk awal di memori TIDAK terhapus/rusak | STATUS: SANGAT AMAN! 5 produk aktif tetap utuh berkat Staging Isolation | **SUKSES** |
| **Tahap 5: Integrasi Transaksi POS**| Beli 5 rim HVS & 20 bolpoin, simpan status mutasi ke disk | Stok terpotong, struk tercetak, data disk terbarui | Struk TRX-20261111-0001 tercetak, file produk.txt terbarui | **SUKSES** |
| **Tahap 6: Verifikasi Persistensi** | Sesi baru membaca `produk.txt` pasca transaksi kasir | Sisa stok HVS terbaca 20 rim dan Bolpoin 100 pcs | Terverifikasi: Stok HVS = 20 rim, Bolpoin = 100 pcs (Permanen di Disk) | **SUKSES** |

Bukti tangkapan layar eksekusi:  
🖼️ [`Screenshot/Screenshot_2_TegarCopyFlow_SimpanMuat.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P12_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_2_TegarCopyFlow_SimpanMuat.png)  
🖼️ [`Screenshot/Screenshot_3_TegarCopyFlow_DefensiveTesting.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P12_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_3_TegarCopyFlow_DefensiveTesting.png)  
🖼️ [`Screenshot/Screenshot_4_TegarCopyFlow_TransaksiIntegrasi.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P12_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_4_TegarCopyFlow_TransaksiIntegrasi.png)

---

## 6. Diagram UML Class Diagram Modul 12

Diagram kelas arsitektur Modul 12 menunjukkan implementasi *Repository Pattern* di mana class `ProdukFileRepository` bertanggung jawab menjembatani persistensi antara berkas fisik (`produk.txt`) dan `KatalogProduk`:

```text
+-----------------------+              1..* +----------------------------------------------------+
|     KatalogProduk     | 1 --------------> |                       Produk                       |
+-----------------------+                   +----------------------------------------------------+
| - daftarProduk: List  |                   | - kode: String <<unique>>                          |
+-----------------------+                   | - id: int                                          |
| + tambahProduk(...)   |                   | - nama: String                                     |
| + cariProduk(...)     |                   | - tipe: String                                     |
| + gantiSemua(List)    |                   | - stok: int                                        |
| + getDaftarProduk()   |                   | - satuan: String                                   |
| + tampilkanKatalog()  |                   | - hargaBeli: double                                |
+-----------------------+                   | - hargaJual: double                                |
            ^                               | - minStok: int                                     |
            | updates                       | - status: String                                   |
+-----------------------+                   +----------------------------------------------------+
|  ProdukFileRepository |                   | + toFileLine(): String                             |
+-----------------------+                   | + fromFileLine(String): Produk                     |
| - filePath: Path      |                   | + kurangiStok(int)                                 |
+-----------------------+                   | + getKode(): String                                |
| + simpan(List<Produk>)|                   +----------------------------------------------------+
| + muat(): List<Produk>|                                             ^
+-----------------------+                                             | references
                                            +----------------------------------------------------+
                                            |                  DetailTransaksi                   |
                                            +----------------------------------------------------+
```

Berkas gambar diagram beresolusi tinggi tersedia di:  
🖼️ [`UML/ClassDiagram_P12.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P12_Tegar%20Mahardika_TegarCopyFlow/UML/ClassDiagram_P12.png)

---

## 7. Jawaban Pertanyaan Evaluasi (Bab 8 Modul)

Berikut adalah analisis mendalam untuk menjawab empat pertanyaan evaluasi praktikum pada Bagian 8 Modul 12:

### 1. Mengapa penutupan resource seperti file penting dilakukan? Apa akibatnya jika file tidak ditutup?
Penutupan resource I/O berkas sangat krusial karena berkas merupakan sumber daya sistem operasi (*OS-level resource*) yang dialokasikan melalui *file descriptor* atau *file handle*. Akibat jika file tidak ditutup:
1. **Kebocoran Sumber Daya Sistem (*Resource Leak*):** Setiap sistem operasi memiliki batas maksimal *file descriptor* terbuka secara simultan. Jika aplikasi terus membuka stream tanpa menutupnya, sistem operasi akan kehabisan descriptor (*Too many open files error*), yang mengakibatkan sistem gagal membuka berkas lain.
2. **Kehilangan Data Akibat Unflushed Buffer (*Data Loss*):** `BufferedWriter` mengumpulkan data di RAM sebelum menuliskannya secara fisik ke piringan disk. Operasi penulisan sesungguhnya seringkali baru terjadi saat buffer penuh atau saat stream ditutup (`close()`), yang secara implisit memicu `flush()`. Jika stream tidak ditutup dan program berhenti mendadak, data yang masih berada di dalam buffer RAM akan hilang seketika dan berkas di disk menjadi kosong atau terpotong.
3. **Kuncian Berkas (*File Locking*):** Pada sistem operasi tertentu (terutama Microsoft Windows), berkas yang terbuka akan dikunci secara eksklusif oleh proses JVM. Akibatnya, berkas tersebut tidak dapat dipindahkan, diganti namanya, dihapus, atau diakses oleh thread/proses aplikasi lain sampai JVM dimatikan.

### 2. Apa keuntungan menggunakan `try-with-resources` dibanding blok `try-catch-finally` biasa?
Konstruksi `try-with-resources` (diperkenalkan sejak Java 7) memberikan sejumlah keunggulan utama:
1. **Jaminan Penutupan Otomatis (*Automatic Resource Cleanup*):** Semua resource yang mengimplementasikan antarmuka `AutoCloseable` secara otomatis ditutup saat blok `try` berakhir, terlepas dari apakah eksekusi normal, melempar exception, atau menjalankan `return`.
2. **Mencegah Bug Penutupan Manual di Blok Finally:** Pada pola `finally` konvensional, penutupan resource memerlukan pengecekan `null` dan blok `try-catch` tersarang (*nested try-catch*) di dalam `finally` karena method `close()` sendiri dapat melempar `IOException`. Hal ini seringkali dilupakan pengembang sehingga penutupan resource gagal dieksekusi.
3. **Penanganan *Suppressed Exceptions*:** Jika terjadi exception pada blok `try` dan terjadi pula exception kedua saat proses `close()`, pola `finally` manual akan menimpa (*swallow*) exception utama dengan exception penutupan. Sebaliknya, `try-with-resources` mempertahankan exception utama sebagai penyebab inti (*root cause*) dan menyematkan exception penutupan sebagai *suppressed exception* (`Throwable.getSuppressed()`), sehingga memudahkan pelacakan bug (*debugging*).
4. **Kode Lebih Ringkas dan Mudah Dibaca (*Boilerplate Reduction*):** Mengeliminasi puluhan baris kode seremonial yang hanya bertugas menutup stream.

### 3. Apa yang terjadi jika file yang dibaca memiliki format yang tidak sesuai (misal: jumlah kolom kurang atau tipe data salah)? Bagaimana cara menanganinya secara aman?
- **Jika format tidak sesuai:**
  - *Jumlah kolom kurang:* Mengakses indeks array hasil `split(",")` akan memicu `ArrayIndexOutOfBoundsException`.
  - *Tipe data salah:* Mengonversi string teks ke tipe data numerik (seperti mem-parsing huruf ke `Integer.parseInt` atau `Double.parseDouble`) akan melempar `NumberFormatException`.
  - Tanpa penanganan, exception yang tidak tertangkap (*unhandled runtime exception*) akan menyebabkan aplikasi mengalami *crash* mendadak di tengah jalan dan membiarkan data aplikasi dalam kondisi tidak konsisten.
- **Strategi Penanganan Aman (*Defensive Parsing*):**
  1. **Validasi Jumlah Kolom (*Column Guard*):** Lakukan pengecekan panjang array hasil pemisahan delimitator sebelum membaca nilainya:  
     ```java
     String[] parts = line.split(",");
     if (parts.length != 10) {
         throw new IllegalArgumentException("Format baris tidak valid pada baris " + nomorBaris);
     }
     ```
  2. **Validasi Tipe Data Spesifik:** Bungkus operasi konversi tipe numerik dengan blok penanganan terisolasi atau tangkap `NumberFormatException`, lalu transformasikan ke dalam pesan galat yang informatif yang mencantumkan nomor baris dan nilai input yang rusak.
  3. **Abaikan Baris Kosong:** Lakukan *trim* pada setiap baris dan lewati baris yang tidak memiliki karakter (`if (line.trim().isEmpty()) continue;`) untuk mengantisipasi baris baru di akhir berkas.
  4. **Pola Staging Isolation:** Kumpulkan objek-objek hasil pembacaan ke dalam list sementara. Jangan langsung memodifikasi koleksi master di memori sebelum seluruh baris berkas terverifikasi valid 100%.

### 4. Mengapa pada aplikasi nyata penyimpanan data sering dipisahkan ke class tersendiri (seperti konsep Repository)?
Pemisahan mekanisme penyimpanan data ke dalam class *Repository* merupakan penerapan prinsip arsitektur perangkat lunak yang matang (*Separation of Concerns* dan *Single Responsibility Principle*):
1. **Pemisahan Tanggung Jawab (*Separation of Concerns*):** Class domain bisnis (seperti `Produk` atau `KatalogProduk`) hanya fokus pada logika bisnis, perhitungan stok, dan validasi status. Class domain tidak boleh dibebani dengan detail teknis I/O berkas, jalur direktori, parsing teks, atau koneksi database.
2. **Kemudahan Pengujian (*Testability & Mocking*):** Dengan mengisolasi akses I/O ke dalam class repository, pengembang dapat dengan mudah membuat repositori tiruan (*mock repository*) saat melakukan unit testing logika bisnis tanpa harus membaca atau menulis berkas fisik di disk.
3. **Fleksibilitas Perubahan Media Persistensi (*Maintainability & Loose Coupling*):** Jika suatu saat sistem ingin beralih dari berkas teks `.txt` ke format JSON, database SQLite, PostgreSQL, atau REST API cloud, perubahan hanya perlu dilakukan pada class Repository tanpa mengubah satu baris pun kode pada entitas domain dan antarmuka kasir.
4. **Sentralisasi Keamanan Data:** Logika transaksi atomik (*Atomic Writing*), enkripsi data, pencadangan (*backup*), dan penanganan konkurensi dapat dipusatkan di satu tempat, sehingga mengurangi duplikasi kode di berbagai penjuru sistem.

---

## 8. Panduan Kompilasi dan Eksekusi Program

### Prasyarat
- Java Development Kit (JDK) versi 17 atau lebih baru (telah diuji pada Java 25 LTS).

### Perintah Terminal / PowerShell
Masuk ke direktori `SourceCode`:
```powershell
# 1. Masuk ke direktori SourceCode
cd "P12_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Kompilasi program studi kasus dasar Modul 12 (Langkah 1 s.d. 5)
javac -encoding UTF-8 -d bin src/Modul12Dasar.java

# 3. Jalankan pengujian studi kasus dasar
java -cp bin Modul12Dasar

# 4. Kompilasi seluruh arsitektur proyek Tegar CopyFlow (Model, Exception, Repository, dan Main)
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/repository/*.java src/Main.java

# 5. Jalankan pengujian komprehensif proyek Tegar CopyFlow (6 Tahap Pengujian)
java -cp bin Main
```

---

## 9. Struktur Berkas Repositori

```text
P12_Tegar Mahardika_TegarCopyFlow/
├── Laporan Praktikum_Pertemuan_12.pdf      # Dokumen PDF Resmi Laporan Praktikum Modul 12
├── README.md                               # Laporan Lengkap Praktikum (Markdown)
├── tools_generate_assets.py                # Utilitas otomasi pengujian, gambar terminal & rendering PDF
├── UML/
│   └── ClassDiagram_P12.png                # Diagram UML Class Terkini dengan Repository Pattern
├── Screenshot/
│   ├── Screenshot_1_Modul12_StudiKasus.png
│   ├── Screenshot_2_TegarCopyFlow_SimpanMuat.png
│   ├── Screenshot_3_TegarCopyFlow_DefensiveTesting.png
│   └── Screenshot_4_TegarCopyFlow_TransaksiIntegrasi.png
└── SourceCode/
    ├── bin/                                # Direktori bytecode hasil kompilasi (.class)
    ├── data/                               # Direktori penyimpanan berkas data teks (.txt)
    │   ├── buku.txt
    │   ├── buku_duplikat.txt
    │   ├── buku_format_salah.txt
    │   ├── buku_judul_kosong.txt
    │   ├── buku_kosong.txt
    │   ├── produk.txt
    │   ├── produk_angka_rusak.txt
    │   ├── produk_duplikat.txt
    │   ├── produk_format_salah.txt
    │   ├── produk_harga_negatif.txt
    │   └── produk_kosong.txt
    ├── hasil-pengujian.txt                 # Log lengkap hasil pengujian teks murni UTF-8
    ├── hasil pengujian.pdf                 # Salinan PDF laporan pengujian
    ├── README.md                           # Panduan teknis kompilasi source code
    └── src/
        ├── Modul12Dasar.java               # Kode implementasi Langkah 1-5 Modul 12
        ├── Main.java                       # Main test runner proyek Tegar CopyFlow (6 Tahap)
        ├── exception/
        │   ├── StokTidakCukupException.java
        │   └── TransaksiInvalidException.java
        ├── model/
        │   ├── BatchStok.java
        │   ├── DetailTransaksi.java
        │   ├── KatalogProduk.java          # Aggregator koleksi List<Produk> dengan atomic swap
        │   ├── Produk.java                 # Entitas produk lengkap dengan serialisasi toFileLine()
        │   └── TransaksiPenjualan.java     # Agregator transaksi penjualan kasir POS
        └── repository/
            └── ProdukFileRepository.java   # Repository Pattern: Atomic Writing & Staging Isolation
```


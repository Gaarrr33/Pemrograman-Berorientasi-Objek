# Laporan Praktikum Pemrograman Berorientasi Objek — Modul 11
**Topik:** Collection dan Pengelolaan Kumpulan Objek (ArrayList & Generics)  
**Judul Project:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Penyusun:** Tegar Mahardika  
**Dosen Pengampu:** Nirwana Haidar Hari, S.Pd., M.Kom.  
**Institusi:** Politeknik Elektronika Negeri Surabaya (PENS) — PSDKU Sumenep  
**Program Studi:** D3 Teknik Informatika  
**Tahun Ajaran:** 2026  

---

## 1. Tujuan Praktikum

Setelah menyelesaikan praktikum Modul 11 ini, mahasiswa mampu:
1. **Menyimpan Kumpulan Objek Menggunakan ArrayList:** Memanfaatkan class `ArrayList` dari Java Collection Framework untuk mengelola sekumpulan objek domain secara dinamis.
2. **Menguasai Operasi Dasar Collection:** Mengimplementasikan secara mandiri operasi penambahan (`add`), pencarian (`search/lookup`), penampilan (*traversing*), dan penghapusan (`remove`) objek.
3. **Menerapkan Generics dan Perulangan For-Each:** Menjamin keamanan tipe data (*type-safety*) saat kompilasi menggunakan generics `<T>` serta melakukan iterasi koleksi secara bersih tanpa *type casting*.
4. **Menjaga Validitas dan Integritas Data Koleksi:** Menolak penambahan data duplikat, objek `null`, dan data berstatus invalid sebelum masuk ke dalam koleksi (*fail-fast principle*).
5. **Melindungi Enkapsulasi Collection:** Mengisolasi koleksi internal dari modifikasi langsung oleh pihak luar menggunakan class pengelola (*manager class*) dan referensi *unmodifiable collection*.

---

## 2. Dasar Teori Singkat & Operasi List

Sebagian besar program aplikasi mengelola banyak objek, seperti daftar buku, katalog produk, atau riwayat transaksi kasir. `ArrayList` adalah struktur data berbasis array dinamis yang ukurannya dapat bertambah secara otomatis saat elemen baru dimasukkan.

### Interface `List` vs Implementasi `ArrayList`
```java
List<Produk> daftarProduk = new ArrayList<>();
```
- `List` adalah sebuah *interface* yang mendefinisikan kontrak operasi kumpulan objek terurut.
- `ArrayList` adalah implementasi konkret berbasis struktur data array yang dapat diubah ukurannya (*resizable array*).
- `<Produk>` (*Generics*) menyatakan bahwa koleksi hanya menyimpan referensi objek bertipe `Produk`.

### Tabel Operasi Dasar Collection Java
| Operasi | Fungsi Utama | Peran dalam Arsitektur Perangkat Lunak |
|---|---|---|
| **`add(objek)`** | Menambahkan objek ke posisi akhir list. | Digunakan setelah objek divalidasi tidak null dan kodenya belum terdaftar. |
| **`get(index)`** | Mengambil elemen berdasarkan posisi indeks numerik (mulai dari 0). | Akses data berkecepatan $O(1)$, namun indeks bersifat posisional dan dapat bergeser jika ada data yang dihapus. |
| **`remove(objek)`** | Menghapus kemunculan pertama objek yang cocok dari list. | Menghapus referensi objek dari memori koleksi dan merapatkan kembali indeks elemen di kanannya. |
| **`size()`** | Menghitung jumlah elemen aktual di dalam koleksi. | Memantau kapasitas data yang tersimpan secara real-time. |
| **`isEmpty()`** | Memeriksa apakah koleksi tidak memiliki elemen sama sekali. | Guard condition sebelum perulangan untuk menghindari pemrosesan data kosong. |

> **Catatan Desain Penting:**  
> `ArrayList` secara default mengizinkan elemen duplikat. Oleh karena itu, aturan integritas bisnis (seperti keunikan kode buku atau kode produk) harus ditegakkan secara eksplisit oleh class pengelola melalui pengecekan sebelum `add()` dipanggil.

---

## 3. Langkah Praktikum 1 s.d. 3 (Studi Kasus Buku Perpustakaan)

Pada bagian awal modul (Langkah 1–3), diimplementasikan class `Buku` dan `Perpustakaan`. Pengujian dijalankan secara berurutan pada satu objek `Perpustakaan` yang sama, dilengkapi dengan pengujian skenario tambahan (judul kosong, objek null, dan koleksi kosong) sesuai arahan modul halaman 5.

### Tabel Hasil Pengujian Berurutan
| No | Skenario Pengujian | Input / Operasi | Hasil yang Diharapkan | Hasil Aktual Program | Jumlah Data Akhir |
|:---:|:---|:---|:---|:---|:---:|
| 1 | **Tambah Buku Awal** | Tambah `B001` ("Dasar Java") & `B002` ("Konsep OOP") | Kedua buku tersimpan ke dalam list | Kedua buku berhasil tersimpan. | **2** |
| 2 | **Cari Buku Valid** | `cariBuku("B002")` | Judul "Konsep OOP" ditemukan | Ditemukan: Konsep OOP | **2** |
| 3 | **Uji Kode Duplikat** | Tambah `B001` ("Buku Duplikat") | Ditolak `IllegalArgumentException` | Ditolak: Kode buku sudah terdaftar. | **2** *(Tetap)* |
| 4 | **Hapus Buku Terdaftar** | `hapusBuku("B001")` | Menghasilkan `true`, data berkurang | Hasil penghapusan B001: true | **1** |
| 5 | **Cari Buku Terhapus** | `cariBuku("B001")` | Menghasilkan `null` | null (Buku tidak ditemukan) | **1** |
| 6 | **Hapus Kode Fiktif** | `hapusBuku("B999")` | Menghasilkan `false`, data tetap | Hasil penghapusan B999: false | **1** |
| 7 | **Uji Judul Kosong** | `new BukuModul("B003", "   ")` | Ditolak `IllegalArgumentException` | Ditolak: Kode dan judul wajib diisi. | **1** |
| 8 | **Uji Objek Null** | `perpustakaan.tambahBuku(null)` | Ditolak `IllegalArgumentException` | Ditolak: Objek buku tidak boleh null. | **1** |
| 9 | **Tampilkan List Kosong**| Hapus `B002`, lalu `tampilkanBuku()` | Menampilkan pesan koleksi kosong | Memanggil tampilkanBuku() -> Koleksi masih kosong. | **0** |

---

## 4. Tugas Proyek Kelompok — Sistem Tegar CopyFlow

Melanjutkan arsitektur proyek kasir dan inventaris toko **Tegar CopyFlow**, dibangun class pengelola kumpulan objek bernama `KatalogProduk`.

### 4.1 Identitas Unik Produk (Kode Produk)
Setiap entitas `Produk` memiliki atribut identitas unik tetap:
```java
private final String kode; // Contoh: "PRD-001", "PRD-002", "SRV-001"
```
Kode produk dijamin unik di seluruh sistem, tidak dapat diubah setelah instansiasi (*immutable*), dan divalidasi tidak boleh null maupun berupa string kosong.

### 4.2 Class Pengelola: `KatalogProduk`
Class `KatalogProduk` mengelola koleksi internal `private final List<Produk> daftarProduk = new ArrayList<>();` dengan tanggung jawab (*Responsibility-Driven Design*):
1. **`tambahProduk(Produk produk)`:**  
   Memeriksa apakah `produk != null` dan melakukan pencarian `cariProduk(produk.getKode()) == null`. Jika kode sudah terdaftar, melempar `IllegalArgumentException` sehingga isi list tidak bertambah (*fail-fast*).
2. **`cariProduk(String kode)`:**  
   Melakukan penelusuran for-each pada `daftarProduk` untuk mencocokkan kode secara *case-insensitive*. Mengembalikan referensi objek `Produk` jika ditemukan, atau `null` jika tidak ditemukan.
3. **`hapusProduk(String kode)`:**  
   Mencari produk berdasarkan kode. Jika ditemukan, menghapus objek dari koleksi menggunakan `daftarProduk.remove(produk)` dan mengembalikan `true`. Jika tidak ada, mengembalikan `false`.
4. **`tampilkanKatalog()`:**  
   Menampilkan daftar lengkap seluruh produk toko dalam bentuk tabel ASCII profesional (Kode, ID, Nama, Tipe, Stok Fisik, Status Stok, Harga Jual, dan Nilai Aset Inventaris).
5. **`getDaftarProduk()`:**  
   Mengembalikan `Collections.unmodifiableList(daftarProduk)` untuk menjamin proteksi enkapsulasi, sehingga pihak luar tidak dapat memodifikasi isi list secara langsung.

---

## 5. Tabel Hasil Pengujian Komprehensif Proyek

Pengujian proyek Tegar CopyFlow terdiri dari 10 skenario lengkap (melampaui syarat minimal 6 skenario) yang mencakup seluruh operasi CRUD, penolakan kode duplikat, integritas enkapsulasi, dan integrasi transaksi:

| Skenario Pengujian | Input / Operasi yang Diuji | Hasil yang Diharapkan | Hasil Aktual Program | Jumlah Data Akhir |
|---|---|---|---|:---:|
| **Skenario 1: Katalog Kosong** | `tampilkanKatalog()` sebelum ada produk | Menampilkan pesan katalog masih kosong | Status Katalog Awal: Katalog produk toko masih kosong. | **0** |
| **Skenario 2: Tambah 5 Produk** | Tambah `PRD-001`, `PRD-002`, `PRD-003`, `SRV-001`, `SRV-002` | Kelima produk fisik dan layanan jasa tersimpan ke list | Berhasil menambahkan 5 produk ke dalam koleksi. | **5** |
| **Skenario 3: Tampilkan List** | `tampilkanKatalog()` saat terisi | Tabel produk dan total inventaris toko tercetak rapi | Tabel tercetak (Total Nilai Inventaris: Rp 1.388.000) | **5** |
| **Skenario 4: Cari Produk Valid** | `cariProduk("PRD-001")` | Objek Kertas HVS ditemukan beserta sisa stoknya | Ditemukan! Detail: `[PRD-001 \| Kertas HVS...]`, Stok: 25 rim | **5** |
| **Skenario 5: Cari Kode Fiktif** | `cariProduk("PRD-999")` | Mengembalikan `null` (tidak ditemukan) | Hasil: null (Produk tidak ditemukan) | **5** *(Tetap)* |
| **Skenario 6: Uji Kode Duplikat** | `tambahProduk("PRD-001" baru)` | Ditolak `IllegalArgumentException`, list tidak berubah | STATUS: BERHASIL DICEGAH! *"Produk dengan kode 'PRD-001' sudah terdaftar..."* | **5** *(Tetap)* |
| **Skenario 7a: Input Null** | `tambahProduk(null)` | Ditolak `IllegalArgumentException` | STATUS: BERHASIL DICEGAH! *"Objek produk tidak boleh null."* | **5** *(Tetap)* |
| **Skenario 7b: Kode Kosong** | `new Produk("   ", ...)` | Ditolak `IllegalArgumentException` | STATUS: BERHASIL DICEGAH! *"Kode produk tidak boleh kosong."* | **5** *(Tetap)* |
| **Skenario 8a: Hapus Produk Ada** | `hapusProduk("PRD-003")` | Menghasilkan `true`, Spidol Snowman terhapus dari list | Hasil hapus PRD-003: true (Berhasil dihapus dari list) | **4** |
| **Skenario 8b: Hapus Kode Fiktif** | `hapusProduk("PRD-999")` | Menghasilkan `false`, tidak ada data terhapus | Hasil hapus PRD-999: false (Tidak ada data terhapus) | **4** *(Tetap)* |
| **Skenario 9: Proteksi Enkapsulasi** | `getDaftarProduk().clear()` | Ditolak `UnsupportedOperationException` | STATUS: BERHASIL DIPROTEKSI! (Koleksi bersifat Read-Only) | **4** *(Tetap)* |
| **Skenario 10: Integrasi POS** | Transaksi kasir mengambil produk via `cariProduk()` | Struk belanja tercetak, stok terpotong aman | Struk TRX-20261111-0001 tercetak rapi (Total: Rp 380.000) | **4** |

---

## 6. Diagram UML Class Diagram Modul 11

Diagram UML Class Diagram telah diperbarui untuk merefleksikan integrasi class pengelola `KatalogProduk` yang memiliki relasi agregasi/komposisi satu-ke-banyak ($1 \longrightarrow *$) dengan `Produk`:

```text
+-----------------------+              1..* +----------------------------------------------------+
|     KatalogProduk     | 1 --------------> |                       Produk                       |
+-----------------------+                   +----------------------------------------------------+
| - daftarProduk: List  |                   | - kode: String <<unique>>                          |
+-----------------------+                   | - id: int                                          |
| + tambahProduk(...)   |                   | - nama: String                                     |
| + cariProduk(...)     |                   | - tipe: String                                     |
| + hapusProduk(...)    |                   | - daftarBatch: List<BatchStok>                     |
| + tampilkanKatalog()  |                   +----------------------------------------------------+
| + getDaftarProduk()   |                   | + alokasikanStokFifo(...)                          |
+-----------------------+                   | + getKode(): String                                |
                                            +----------------------------------------------------+
```

Berkas gambar diagram beresolusi tinggi tersedia di:  
🖼️ [`UML/ClassDiagram_P11.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P11_Tegar%20Mahardika_TegarCopyFlow/UML/ClassDiagram_P11.png)

---

## 7. Jawaban Pertanyaan Evaluasi (Bab 8 Modul)

### 1. Apa perbedaan `Buku` dan `List<Buku>`?
- **`Buku`:** Merupakan representasi dari sebuah entitas tunggal (*single domain object*) yang memodelkan karakteristik dan perilaku dari satu unit buku (seperti atribut `kode` dan `judul`).
- **`List<Buku>`:** Merupakan struktur data koleksi (*collection container*) yang menampung sekumpulan referensi objek-objek `Buku`. `List<Buku>` menyediakan metode pengelolaan kumpulan data secara dinamis, seperti menambah, mencari, menghitung ukuran koleksi, mengiterasi data, dan menghapus elemen.

### 2. Mengapa indeks list kurang tepat dijadikan identitas tetap sebuah buku?
Indeks pada list bersifat **relatif terhadap urutan posisi saat itu**, bukan identitas intrinsik dari data objek tersebut. Jika terjadi operasi penghapusan (`remove`) atau penyisipan elemen di tengah koleksi, maka seluruh objek yang berada setelah elemen tersebut akan mengalami pergeseran indeks (*index shifting*). Akibatnya, buku yang tadinya berada di indeks ke-2 dapat berpindah menjadi indeks ke-1. Jika sistem mengandalkan indeks list sebagai ID transaksi atau peminjaman, maka referensi data akan tertukar dan merusak konsistensi basis data. Oleh sebab itu, identitas unik wajib menggunakan atribut permanen seperti `kode` atau `ISBN`.

### 3. Mengapa pengecekan kode duplikat dilakukan sebelum `add()`?
Pengecekan kode duplikat dilakukan sebelum `add()` untuk menerapkan prinsip **Fail-Fast** dan **menjamin integritas data koleksi**. Secara default, implementasi `ArrayList` mengizinkan duplikasi objek. Jika objek duplikat terlanjur ditambahkan ke dalam list, maka operasi bisnis berikutnya (seperti pencarian berdasarkan kode) akan menjadi ambigu karena terdapat lebih dari satu objek dengan identitas yang sama. Dengan memverifikasi keunikan kode terlebih dahulu melalui method `cariBuku()` atau `cariProduk()`, sistem dapat langsung menolak operasi ilegal sebelum mutasi state terjadi.

### 4. Apa risiko jika collection internal dapat diubah langsung oleh class lain?
Jika variabel koleksi internal diberi visibilitas selain `private` atau getter mengembalikan referensi list aslinya tanpa proteksi:
1. **Pembobolan Aturan Bisnis (*Bypassing Business Invariants*):** Class eksternal dapat memanggil `list.add(item)` secara langsung tanpa melalui method `tambahProduk()`, sehingga validasi objek null dan penolakan kode duplikat dapat dilewati.
2. **Risiko Kerusakan Data Tak Terduga:** Pihak luar dapat mengeksekusi operasi destruktif secara tidak sengaja (misalnya memanggil `list.clear()`), yang menyebabkan hilangnya seluruh data master produk dari memori.
3. **Kehilangan Sinkronisasi State:** Class pengelola tidak dapat melacak mutasi yang terjadi di luar method resminya, sehingga total nilai inventaris atau perhitungan stok tidak lagi akurat.  
Solusinya: koleksi internal wajib berstatus `private final` dan method pengakses getter wajib mengembalikan `Collections.unmodifiableList()`.

---

## 8. Panduan Kompilasi dan Eksekusi Program

### Prasyarat
- Java Development Kit (JDK) versi 17 atau lebih baru (telah diuji pada Java 25 LTS).

### Perintah Terminal / PowerShell
Masuk ke direktori `SourceCode`:
```powershell
# 1. Masuk ke direktori SourceCode
cd "P11_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Kompilasi program studi kasus dasar Modul 11 (Langkah 1 s.d. 3)
javac -encoding UTF-8 -d bin src/Modul11Dasar.java

# 3. Jalankan pengujian studi kasus dasar
java -cp bin Modul11Dasar

# 4. Kompilasi seluruh arsitektur proyek Tegar CopyFlow (Model, Exception, dan Main)
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/Main.java

# 5. Jalankan pengujian komprehensif proyek Tegar CopyFlow
java -cp bin Main
```

---

## 9. Struktur Berkas Repositori

```text
P11_Tegar Mahardika_TegarCopyFlow/
├── Laporan Praktikum_Pertemuan_11.pdf      # Dokumen PDF Resmi Modul 11
├── README.md                               # Laporan Lengkap Praktikum (Markdown)
├── tools_generate_assets.py                # Utilitas otomasi pengujian, gambar & rendering PDF
├── UML/
│   └── ClassDiagram_P11.png                # Diagram UML Class Terkini dengan KatalogProduk
├── Screenshot/
│   ├── Screenshot_1_Modul11_StudiKasus.png
│   ├── Screenshot_2_TegarCopyFlow_KatalogProduk.png
│   ├── Screenshot_3_TegarCopyFlow_PengujianCollection.png
│   └── Screenshot_4_TegarCopyFlow_TransaksiIntegrasi.png
└── SourceCode/
    ├── bin/                                # Direktori bytecode hasil kompilasi (.class)
    ├── hasil-pengujian.txt                 # Log lengkap hasil pengujian teks murni UTF-8
    ├── hasil pengujian.pdf                 # Salinan PDF laporan pengujian
    ├── README.md                           # Panduan teknis kompilasi source code
    └── src/
        ├── Modul11Dasar.java               # Kode implementasi Langkah 1-3 Modul 11
        ├── Main.java                       # Main test runner proyek Tegar CopyFlow
        ├── exception/
        │   ├── StokTidakCukupException.java
        │   └── TransaksiInvalidException.java
        └── model/
            ├── BatchStok.java
            ├── DetailTransaksi.java
            ├── KatalogProduk.java          # Class pengelola kumpulan objek List<Produk>
            ├── Produk.java                 # Entitas produk dengan identitas unik kode
            └── TransaksiPenjualan.java
```


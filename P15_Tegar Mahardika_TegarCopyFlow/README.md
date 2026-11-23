# Laporan Praktikum Pemrograman Berorientasi Objek — Modul 15
**Topik:** Demonstrasi Proyek dan Evaluasi Pemahaman Individu  
**Judul Project:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Penyusun:** Tegar Mahardika  
**Dosen Pengampu:** Nirwana Haidar Hari, S.Pd., M.Kom.  
**Institusi:** Politeknik Elektronika Negeri Surabaya (PENS) — PSDKU Sumenep  
**Program Studi:** D3 Teknik Informatika  
**Tahun Ajaran:** 2026  

---

## 1. Tujuan Praktikum

Setelah menyelesaikan praktikum Modul 15 ini, mahasiswa mampu:
1. **Mendemonstrasikan Aplikasi Sesuai Kebutuhan Kasus:** Menyajikan alur operasional sistem kasir POS dan inventaris Tegar CopyFlow secara langsung, mencakup penambahan produk, alokasi stok FIFO kasir POS, pencetakan struk, dan persistensi berkas fisik.
2. **Menjelaskan Penerapan Prinsip OOP Berdasarkan Source Code:** Menguraikan secara mendalam implementasi Encapsulation, Relasi Objek, Validasi Domain Invariant, Custom Exception, Collection Management, dan File I/O.
3. **Mempertanggungjawabkan Keputusan Desain (*Architectural Rationale*):** Menjelaskan landasan rekayasa di balik penerapan Repository Pattern, Dirty Flag Protection, Atomic Writing, Staging Isolation, dan unmodifiable collection encapsulation.
4. **Menunjukkan Pemahaman Individu melalui Live Coding (*Perubahan Terarah*):** Menerapkan modifikasi kode secara mandiri sesuai skenario evaluasi dosen penguji, menganalisis dampaknya, dan memvalidasi hasilnya.
5. **Menyusun Refleksi dan Rencana Tindak Lanjut:** Mendokumentasikan kontribusi mandiri, catatan masukan evaluasi dosen, dan rencana penyempurnaan sistem di masa mendatang.

---

## 2. Prinsip Evaluasi & Rubrik Penilaian

Evaluasi proyek pada Modul 15 mencakup produk kelompok dan penguasaan individu:

| Komponen Evaluasi | Bobot | Bukti Pemeriksaan Spesifik dalam Tegar CopyFlow |
|---|:---:|---|
| **Fungsionalitas & Konsistensi Data** | 25% | Fitur CRUD katalog, transaksi kasir POS, alokasi stok FIFO multi-batch, dan cetak struk belanja menghasilkan keluaran 100% akurat. |
| **Desain OOP & Kesesuaian UML** | 25% | Class memiliki tanggung jawab tunggal (*Single Responsibility Principle*), relasi terdefinisi bersih, dan arsitektur selaras dengan diagram UML. |
| **Pengujian & Penanganan Kegagalan** | 15% | Data tidak valid (stok kurang, kode duplikat, delimiter pipa `|`) ditolak seketika dan state data aktif tetap utuh (*no crash*). |
| **Dokumentasi & Kesiapan Demonstrasi** | 5% | Source code siap kompilasi, data uji bebas informasi pribadi, README lengkap, naskah presentasi 5 slide terstruktur. |
| **Penjelasan & Penelusuran Kode (Individu)** | 20% | Mahasiswa mampu menelusuri alur eksekusi lintas class dan menjawab tuntas 7 pertanyaan mendalam seputar OOP. |
| **Analisis & Kontribusi Perubahan Kode** | 10% | Ketepatan penempatan kode baru saat live coding (pencarian sebagian nama, stok kritis, konfirmasi hapus) dan pemahaman dampaknya. |

---

## 3. Rangkuman Materi Presentasi 5 Slide Demonstrasi

Tersedia berkas naskah presentasi lengkap pada direktori:  
📁 [`Slide/Presentasi_P15.md`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P15_Tegar%20Mahardika_TegarCopyFlow/Slide/Presentasi_P15.md)

- **Slide 1 — Identitas Proyek, Masalah, & Pengguna Sasaran:**  
  *Tegar CopyFlow:* Mengatasi masalah selisih stok fisik, ketidakakuratan perhitungan HPP akibat fluktuasi harga kulakan, dan risiko kehilangan data kasir pada UMKM fotokopi & toko ATK kampus.
- **Slide 2 — Fitur Utama & Batasan Aplikasi:**  
  Katalog terpadu barang & jasa, valuasi inventaris FIFO, transaksi POS interaktif, alokasi stok otomatis, cetak struk kasir, atomic file persistence, dan pencarian fleksibel. Batasan: Antarmuka konsol CLI teks murni UTF-8.
- **Slide 3 — UML & Pembagian Tugas Class:**  
  Penerapan *Separation of Concerns*: `Main` (UI & Dirty Flag), `KatalogProduk` (Collection Manager), `Produk` (Domain Invariant), `BatchStok` (FIFO), `TransaksiPenjualan` (POS), `ProdukFileRepository` (I/O).
- **Slide 4 — Penerapan OOP, Validasi, & Penyimpanan Data:**  
  Encapsulation penuh via unmodifiable collection, validasi *fail-fast* (menolak `|`, nilai negatif, kode duplikat), *Atomic Writing* (`.tmp` -> target), dan *Staging Isolation* saat pembacaan berkas.
- **Slide 5 — Hasil Pengujian, Keterbatasan, & Rencana Perbaikan:**  
  100% Lulus pada seluruh skenario fungsional & regresi. Keterbatasan: simulasi struk konsol ASCII. Rencana: Pengembangan GUI desktop (JavaFX) dan modul laporan keuangan terintegrasi spreadsheet/PDF.

---

## 4. Alur Demonstrasi & Tabel Pengujian Fungsional (Langkah 2)

Demonstrasi proyek dieksekusi secara terstruktur mencakup 6 tahapan wajib Modul 15:

| Tahap Demonstrasi | Skenario / Operasi yang Diuji | Hasil yang Diharapkan | Hasil Aktual Aplikasi | Status |
|---|---|---|---|:---:|
| **1. Masalah & Desain** | Menjelaskan kebutuhan kasir & struktur class pengelola | Tanggung jawab class terpetakan jelas sesuai prinsip SRP | Arsitektur SRP terverifikasi rapi dan modular | **VALID** |
| **2. Alur Normal** | Tambah PRD-001, PRD-002, transaksi kasir beli 5 rim HVS, bayar tunai Rp 300rb | Stok terpotong ke 20 rim, kembalian Rp 25rb, struk tercetak, `berubah = true` | Struk kasir tercetak sempurna, stok HVS terpotong aman | **VALID** |
| **3. Kondisi Gagal** | Input nama mengandung pemisah `|` (`Spidol \| Whiteboard`) & kode duplikat `PRD-001` | Ditolak seketika oleh validasi domain tanpa membuat aplikasi crash | `[DATA DITOLAK]` Karakter `|` & kode duplikat dicegah | **VALID** |
| **4. Konsistensi State** | Injeksi file korup (kolom < 10) ke repositori saat memori terisi 2 produk aktif | Deserialisasi gagal tertangkap, 2 produk aktif di memori tetap utuh 100% | `[BUKTI RESILIENSI]` File korup ditolak; memori tetap utuh 2 produk | **VALID** |
| **5. Persistensi** | Simpan katalog atomik, tutup program, jalankan sesi baru, baca `produk.txt` | Sisa stok pasca transaksi terbukti termuat permanen dari media disk | `[INISIALISASI]` Data awal termuat otomatis: HVS = 20 rim | **VALID** |
| **6. Evaluasi** | Membahas keterbatasan (struk konsol ASCII) dan rencana integrasi GUI | Kelebihan & rencana perbaikan sistem terdokumentasi jelas | Terdokumentasi lengkap pada laporan & presentasi | **VALID** |

Bukti tangkapan layar terminal eksekusi:  
🖼️ [`Screenshot/Screenshot_1_Modul15_StudiKasus.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P15_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_1_Modul15_StudiKasus.png)  
🖼️ [`Screenshot/Screenshot_2_TegarCopyFlow_AlurNormal_LiveCoding.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P15_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_2_TegarCopyFlow_AlurNormal_LiveCoding.png)  
🖼️ [`Screenshot/Screenshot_3_TegarCopyFlow_TransaksiKasir_StokKritis.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P15_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_3_TegarCopyFlow_TransaksiKasir_StokKritis.png)  
🖼️ [`Screenshot/Screenshot_4_TegarCopyFlow_Persistensi_StagingIsolation.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P15_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_4_TegarCopyFlow_Persistensi_StagingIsolation.png)

---

## 5. Penelusuran Kode Individu (Langkah 3 Modul 15)

Berikut adalah pertanggungjawaban teknis atas 7 fokus pertanyaan penelusuran kode yang diujikan dosen:

1. **Encapsulation — Method mana yang mengendalikan perubahan state?**  
   Perubahan persediaan fisik produk dikendalikan oleh method `Produk.alokasikanStokFifo(int)` dan `Produk.kembalikanStok(int)`, bukan melalui setter langsung terhadap list batch. Mutasi katalog dikendalikan oleh `KatalogProduk.tambahProduk()` dan `hapusProduk()`. Koleksi internal bersifat `private final` dan method `getDaftarProduk()` mengembalikan `Collections.unmodifiableList(daftarProduk)` sehingga pihak luar tidak dapat memodifikasi isi list secara ilegal.
2. **Relasi Objek — Bagaimana satu objek menggunakan atau menyimpan referensi objek lain?**  
   - *Komposisi (Composition):* `Produk` memiliki relasi satu-ke-banyak (1..*) dengan `BatchStok`; siklus hidup batch persediaan bergantung pada eksistensi objek Produk.  
   - *Agregasi / Pengelolaan:* `KatalogProduk` menampung sekumpulan referensi objek `Produk`.  
   - *Asosiasi / Referensi:* `DetailTransaksi` menyimpan referensi ke objek `Produk` yang dibeli untuk membaca nama dan mendelegasikan alokasi stok.
3. **Validasi — Di mana kode duplikat atau nilai tidak valid ditolak?**  
   - Nilai tidak valid intrinsik (harga negatif, batas stok minus, teks kosong, serta karakter pemisah `|` dan baris baru) ditolak langsung di constructor `Produk` dan method `validasiDasar()` (*Fail-Fast*).  
   - Kode duplikat ditolak di class `KatalogProduk.tambahProduk()` dengan memeriksa `cariProduk(kode) != null` karena hanya class pengelola inilah yang memegang visibilitas terhadap seluruh koleksi produk toko.
4. **Exception — Dari mana exception berasal dan siapa yang menanganinya?**  
   `StokTidakCukupException` dan `TransaksiInvalidException` dilempar oleh domain entity saat aturan bisnis dilanggar. `IOException` dilempar oleh `ProdukFileRepository` saat terjadi anomali berkas fisik. Seluruh exception ditangkap secara elegan pada controller UI (`Main.java`) di dalam perulangan menu sehingga pesan kesalahan informatif dapat ditampilkan ke kasir tanpa menghentikan paksa aplikasi (*no crash*).
5. **Collection — Bagaimana pencarian dan penghapusan dilakukan?**  
   - *Pencarian:* `KatalogProduk.cariProduk(String kode)` melakukan iterasi for-each pada `daftarProduk` dan membandingkan kode secara *case-insensitive* (`p.getKode().equalsIgnoreCase(kode.trim())`).  
   - *Penghapusan:* `KatalogProduk.hapusProduk(String kode)` mencari objek terkait, lalu memanggil `daftarProduk.remove(produk)` yang secara otomatis merapatkan kembali indeks elemen list.
6. **File — Bagaimana teks dibentuk kembali menjadi objek?**  
   Pada `ProdukFileRepository.muat()`, berkas dibaca baris per baris via `Files.readAllLines()`, setiap baris dipecah menggunakan delimitator pipa via `baris.split("\\|", -1)` dan divalidasi memiliki tepat 10 kolom. Masing-masing token dikonversi ke tipe data primitif dengan normalisasi titik desimal, lalu dikonstruksi ulang melalui konstruktor lengkap `Produk` dan dimasukkan ke dalam *staging list*.
7. **Konsistensi — Apa yang terjadi jika pemuatan gagal di tengah proses?**  
   Repositori menerapkan pola **Staging Isolation**: parsing dilakukan pada koleksi sementara (`stagingList`). Jika salah satu baris korup di tengah jalan, exception dilempar seketika dan `stagingList` dibuang. Method `katalog.gantiSemua()` tidak pernah dipanggil, sehingga katalog aktif yang sedang berada di memori RAM tetap utuh 100% (*zero data loss*).

---

## 6. Implementasi Perubahan Terarah / Live Coding (Langkah 4 Modul 15)

Sebagai bukti kompetensi mandiri dalam menganalisis dan memodifikasi sistem secara terarah, telah diimplementasikan tiga fitur baru:

| Fitur Live Coding | Class yang Diubah | Alasan Penempatan & Implementasi Teknis | Hasil Uji Verifikasi |
|---|---|---|---|
| **1. Pencarian Sebagian Nama Produk** | `KatalogProduk` & `Main` | Menambahkan method `cariProdukByNama(String keyword)` pada class agregator. Di `Main` disediakan `menuCariByNama()`. | Pencarian kata kunci `"HVS"` berhasil menemukan produk `"Kertas HVS A4 70gsm"`. |
| **2. Laporan Stok Kritis / Menipis** | `KatalogProduk` & `Main` | Menambahkan method `getProdukStokKritis()` yang memfilter produk fisik dengan `sisaStok <= minStok`. Di `Main` disediakan `menuLaporanStokKritis()`. | Sistem mampu menyajikan rekap produk yang memerlukan kulakan ulang secara otomatis. |
| **3. Konfirmasi Eksplisit Sebelum Hapus** | `Main` (UI Layer) | Menambahkan dialog konfirmasi: *"Apakah Anda yakin ingin menghapus produk X? Ketik YA: "* pada method `menuHapus()`. | Penghapusan produk dapat dibatalkan dengan aman jika pengguna mengetik selain 'YA'. |

---

## 7. Diagram UML Class Diagram Modul 15

Diagram kelas final merefleksikan seluruh arsitektur proyek Tegar CopyFlow beserta fitur-fitur baru hasil implementasi perubahan terarah (Live Coding Modul 15):

```text
+-------------------------------------------------------+
|                         Main                          |
+-------------------------------------------------------+
| - katalog: KatalogProduk                              |
| - repository: ProdukFileRepository                    |
| - lokasiFile: Path                                    |
| - berubah: boolean <<dirty flag>>                     |
+-------------------------------------------------------+
| + menuTambah(Scanner): void                           |
| + menuTampilkan(): void                               |
| + menuCari(Scanner): void                             |
| + menuCariByNama(Scanner): void <<live coding>>       |
| + menuHapus(Scanner): void <<with confirmation>>      |
| + menuTransaksiKasir(Scanner): void                   |
| + menuLaporanStokKritis(): void <<live coding>>       |
| + menuSimpan(): void                                  |
| + menuMuatUlang(Scanner): void                        |
| - konfirmasi(Scanner, String): boolean                |
| + jalankan(Scanner): void                             |
+-------------------------------------------------------+
        | 1 controls 1                | uses
        v                             v
+-----------------------+     +-----------------------------------+
|     KatalogProduk     |     |       ProdukFileRepository        |
+-----------------------+     +-----------------------------------+
| - daftarProduk: List  |     | + simpan(KatalogProduk, Path)     |
+-----------------------+     | + muat(Path): List<Produk>        |
| + tambahProduk(...)   |     +-----------------------------------+
| + cariProduk(...)     |                       ^
| + cariProdukByNama()..|                       | serializes / loads
| + getProdukStokKritis |                       |
| + hapusProduk(...)    |                       v
| + gantiSemua(List)    |       +---------------------------------+
+-----------------------+       |             Produk              |
        | 1                     +---------------------------------+
        |                       | - kode: String <<unique>>       |
        | *                     | - nama: String <<no '|'>>       |
        +---------------------> | - satuan: String <<no '|'>>     |
                                | - hargaJual: double             |
                                | - daftarBatch: List<BatchStok>  |
                                +---------------------------------+
                                | - validasiDasar(...): void      |
                                | + alokasikanStokFifo(int)       |
                                | + toFileLine(): String          |
                                +---------------------------------+
                                                ^
                                                | references
                                +---------------------------------+
                                |         DetailTransaksi         |
                                +---------------------------------+
                                                ^
                                                | 1..*
                                +---------------------------------+
                                |       TransaksiPenjualan        |
                                +---------------------------------+
                                | + tambahItem(Produk, int)       |
                                | + batalkanTransaksi(): void     |
                                | + cetakStruk(): void            |
                                | + getTotal(): double            |
                                +---------------------------------+
```

Berkas gambar diagram beresolusi tinggi tersedia di:  
🖼️ [`UML/ClassDiagram_P15.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P15_Tegar%20Mahardika_TegarCopyFlow/UML/ClassDiagram_P15.png)

---

## 8. Refleksi Individu & Rencana Tindak Lanjut (Langkah 5 Modul 15)

### A. Tabel Catatan Masukan dan Rencana Perbaikan
| Temuan / Masukan | Dampak terhadap Sistem | Rencana Perbaikan Prioritas | Penanggung Jawab | Bukti Verifikasi |
|---|---|---|---|---|
| Karakter pemisah pipa `|` berpotensi merusak struktur berkas jika diinput kasir. | Korupsi parsing kolom saat berkas dibaca kembali pada sesi berikutnya. | Pindahkan validasi langsung ke constructor `Produk` (*Fail-Fast*). | Tegar Mahardika | Terverifikasi pada Skenario 3 (Penolakan seketika). |
| Belum ada dialog konfirmasi saat menghapus produk di katalog. | Data master berisiko terhapus permanen akibat ketidaksengajaan kasir. | Tambahkan dialog konfirmasi interaktif di `menuHapus()`. | Tegar Mahardika | Terverifikasi pada Live Coding Fitur 3. |
| Pencarian produk hanya mengandalkan kode persis (tidak fleksibel). | Kasir kesulitan mencari produk jika tidak menghafal kode unik. | Tambahkan pencarian fleksibel berdasarkan sebagian nama produk. | Tegar Mahardika | Terverifikasi pada Live Coding Fitur 1 (`menuCariByNama`). |

### B. Refleksi Pribadi Penguasaan Materi
- **Kontribusi yang Dapat Dibuktikan:** Berperan penuh dalam merancang arsitektur sistem dari P9 hingga P15, membangun metode alokasi stok FIFO multi-batch pada `Produk`, mengimplementasikan `ProdukFileRepository` dengan *Atomic Writing* dan *Staging Isolation*, serta mengimplementasikan seluruh fitur live coding secara deterministik.
- **Keputusan Desain yang Paling Dipahami:** Pemisahan *Domain Logic* dan *Persistence Logic* melalui Repository Pattern. Pola ini membebaskan class bisnis dari urusan I/O berkas teks dan menjamin keamanan enkapsulasi melalui `Collections.unmodifiableList()`.
- **Kelemahan Teknis yang Masih Perlu Diperbaiki:** Aplikasi saat ini masih berbasis konsol teks CLI sehingga proses transaksi kasir dengan volume belanjaan tinggi memerlukan pengetikan manual. Rencana ke depan adalah membangun antarmuka grafis JavaFX dan integrasi barcode scanner fisik.

---

## 9. Jawaban Pertanyaan Evaluasi (Bab 8 Modul 15)

Berikut adalah analisis konseptual untuk menjawab empat pertanyaan evaluasi praktikum pada Bab 8 Modul 15:

### 1. Keputusan desain apa yang paling membantu menjaga konsistensi data?
Keputusan desain yang paling fundamental adalah sinergi antara **Pola Staging Isolation pada Repositori** dan **Enkapsulasi Koleksi Tak Dapat Diubah (*Unmodifiable Collection*)**:
- *Staging Isolation:* Saat membaca berkas dari media disk, repositori membaca seluruh baris ke dalam koleksi sementara terlebih dahulu. Jika terjadi anomali format atau data numerik rusak di tengah proses, operasi dibatalkan dan koleksi sementara dibuang, sehingga katalog aktif di memori RAM sama sekali tidak terpengaruh.
- *Atomic File Replacement:* Saat menyimpan data, file ditulis ke berkas temporer (`.tmp`) sebelum diganti secara atomik ke berkas utama, mencegah berkas utama rusak atau terpotong jika komputer mati mendadak.
- *Unmodifiable List:* Mencegah class luar merusak isi koleksi tanpa melalui method resmi class pengelola.

### 2. Bagian mana yang paling sulit diubah, dan apa penyebabnya?
Bagian yang paling kompleks dan membutuhkan kehati-hatian tertinggi adalah **Algoritma Alokasi Stok FIFO Berbasis Multi-Batch (`Produk.alokasikanStokFifo`) dan Mekanisme Rollback-nya**:
- Algoritma ini memutasi state kuantitas pada sekumpulan objek `BatchStok` yang berbeda-beda harga modal belinya. Satu baris transaksi belanja kasir dapat mengambil stok dari dua atau lebih batch kulakan yang berbeda.
- Jika kuantitas stok di tengah alokasi ternyata tidak mencukupi atau transaksi dibatalkan kasir, sistem harus mampu mengembalikan kuantitas persis ke masing-masing batch asalnya tanpa ada nilai rupiah modal yang terdistorsi.

### 3. Pengujian apa yang paling kuat membuktikan aplikasi bekerja sesuai kebutuhan?
Pengujian yang paling kuat adalah **Pengujian Ketahanan Inter-Sesi Berantai (*Lifecycle & Persistence Testing*)**:
- Alur pengujian: Inisialisasi -> Transaksi Penjualan Kasir POS (memotong stok fisik) -> Penyimpanan Atomik ke Disk -> Penutupan Total Program -> Inisialisasi Ulang Sesi Baru dari Disk.
- Pengujian ini membuktikan secara empiris bahwa data tidak hanya beroperasi di memori RAM, tetapi seluruh mutasi bisnis tersimpan persisten secara permanen dan dapat dipulihkan dengan akurasi 100% tanpa adanya kesalahan format desimal lokal.

### 4. Jika fitur baru ditambahkan, class mana yang perlu berubah dan mengapa?
Class yang perlu berubah bergantung pada sifat dan domain fitur baru tersebut, sesuai prinsip *Single Responsibility Principle*:
- *Jika fitur berupa filter/pencarian data (misal: pencarian berdasarkan rentang harga):* Yang berubah hanyalah `KatalogProduk` (menambahkan method filter) dan `Main` (menambahkan menu konsol). Class `Produk` tidak perlu disentuh sama sekali.
- *Jika fitur berupa atribut domain baru (misal: diskon persentase produk):* Yang berubah adalah class `Produk` (menambahkan atribut dan validasi), `ProdukFileRepository` (menyesuaikan format kolom berkas teks), dan `DetailTransaksi` (menghitung diskon subtotal).
- Pemisahan tanggung jawab ini membuktikan ketangguhan arsitektur OOP: penambahan fungsionalitas terlokalisasi pada modul yang relevan tanpa merusak subsistem lainnya.

---

## 10. Panduan Kompilasi dan Eksekusi Program

### Prasyarat
- Java Development Kit (JDK) versi 17 atau lebih baru (telah diuji pada Java 25 LTS).

### Perintah Terminal / PowerShell
Masuk ke direktori `SourceCode`:
```powershell
# 1. Masuk ke direktori SourceCode
cd "P15_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Kompilasi program studi kasus dasar Modul 15
javac -encoding UTF-8 -d bin src/Modul15Dasar.java

# 3. Jalankan pengujian fungsional otomatis studi kasus dasar
java -cp bin Modul15Dasar --test

# 4. Kompilasi seluruh arsitektur proyek Tegar CopyFlow Final (Model, Exception, Repository, dan Main)
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/repository/*.java src/Main.java

# 5. Jalankan demonstrasi fungsional otomatis Tegar CopyFlow (Live Coding Extensions)
java -cp bin Main --test

# 6. Jalankan mode interaktif manual konsol untuk demonstrasi
java -cp bin Main
```

---

## 11. Struktur Berkas Repositori

```text
P15_Tegar Mahardika_TegarCopyFlow/
├── Laporan Praktikum_Pertemuan_15.pdf      # Dokumen PDF Resmi Laporan Praktikum Modul 15
├── README.md                               # Laporan Lengkap Praktikum (Markdown)
├── tools_generate_assets.py                # Utilitas otomasi pengujian, gambar terminal & rendering PDF
├── Slide/
│   └── Presentasi_P15.md                   # Naskah Lengkap 5 Slide Presentasi Demonstrasi
├── UML/
│   └── ClassDiagram_P15.png                # Diagram UML Class Terkini dengan Fitur Live Coding
├── Screenshot/
│   ├── Screenshot_1_Modul15_StudiKasus.png
│   ├── Screenshot_2_TegarCopyFlow_AlurNormal_LiveCoding.png
│   ├── Screenshot_3_TegarCopyFlow_TransaksiKasir_StokKritis.png
│   └── Screenshot_4_TegarCopyFlow_Persistensi_StagingIsolation.png
└── SourceCode/
    ├── bin/                                # Direktori bytecode hasil kompilasi (.class)
    ├── data/                               # Direktori penyimpanan berkas data teks (.txt)
    │   ├── buku.txt                        # Berkas persistensi Modul15Dasar
    │   └── produk.txt                      # Berkas persistensi master produk Tegar CopyFlow
    ├── hasil-pengujian.txt                 # Log lengkap hasil pengujian teks murni UTF-8 (18.944 karakter)
    ├── hasil pengujian.pdf                 # Salinan PDF laporan pengujian
    ├── README.md                           # Panduan teknis kompilasi source code
    └── src/
        ├── Modul15Dasar.java               # Kode implementasi studi kasus dasar Modul 15
        ├── Main.java                       # Aplikasi konsol interaktif Tegar CopyFlow (Live Coding)
        ├── exception/
        │   ├── StokTidakCukupException.java
        │   └── TransaksiInvalidException.java
        ├── model/
        │   ├── BatchStok.java
        │   ├── DetailTransaksi.java
        │   ├── KatalogProduk.java          # Aggregator domain dengan fitur Live Coding
        │   ├── Produk.java                 # Entitas produk dengan validasi delimiter '|' sejak awal
        │   └── TransaksiPenjualan.java     # Agregator transaksi penjualan kasir POS
        └── repository/
            └── ProdukFileRepository.java   # Repository Pattern: Atomic Writing & Staging Isolation
```

# Laporan Praktikum Pemrograman Berorientasi Objek — Modul 14
**Topik:** Pengujian, Refactoring, dan Finalisasi Proyek  
**Judul Project:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Penyusun:** Tegar Mahardika  
**Dosen Pengampu:** Nirwana Haidar Hari, S.Pd., M.Kom.  
**Institusi:** Politeknik Elektronika Negeri Surabaya (PENS) — PSDKU Sumenep  
**Program Studi:** D3 Teknik Informatika  
**Tahun Ajaran:** 2026  

---

## 1. Tujuan Praktikum

Setelah menyelesaikan praktikum Modul 14 ini, mahasiswa mampu:
1. **Menguji Fungsi dan Konsistensi Data Aplikasi:** Memvalidasi ketepatan keluaran fitur, batasan invariant bisnis, dan integritas data baik di memori maupun pada penyimpanan berkas permanen.
2. **Menemukan serta Memperbaiki Kesalahan Berdasarkan Bukti (*Bug Fixing*):** Mengidentifikasi cacat logika, menelusuri akar penyebabnya (*root cause*), dan menerapkan perbaikan pada class yang bertanggung jawab.
3. **Melakukan Refactoring Kode (*Clean Code & Architecture*):** Memperbaiki struktur internal program tanpa mengubah perilaku eksternal sistem (*DRY principle* dan *Single Responsibility Principle*).
4. **Menjalankan Pengujian Regresi (*Regression Testing*):** Memastikan perbaikan atau refactoring tidak menimbulkan cacat atau dampak samping baru (*zero regression bugs*).
5. **Menyiapkan Proyek untuk Finalisasi dan Demonstrasi:** Memastikan arsitektur sistem, dokumentasi teknis, diagram UML, dan berkas data siap untuk evaluasi menyeluruh pada Modul 15.

---

## 2. Dasar Teori & Konsep Rekayasa Perangkat Lunak

Program yang berhasil dikompilasi (*compile clean*) belum tentu benar. Keberhasilan sistem ditentukan oleh ketepatan hasil komputasi, penanganan kegagalan (*fault tolerance*), dan konsistensi state.

| Konsep Pengujian & Desain | Definisi Konseptual | Penerapan dalam Tegar CopyFlow |
|---|---|---|
| **Pengujian Fungsional** | Memeriksa apakah setiap fitur aplikasi beroperasi sesuai kebutuhan spesifikasi. | Menguji menu CRUD produk, alokasi stok FIFO, kalkulasi laba kotor, dan pencetakan struk belanja. |
| **Pengujian Integrasi** | Memeriksa interaksi dan kerja sama antar-komponen class yang berbeda. | Menguji rantai: `Main` -> `KatalogProduk` -> `Produk` -> `BatchStok` -> `TransaksiPenjualan` -> `ProdukFileRepository`. |
| **Pengujian Regresi** | Mengulang seluruh skenario pengujian setelah kode dimodifikasi untuk menjamin fitur lama tetap bekerja. | Menguji ulang 13 skenario pasca refactoring input helper dan sentralisasi normalisasi kode. |
| **Refactoring** | Memperbaiki kualitas struktur kode tanpa mengubah perilaku fungsional yang teramati. | Mengekstrak method `bacaStringWajib()`, `bacaInt()`, `bacaDouble()`, dan normalisasi kode terpusat. |
| **Class Invariant** | Aturan integritas data yang harus selalu bernilai benar sepanjang siklus hidup objek. | Harga >= 0, stok >= 0, ID > 0, nama tidak boleh kosong dan tidak boleh mengandung karakter pemisah pipa `|`. |

---

## 3. Catatan Temuan & Perbaikan Kesalahan (Bug Fixing)

Berdasarkan penelusuran mendalam pada tahap pengujian komprehensif, ditemukan dan diselesaikan bug nyata berikut:

| ID Bug | Langkah Pemicu | Hasil yang Diharapkan | Hasil Aktual Awal | Tindakan Perbaikan & Status |
|:---:|:---|:---|:---|:---|
| **BUG-01** | Input nama produk mengandung karakter pemisah pipa (`Buku \| Gambar A3`). | Ditolak saat pembuatan objek sebelum masuk memori atau repositori. | Sempat lolos ke koleksi, merusak penguraian kolom saat file dibaca kembali. | **SUKSES DIPERBAIKI:** Menambahkan validasi delimiter `|` di constructor `Produk` dan `validasiDasar()`. |
| **BUG-02** | Input nilai desimal menggunakan tanda koma (`55000,00` pada regional Windows ID-ID). | Nilai desimal dinormalisasi dan diterima sebagai angka valid. | Melempar `NumberFormatException: For input string: "55000,00"`. | **SUKSES DIPERBAIKI:** Menerapkan `.replace(',', '.')` dan `Locale.US` pada pembacaan dan serialisasi. |
| **BUG-03** | Pencarian produk menggunakan huruf kecil (`prd-001`). | Produk ditemukan secara akurat (*case-insensitive*). | Mengembalikan `null` jika pembanding menggunakan `equals()` sensitif huruf. | **SUKSES DIPERBAIKI:** Menggunakan `p.getKode().equalsIgnoreCase(kode.trim())` pada `KatalogProduk`. |
| **BUG-04** | Transaksi kasir dibatalkan karena pembayaran tunai kurang dari total belanja. | Stok yang sudah sempat teralokasi harus dikembalikan utuh (*rollback*). | Stok batch FIFO terpotong permanen meskipun transaksi kasir batal. | **SUKSES DIPERBAIKI:** Mengintegrasikan `trx.batalkanTransaksi()` untuk merestorasi saldo batch. |

---

## 4. Rincian Refactoring Struktur Kode (Poin 4 Modul 14)

Sesuai ketentuan Langkah 4 Modul 14, dilakukan minimal dua perbaikan struktur kode utama:

### 4.1 Refactoring 1 — Ekstraksi Helper Input Konsol Reusable & Type-Safe
- **Masalah Sebelum:** Pada setiap menu input konsol, pembacaan nilai numerik selalu menggunakan blok `try-catch (NumberFormatException)` tersendiri yang berulang-ulang memenuhi logika menu.
- **Tindakan Refactoring:** Mengekstrak fungsi-fungsi pembantu statis pada `Main`:
  ```java
  public static String bacaStringWajib(Scanner input, String pesan);
  public static int bacaInt(Scanner input, String pesan, int nilaiMinimal);
  public static double bacaDouble(Scanner input, String pesan, double nilaiMinimal);
  ```
- **Alasan & Manfaat:** Menerapkan prinsip **DRY (Don't Repeat Yourself)**, mengeliminasi duplikasi kode, dan menjamin bahwa data masukan pengguna selalu ter-sanitize sebelum objek domain dibentuk.

### 4.2 Refactoring 2 — Sentralisasi Normalisasi String & Pencarian Case-Insensitive
- **Masalah Sebelum:** Penanganan spasi dan kapitalisasi kode dilakukan secara parsial di beberapa method berbeda sehingga rawan ketidakkonsistenan.
- **Tindakan Refactoring:** Menyatukan normalisasi di constructor `Produk` (`this.kode = kode.trim().toUpperCase()`) serta method `KatalogProduk.cariProduk(String kode)` menggunakan pembanding `equalsIgnoreCase(kode.trim())`.
- **Alasan & Manfaat:** Menegakkan prinsip **Single Source of Truth** untuk identitas unik kode di seluruh lapisan aplikasi.

### 4.3 Refactoring 3 — Transaksional Rollback Alokasi Stok Kasir POS
- **Masalah Sebelum:** Keranjang belanja kasir yang dibatalkan di tengah jalan dapat meninggalkan saldo stok produk dalam kondisi terpotong.
- **Tindakan Refactoring:** Memanfaatkan method `batalkanTransaksi()` pada `TransaksiPenjualan` yang memanggil `batch.kembalikanStok()` secara rekursif ke seluruh item belanja saat kasir membatalkan transaksi.
- **Alasan & Manfaat:** Menjamin kepatuhan prinsip **Atomicity** pada transaksi inventaris.

---

## 5. Tabel Hasil Pengujian Fungsional & Regresi (13 Skenario)

Pengujian regresi dijalankan secara deterministik melalui runner otomatis `Main.java --test` mencakup 3 sesi eksekusi:

| No | Skenario Pengujian | Input Pengguna / Operasi | Hasil yang Diharapkan | Hasil Aktual Program (Regresi) | Status |
|:---:|---|---|---|---|:---:|
| **1** | **Katalog Kosong Awal** | Menu 2 saat awal program | Pesan katalog kosong, sistem tetap berjalan stabil | `Katalog produk toko masih kosong.` | **VALID** |
| **2** | **Tambah Produk Valid** | `PRD-001`, HVS A4 70gsm, stok 25 rim @ 45rb, jual 55rb | Objek tersimpan di katalog memori, status `berubah = true` | `[SUKSES] Produk 'Kertas HVS A4 70gsm' berhasil ditambahkan.` | **VALID** |
| **3** | **Penolakan Delimiter '|'** | Input nama: `Buku \| Gambar A3` | Ditolak seketika saat objek `Produk` dibentuk di konstruktor | `[DATA DITOLAK] Nama produk tidak boleh mengandung karakter pemisah '\|' atau baris baru.` | **VALID** |
| **4** | **Penolakan Kode Duplikat** | Input ulang kode `PRD-001` | Ditolak oleh aturan keunikan class `KatalogProduk` | `[DATA DITOLAK] Produk dengan kode 'PRD-001' sudah terdaftar dalam katalog.` | **VALID** |
| **5** | **Pencarian Huruf Kecil** | Cari kode: `prd-001` | Produk ditemukan secara akurat (*case-insensitive*) | `[DITEMUKAN] Detail Produk: Kode PRD-001, Stok 25 rim (AMAN).` | **VALID** |
| **6** | **Pencarian Kode Fiktif** | Cari kode: `PRD-999` | Pesan produk tidak ditemukan ditampilkan | `[HASIL] Produk dengan kode 'PRD-999' tidak ditemukan dalam katalog.` | **VALID** |
| **7** | **Transaksi Kasir POS** | Tambah `PRD-002`, beli 5 rim HVS, bayar tunai Rp 300.000 | Stok terpotong dari 25 ke 20 rim, struk tercetak, `berubah = true` | Struk belanja tercetak rapi, kembalian Rp 25.000, sisa stok HVS = 20 rim. | **VALID** |
| **8** | **Hapus Kode Fiktif** | Hapus kode: `PRD-888` | Gagal menghapus, koleksi data tetap utuh 2 produk | `[GAGAL] Produk dengan kode 'PRD-888' tidak ditemukan. Koleksi tidak berubah.` | **VALID** |
| **9** | **Proteksi Keluar Belum Simpan** | Pilih Menu 0 (Keluar), jawab: `TIDAK` | Operasi keluar dibatalkan, kembali ke menu utama | `Perubahan belum disimpan. Abaikan? Ketik YA: TIDAK`<br/>`[INFO] Batal keluar. Kembali ke menu utama.` | **VALID** |
| **10**| **Simpan Data Atomik** | Pilih Menu 6 (Simpan Data ke File) | Data 2 produk tertulis via `.tmp`, status `berubah = false` | `[SUKSES] Seluruh data (2 produk) berhasil disimpan secara atomik.` | **VALID** |
| **11**| **Regresi Sesi Baru (Disk)** | Tutup program, buka sesi baru memuat `produk.txt` | Data termuat otomatis; sisa stok pasca transaksi permanen di disk | `[INISIALISASI] Data awal berhasil dimuat (2 produk).`<br/>Stok HVS = 20 rim, Bolpoin = 120 pcs. | **VALID** |
| **12**| **Regresi Staging Isolation** | Muat file corrupt (`produk_corrupt.txt`) ke katalog aktif | Pemuatan gagal tertangkap; 2 produk aktif di memori tetap utuh | `[REGRESI SUKSES] Penolakan berkas korup tertangkap.`<br/>State memori tetap 2 produk utuh 100%. | **VALID** |

Bukti tangkapan layar terminal eksekusi:  
🖼️ [`Screenshot/Screenshot_1_Modul14_StudiKasus.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P14_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_1_Modul14_StudiKasus.png)  
🖼️ [`Screenshot/Screenshot_2_TegarCopyFlow_RefactoringInput.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P14_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_2_TegarCopyFlow_RefactoringInput.png)  
🖼️ [`Screenshot/Screenshot_3_TegarCopyFlow_TransaksiPOS_Rollback.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P14_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_3_TegarCopyFlow_TransaksiPOS_Rollback.png)  
🖼️ [`Screenshot/Screenshot_4_TegarCopyFlow_RegresiPersistensiStaging.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P14_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_4_TegarCopyFlow_RegresiPersistensiStaging.png)

---

## 6. Diagram UML Class Diagram Modul 14

Diagram kelas arsitektur Modul 14 menunjukkan penyempurnaan struktur internal class `Main` dengan method helper input terpusat, class `KatalogProduk` dengan normalisasi pencarian terpusat, dan integrasi penanganan transaksi kasir POS yang aman:

```text
+-------------------------------------------------------+
|                         Main                          |
+-------------------------------------------------------+
| - katalog: KatalogProduk                              |
| - repository: ProdukFileRepository                    |
| - lokasiFile: Path                                    |
| - berubah: boolean <<dirty flag>>                     |
+-------------------------------------------------------+
| + bacaStringWajib(Scanner, String): String            |
| + bacaInt(Scanner, String, int): int                  |
| + bacaDouble(Scanner, String, double): double         |
| + menuTambah(Scanner): void                           |
| + menuTampilkan(): void                               |
| + menuCari(Scanner): void                             |
| + menuHapus(Scanner): void                            |
| + menuTransaksiKasir(Scanner): void                   |
| + menuSimpan(): void                                  |
| + menuMuatUlang(Scanner): void                        |
| - konfirmasi(Scanner): boolean                        |
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
| + hapusProduk(...)    |                       | serializes / loads
| + gantiSemua(List)    |                       |
+-----------------------+                       v
        | 1                     +---------------------------------+
        |                       |             Produk              |
        |                       +---------------------------------+
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
🖼️ [`UML/ClassDiagram_P14.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P14_Tegar%20Mahardika_TegarCopyFlow/UML/ClassDiagram_P14.png)

---

## 7. Jawaban Pertanyaan Evaluasi (Bab 8 Modul 14)

Berikut adalah analisis mendalam untuk menjawab empat pertanyaan evaluasi praktikum pada Bab 8 Modul 14:

### 1. Mengapa program yang berhasil dikompilasi masih perlu diuji?
Kompilator Java (`javac`) hanya memverifikasi kebenaran sintaksis (*syntax check*), kesesuaian tipe data (*type checking*), dan keberadaan deklarasi method atau class saat waktu kompilasi (*compile-time correctness*):
- Kompilator tidak memiliki kapabilitas untuk memahami logika bisnis (*business logic*), batasan semantik, maupun konsistensi state pada saat runtime.
- Contoh nyata: Suatu program yang mengizinkan input harga negatif (misal: `-50000`) atau stok minus tetap akan lolos kompilasi secara sempurna karena tipe data numerik `double` atau `int` valid secara sintaksis, namun secara bisnis transaksi tersebut salah fatal.
- Kesalahan penguraian format delimitator berkas, kegagalan penanganan null pointer, dan pelanggaran invariant hanya dapat ditemukan saat aplikasi dijalankan (*runtime*). Oleh sebab itu, pengujian fungsional dan pengujian batas (*boundary testing*) mutlak diperlukan.

### 2. Apa perbedaan perbaikan bug (Bug Fixing) dan refactoring?
Perbedaan mendasar antara keduanya terletak pada tujuan, dampak pada perilaku eksternal sistem, dan motivasinya:
- **Perbaikan Bug (*Bug Fixing*):** Bertujuan memperbaiki kesalahan logika atau kecacatan yang menyebabkan program menyimpang dari perilaku yang diharapkan (misal: karakter pemisah `|` sempat lolos ke berkas teks, atau transaksi yang batal tidak mengembalikan stok). Bug fixing **mengubah perilaku eksternal sistem** dari yang semula salah/rusak menjadi benar sesuai spesifikasi.
- **Refactoring:** Bertujuan menyempurnakan struktur internal program, mengurangi duplikasi (*DRY*), meningkatkan keterbacaan (*readability*), dan mempermudah pemeliharaan (*maintainability*) **tanpa mengubah sedikit pun perilaku eksternal yang teramati**. Pada Modul 14, mengekstrak helper `bacaInt()` dan `bacaDouble()` adalah refactoring karena alur penambahan produk bagi pengguna tetap menghasilkan perilaku yang sama persis, namun kode internalnya menjadi jauh lebih modular dan bersih.

### 3. Mengapa pengujian harus diulang setelah struktur kode diubah (Pengujian Regresi)?
Ketika struktur kode diubah selama proses refactoring atau perbaikan bug, selalu terdapat potensi timbulnya dampak samping tak terduga (*unintended side effects*) atau kecacatan regresi (*regression bugs*):
- Modifikasi pada satu method dapat mematahkan asumsi atau dependensi class lain yang sebelumnya berjalan normal.
- Pengujian regresi (*Regression Testing*) dilakukan dengan mengeksekusi kembali seluruh rangkaian kasus uji yang sudah pernah lulus sebelumnya.
- Hal ini memberikan jaminan dan pembuktian empiris bahwa perubahan yang diterapkan murni menyempurnakan arsitektur dan tidak merusak fungsionalitas sistem yang sudah stabil.

### 4. Bagaimana membuktikan operasi yang gagal tidak merusak state objek?
Membuktikan bahwa operasi yang gagal tidak merusak state objek dilakukan melalui pengujian ketahanan (*Fault Injection Testing*) dengan membandingkan kondisi sebelum dan sesudah kegagalan (*Pre-condition vs Post-condition Verification*):
1. **Catat State Awal (*Baseline State*):** Periksa jumlah dan data spesifik koleksi di memori (misalnya katalog aktif berisi tepat 2 produk).
2. **Picu Operasi Gagal (*Trigger Failure*):** Masukkan data atau berkas yang dirancang gagal (misalnya memuat berkas dengan kolom kurang dari 10 atau mencoba menambahkan produk dengan kode duplikat).
3. **Pastikan Penanganan Galat Terisolasi:** Verifikasi bahwa exception yang sesuai berhasil tertangkap secara elegan tanpa menyebabkan crash pada aplikasi.
4. **Verifikasi State Akhir (*Post-condition Check*):** Periksa kembali koleksi di memori. Jika jumlah data tetap persis 2 dan atribut-atributnya tidak mengalami mutasi sebagian (*partial corruption*), maka terbukti bahwa pola isolasi (seperti *Staging List* pada repositori atau *Rollback* pada transaksi) berhasil mempertahankan konsistensi state secara sempurna.

---

## 8. Panduan Kompilasi dan Eksekusi Program

### Prasyarat
- Java Development Kit (JDK) versi 17 atau lebih baru (telah diuji pada Java 25 LTS).

### Perintah Terminal / PowerShell
Masuk ke direktori `SourceCode`:
```powershell
# 1. Masuk ke direktori SourceCode
cd "P14_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Kompilasi program studi kasus dasar Modul 14 (10 Skenario)
javac -encoding UTF-8 -d bin src/Modul14Dasar.java

# 3. Jalankan pengujian fungsional otomatis studi kasus dasar
java -cp bin Modul14Dasar --test

# 4. Kompilasi seluruh arsitektur proyek Tegar CopyFlow (Model, Exception, Repository, dan Main)
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/repository/*.java src/Main.java

# 5. Jalankan pengujian fungsional & regresi otomatis Tegar CopyFlow (13 Skenario)
java -cp bin Main --test

# 6. Jalankan mode interaktif manual konsol
java -cp bin Main
```

---

## 9. Struktur Berkas Repositori

```text
P14_Tegar Mahardika_TegarCopyFlow/
├── Laporan Praktikum_Pertemuan_14.pdf      # Dokumen PDF Resmi Laporan Praktikum Modul 14
├── README.md                               # Laporan Lengkap Praktikum (Markdown)
├── tools_generate_assets.py                # Utilitas otomasi pengujian, gambar terminal & rendering PDF
├── UML/
│   └── ClassDiagram_P14.png                # Diagram UML Class Terkini Modul 14
├── Screenshot/
│   ├── Screenshot_1_Modul14_StudiKasus.png
│   ├── Screenshot_2_TegarCopyFlow_RefactoringInput.png
│   ├── Screenshot_3_TegarCopyFlow_TransaksiPOS_Rollback.png
│   └── Screenshot_4_TegarCopyFlow_RegresiPersistensiStaging.png
└── SourceCode/
    ├── bin/                                # Direktori bytecode hasil kompilasi (.class)
    ├── data/                               # Direktori penyimpanan berkas data teks (.txt)
    │   ├── buku.txt                        # Berkas persistensi Modul14Dasar
    │   └── produk.txt                      # Berkas persistensi master produk Tegar CopyFlow
    ├── hasil-pengujian.txt                 # Log lengkap hasil pengujian teks murni UTF-8 (18.953 karakter)
    ├── hasil pengujian.pdf                 # Salinan PDF laporan pengujian
    ├── README.md                           # Panduan teknis kompilasi source code
    └── src/
        ├── Modul14Dasar.java               # Kode implementasi 10 skenario dasar Modul 14
        ├── Main.java                       # Aplikasi konsol refactored Tegar CopyFlow
        ├── exception/
        │   ├── StokTidakCukupException.java
        │   └── TransaksiInvalidException.java
        ├── model/
        │   ├── BatchStok.java
        │   ├── DetailTransaksi.java
        │   ├── KatalogProduk.java          # Aggregator domain koleksi List<Produk>
        │   ├── Produk.java                 # Entitas produk dengan validasi delimiter '|' sejak awal
        │   └── TransaksiPenjualan.java     # Agregator transaksi penjualan kasir POS
        └── repository/
            └── ProdukFileRepository.java   # Repository Pattern: Atomic Writing & Staging Isolation
```

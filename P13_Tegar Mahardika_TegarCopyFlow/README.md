# Laporan Praktikum Pemrograman Berorientasi Objek — Modul 13
**Topik:** Integrasi Aplikasi Berbasis Menu dan Pengujian Fungsional  
**Judul Project:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Penyusun:** Tegar Mahardika  
**Dosen Pengampu:** Nirwana Haidar Hari, S.Pd., M.Kom.  
**Institusi:** Politeknik Elektronika Negeri Surabaya (PENS) — PSDKU Sumenep  
**Program Studi:** D3 Teknik Informatika  
**Tahun Ajaran:** 2026  

---

## 1. Tujuan Praktikum

Setelah menyelesaikan praktikum Modul 13 ini, mahasiswa mampu:
1. **Mengintegrasikan Fitur ke Aplikasi Konsol Interaktif:** Menggabungkan komponen model domain, struktur data dinamis (*Collection* - Modul 11), penanganan kesalahan (*Exception Handling* - Modul 10), dan repositori penyimpanan berkas (*File I/O* - Modul 12) ke dalam antarmuka berbasis menu konsol yang terpadu.
2. **Menerapkan Pemisahan Tanggung Jawab (*Separation of Concerns*):** Memisahkan logika presentasi/interaksi pengguna (`Main`) dari aturan validasi bisnis domain (`Produk`, `KatalogProduk`).
3. **Menjaga Konsistensi State & Proteksi Perubahan Belum Disimpan (*Dirty Flag*):** Mengelola siklus status perubahan data (`boolean berubah`) serta menyediakan dialog konfirmasi untuk mencegah hilangnya data saat pengguna keluar atau memuat ulang berkas.
4. **Menerapkan Pemeriksaan Karakter Delimiter Sejak Awal (*Perbaikan Wajib*):** Menolak karakter pemisah (pipa `|` atau baris baru) langsung pada saat objek domain dibentuk di konstruktor, sehingga data yang tidak dapat disimpan tertolak sejak awal (*fail-fast*).
5. **Melakukan Pengujian Fungsional Menyeluruh (*End-to-End Testing*):** Menguji alur interaksi multi-langkah dari penambahan data, penolakan input invalid, transaksi penjualan kasir POS, alokasi stok FIFO, hingga pemulihan data pasca aplikasi ditutup.

---

## 2. Dasar Teori & Pembagian Tanggung Jawab Antarclass

Aplikasi berbasis menu memungkinkan pengguna menjalankan serangkaian operasi secara interaktif dalam satu siklus hidup eksekusi program. Agar sistem tetap tangguh (*robust*), modular, dan terstruktur, tanggung jawab dibagi secara tegas antar-kelas:

| Komponen / Class | Tanggung Jawab Arsitektural | Implementasi dalam Tegar CopyFlow |
|---|---|---|
| **`Main`** | Menampilkan antarmuka menu, membaca input via `Scanner.nextLine()`, memanggil layanan objek, dan menyajikan hasil atau pesan galat. | Dipecah menjadi method modular: `menuTambah()`, `menuTampilkan()`, `menuCari()`, `menuHapus()`, `menuTransaksiKasir()`, `menuSimpan()`, `menuMuatUlang()`, dan `konfirmasi()`. |
| **`Produk`** | Menjaga validitas dan integritas satu entitas barang fisik atau layanan jasa toko. | Memvalidasi batasan nilai harga, minimum stok, serta menolak karakter pemisah pipa `|` dan baris baru di konstruktor. |
| **`KatalogProduk`** | Mengelola koleksi `List<Produk>` di memori, menjamin keunikan kode produk (*unique identifier*), serta menyediakan operasi CRUD. | Menyediakan method `tambahProduk()`, `cariProduk()`, `hapusProduk()`, `gantiSemua()`, dan proteksi `Collections.unmodifiableList()`. |
| **`ProdukFileRepository`** | Menangani mekanisme persistensi fisik ke berkas teks secara aman. | Menerapkan *Atomic Writing* via berkas sementara `.tmp` dan *Staging Isolation* saat membaca berkas. |
| **`TransaksiPenjualan` & `DetailTransaksi`** | Mengelola transaksi kasir POS, mengalokasikan stok fisik secara FIFO, menghitung omzet penjualan & laba kotor, serta mencetak struk belanja. | Memotong stok batch persediaan saat belanja selesai dan menandai bahwa telah terjadi perubahan stok di memori (`berubah = true`). |

---

## 3. Langkah Praktikum 1 s.d. 5 (Studi Kasus Buku Perpustakaan)

Pada implementasi dasar (`Modul13Dasar.java`), sistem perpustakaan diintegrasikan ke dalam menu konsol interaktif lengkap dengan penanganan dirty flag (`berubah`). Pengujian dijalankan secara berurutan sesuai 9 skenario resmi modul ditambah 1 skenario uji ketahanan berkas rusak:

### Tabel Hasil Pengujian Fungsional Studi Kasus Modul 13
| No | Skenario Pengujian | Input Pengguna | Hasil yang Diharapkan | Hasil Aktual Program | Evaluasi / Status |
|:---:|:---|:---|:---|:---|:---:|
| 1 | **Pilihan Tidak Tersedia** | Input: `abc` | Pesan pilihan tidak tersedia; menu utama muncul kembali | `Pilihan tidak tersedia.`<br/>Menu utama kembali tampil | **VALID (100%)** |
| 2 | **Tambah Buku Baru** | Menu 1, Kode: `B001`, Judul: `Dasar Pemrograman Java` | Buku tersimpan dalam collection; status `berubah = true` | `Buku ditambahkan.`<br/>Collection terisi 1 buku | **VALID (100%)** |
| 3 | **Tambah Kode Duplikat** | Menu 1, Kode: `B001`, Judul: `Java Lanjut` | Ditolak oleh class pengelola; data sebelumnya tetap ada | `Data ditolak: Kode buku 'B001' sudah terdaftar.` (Koleksi tetap 1 buku) | **VALID (100%)** |
| 4 | **Cari Kode Terdaftar** | Menu 3, Kode: `B001` | Judul buku ditampilkan dengan benar | `Judul: Dasar Pemrograman Java` | **VALID (100%)** |
| 5 | **Hapus Kode Fiktif** | Menu 4, Kode: `B999` | Pesan buku tidak ditemukan; isi collection tidak berkurang | `Buku tidak ditemukan.`<br/>Collection tetap utuh 1 buku | **VALID (100%)** |
| 6 | **Keluar Sebelum Simpan (Batal)** | Menu 0, Konfirmasi: `TIDAK` | Operasi keluar dibatalkan; pengguna kembali ke menu utama | `Perubahan belum disimpan. Abaikan? Ketik YA: TIDAK`<br/>`[INFO] Batal keluar, kembali ke menu.` | **VALID (100%)** |
| 7 | **Simpan, Keluar, dan Jalankan Ulang** | Menu 5 (Simpan), Menu 0 (Keluar), lalu re-run program | Data tersimpan dimuat kembali secara otomatis saat program dibuka | `[INFO] Data awal berhasil dimuat (1 buku).`<br/>`B001` tampil saat dimuat | **VALID (100%)** |
| 8 | **Hapus Buku, Simpan, dan Verifikasi** | Menu 4 (Hapus B001), Menu 5 (Simpan), Menu 0, lalu re-run | Buku yang telah dihapus tidak muncul kembali di sesi baru | `[INFO] Data awal berhasil dimuat (0 buku).`<br/>`Koleksi buku masih kosong.` | **VALID (100%)** |
| 9 | **Tambah Buku, Muat Ulang, dan Batal** | Menu 1 (B002), Menu 6 (Muat Ulang), Jawab: `TIDAK` | Pemuatan ulang dibatalkan; perubahan tetap tersedia di collection | `[INFO] Pemuatan ulang dibatalkan.`<br/>`B002` tetap ada di dalam collection | **VALID (100%)** |
| 10 | **Uji Ekstra: Muat Berkas Korup** | Berkas disk dirusak tanpa koma, paksa muat ulang | Menampilkan pesan kesalahan & mempertahankan collection lama | `Terdeteksi penolakan file rusak: Format baris tidak valid.`<br/>Data aktif tetap utuh 100% | **VALID (Robust)** |

Bukti tangkapan layar terminal eksekusi:  
🖼️ [`Screenshot/Screenshot_1_Modul13_StudiKasus.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P13_Tegar Mahardika_TegarCopyFlow/Screenshot/Screenshot_1_Modul13_StudiKasus.png)

---

## 4. Tugas Proyek Kelompok — Sistem Tegar CopyFlow

Sistem **Tegar CopyFlow** menyatukan seluruh subsistem bisnis toko ke dalam antarmuka berbasis menu modular dengan rincian pemenuhan ketentuan Modul 13:

### 4.1 Pemenuhan Enam Ketentuan Modul 13
1. **Kelengkapan Fitur Menu:**  
   Menyediakan menu: `[1] Tambah Produk Baru`, `[2] Tampilkan Katalog`, `[3] Cari Produk`, `[4] Hapus Produk`, `[5] Transaksi Kasir POS`, `[6] Simpan Data ke File`, `[7] Muat Ulang Data File`, dan `[0] Keluar Aplikasi`.
2. **Pemisahan Validasi Domain:**  
   Validasi atribut numerik dan batasan karakter dilakukan di class entity `Produk`, sedangkan validasi keunikan kode unik dilakukan di class agregator `KatalogProduk`.
3. **Pesan Kesalahan Deskriptif:**  
   Setiap input salah ditangani secara spesifik (misal: `"Format harga jual tidak valid."`, `"Nama produk tidak boleh mengandung karakter pemisah '|' atau baris baru."`, `"Stok tidak cukup."`).
4. **Proteksi Perubahan Belum Disimpan (*Dirty Flag*):**  
   Variabel `berubah` bernilai `true` saat ada produk baru, produk terhapus, atau transaksi kasir yang memotong stok. Jika pengguna mencoba keluar (menu 0) atau memuat ulang file (menu 7), aplikasi mewajibkan konfirmasi eksplisit (`"Perubahan belum disimpan. Abaikan? Ketik YA: "`).
5. **Struktur Menu Modular:**  
   Logika per menu dipisahkan ke method tersendiri (`menuTambah()`, `menuCari()`, `menuTransaksiKasir()`, dll.) sehingga method `main()` dan perulangan `jalankan()` tetap ringkas dan elegan.
6. **Perbaikan Wajib (Validasi Delimiter Sejak Awal):**  
   Pemeriksaan karakter pemisah pipa `|` dan baris baru (`\n`, `\r`) dipindahkan langsung ke konstruktor `Produk` dan method `validasiDasar()`. Objek ilegal ditolak seketika pada saat pembuatan (*fail-fast*), sehingga data yang tidak dapat disimpan ke format file teks tertolak sebelum masuk ke memori atau repositori.

---

## 5. Tabel Hasil Pengujian Komprehensif Proyek Tegar CopyFlow

Pengujian komprehensif dieksekusi melalui runner otomatis `Main.java --test` yang mensimulasikan 3 sesi kerja kasir dan 11 skenario enterprise:

| No | Skenario Pengujian | Input Pengguna / Operasi | Hasil yang Diharapkan | Hasil Aktual Program | Status |
|:---:|---|---|---|---|:---:|
| **1** | **Menu Tidak Tersedia** | Input: `xyz` | Peringatan pilihan tidak tersedia, menu utama muncul kembali | `[PERINGATAN] Pilihan 'xyz' tidak tersedia. Silakan pilih 0 s.d. 7.` | **VALID** |
| **2** | **Tambah Produk Fisik Valid** | `PRD-001`, HVS A4 70gsm, stok 25 rim @ Rp 45.000, jual Rp 55.000 | Produk tersimpan ke katalog, flag `berubah = true` | `[SUKSES] Produk 'Kertas HVS A4 70gsm' (PRD-001) berhasil ditambahkan.` | **VALID** |
| **3** | **Perbaikan Wajib: Delimiter '|'** | Nama: `Buku \| Tulis Rusak` | Ditolak seketika saat objek `Produk` dibentuk di memori | `[DATA DITOLAK] Nama produk tidak boleh mengandung karakter pemisah '\|' atau baris baru.` | **VALID** |
| **4** | **Uji Kode Duplikat** | Input ulang kode `PRD-001` | Ditolak oleh class `KatalogProduk`, koleksi tidak bertambah | `[DATA DITOLAK] Produk dengan kode 'PRD-001' sudah terdaftar dalam katalog.` | **VALID** |
| **5** | **Pencarian Produk Valid & Fiktif** | Cari `PRD-001`, lalu cari `PRD-999` | `PRD-001` ditemukan lengkap beserta status stok; `PRD-999` dilaporkan tidak ditemukan | Detail `PRD-001` (25 rim, AMAN, Rp 55.000) tampil; `PRD-999` tidak ditemukan. | **VALID** |
| **6** | **Transaksi Kasir POS** | Tambah `PRD-002` (Bolpoin 120 pcs), beli 5 rim HVS, bayar tunai Rp 300.000 | Stok terpotong dari 25 ke 20 rim, struk tercetak, `berubah = true` | Struk belanja tercetak rapi, kembalian Rp 25.000, sisa stok HVS = 20 rim. | **VALID** |
| **7** | **Hapus Produk Fiktif** | Hapus kode `PRD-888` | Gagal menghapus, koleksi data tetap utuh 2 produk | `[GAGAL] Produk dengan kode 'PRD-888' tidak ditemukan. Koleksi tidak berubah.` | **VALID** |
| **8** | **Proteksi Keluar Belum Simpan** | Pilih Menu 0 (Keluar), jawab: `TIDAK` | Pembatalan keluar, program tetap berada di menu utama | `Perubahan belum disimpan. Abaikan? Ketik YA: TIDAK`<br/>`[INFO] Batal keluar. Kembali ke menu utama.` | **VALID** |
| **9** | **Simpan Data Atomik ke File** | Pilih Menu 6 (Simpan Data ke File) | Data 2 produk tertulis aman via `.tmp`, status `berubah = false` | `[SUKSES] Seluruh data (2 produk) berhasil disimpan secara atomik ke: data\produk.txt` | **VALID** |
| **10**| **Verifikasi Persistensi Sesi Baru** | Program ditutup, lalu dibuka sesi baru memuat `produk.txt` | Data termuat otomatis; sisa stok pasca transaksi terbukti permanen di disk | `[INISIALISASI] Data awal berhasil dimuat (2 produk).`<br/>Stok HVS = 20 rim, Bolpoin = 120 pcs. | **VALID** |
| **11**| **Uji Staging Isolation (File Rusak)**| Muat file corrupt (`produk_corrupt.txt`) ke katalog aktif | Pemuatan gagal tertangkap; 2 produk aktif di memori tetap utuh 100% | `[SUKSES DITANGKAP] Format salah pada baris 1: Ditemukan 4 kolom`<br/>Koleksi aktif tetap 2 produk utuh. | **VALID** |

Bukti tangkapan layar terminal eksekusi:  
🖼️ [`Screenshot/Screenshot_2_TegarCopyFlow_MenuTambahCari.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P13_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_2_TegarCopyFlow_MenuTambahCari.png)  
🖼️ [`Screenshot/Screenshot_3_TegarCopyFlow_TransaksiPOS_Simpan.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P13_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_3_TegarCopyFlow_TransaksiPOS_Simpan.png)  
🖼️ [`Screenshot/Screenshot_4_TegarCopyFlow_Persistensi_Staging.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P13_Tegar%20Mahardika_TegarCopyFlow/Screenshot/Screenshot_4_TegarCopyFlow_Persistensi_Staging.png)

---

## 6. Diagram UML Class Diagram Modul 13

Diagram kelas arsitektur Modul 13 menunjukkan pengintegrasian antarmuka konsol modular pada class `Main` yang mengendalikan alur aplikasi, dirty flag, pemanggilan model domain, alur transaksi kasir, dan persistensi berkas:

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
                                | + cetakStruk(): void            |
                                | + getTotal(): double            |
                                +---------------------------------+
```

Berkas gambar diagram beresolusi tinggi tersedia di:  
🖼️ [`UML/ClassDiagram_P13.png`](file:///d:/Mini%20Project/Pencatatan%20barang%20toko/P13_Tegar%20Mahardika_TegarCopyFlow/UML/ClassDiagram_P13.png)

---

## 7. Jawaban Pertanyaan Evaluasi (Bab 8 Modul 13)

Berikut adalah analisis konseptual dan rekayasa perangkat lunak untuk menjawab empat pertanyaan evaluasi praktikum pada Bab 8 Modul 13:

### 1. Mengapa aturan kode unik tetap ditempatkan di Perpustakaan (atau KatalogProduk)?
Aturan keunikan kode merupakan aturan bisnis pada tataran koleksi secara keseluruhan (*collection-level invariant*), bukan aturan milik entitas individu (*single-entity invariant*):
- Objek `Buku` atau `Produk` hanya bertanggung jawab atas validitas data intrinsiknya sendiri (seperti harga tidak boleh negatif atau nama tidak boleh kosong), dan secara desain tidak boleh memegang referensi ke objek-objek lain di luar dirinya (*High Cohesion*).
- Sebaliknya, class pengelola seperti `Perpustakaan` atau `KatalogProduk` adalah pemilik resmi dari kontainer struktur data (`List<Produk>`). Class inilah yang memiliki visibilitas penuh untuk menelusuri seluruh data yang sudah terdaftar sebelum mengeksekusi operasi `add()`.
- Jika aturan keunikan diletakkan di antarmuka menu `Main`, maka aturan tersebut rentan terbongkar jika di masa depan ada class atau modul lain (seperti modul REST API atau unit test) yang menambahkan data ke katalog tanpa melalui antarmuka konsol. Menempatkan aturan di class aggregator menjamin enkapsulasi domain tidak dapat dibobol.

### 2. Mengapa status berubah (dirty flag) direset setelah penyimpanan berhasil?
Variabel `berubah` (*Dirty Flag*) mencerminkan status keselarasan antara data di memori volatil (RAM) dengan data di media penyimpanan persisten (Disk):
- Ketika pengguna menambah produk, menghapus produk, atau melakukan transaksi kasir yang memotong stok, status di memori telah berubah sementara berkas di disk masih menyimpan status lama. Pada kondisi ini, `berubah = true`.
- Ketika operasi penyimpanan (`simpan()`) berhasil dieksekusi secara atomik, data pada berkas fisik telah sepenuhnya identik dengan data di RAM (**kondisi tersinkronisasi 100%**).
- Mereset variabel `berubah` menjadi `false` menandakan bahwa tidak ada data yang terancam hilang. Dengan demikian, sistem tidak perlu mengganggu pengguna dengan dialog konfirmasi saat pengguna hendak keluar dari aplikasi atau memuat ulang data.

### 3. Apa akibatnya jika status tersebut direset ketika penyimpanan gagal?
Jika status `berubah` keliru direset menjadi `false` padahal penyimpanan berkas gagal (misalnya karena media penyimpanan penuh, hak akses ditolak, atau terjadi `IOException`), maka sistem akan memberikan **ilusi keamanan data palsu (*false sense of security*)**:
- Pengguna mengira datanya telah aman di disk, padahal berkas fisik di media penyimpanan belum terbarui sama sekali.
- Ketika pengguna kemudian memilih menu keluar (menu 0), sistem tidak akan memunculkan dialog peringatan konfirmasi karena menduga status data telah tersinkronisasi (`berubah == false`).
- Akibatnya, pengguna akan menutup aplikasi tanpa rasa curiga, dan seluruh data mutasi terakhir (seperti transaksi penjualan atau alokasi stok) akan hilang permanen saat memori RAM dibebaskan oleh JVM.
- Oleh sebab itu, prinsip *Fault-Tolerant State Preservation* mewajibkan status `berubah` tetap dipertahankan bernilai `true` apabila terjadi galat saat penyimpanan.

### 4. Mengapa pengujian setiap fitur perlu dilengkapi pengujian alur penggunaan secara utuh (End-to-End)?
Pengujian fitur per unit secara terpisah hanya membuktikan bahwa potongan logika lokal berjalan benar, namun tidak mampu memverifikasi interaksi dinamis antar-fitur (*cumulative operational effects*):
1. **Memverifikasi Konsistensi Transaksional Berantai:** Pengujian alur utuh membuktikan bahwa saat kasir menjual barang di menu 5, stok batch berkurang secara FIFO, status dirty flag berubah menjadi `true`, dan saat menu 6 dijalankan, pengurangan stok tersebut benar-benar tersimpan ke piringan disk.
2. **Memverifikasi Ketahanan Antar-Sesi (*Inter-Session Lifecycle*):** Pengujian seperti Skenario 10 membuktikan bahwa seluruh state yang dimutasi pada sesi pertama dapat dibaca kembali secara utuh dan presisi pada sesi kedua setelah program ditutup total.
3. **Menguji Ketahanan terhadap Galat Beruntun (*Failure Recovery & Isolation*):** Membuktikan bahwa penolakan input (seperti karakter `|` pada Skenario 3 atau berkas korup pada Skenario 11) tidak merusak alur aplikasi berikutnya dan tidak mengotori katalog data aktif di memori (*Staging Isolation*).

---

## 8. Panduan Kompilasi dan Eksekusi Program

### Prasyarat
- Java Development Kit (JDK) versi 17 atau lebih baru (telah diuji pada Java 25 LTS).

### Perintah Terminal / PowerShell
Masuk ke direktori `SourceCode`:
```powershell
# 1. Masuk ke direktori SourceCode
cd "P13_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Kompilasi program studi kasus dasar Modul 13 (Langkah 1 s.d. 5)
javac -encoding UTF-8 -d bin src/Modul13Dasar.java

# 3. Jalankan pengujian fungsional otomatis studi kasus dasar (9 Skenario Resmi)
java -cp bin Modul13Dasar --test

# 4. Kompilasi seluruh arsitektur proyek Tegar CopyFlow (Model, Exception, Repository, dan Main)
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/repository/*.java src/Main.java

# 5. Jalankan pengujian fungsional komprehensif Tegar CopyFlow (11 Skenario)
java -cp bin Main --test

# 6. Jalankan mode interaktif manual konsol
java -cp bin Main
```

---

## 9. Struktur Berkas Repositori

```text
P13_Tegar Mahardika_TegarCopyFlow/
├── Laporan Praktikum_Pertemuan_13.pdf      # Dokumen PDF Resmi Laporan Praktikum Modul 13
├── README.md                               # Laporan Lengkap Praktikum (Markdown)
├── tools_generate_assets.py                # Utilitas otomasi pengujian, gambar terminal & rendering PDF
├── UML/
│   └── ClassDiagram_P13.png                # Diagram UML Class Terkini dengan Menu Controller & Dirty Flag
├── Screenshot/
│   ├── Screenshot_1_Modul13_StudiKasus.png
│   ├── Screenshot_2_TegarCopyFlow_MenuTambahCari.png
│   ├── Screenshot_3_TegarCopyFlow_TransaksiPOS_Simpan.png
│   └── Screenshot_4_TegarCopyFlow_Persistensi_Staging.png
└── SourceCode/
    ├── bin/                                # Direktori bytecode hasil kompilasi (.class)
    ├── data/                               # Direktori penyimpanan berkas data teks (.txt)
    │   ├── buku.txt                        # Berkas persistensi Modul13Dasar
    │   └── produk.txt                      # Berkas persistensi master produk Tegar CopyFlow
    ├── hasil-pengujian.txt                 # Log lengkap hasil pengujian teks murni UTF-8 (19.510 karakter)
    ├── hasil pengujian.pdf                 # Salinan PDF laporan pengujian
    ├── README.md                           # Panduan teknis kompilasi source code
    └── src/
        ├── Modul13Dasar.java               # Kode implementasi Langkah 1-5 Modul 13
        ├── Main.java                       # Main console interactive runner Tegar CopyFlow
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

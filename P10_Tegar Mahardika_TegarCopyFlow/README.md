# Laporan Praktikum Pemrograman Berorientasi Objek — Modul 10
**Topik:** Exception Handling dan Validasi State Objek  
**Judul Project:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Penyusun:** Tegar Mahardika  
**Dosen Pengampu:** Nirwana Haidar Hari, S.Pd., M.Kom.  
**Institusi:** Politeknik Elektronika Negeri Surabaya (PENS) — PSDKU Sumenep  
**Program Studi:** D3 Teknik Informatika  
**Tahun Ajaran:** 2026  

---

## 1. Tujuan Praktikum

Setelah menyelesaikan praktikum Modul 10 ini, mahasiswa mampu:
1. **Membedakan Validasi Input dan Penanganan Exception:** Mengidentifikasi secara tegas batas antara pencegahan input tidak valid pada gerbang awal data dan penanganan kondisi abnormal ketika alur logika bisnis berjalan.
2. **Menggunakan Komponen Exception Handling Java:** Menguasai implementasi kata kunci `try`, `catch`, `finally`, `throw`, dan `throws` secara disiplin dan tepat sasaran.
3. **Membuat Custom Exception:** Merancang dan mengimplementasikan class *checked exception* buatan sendiri yang membawa konteks data domain permasalahan aplikasi kasir dan persediaan toko.
4. **Memastikan Kegagalan Operasi Tidak Merusak State Objek (*Defensive Programming*):** Menjamin bahwa setiap kali terjadi penolakan transaksi atau kesalahan operasi, state objek (nilai atribut pada memori) tetap konsisten, tidak berubah, dan tidak korup.

---

## 2. Dasar Teori Singkat & Komponen Exception Handling

*Exception* adalah kondisi luar biasa (*abnormal event*) yang mengganggu jalannya alur normal eksekusi instruksi program. Contohnya konversi string teks menjadi integer gagal, atau transaksi kasir ditolak akibat stok barang fisik yang diminta tidak mencukupi.

### Tabel Komponen Exception Handling Java
| Komponen | Fungsi dan Peran Desain Perangkat Lunak |
|---|---|
| **`try`** | Menempatkan blok kode instruksi yang memiliki kemungkinan menghasilkan satu atau lebih exception saat dieksekusi. |
| **`catch`** | Menangkap dan menangani jenis exception spesifik dengan memberikan respon yang informatif bagi pengguna. Dilarang membuat blok `catch` kosong (*swallowed exception*). |
| **`finally`** | Menjalankan instruksi pembersihan (*cleanup*) atau pelaporan status akhir yang **pasti dieksekusi** baik terjadi exception maupun berjalan normal. Tidak boleh digunakan untuk mengubah nilai bisnis objek. |
| **`throw`** | Kata kunci aksi (*statement*) untuk secara eksplisit melempar objek instance exception ke runtime ketika suatu invariant atau aturan dilanggar. |
| **`throws`** | Kata kunci deklarasi klausul pada *method signature* untuk memberitahukan compiler dan pemanggil bahwa method tersebut berpotensi meneruskan *checked exception*. |
| **`Custom Exception`** | Class turunan dari `java.lang.Exception` yang digunakan untuk merepresentasikan kegagalan spesifik sesuai logika domain bisnis sistem. |

> **Prinsip Utama Validasi State:**  
> Lakukan seluruh pemeriksaan validasi **SEBELUM** melakukan modifikasi nilai pada variabel instance (*state*). Operasi yang ditolak wajib mempertahankan nilai stok dan status sebelumnya secara utuh (*fail-fast & atomic consistency*).

---

## 3. Langkah Praktikum 1 s.d. 3 (Studi Kasus Pembelian Produk Stok 5 Unit)

Pada bagian awal modul (Langkah 1–3), diimplementasikan class `Produk` dengan stok awal 5 unit dan custom exception `StokTidakCukupException`.

### Skenario Pengujian Berurutan
Pengujian dilakukan secara berurutan pada objek yang sama dengan daftar input awal `{"2", "0", "9", "abc"}`, ditambah pengujian nilai `"-1"` dan `"3"` sesuai instruksi modul halaman 4:

| No | Input | Exception yang Terlibat | Hasil Eksekusi Program | Stok Akhir | Keterangan Status |
|:---:|:---:|:---|:---|:---:|:---|
| 1 | `"2"` | - | Pembelian berhasil. | **3** | Pengurangan stok valid: 5 - 2 = 3. |
| 2 | `"0"` | `IllegalArgumentException` | Ditolak: Jumlah pembelian harus lebih dari nol. | **3** | State tidak berubah (ditolak di awal). |
| 3 | `"9"` | `StokTidakCukupException` | Ditolak: Stok tidak cukup. Tersedia: 3 | **3** | Minta 9 > tersedia 3, stok tetap 3. |
| 4 | `"abc"` | `NumberFormatException` | Ditolak: input harus bilangan bulat. | **3** | Gagal parsing string teks ke int. |
| 5 | `"-1"` | `IllegalArgumentException` | Ditolak: Jumlah pembelian harus lebih dari nol. | **3** | Angka negatif dicegah sebelum kurangi stok. |
| 6 | `"3"` | - | Pembelian berhasil. | **0** | **Boundary Test:** Pembelian tepat sisa stok. |

### Penjelasan: Mengapa Pembelian Tepat Sejumlah Stok ("3") Harus Diperbolehkan?
1. **Relevansi Logika Bisnis:** Toko atau unit usaha diperbolehkan menjual habis seluruh persediaan barang dagangan yang dimilikinya hingga stok bernilai 0. Menghabiskan stok gudang adalah transaksi legal dan menguntungkan.
2. **Kesesuaian Invarian State Objek:** Invarian state objek mensyaratkan bahwa stok tidak boleh bernilai negatif (`stok >= 0`). Ketika stok awal 3 dikurangi pembelian sejumlah 3 unit, sisa stok menjadi 0 (`stok == 0`). Nilai 0 adalah representasi valid bahwa produk sedang habis, bukan bilangan minus/korup.
3. **Formulasi Guard Condition:** Kondisi penolakan yang benar dalam method adalah `if (jumlah > stok)` dan **bukan** `if (jumlah >= stok)`. Dengan formula ini, kondisi pembelian tepat sejumlah stok (`jumlah == stok`) lolos pemeriksaan dan berhasil diproses.

---

## 4. Tugas Proyek Kelompok — Sistem Tegar CopyFlow

Melanjutkan arsitektur proyek Pertemuan 9 (aplikasi POS dan stok toko ATK & fotokopi **Tegar CopyFlow**), dilakukan penguatan exception handling dan pertahanan state objek.

### 4.1 Identifikasi 3+ Kondisi Kesalahan Kasus Proyek
1. **Kondisi 1: Permintaan Kuantitas Melebihi Ketersediaan Stok Fisik (`StokTidakCukupException`)**  
   Pelanggan meminta barang dagangan fisik (`PRODUCT`, misal Kertas HVS) dengan kuantitas yang melampaui akumulasi seluruh batch stok persediaan FIFO aktif.
2. **Kondisi 2: Pelanggaran Siklus Transaksi Kasir (`TransaksiInvalidException`)**  
   Kasir mencoba menambahkan item belanja ke dalam transaksi yang sudah berstatus dibatalkan (`CANCELLED`), atau melakukan pembatalan ganda (*double rollback*).
3. **Kondisi 3: Validasi Masukan Argumen & Format Numerik (`IllegalArgumentException` & `NumberFormatException`)**  
   - Kuantitas item atau kuantitas batch awal `<= 0`.
   - Harga jual, harga beli kulakan, atau biaya per unit bernilai negatif (`< 0`).
   - Mencoba mendaftarkan batch fisik pada tipe produk jasa non-fisik (`SERVICE` seperti fotokopi/jilid).
   - Kasir salah menginput huruf teks pada form input kuantitas numerik (misal input `"limapuluh"`).
   - Inisialisasi transaksi kasir dengan metode pembayaran yang tidak didukung (misal `"PayLater"`).

### 4.2 Perancangan Custom Exception
Dibuat 2 custom checked exception di package `exception`:
- **`StokTidakCukupException`:**
  ```java
  public class StokTidakCukupException extends Exception {
      private final String namaProduk;
      private final int stokTersedia;
      private final int jumlahDiminta;
      private final String satuan;
      // Constructor membawa konteks lengkap error untuk umpan balik kasir
  }
  ```
- **`TransaksiInvalidException`:**
  ```java
  public class TransaksiInvalidException extends Exception {
      private final String invoiceNumber;
      private final String statusSaatIni;
      // Membawa identitas nomor invoice dan state transaksi saat operasi ditolak
  }
  ```

### 4.3 Penerapan Validasi Sebelum Mutasi State
Pada class `Produk`, method `alokasikanStokFifo(int jumlahMinta)` menerapkan prinsip *Fail-Fast*:
```java
// VALIDASI 1: Integritas status aktif
if (!this.aktif) {
    throw new IllegalStateException("Produk '" + this.nama + "' sedang non-aktif...");
}
// VALIDASI 2: Integritas batas input
if (jumlahMinta <= 0) {
    throw new IllegalArgumentException("Jumlah permintaan stok harus lebih dari 0.");
}
// VALIDASI 3: Ketersediaan stok total sebelum memotong batch
int totalTersedia = hitungTotalStok();
if (jumlahMinta > totalTersedia) {
    // Lempar custom exception! State batch belum disentuh sama sekali
    throw new StokTidakCukupException(this.nama, totalTersedia, jumlahMinta, this.satuan);
}

// MUTASI STATE: Hanya berjalan jika SELURUH validasi di atas lolos
for (BatchStok batch : this.daftarBatch) { ... }
```

---

## 5. Tabel Hasil Pengujian Komprehensif Proyek

| Skenario Pengujian | Input / Operasi yang Diuji | Hasil yang Diharapkan | Hasil Aktual Program | State Akhir Objek & Integritas |
|---|---|---|---|---|
| **Tahap 3: Trx Kasir 1** | HVS 12 rim, Pulpen 5 pcs, Jasa fotokopi 100 lbr, Jilid 2 buku | Transaksi sukses, alokasi FIFO terpotong, struk tercetak | Transaksi Sukses, Struk tercetak rapi (Margin: 20.4%) | HVS sisa 13 rim, Pulpen sisa 35 pcs. State valid. |
| **Tahap 4: Trx Kasir 2 & Rollback** | Ambil 8 rim HVS, lalu panggil `batalkanTransaksi()` | Kuantitas terpotong, lalu kembali utuh ke batch FIFO asal | Status Transaksi menjadi `CANCELLED`, stok kembali utuh | Sisa stok HVS kembali utuh 13 rim. Rollback konsisten. |
| **Uji 1: Permintaan Stok Kurang** | Beli 30 rim HVS (hanya tersedia 13 rim) | Ditolak melempar `StokTidakCukupException` | `Ditolak via Custom Exception [StokTidakCukupException]` | Sisa stok HVS tetap **13 rim** (tidak berubah / konsisten). |
| **Uji 2: Kuantitas Tidak Positif** | Beli -5 pcs Pulpen | Ditolak melempar `IllegalArgumentException` | `Ditolak: Jumlah item pembelian harus lebih dari 0.` | Sisa stok Pulpen tetap **35 pcs** (tidak berkurang). |
| **Uji 3: Parsing Input Bukan Angka** | Form input kasir berisi string `"limapuluh"` | Ditolak melempar `NumberFormatException` | `Ditolak: format kuantitas harus berupa bilangan bulat valid!` | Sisa stok Pulpen tetap **35 pcs** (tidak disentuh). |
| **Uji 4: Boundary Nilai Pas Stok** | Beli tepat sisa 13 rim dari persediaan 13 rim HVS | Berhasil diproses, stok habis menjadi 0 | `Pembelian tepat sejumlah stok BERHASIL DIPERBOLEHKAN!` | Sisa stok HVS menjadi **0 rim** `[Status: HABIS]`. |
| **Uji 5: Mutasi Transaksi Batal** | Tambah item baru ke transaksi berstatus `CANCELLED` | Ditolak melempar `TransaksiInvalidException` | `Ditolak: Tidak dapat menambahkan item ke transaksi yang sudah CANCELLED` | Transaksi tetap `CANCELLED`, tidak ada item bertambah. |
| **Uji 6: Double Cancel (Pembatalan Ganda)** | Panggil `batalkanTransaksi()` 2 kali berturut-turut | Ditolak melempar `TransaksiInvalidException` | `Ditolak: Transaksi ini sudah dibatalkan sebelumnya` | Mencegah pengembalian stok berulang (anti kelebihan stok). |
| **Uji 7: Metode Pembayaran Invalid** | Inisialisasi transaksi dengan metode `"PayLater"` | Ditolak melempar `IllegalArgumentException` | `Ditolak: Metode pembayaran 'PayLater' tidak valid.` | Objek transaksi tidak valid dicegah sebelum masuk sistem. |
| **Uji 8: Transaksi Produk Non-Aktif** | Transaksi pada pulpen yang di-nonaktifkan | Ditolak melempar `IllegalStateException` | `Ditolak: Produk 'Pulpen' sedang non-aktif...` | Sisa stok Pulpen tetap **35 pcs**. State aman. |

---

## 6. Penjelasan Singkat Alasan Penggunaan Custom Exception

Penggunaan custom exception seperti `StokTidakCukupException` dan `TransaksiInvalidException` memiliki keunggulan fundamental dibandingkan tipe exception bawaan Java standar:
1. **Keterbacaan dan Makna Domain Bisnis yang Kuat (*Domain-Driven Clarity*):**  
   Nama class exception secara instan menjelaskan hakikat kegagalan bisnis yang terjadi kepada programmer dan arsitek sistem, tanpa harus menebak arti pesan error generik seperti `Exception` atau `RuntimeException`.
2. **Menyimpan Informasi Kontekstual Kaya (*Rich Contextual Data*):**  
   Custom exception dapat dirancang memiliki atribut spesifik, seperti `namaProduk`, `stokTersedia`, dan `jumlahDiminta`. Hal ini memungkinkan modul antarmuka (UI Kasir) membuat respon yang solutif, misalnya: *"Stok Kertas HVS hanya tersisa 13 rim. Apakah Anda ingin membeli 13 rim tersebut?"*.
3. **Pemisahan Alur Penanganan yang Presisi (*Granular Handling*):**  
   Pemanggil dapat memisahkan blok `catch` untuk masalah persediaan (`StokTidakCukupException`) dengan logika menawarkan restock kulakan, sementara `IllegalArgumentException` diarahkan untuk menampilkan pesan koreksi input form kasir.

---

## 7. Jawaban Pertanyaan Evaluasi (Bab 8 Modul)

### 1. Apa perbedaan `throw` dan `throws`?
* **`throw`:** Merupakan kata kunci aksi (*statement*) yang diletakkan di dalam badan method untuk secara aktif melempar instance objek exception tertentu (contoh: `throw new StokTidakCukupException("Stok habis");`).
* **`throws`:** Merupakan kata kunci deklarasi klausul pada signature deklarasi method untuk memberi tahu compiler dan pemanggil bahwa method tersebut berpotensi melemparkan checked exception (contoh: `public void beli(int jumlah) throws StokTidakCukupException`). Pemanggil diwajibkan oleh compiler Java untuk membungkus panggilan dalam blok `try-catch` atau mendeklarasikan ulang dengan `throws`.

### 2. Mengapa pemeriksaan stok dilakukan sebelum pengurangan stok?
Pemeriksaan stok dilakukan sebelum pemotongan stok untuk menerapkan prinsip **Fail-Fast** dan menjamin **integritas invarian state objek**. Jika pengurangan dilakukan terlebih dahulu kemudian baru dicek apakah hasilnya negatif, maka state objek pada memori sempat mengalami korupsi data (bernilai minus). Jika di tengah proses terjadi interupsi atau exception lain, data sudah terlanjur rusak. Dengan memeriksa di awal (*guard clause*), bila kondisi tidak memenuhi, exception segera dilempar dan nilai variabel stok dijamin tetap utuh pada kondisi valid terakhir.

### 3. Apa akibatnya jika exception ditangkap tetapi tidak ditangani (*Empty Catch / Swallowed Exception*)?
Jika exception ditangkap dengan blok catch kosong (`catch (Exception e) {}`):
1. **Kegagalan Tersembunyi (*Silent Failure*):** Aplikasi melanjutkan alur instruksi berikutnya seolah-olah seluruh operasi berhasil, padahal data bisnis di dalamnya gagal diproses.
2. **Korosi Data Sistem:** Dapat memicu inkonsistensi berantai pada data lain (misal uang kas bertambah namun barang tidak berkurang).
3. **Debugging Sangat Sulit:** Pengembang dan tim operasional kasir kehilangan jejak diagnosa karena tidak ada pesan kesalahan atau *stack trace* yang dicatat.

### 4. Mengapa perubahan stok tidak ditempatkan dalam `finally`?
Blok `finally` memiliki karakteristik dasar yaitu **selalu dieksekusi dalam kondisi apa pun**, baik operasi pada blok `try` berhasil maupun terjadi kegagalan/exception pada blok `catch`. Jika perubahan stok (misal pengurangan unit) diletakkan di dalam `finally`, maka stok barang akan tetap terpotong meskipun transaksi ditolak (misalnya karena uang pembayaran kurang atau input kuantitas salah format). Blok `finally` dirancang khusus untuk rutinitas pembersihan sumber daya (*resource cleanup*) seperti menutup stream berkas dan koneksi database, bukan untuk mutasi logika bisnis.

---

## 8. Panduan Kompilasi dan Eksekusi Program

### Prasyarat
- Java Development Kit (JDK) 17 atau lebih baru (telah diuji pada Java 25 LTS).

### Perintah Terminal / PowerShell
Masuk ke direktori `SourceCode`:
```powershell
# 1. Pindah ke direktori SourceCode
cd "P10_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Kompilasi program studi kasus dasar Modul 10 (Langkah 1 - 3)
javac -encoding UTF-8 -d bin src/Modul10Dasar.java

# 3. Jalankan pengujian studi kasus dasar
java -cp bin Modul10Dasar

# 4. Kompilasi seluruh arsitektur proyek Tegar CopyFlow
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/Main.java

# 5. Jalankan pengujian komprehensif proyek Tegar CopyFlow
java -cp bin Main
```

---

## 9. Struktur Berkas Repositori

```text
P10_Tegar Mahardika_TegarCopyFlow/
├── Laporan_Praktikum_Pertemuan_10.pdf       # Dokumen PDF Resmi Modul 10
├── README.md                               # Laporan Lengkap Praktikum (Markdown)
├── tools_generate_assets.py                # Utilitas otomatisasi pengujian & rendering PDF
├── UML/
│   └── ClassDiagram_P10.png                # Diagram UML Class Terkini dengan Exception
├── Screenshot/
│   ├── Screenshot_1_Modul10_StudiKasus.png
│   ├── Screenshot_2_TegarCopyFlow_AlurUtama.png
│   ├── Screenshot_3_TegarCopyFlow_PengujianException.png
│   └── Screenshot_4_TegarCopyFlow_Rekapitulasi.png
└── SourceCode/
    ├── bin/                                # Direktori bytecode (.class)
    ├── hasil-pengujian.txt                 # Log lengkap hasil pengujian teks UTF-8
    ├── hasil_pengujian.pdf                 # Salinan PDF laporan pengujian
    ├── README.md                           # Panduan teknis kompilasi source code
    └── src/
        ├── Modul10Dasar.java               # Kode implementasi Langkah 1-3 Modul 10
        ├── Main.java                       # Main test runner proyek Tegar CopyFlow
        ├── exception/
        │   ├── StokTidakCukupException.java
        │   └── TransaksiInvalidException.java
        └── model/
            ├── BatchStok.java
            ├── DetailTransaksi.java
            ├── Produk.java
            └── TransaksiPenjualan.java
```


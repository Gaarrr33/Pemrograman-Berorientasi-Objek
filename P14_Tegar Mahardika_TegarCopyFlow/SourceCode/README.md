# Panduan Kompilasi & Pengujian Source Code — Modul 14 PBO
**Proyek:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Modul:** Modul 14 — Pengujian, Refactoring, dan Finalisasi Proyek  
**Penyusun:** Tegar Mahardika (PENS PSDKU Sumenep)  

---

## 1. Struktur Direktori Source Code

```text
SourceCode/
├── bin/                             # Direktori bytecode hasil kompilasi (.class)
├── data/                            # Direktori penyimpanan berkas data teks (.txt)
│   ├── buku.txt                     # Data persistensi Modul14Dasar (format CSV kode,judul)
│   └── produk.txt                   # Data master produk toko Tegar CopyFlow (10 kolom delimitator '|')
├── hasil-pengujian.txt              # Log lengkap eksekusi pengujian murni teks UTF-8 (18.953 karakter)
├── hasil pengujian.pdf              # Salinan dokumen laporan resmi pengujian
├── README.md                        # Berkas panduan teknis kompilasi ini
└── src/
    ├── Modul14Dasar.java            # Implementasi 10 skenario pengujian dasar (Buku & Perpustakaan)
    ├── Main.java                    # Aplikasi konsol refactored Tegar CopyFlow (Dual Mode)
    ├── exception/
    │   ├── StokTidakCukupException.java    # Custom Checked Exception: Alokasi stok gagal
    │   └── TransaksiInvalidException.java  # Custom Checked Exception: Siklus transaksi invalid
    ├── model/
    │   ├── BatchStok.java                  # Entitas batch kulakan FIFO
    │   ├── DetailTransaksi.java            # Entitas baris belanja kasir
    │   ├── KatalogProduk.java              # Aggregator domain koleksi List<Produk>
    │   ├── Produk.java                     # Entitas produk lengkap dengan validasi delimiter '|'
    │   └── TransaksiPenjualan.java         # Agregator transaksi penjualan kasir POS (dengan Rollback)
    └── repository/
        └── ProdukFileRepository.java       # Repository Pattern: Atomic Writing & Staging Isolation
```

---

## 2. Cara Kompilasi dan Menjalankan Program

### Prasyarat
- Java Development Kit (JDK) versi 17 atau lebih baru (telah diuji pada Java 25 LTS).
- Shell: Command Prompt, PowerShell, atau Terminal Linux/macOS.
- Disarankan menggunakan `-encoding UTF-8` agar karakter teks dan format simbol mata uang tampil konsisten.

### Perintah Terminal / PowerShell

```powershell
# 1. Pindah ke direktori SourceCode
cd "P14_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Buat direktori bin jika belum ada
mkdir bin -ErrorAction SilentlyContinue

# 3. Kompilasi dan Jalankan Studi Kasus Dasar Modul 14 (10 Skenario Resmi)
javac -encoding UTF-8 -d bin src/Modul14Dasar.java
java -cp bin Modul14Dasar --test

# Atau jalankan interaktif:
# java -cp bin Modul14Dasar

# 4. Kompilasi Seluruh Class Proyek Tegar CopyFlow (Model, Exception, Repository, dan Main)
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/repository/*.java src/Main.java

# 5. Jalankan Pengujian Fungsional & Regresi Otomatis (13 Skenario Lengkap)
java -cp bin Main --test

# 6. Jalankan Aplikasi Konsol Interaktif Manual
java -cp bin Main
```

---

## 3. Rangkuman Refactoring & Skenario Pengujian

### A. Refactoring Struktur Kode (Poin 4 Modul 14)
1. **Helper Input Konsol Reusable & Type-Safe:**  
   Mengekstrak method `bacaStringWajib()`, `bacaInt()`, dan `bacaDouble()` pada class `Main`. Menghilangkan belasan duplikasi blok `try-catch (NumberFormatException)` pada penanganan input menu.
2. **Sentralisasi Normalisasi String:**  
   Pencarian kode produk di `KatalogProduk.cariProduk()` dinormalisasi menggunakan `p.getKode().equalsIgnoreCase(kode.trim())` sehingga pencarian huruf kecil (misal: `prd-001`) dan variasi whitespace ditangani secara deterministik.
3. **Isolasi Transaksi & Rollback Otomatis:**  
   Menambahkan method `trx.batalkanTransaksi()` yang otomatis mengembalikan kuantitas alokasi stok ke batch FIFO jika nominal pembayaran uang tunai kurang dari total belanja.

### B. Hasil Pengujian Regresi
- **Studi Kasus Perpustakaan (`Modul14Dasar.java`):** Lulus 10 dari 10 skenario resmi (koleksi kosong, tambah valid, penolakan duplikat, penolakan spasi saja, penolakan karakter `|`, pencarian huruf kecil `b001`, hapus kode fiktif, persistensi disk, staging isolation berkas rusak, dan pembatalan keluar aplikasi).
- **Proyek Tegar CopyFlow (`Main.java`):** Lulus 13 skenario komprehensif mencakup operasional CRUD, alokasi FIFO kasir POS, pencetakan struk belanja, pembatalan keluar saat dirty flag aktif, penyimpanan atomik, verifikasi pemulihan data antar-sesi, dan isolasi staging saat berkas rusak ditolak.

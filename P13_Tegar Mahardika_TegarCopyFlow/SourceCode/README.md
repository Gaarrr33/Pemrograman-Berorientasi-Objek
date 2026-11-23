# Panduan Kompilasi & Pengujian Source Code — Modul 13 PBO
**Proyek:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Modul:** Modul 13 — Integrasi Aplikasi Berbasis Menu dan Pengujian Fungsional  
**Penyusun:** Tegar Mahardika (PENS PSDKU Sumenep)  

---

## 1. Struktur Direktori Source Code

```text
SourceCode/
├── bin/                             # Direktori bytecode hasil kompilasi (.class)
├── data/                            # Direktori penyimpanan berkas data teks (.txt)
│   ├── buku.txt                     # Data persistensi Modul13Dasar (format CSV kode,judul)
│   └── produk.txt                   # Data master produk toko Tegar CopyFlow (10 kolom delimitator '|')
├── hasil-pengujian.txt              # Log lengkap eksekusi pengujian murni teks UTF-8 (19.510 karakter)
├── hasil pengujian.pdf              # Salinan dokumen laporan resmi pengujian
├── README.md                        # Berkas panduan teknis kompilasi ini
└── src/
    ├── Modul13Dasar.java            # Implementasi Langkah 1 s.d. 5 Modul 13 (Buku & Perpustakaan)
    ├── Main.java                    # Aplikasi konsol interaktif Tegar CopyFlow (Dual Mode)
    ├── exception/
    │   ├── StokTidakCukupException.java    # Custom Checked Exception: Stok alokasi tidak cukup
    │   └── TransaksiInvalidException.java  # Custom Checked Exception: Siklus transaksi invalid
    ├── model/
    │   ├── BatchStok.java                  # Entitas batch persediaan kulakan FIFO
    │   ├── DetailTransaksi.java            # Entitas baris belanja transaksi kasir
    │   ├── KatalogProduk.java              # Aggregator domain koleksi List<Produk>
    │   ├── Produk.java                     # Entitas produk lengkap dengan validasi delimiter '|'
    │   └── TransaksiPenjualan.java         # Agregator transaksi penjualan kasir POS
    └── repository/
        └── ProdukFileRepository.java       # Repository Pattern: Atomic Writing & Staging Isolation
```

---

## 2. Cara Kompilasi dan Menjalankan Program

### Prasyarat
- Java Development Kit (JDK) versi 17 atau lebih baru (telah diuji pada Java 25 LTS).
- Shell: Command Prompt, PowerShell, atau Terminal Linux/macOS.
- Disarankan menggunakan flag `-encoding UTF-8` untuk memastikan konsistensi karakter teks dan simbol mata uang.

### Perintah Terminal / PowerShell

```powershell
# 1. Pindah ke direktori SourceCode
cd "P13_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Buat direktori bin jika belum ada
mkdir bin -ErrorAction SilentlyContinue

# 3. Kompilasi dan Jalankan Studi Kasus Dasar Modul 13 (Buku & Perpustakaan)
# Menjalankan 9 Skenario Pengujian Fungsional Resmi Modul 13 + Uji Berkas Rusak:
javac -encoding UTF-8 -d bin src/Modul13Dasar.java
java -cp bin Modul13Dasar --test

# Atau jalankan secara interaktif manual:
# java -cp bin Modul13Dasar

# 4. Kompilasi Seluruh Class Proyek Tegar CopyFlow
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/repository/*.java src/Main.java

# 5. Jalankan Pengujian Fungsional Otomatis Tegar CopyFlow (11 Skenario Komprehensif)
java -cp bin Main --test

# 6. Jalankan Aplikasi Konsol Interaktif Tegar CopyFlow Secara Manual
java -cp bin Main
```

---

## 3. Rangkuman Skenario Pengujian

### A. Pengujian Langkah 1 s.d. 5 Modul 13 (`Modul13Dasar.java`)
Menerapkan 9 skenario fungsional berurutan sesuai Bab 5 modul resmi ditambah skenario pemulihan file rusak:
1. **Skenario 1 (Pilihan abc):** Menangani input menu tidak valid tanpa crash; pesan kesalahan ditampilkan dan menu muncul kembali.
2. **Skenario 2 (Tambah Buku Baru):** Menambahkan `B001` ("Dasar Pemrograman Java"); objek tersimpan dalam collection dan flag `berubah = true`.
3. **Skenario 3 (Tambah Kode Duplikat):** Mencoba menambahkan kembali buku berkode `B001`; ditolak oleh method pengelola, koleksi awal tetap 1 buku.
4. **Skenario 4 (Cari Kode Terdaftar):** Mencari buku `B001`; judul berhasil ditampilkan.
5. **Skenario 5 (Hapus Kode Fiktif):** Menghapus kode `B999` yang tidak terdaftar; dilaporkan tidak ditemukan dan isi koleksi tidak berkurang.
6. **Skenario 6 (Keluar Tanpa Simpan & Tolak):** Memilih menu keluar saat ada perubahan, lalu menjawab `TIDAK`; sistem membatalkan proses keluar dan kembali ke menu.
7. **Skenario 7 (Simpan, Keluar, dan Muat Otomatis):** Menyimpan data, keluar dari aplikasi, lalu menjalankan program kembali; data `B001` terbukti otomatis dimuat kembali dari disk.
8. **Skenario 8 (Hapus Buku, Simpan, dan Verifikasi):** Menghapus buku `B001`, menyimpan file, lalu menjalankan ulang program; buku terhapus terbukti tidak muncul kembali (koleksi kosong).
9. **Skenario 9 (Tambah Buku, Muat Ulang, dan Tolak):** Menambahkan buku baru `B002`, memilih muat ulang, lalu menjawab `TIDAK`; buku `B002` terbukti tetap dipertahankan di collection.
10. **Skenario Ekstra (Muat Berkas Korup):** Merusak format berkas di disk, lalu memaksa muat ulang; sistem menangani `IllegalArgumentException` dan mempertahankan data aktif di memori tetap utuh 100%.

### B. Pengujian Proyek Tegar CopyFlow (`Main.java`)
Terbagi ke dalam 3 sesi operasional dengan 11 skenario enterprise:
- **Sesi 1 (Operasional Awal & Transaksi POS):**
  - Skenario 1: Input menu sembarang (`xyz`) ditangani ramah.
  - Skenario 2: Menambahkan produk fisik valid `PRD-001` (Kertas HVS A4, stok 25 rim).
  - Skenario 3 (**Perbaikan Wajib Modul 13**): Menolak input nama produk yang mengandung karakter pemisah pipa `|` (`Buku | Tulis Rusak`) langsung saat objek dibentuk di konstruktor.
  - Skenario 4: Menolak penambahan produk dengan kode duplikat `PRD-001`.
  - Skenario 5: Pencarian produk valid (`PRD-001`) vs produk fiktif (`PRD-999`).
  - Skenario 6: Menambahkan produk `PRD-002` (Bolpoin 120 pcs) dan melakukan transaksi kasir POS (beli 5 rim HVS, total Rp 275.000, stok terpotong aman menjadi 20 rim, cetak struk kasir, `berubah = true`).
  - Skenario 7: Menghapus produk tidak terdaftar `PRD-888` (gagal, state aman).
  - Skenario 8: Perlindungan keluar aplikasi saat `berubah == true` (jawab `TIDAK` -> tetap di menu).
  - Skenario 9: Menyimpan seluruh katalog ke file disk `data/produk.txt` via *Atomic Writing* (`berubah = false`).
- **Sesi 2 (Verifikasi Persistensi Sesi Baru):**
  - Skenario 10: Memulai sesi aplikasi baru dari disk; terverifikasi sisa stok HVS tetap 20 rim dan Bolpoin 120 pcs (tersimpan permanen di disk).
- **Sesi 3 (Uji Ketahanan State / Staging Isolation):**
  - Skenario 11: Memuat berkas korup (`produk_corrupt.txt`); deserialisasi gagal tertangkap, dan 2 produk aktif di memori tetap utuh 100% tanpa ada data yang terhapus atau rusak.


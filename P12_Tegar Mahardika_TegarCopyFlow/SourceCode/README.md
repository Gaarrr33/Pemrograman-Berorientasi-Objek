# Panduan Kompilasi & Pengujian Source Code — Modul 12 PBO
**Proyek:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Modul:** Modul 12 — Penyimpanan dan Pembacaan Data ke File (File I/O & Repository Pattern)  
**Penyusun:** Tegar Mahardika (PENS PSDKU Sumenep)  

---

## 1. Struktur Direktori Source Code

```text
SourceCode/
├── bin/                             # Direktori bytecode hasil kompilasi (.class)
├── data/                            # Direktori penyimpanan berkas data teks (.txt)
│   ├── buku.txt                     # Data buku normal hasil persistensi Modul12Dasar
│   ├── buku_duplikat.txt            # Sampel data uji kode buku duplikat
│   ├── buku_format_salah.txt        # Sampel data uji format baris buku tanpa koma
│   ├── buku_judul_kosong.txt        # Sampel data uji judul buku kosong
│   ├── buku_kosong.txt              # Berkas kosong ukuran 0 byte
│   ├── produk.txt                   # Data master produk toko (10 kolom delimitator koma)
│   ├── produk_angka_rusak.txt       # Sampel data uji kerusakan tipe numerik (NumberFormatException)
│   ├── produk_duplikat.txt          # Sampel data uji kode produk terduplikasi
│   ├── produk_format_salah.txt      # Sampel data uji kolom kurang (< 10 kolom)
│   ├── produk_harga_negatif.txt     # Sampel data uji pelanggaran invariant harga (< 0)
│   └── produk_kosong.txt            # Berkas kosong master produk
├── hasil-pengujian.txt              # Log lengkap eksekusi pengujian murni teks UTF-8
├── hasil pengujian.pdf              # Salinan dokumen laporan resmi pengujian
├── README.md                        # Berkas panduan teknis kompilasi ini
└── src/
    ├── Modul12Dasar.java            # Implementasi Langkah 1 s.d. 5 Modul 12 (Buku & Perpustakaan)
    ├── Main.java                    # Runner pengujian komprehensif Tegar CopyFlow (6 Tahap)
    ├── exception/
    │   ├── StokTidakCukupException.java    # Custom Checked Exception: Kegagalan alokasi stok
    │   └── TransaksiInvalidException.java  # Custom Checked Exception: Siklus transaksi invalid
    ├── model/
    │   ├── BatchStok.java                  # Entitas batch kulakan persediaan barang
    │   ├── DetailTransaksi.java            # Entitas baris belanja transaksi kasir
    │   ├── KatalogProduk.java              # Pengelola koleksi List<Produk> (Aggregator)
    │   ├── Produk.java                     # Entitas produk lengkap dengan serialisasi toFileLine()
    │   └── TransaksiPenjualan.java         # Agregator transaksi penjualan kasir POS
    └── repository/
        └── ProdukFileRepository.java       # Repository Pattern: Serialisasi/Deserialisasi I/O aman
```

---

## 2. Cara Kompilasi dan Menjalankan Program

### Prasyarat
- Java Development Kit (JDK) versi 17 atau lebih baru (telah teruji pada Java 25 LTS).
- Shell: Command Prompt, PowerShell, atau Terminal Linux/macOS.
- Enkoding: UTF-8 disarankan untuk menjaga integritas karakter teks.

### Perintah Terminal / PowerShell

```powershell
# 1. Pindah ke direktori SourceCode
cd "P12_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Buat folder bin jika belum ada
mkdir bin -ErrorAction SilentlyContinue

# 3. Kompilasi dan Jalankan Studi Kasus Dasar Modul 12 (Buku & Perpustakaan)
javac -encoding UTF-8 -d bin src/Modul12Dasar.java
java -cp bin Modul12Dasar

# 4. Kompilasi Seluruh Class Proyek Tegar CopyFlow (Model, Exception, Repository, dan Main)
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/repository/*.java src/Main.java

# 5. Jalankan Pengujian Lengkap Proyek Tegar CopyFlow (6 Tahap Pengujian Komprehensif)
java -cp bin Main
```

---

## 3. Rangkuman Skenario Pengujian

### A. Pengujian Langkah 1 s.d. 5 Modul 12 (`Modul12Dasar.java`)
Menerapkan 8 skenario pengujian berurutan untuk memverifikasi fungsionalitas persistensi file teks dasar:
1. **Skenario 1 (Simpan Valid):** Menyimpan daftar buku (`B001` & `B002`) ke `data/buku.txt`. File terbentuk dan terisi dengan format `kode,judul`.
2. **Skenario 2 (Muat Valid):** Membaca kembali `data/buku.txt` ke dalam objek `PerpustakaanModul` baru. Data 2 buku berhasil dipulihkan secara akurat.
3. **Skenario 3 (Uji File Tidak Ditemukan):** Membaca dari berkas yang tidak ada (`data/buku_hilang.txt`). Program menangani `IOException` secara elegan tanpa *crash*.
4. **Skenario 4 (Uji Format Salah):** Membaca baris tanpa delimitator koma (`data/buku_format_salah.txt`). Program melempar dan menangani `IllegalArgumentException`.
5. **Skenario 5 (Uji Judul Kosong):** Membaca baris dengan judul kosong (`data/buku_judul_kosong.txt`). Ditolak oleh validasi entitas buku.
6. **Skenario 6 (Uji Kode Duplikat):** Membaca file dengan dua buku berkode sama (`data/buku_duplikat.txt`). Ditolak oleh aturan keunikan repositori.
7. **Skenario 7 (Uji File Kosong):** Membaca berkas 0 byte (`data/buku_kosong.txt`). Ditangani dengan mengembalikan status koleksi kosong.
8. **Skenario 8 (Uji Konsistensi State / Staging):** Memuat file korup ke dalam repositori yang sudah berisi data awal. Koleksi lama terbukti tetap utuh 100% karena pembacaan diproses di katalog staging sebelum diterapkan (*atomic swap*).

### B. Pengujian Proyek Tegar CopyFlow (`Main.java`)
Terbagi ke dalam 6 fase pengujian enterprise:
1. **Tahap 1: Inisialisasi & Persistensi Atomic:** Menginstansiasi 5 master produk (kertas HVS, bolpoin, spidol, fotokopi, laminating) dan menyimpannya secara atomik ke `data/produk.txt` melalui file `.tmp`.
2. **Tahap 2: Sesi Baru & Deserialisasi Bersih:** Membuat sesi aplikasi baru, membaca `data/produk.txt`, dan memvalidasi keutuhan 10 atribut untuk masing-masing entitas produk.
3. **Tahap 3: Pengujian Bertahan (Defensive Testing):** Menguji 6 kondisi anomali berkas:
   - *File Missing:* Menangani berkas fiktif.
   - *Corrupt Columns:* Menolak baris dengan kolom kurang dari 10.
   - *Malformed Number:* Menolak baris dengan angka rusak via `NumberFormatException`.
   - *Negative Price:* Menolak nilai harga/stok negatif via aturan invariant entitas.
   - *Duplicate Code:* Menolak data duplikat dalam berkas.
   - *Empty File:* Menangani berkas kosong tanpa galat.
4. **Tahap 4: Uji Konsistensi State (Atomic State Preservation):** Membuktikan bahwa kegagalan pembacaan file korup tidak mengotori katalog aktif yang sedang berjalan di memori.
5. **Tahap 5: Integrasi Transaksi Kasir POS:** Melakukan transaksi penjualan (mengurangi stok fisik), mencetak struk belanja, dan menyimpan status terbaru ke disk.
6. **Tahap 6: Verifikasi Persistensi Pasca Transaksi:** Membuka sesi baru ketiga untuk membuktikan bahwa perubahan stok akibat transaksi kasir telah tersimpan permanen di media disk.


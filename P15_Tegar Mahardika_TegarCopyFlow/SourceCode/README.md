# Panduan Kompilasi & Pengujian Source Code — Modul 15 PBO
**Proyek:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Modul:** Modul 15 — Demonstrasi Proyek dan Evaluasi Pemahaman Individu  
**Penyusun:** Tegar Mahardika (PENS PSDKU Sumenep)  

---

## 1. Struktur Direktori Source Code

```text
SourceCode/
├── bin/                             # Direktori bytecode hasil kompilasi (.class)
├── data/                            # Direktori penyimpanan berkas data teks (.txt)
│   ├── buku.txt                     # Data persistensi Modul15Dasar (format CSV kode,judul)
│   └── produk.txt                   # Data master produk toko Tegar CopyFlow (10 kolom delimitator '|')
├── hasil-pengujian.txt              # Log lengkap eksekusi demonstrasi murni teks UTF-8 (18.944 karakter)
├── hasil pengujian.pdf              # Salinan dokumen laporan resmi pengujian
├── README.md                        # Berkas panduan teknis kompilasi ini
└── src/
    ├── Modul15Dasar.java            # Implementasi studi kasus Perpustakaan alur demonstrasi penuh
    ├── Main.java                    # Aplikasi konsol interaktif Tegar CopyFlow (Versi Final + Live Coding)
    ├── exception/
    │   ├── StokTidakCukupException.java    # Custom Checked Exception: Alokasi stok gagal
    │   └── TransaksiInvalidException.java  # Custom Checked Exception: Siklus transaksi invalid
    ├── model/
    │   ├── BatchStok.java                  # Entitas batch persediaan kulakan FIFO
    │   ├── DetailTransaksi.java            # Entitas baris belanja kasir
    │   ├── KatalogProduk.java              # Aggregator domain koleksi List<Produk> (dengan Live Coding)
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
- Gunakan flag `-encoding UTF-8` agar karakter teks dan format simbol mata uang tampil konsisten.

### Perintah Terminal / PowerShell

```powershell
# 1. Pindah ke direktori SourceCode
cd "P15_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Buat direktori bin jika belum ada
mkdir bin -ErrorAction SilentlyContinue

# 3. Kompilasi dan Jalankan Studi Kasus Dasar Modul 15 (Alur Penuh Perpustakaan)
javac -encoding UTF-8 -d bin src/Modul15Dasar.java
java -cp bin Modul15Dasar --test

# Atau jalankan interaktif:
# java -cp bin Modul15Dasar

# 4. Kompilasi Seluruh Class Proyek Tegar CopyFlow Final (Model, Exception, Repository, dan Main)
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/repository/*.java src/Main.java

# 5. Jalankan Demonstrasi Otomatis Tegar CopyFlow (Mencakup Fitur Live Coding)
java -cp bin Main --test

# 6. Jalankan Mode Konsol Interaktif Manual untuk Demonstrasi Dosen
java -cp bin Main
```

---

## 3. Fitur Hasil Live Coding / Perubahan Terarah (Langkah 4 Modul 15)

Sebagai pemenuhan evaluasi modifikasi kode individu pada saat demonstrasi, diimplementasikan 3 fitur baru:
1. **Pencarian Produk Berdasarkan Sebagian Nama (`menuCariByNama`):**  
   Menambahkan `KatalogProduk.cariProdukByNama(String keyword)` untuk mencari produk secara fleksibel berdasarkan substring nama (misal: `"HVS"` akan menemukan `"Kertas HVS A4 70gsm"`).
2. **Laporan & Rekapitulasi Produk Stok Kritis (`menuLaporanStokKritis`):**  
   Menambahkan `KatalogProduk.getProdukStokKritis()` yang secara otomatis memfilter seluruh barang dagangan fisik yang sisa stoknya `<= batas minimum stok peringatan` untuk memudahkan kasir merencanakan kulakan ulang.
3. **Dialog Konfirmasi Eksplisit Sebelum Hapus (`menuHapus`):**  
   Menambahkan pengaman interaktif: *"Apakah Anda yakin ingin menghapus produk X? Ketik YA: "* untuk mencegah kehilangan data akibat ketidaksengajaan pengguna.

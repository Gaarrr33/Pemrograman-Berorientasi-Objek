# Panduan Kompilasi & Pengujian Source Code — Modul 10 PBO
**Proyek:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Modul:** Modul 10 — Exception Handling dan Validasi State Objek  
**Penyusun:** Tegar Mahardika (PENS PSDKU Sumenep)  

---

## 1. Struktur Direktori Source Code

```text
SourceCode/
├── bin/                       # Direktori hasil kompilasi (.class)
├── hasil-pengujian.txt        # Bukti log luaran hasil pengujian program
├── hasil_pengujian.pdf        # Dokumen laporan resmi pengujian
├── README.md                  # Berkas panduan ini
└── src/
    ├── Modul10Dasar.java      # Implementasi Langkah 1 s.d. 3 (Studi Kasus Stok 5 Unit)
    ├── Main.java              # Main class pengujian proyek Tegar CopyFlow (Tahap 1 - 6)
    ├── exception/
    │   ├── StokTidakCukupException.java    # Custom Checked Exception: Kuantitas melebihi stok
    │   └── TransaksiInvalidException.java  # Custom Checked Exception: Siklus transaksi tidak valid
    └── model/
        ├── BatchStok.java                  # Entitas batch persediaan kulakan FIFO
        ├── DetailTransaksi.java            # Entitas baris belanja transaksi kasir
        ├── Produk.java                     # Entitas produk fisik & layanan jasa fotokopi
        └── TransaksiPenjualan.java         # Agregator transaksi penjualan kasir (POS)
```

---

## 2. Cara Kompilasi dan Menjalankan Program

### Prasyarat
- Java Development Kit (JDK) versi 17 atau lebih baru (telah teruji pada Java 25 LTS).

### Perintah PowerShell

```powershell
# 1. Pindah ke direktori SourceCode
cd "P10_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Buat folder bin jika belum ada
mkdir bin -ErrorAction SilentlyContinue

# 3. Kompilasi dan Jalankan Studi Kasus Dasar Modul 10
javac -encoding UTF-8 -d bin src/Modul10Dasar.java
java -cp bin Modul10Dasar

# 4. Kompilasi Seluruh Class Proyek Tegar CopyFlow
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/Main.java

# 5. Jalankan Pengujian Lengkap Proyek Tegar CopyFlow
java -cp bin Main
```

---

## 3. Rangkuman Skenario Pengujian

1. **Pengujian Langkah 1 s.d. 3 Modul:**
   - Input `"2"`: Sukses, stok berkurang dari 5 menjadi 3.
   - Input `"0"`: Ditolak `IllegalArgumentException`, stok tetap 3.
   - Input `"9"`: Ditolak `StokTidakCukupException`, stok tetap 3.
   - Input `"abc"`: Ditolak `NumberFormatException`, stok tetap 3.
   - Input `"-1"`: Ditolak `IllegalArgumentException`, stok tetap 3.
   - Input `"3"`: Sukses, stok berkurang tepat habis menjadi 0.
2. **Pengujian Proyek Tegar CopyFlow (Tahap 1 s.d. 6):**
   - Transaksi multi-item barang fisik & jasa fotokopi dengan pemotongan stok FIFO.
   - Pembatalan transaksi kasir (*rollback* konsisten ke batch persediaan asal).
   - Uji 1 s.d. 8 validasi defensive programming dan pembuktian bahwa seluruh kegagalan mempertahankan konsistensi state objek.


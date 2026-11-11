# Panduan Kompilasi & Pengujian Source Code — Modul 11 PBO
**Proyek:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Modul:** Modul 11 — Collection dan Pengelolaan Kumpulan Objek (ArrayList & Generics)  
**Penyusun:** Tegar Mahardika (PENS PSDKU Sumenep)  

---

## 1. Struktur Direktori Source Code

```text
SourceCode/
├── bin/                       # Direktori hasil kompilasi bytecode (.class)
├── hasil-pengujian.txt        # Bukti log luaran hasil pengujian program (UTF-8)
├── hasil pengujian.pdf        # Dokumen laporan resmi pengujian
├── README.md                  # Berkas panduan teknis ini
└── src/
    ├── Modul11Dasar.java      # Implementasi Langkah 1 s.d. 3 (Studi Kasus Perpustakaan)
    ├── Main.java              # Main class pengujian proyek Tegar CopyFlow (10 Skenario)
    ├── exception/
    │   ├── StokTidakCukupException.java    # Custom Checked Exception: Stok tidak cukup
    │   └── TransaksiInvalidException.java  # Custom Checked Exception: Siklus transaksi invalid
    └── model/
        ├── BatchStok.java                  # Entitas batch persediaan kulakan FIFO
        ├── DetailTransaksi.java            # Entitas baris item transaksi kasir
        ├── KatalogProduk.java              # Class pengelola kumpulan objek List<Produk>
        ├── Produk.java                     # Entitas produk fisik & layanan jasa dengan kode unik
        └── TransaksiPenjualan.java         # Agregator transaksi penjualan kasir POS
```

---

## 2. Cara Kompilasi dan Menjalankan Program

### Prasyarat
- Java Development Kit (JDK) versi 17 atau lebih baru (telah teruji pada Java 25 LTS).

### Perintah Terminal / PowerShell

```powershell
# 1. Pindah ke direktori SourceCode
cd "P11_Tegar Mahardika_TegarCopyFlow/SourceCode"

# 2. Buat folder bin jika belum ada
mkdir bin -ErrorAction SilentlyContinue

# 3. Kompilasi dan Jalankan Studi Kasus Dasar Modul 11 (Buku & Perpustakaan)
javac -encoding UTF-8 -d bin src/Modul11Dasar.java
java -cp bin Modul11Dasar

# 4. Kompilasi Seluruh Class Proyek Tegar CopyFlow
javac -encoding UTF-8 -d bin src/exception/*.java src/model/*.java src/Main.java

# 5. Jalankan Pengujian Lengkap Proyek Tegar CopyFlow (10 Skenario)
java -cp bin Main
```

---

## 3. Rangkuman Skenario Pengujian

1. **Pengujian Langkah 1 s.d. 3 Modul 11 (`Modul11Dasar.java`):**
   - Tambah buku `B001` & `B002` (sukses, total: 2).
   - Cari buku `B002` (sukses ditemukan).
   - Tambah buku `B001` duplikat (ditolak `IllegalArgumentException`, total tetap 2).
   - Hapus buku `B001` (sukses `true`, total: 1).
   - Cari buku `B001` setelah dihapus (mengembalikan `null`).
   - Hapus kode fiktif `B999` (menghasilkan `false`).
   - Skenario tambahan: Judul kosong ditolak, objek `null` ditolak, dan menampilkan koleksi kosong.

2. **Pengujian Proyek Tegar CopyFlow (`Main.java`):**
   - Skenario 1: Menampilkan katalog saat masih kosong (`tampilkanKatalog()` awal).
   - Skenario 2: Menambahkan 5 produk fisik dan layanan jasa ke dalam `KatalogProduk`.
   - Skenario 3: Menampilkan seluruh koleksi katalog produk dengan kalkulasi nilai aset inventaris.
   - Skenario 4: Pencarian produk berbasis kode unik yang valid (`"PRD-001"`).
   - Skenario 5: Pencarian produk dengan kode yang tidak terdaftar (`"PRD-999"` -> `null`).
   - Skenario 6: Uji validasi kode duplikat (mencegah pendaftaran `"PRD-001"` kedua kalinya).
   - Skenario 7: Uji validasi objek null dan kode string kosong.
   - Skenario 8: Operasi hapus produk terdaftar (`"PRD-003"` -> `true`) dan kode fiktif (`false`).
   - Skenario 9: Proteksi enkapsulasi (`getDaftarProduk().clear()` ditolak `UnsupportedOperationException`).
   - Skenario 10: Integrasi transaksi kasir POS menggunakan produk yang diambil dari katalog.


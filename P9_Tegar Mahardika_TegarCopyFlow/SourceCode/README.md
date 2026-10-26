# Source Code Java — Tegar CopyFlow (PBO Modul 9)

Folder ini berisi implementasi program Java berorientasi objek yang menerjemahkan alur bisnis utama proyek **Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi** sesuai pedoman Praktikum PBO Modul 9.

---

## 1. Struktur Direktori

```text
SourceCode/
├── README.md                  # Dokumentasi teknis source code Java
├── bin/                       # Bytecode terkompilasi (.class)
├── hasil-pengujian.pdf        # Dokumen PDF bukti hasil eksekusi program
├── hasil-pengujian.txt        # Catatan teks mentah hasil eksekusi program
└── src/
    ├── Main.java              # Entry point pengujian alur bisnis dan validasi state
    └── model/
        ├── BatchStok.java     # Model batch inventaris & pelacakan HPP unit
        ├── DetailTransaksi.java # Model baris detail transaksi & subtotal/margin
        ├── Produk.java        # Model produk (fisik & jasa) serta alokasi FIFO
        └── TransaksiPenjualan.java # Model transaksi kasir, struk, & pembatalan
```

---

## 2. Gambaran Class dan Tanggung Jawab

| No | Class Java | Package | Tanggung Jawab (Responsibility) |
|---|---|---|---|
| 1 | `Produk` | `model` | Mengelola data produk fisik (`PRODUCT`) maupun jasa fotokopi (`SERVICE`), menjaga batasan minimum stok, memantau status stok (`AMAN`, `MENIPIS`, `HABIS`), serta mengorkestrasikan pemotongan stok berbasis antrean batch FIFO (*First In, First Out*). |
| 2 | `BatchStok` | `model` | Merepresentasikan satu gelombang pengadaan/kulakan barang dengan kuantitas awal, sisa stok, dan harga beli per unit (*unit cost*). Menjamin bahwa sisa stok tidak menjadi negatif dan tidak melebihi kuantitas awal saat terjadi *rollback*. |
| 3 | `DetailTransaksi` | `model` | Merepresentasikan satu baris item pada struk transaksi yang terhubung (*associates*) dengan objek `Produk`. Menghitung subtotal transaksi, menampung nilai HPP (*COGS*), serta menghitung laba kotor (*gross profit*) per baris. |
| 4 | `TransaksiPenjualan` | `model` | Mengelola transaksi kasir (POS), penomoran invoice, metode pembayaran (`Cash`, `QRIS`, `Transfer`), daftar item belanja (`List<DetailTransaksi>`), kalkulasi total belanja & margin laba kotor, pencetakan struk, dan pembatalan transaksi dengan pengembalian stok otomatis. |
| 5 | `Main` | `default` | Program demonstrasi dan pengujian otomatis yang menyimulasikan seluruh skenario bisnis nyata (pencatatan master data, restock multi-batch, transaksi multi-item, pembatalan invoice, dan 5 uji coba *defensive validation*). |

---

## 3. Relasi Antarobjek

1. **Composition (`TransaksiPenjualan` 1 ──* `DetailTransaksi`)**:
   - Satu objek transaksi penjualan mengelola koleksi 1 atau lebih objek `DetailTransaksi`. Siklus hidup baris transaksi sepenuhnya dikendalikan oleh objek transaksi tersebut.
2. **Association (`DetailTransaksi` * ──1 `Produk`)**:
   - Setiap baris transaksi merujuk pada satu objek `Produk` tertentu untuk mengakses data nama, satuan, dan memicu pengembalian stok jika pesanan dibatalkan.
3. **Composition / Aggregation (`Produk` 1 ──* `BatchStok`)**:
   - Objek `Produk` berjenis fisik mengelola daftar batch stok persediaan yang masuk untuk mendukung perhitungan nilai inventaris dan alokasi stok FIFO.

---

## 4. Cara Kompilasi dan Menjalankan Program

Pastikan Anda memiliki Java SDK (JDK 17 atau lebih baru) terpasang di komputer Anda.

### A. Kompilasi
Buka terminal/PowerShell pada folder root proyek atau pada folder `SourceCode`, lalu jalankan:

```powershell
# Jalankan dari folder P9_NamaMHS_TegarCopyFlow/SourceCode:
mkdir bin -ErrorAction SilentlyContinue
javac -encoding UTF-8 -d bin src/model/*.java src/Main.java
```

### B. Menjalankan Program

```powershell
# Jalankan class Main dari folder bin:
java -cp bin Main
```

Output program akan ditampilkan di konsol terminal dan mencakup pengujian dari Tahap 1 sampai Tahap 6 secara berurutan.

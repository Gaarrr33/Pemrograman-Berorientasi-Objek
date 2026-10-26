# Laporan Praktikum Pemrograman Berorientasi Objek — Modul 9
**Topik:** Implementasi UML Class Diagram ke Program Java  
**Judul Project:** Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi  
**Penyusun:** Mahasiswa PBO (Placeholder: `NamaMHS`)  
**Institusi:** Politeknik Elektronika Negeri Surabaya — PSDKU Sumenep  
**Tahun Ajaran:** 2026  

---

## 1. Tujuan Implementasi

Implementasi ini bertujuan untuk:
1. Menganalisis alur bisnis dan struktur data pada sistem nyata **Tegar CopyFlow** (aplikasi POS dan manajemen persediaan toko ATK & fotokopi).
2. Merancang UML Class Diagram terkini yang merepresentasikan relasi, atribut, metode, dan visibilitas entitas bisnis inti.
3. Mengonversi diagram UML tersebut ke dalam source code bahasa pemrograman Java berstandar OOP.
4. Menerapkan prinsip *Encapsulation* (atribut `private` dengan validasi ketat), *Responsibility-Driven Design*, serta relasi antarobjek (*Composition* dan *Association*).
5. Menguji interaksi objek melalui skenario nyata (pengadaan batch stok FIFO, transaksi penjualan kasir, perhitungan HPP & laba kotor, pembatalan pesanan/rollback, dan pencegahan error defensif).

---

## 2. Ringkasan Fitur yang Direpresentasikan

Dari proyek web & database Tegar CopyFlow asli, logika bisnis yang dimodelkan ke dalam Java meliputi:
1. **Pembedaan Jenis Produk Toko vs Layanan Jasa Fotokopi:**
   - Produk fisik (`PRODUCT`): memiliki stok fisik yang dikelola dalam gelombang batch kulakan.
   - Layanan jasa (`SERVICE`): tidak dibatasi batch fisik, namun memiliki biaya per unit (bahan baku fotokopi/jilid) untuk menghitung HPP.
2. **Manajemen Persediaan Berbasis FIFO (*First In, First Out*):**
   - Mendukung pencatatan batch stok masuk dengan tanggal dan harga beli (*unit cost*) yang berbeda.
   - Penjualan memotong unit dari batch persediaan yang masuk paling awal terlebih dahulu.
3. **Transaksi Kasir (POS) & Kalkulasi Margin Finansial:**
   - Pembuatan nomor faktur (*invoice*), pencatatan metode pembayaran (`Cash`, `QRIS`, `Transfer`).
   - Perhitungan otomatis subtotal belanja, total omset, akumulasi HPP (*COGS*), dan laba kotor (*gross profit*).
4. **Mekanisme Pembatalan Transaksi (*Rollback* Aman):**
   - Jika transaksi dibatalkan oleh kasir/pelanggan, sistem mengembalikan kuantitas barang ke batch stok asalnya secara otomatis dan konsisten.
5. **Validasi State & *Defensive Programming*:**
   - Mencegah transaksi jika stok tidak mencukupi.
   - Menolak input jumlah barang bernilai nol/negatif.
   - Menolak harga jual bernilai negatif.
   - Menolak penambahan item pada transaksi yang sudah berstatus dibatalkan (`CANCELLED`).

---

## 3. Daftar Class dan Tanggung Jawab (*Responsibility*)

| Nama Class | Package | Responsibility |
|---|---|---|
| **`Produk`** | `model` | Mengelola identitas barang fisik atau layanan jasa, menjaga batas minimum stok, memantau status stok (`AMAN`, `MENIPIS`, `HABIS`, `SERVICE`), serta mengendalikan alokasi batch stok secara FIFO. |
| **`BatchStok`** | `model` | Menyimpan data gelombang pengadaan barang (kuantitas awal, sisa stok, dan harga beli per unit). Bertanggung jawab memastikan sisa stok tidak bernilai minus dan tidak melebihi kuantitas awal saat pengembalian stok. |
| **`DetailTransaksi`** | `model` | Menyimpan data satu baris belanja pada transaksi yang terhubung ke objek `Produk`. Menghitung subtotal penjualan, menyimpan HPP, dan menghitung laba kotor baris tersebut. |
| **`TransaksiPenjualan`** | `model` | Mengorkestrasikan keseluruhan proses transaksi kasir: validasi nomor invoice dan metode pembayaran, penambahan item, kalkulasi total belanja & margin laba, pencetakan struk belanja kasir, serta pembatalan transaksi dengan pengembalian stok. |
| **`Main`** | `default` | Program demonstrasi utama yang menguji skenario end-to-end: inisialisasi master data, pencatatan batch stok masuk, pembuatan transaksi multi-item, pembatalan pesanan, serta 5 skenario pengujian validasi exception. |

---

## 4. Relasi Antarobjek

Proyek ini mengimplementasikan 3 relasi antarobjek:
1. **Composition (`TransaksiPenjualan` 1 ──* `DetailTransaksi`):**  
   Setiap transaksi penjualan memiliki 1 atau lebih detail transaksi. Objek `DetailTransaksi` dibuat dan dikelola di dalam `TransaksiPenjualan`.
2. **Association (`DetailTransaksi` * ──1 `Produk`):**  
   Objek detail transaksi merujuk ke objek produk untuk membaca nama/satuan produk dan melakukan koordinasi *rollback* stok ketika transaksi dibatalkan.
3. **Composition / Aggregation (`Produk` 1 ──* `BatchStok`):**  
   Objek produk fisik memiliki daftar batch stok persediaan yang masuk untuk mendukung perhitungan valuasi aset inventaris dan pemotongan stok FIFO.

---

## 5. Penerapan Encapsulation dan Validasi

* **Atribut Private:** Seluruh variabel instance pada keempat class diberi visibilitas `private` (`-`) agar tidak dapat dimanipulasi secara langsung dari luar class.
* **Akses Terkendali (Getter & Business Method):** Pengaksesan data dilakukan melalui getter dan method bisnis yang relevan, bukan sekadar getter/setter otomatis.
* **Validasi Integritas State:**
  * Kuantitas pembelian harus bernilai bilangan bulat positif (`> 0`).
  * Harga jual dan harga beli per unit tidak boleh negatif (`>= 0`).
  * Pemotongan stok pada produk fisik divalidasi terlebih dahulu; jika `jumlahMinta > totalStok`, sistem melempar `IllegalStateException` dengan pesan informatif.
  * Pembatalan transaksi menjaga agar sisa stok pada batch tidak melebihi kuantitas kulakan awal (`Math.min`).

---

## 6. Cara Kompilasi dan Menjalankan Program

### Prasyarat
* Java Development Kit (JDK) versi 17 atau lebih baru (telah diuji pada Java 25 LTS).

### Perintah Terminal / PowerShell
Masuk ke direktori `SourceCode`:

```powershell
# 1. Pindah ke folder SourceCode
cd "P9_NamaMHS_TegarCopyFlow/SourceCode"

# 2. Buat folder output bytecode (jika belum ada)
mkdir bin -ErrorAction SilentlyContinue

# 3. Kompilasi seluruh source code Java
javac -encoding UTF-8 -d bin src/model/*.java src/Main.java

# 4. Jalankan program pengujian
java -cp bin Main
```

---

## 7. Daftar Luaran yang Tersedia

Seluruh file tugas tersusun rapi di dalam folder mandiri ini:

```text
P9_NamaMHS_TegarCopyFlow/
├── README.md                      # Laporan utama proyek dan dokumentasi lengkap
├── Laporan_Praktikum_P9.pdf       # LAPORAN LENGKAP PRAKTIKUM (Format PDF Siap Kumpul)
├── generate_reports_pdf.py        # Script generator PDF otomatis (ReportLab)
├── UML/
│   ├── ClassDiagram.puml          # Source code PlantUML terkini
│   ├── ClassDiagram.png           # Gambar render diagram UML resolusi tinggi
│   └── Penjelasan_UML.md          # Penjelasan hubungan UML-Java & 5 jawaban evaluasi
├── SourceCode/
│   ├── README.md                  # Dokumentasi teknis source code Java
│   ├── bin/                       # Bytecode Java terkompilasi (.class)
│   ├── hasil-pengujian.pdf        # BUKTI HASIL PENGUJIAN PROGRAM AKTUAL (Format PDF)
│   ├── hasil-pengujian.txt        # Catatan mentah output teks pengujian program
│   └── src/
│       ├── Main.java              # Kelas penguji interaksi objek & alur bisnis
│       └── model/
│           ├── BatchStok.java     # Class model batch inventaris & HPP unit
│           ├── DetailTransaksi.java # Class model baris item transaksi & margin
│           ├── Produk.java        # Class model produk fisik/jasa & alokasi FIFO
│           └── TransaksiPenjualan.java # Class model transaksi kasir & struk
└── Screenshot/
    └── PETUNJUK_SCREENSHOT.md     # Panduan rinci pengambilan 4 screenshot pengujian
```

# UTS Praktikum Pemrograman Berorientasi Objek (PBO)
## STUDI KASUS TIPE B: SISTEM INFORMASI KLINIK "SEHAT MEDIKA"

---

## 1. Deskripsi Singkat Sistem

**Sistem Informasi Klinik Sehat Medika** adalah aplikasi berbasis console (CLI) yang dikembangkan menggunakan bahasa pemrograman Java standar. Sistem ini dirancang untuk mengelola entitas klinik sehari-hari yang mencakup pendaftaran pasien, pengelolaan tenaga medis (dokter dan perawat), registrasi kunjungan berobat, serta pemrosesan antrean pelayanan medis.

Program ini dirancang khusus untuk mahasiswa D3 Teknik Informatika: mudah dipahami alurnya, bersih struktur kodenya, minim ketergantungan eksternal, tetapi mengimplementasikan seluruh konsep dasar Pemrograman Berorientasi Objek (PBO) secara tepat, utuh, dan taat kaidah.

---

## 2. Struktur Proyek dan File

```
UTS_TIPE_B/
├── class-diagram.png       # Diagram kelas visual resolusi tinggi (PNG)
├── class-diagram.pdf       # Diagram kelas dalam format dokumen PDF
├── class-diagram.puml      # Source code diagram dalam format PlantUML
├── class-diagram.mmd       # Source code diagram dalam format Mermaid
├── generate_diagram.py     # Script generator diagram (Pillow & ReportLab)
├── README.md               # Dokumentasi lengkap & naskah demo pengujian
└── src/
    ├── Main.java           # Entry point aplikasi & skenario pengujian
    ├── PelayananMedis.java # Interface kontrak pelayanan medis
    ├── TenagaMedis.java    # Abstract superclass tenaga medis
    ├── Dokter.java         # Subclass turunan dari TenagaMedis
    ├── Perawat.java        # Subclass turunan dari TenagaMedis
    ├── Pasien.java         # Class entitas data pasien & enkapsulasi
    ├── Kunjungan.java      # Class relasi kunjungan & fitur prioritas
    └── Klinik.java         # Class pengelola agregasi pasien & antrean
```

---

## 3. Daftar Class dan Tanggung Jawab

| No | Nama Class / Interface | Jenis | Tanggung Jawab (Single Responsibility) |
|---|---|---|---|
| 1 | `PelayananMedis` | `Interface` | Mendefinisikan kontrak method pelayanan medis (`beriPelayanan` dan `getPeran`) yang wajib dipenuhi oleh setiap tenaga medis. |
| 2 | `TenagaMedis` | `Abstract Class` | Menjadi superclass bersama bagi seluruh praktisi medis; menyimpan atribut umum (`idTenagaMedis`, `nama`, `spesialisasi`) serta mendefinisikan abstract method `beriPelayanan()`. |
| 3 | `Dokter` | `Subclass` | Mewarisi `TenagaMedis`; menyimpan atribut SIP dan biaya konsultasi; mengimplementasikan pelayanan pemeriksaan klinis, diagnosis, dan resep obat. |
| 4 | `Perawat` | `Subclass` | Mewarisi `TenagaMedis`; menyimpan atribut shift kerja dan ruang tugas; mengimplementasikan pelayanan triase, tanda-tanda vital (tensi/suhu), dan stabilisasi kondisi. |
| 5 | `Pasien` | `Class` | Menyimpan identitas dan data pribadi pasien (`idPasien`, `nama`, `usia`, `jenisKelamin`, `alamat`) dengan enkapsulasi data dan validasi input. |
| 6 | `Kunjungan` | `Class` | Menghubungkan satu pasien dengan satu tenaga medis; menyimpan keluhan, tingkat urgensi, status kunjungan, serta mengimplementasikan `Comparable<Kunjungan>` untuk pengurutan prioritas. |
| 7 | `Klinik` | `Class` | Mengelola koleksi data pasien, tenaga medis, dan kunjungan (agregasi); menyediakan logika pendaftaran, pencarian, dan pemrosesan antrean. |
| 8 | `Main` | `Class` | Menyediakan antarmuka interaktif CLI (Console) berbasis menu untuk pengguna, menangani input `Scanner` yang aman, dan menjalankan skenario demo pengujian otomatis. |

---

## 4. Penerapan Konsep Pemrograman Berorientasi Objek (PBO)

### A. Class dan Object
- Sistem memiliki **7 class dan 1 interface** (melebihi batas minimal 5 class).
- Objek nyata diinstansiasi pada runtime, seperti objek `Dokter`, `Perawat`, `Pasien`, dan `Kunjungan`.

### B. Encapsulation (Enkapsulasi)
- Seluruh atribut pada semua class dideklarasikan dengan access modifier `private`.
- Akses dan mutasi data dilakukan secara terkontrol melalui method getter, setter, dan constructor.
- Contoh: atribut `usia` pada `Pasien` tidak dapat diubah sembarangan tanpa melalui validasi nilai positif.

### C. Validation & Business Rules (Aturan Bisnis Berjalan)
Sistem menerapkan 4 aturan bisnis utama yang aktif dan dapat diuji:
1. **Validasi Data Pasien**: Nama pasien tidak boleh kosong/null, dan usia pasien harus lebih besar dari 0 tahun (`IllegalArgumentException`).
2. **Pencegahan Duplikasi Pasien**: Pasien dengan ID yang sama tidak boleh didaftarkan lebih dari satu kali ke dalam klinik.
3. **Validasi Prioritas Kunjungan**: Prioritas antrean hanya boleh bernilai 1 (Darurat), 2 (Mendesak), atau 3 (Reguler).
4. **Validasi Status Kunjungan**: Kunjungan yang telah berstatus `SELESAI` tidak boleh diproses ulang (`IllegalStateException`).

### D. Relationship (Relasi Antarobjek)
- **Agregasi / Komposisi**: Class `Klinik` memiliki dan mengelola koleksi `List<Pasien>`, `List<TenagaMedis>`, dan `List<Kunjungan>`.
- **Asosiasi**: Class `Kunjungan` memiliki referensi ke tepat 1 objek `Pasien` dan 1 objek `TenagaMedis`.
- **Generalisasi (Inheritance)**: `Dokter` dan `Perawat` mewarisi `TenagaMedis`.
- **Realisasi (Interface Implementation)**: `TenagaMedis` mengimplementasikan interface `PelayananMedis`, serta `Kunjungan` mengimplementasikan interface bawaan Java `Comparable<Kunjungan>`.

### E. Abstract Class dan Abstract Method
- Class `TenagaMedis` dideklarasikan sebagai `abstract class` karena konsep tenaga medis bersifat umum dan tidak boleh diinstansiasi secara langsung tanpa spesifikasi peran konkret.
- Memiliki abstract method:
  ```java
  public abstract void beriPelayanan(Pasien pasien, String keluhan);
  ```
- Method ini mewajibkan setiap subclass (`Dokter` dan `Perawat`) memberikan implementasi tindakan medis yang konkret.

### F. Polymorphism (Polimorfisme)
- **Method Overriding**: Method `beriPelayanan()` dioverride secara berbeda pada `Dokter` (pemeriksaan diagnosis fisik & peresepan obat) dan pada `Perawat` (triase awal, pemeriksaan tensi/suhu, dan stabilisasi).
- **Dynamic Binding / Runtime Polymorphism**: Pada method `Kunjungan.prosesPelayanan()`, pemanggilan dilakukan melalui referensi superclass:
  ```java
  TenagaMedis tm = kunjungan.getTenagaMedis();
  tm.beriPelayanan(pasien, keluhan);
  ```
  Pada saat runtime, Java secara otomatis mengeksekusi perilaku yang sesuai dengan objek riil (`Dokter` atau `Perawat`).

### G. Interface
- Interface `PelayananMedis` mendefinisikan kontrak kemampuan tindakan yang harus dimiliki setiap tenaga kesehatan:
  ```java
  public interface PelayananMedis {
      void beriPelayanan(Pasien pasien, String keluhan);
      String getPeran();
  }
  ```

---

## 5. Fitur Kreatif: Antrean Berdasarkan Tingkat Urgensi (Multi-Level Priority)

Sistem klinik ini dilengkapi dengan fitur antrean pintar berbasis triase medis nyata:
- **Prioritas 1 (Darurat / Emergency)**: Kondisi gawat darurat (misal nyeri dada akut, sesak napas).
- **Prioritas 2 (Mendesak / Urgent)**: Kondisi sakit akut namun stabil (misal demam tinggi, muntah hebat).
- **Prioritas 3 (Reguler / Normal)**: Pemeriksaan rutin, kontrol bulanan, atau konsultasi umum.

### Logika Bisnis Pengurutan:
1. Pasien dengan nilai prioritas lebih kecil (1 > 2 > 3) selalu didahulukan melompati antrean reguler.
2. Jika dua pasien memiliki tingkat prioritas yang sama, maka urutan ditentukan berdasarkan nomor antrean pendaftaran yang lebih awal (FIFO - *First In, First Out*).
3. Diimplementasikan secara elegan menggunakan kontrak `Comparable<Kunjungan>`:
   ```java
   @Override
   public int compareTo(Kunjungan other) {
       if (this.prioritas != other.prioritas) {
           return Integer.compare(this.prioritas, other.prioritas);
       }
       return Integer.compare(this.nomorAntrean, other.nomorAntrean);
   }
   ```

---

## 6. Petunjuk Kompilasi dan Eksekusi

### Prasyarat
- Java Development Kit (JDK) versi 8 atau yang lebih baru (diuji dan berjalan sempurna pada JDK 25).

### Langkah Kompilasi
Buka terminal (Command Prompt / PowerShell / Bash) dan arahkan ke direktori `src`:
```bash
cd "d:\TUGAS KULIAH\Semester 3\Pemrograman Berorientasi Objek\UTS-Praktikum-PBO\UTS_TIPE_B\src"
javac *.java
```

### Menjalankan Program (Menu Interaktif)
```bash
java Main
```

### Menjalankan Skenario Demonstrasi Otomatis (Uji Cepat Persyaratan Soal)
```bash
java Main --demo
```
*(Atau pilih opsi menu nomor **9** saat berada di dalam program).*

---

## 7. Skenario Pengujian dan Bukti Output

### A. Uji Coba Menu Interaktif
Pengguna dapat memilih menu 1 sampai 8 untuk mengoperasikan klinik:
```
========================================================
         SISTEM INFORMASI KLINIK SEHAT MEDIKA          
       UTS PRAKTIKUM PBO - STUDI KASUS TIPE B           
========================================================
1. Menambahkan Pasien Baru
2. Menampilkan Daftar Pasien
3. Menampilkan Tenaga Medis (Dokter & Perawat)
4. Mendaftarkan Kunjungan Pasien
5. Menampilkan Antrean Berdasarkan Prioritas [Fitur Kreatif]
6. Memproses Pelayanan Pasien
7. Menampilkan Status Kunjungan
8. Keluar dari Program
--------------------------------------------------------
9. Jalankan Skenario Demonstrasi OOP & Business Rules
========================================================
```

### B. Hasil Eksekusi Skenario Otomatis (`java Main --demo`)

```text
================================================================================
          SKENARIO PENGUJIAN OTOMATIS & PEMBUKTIAN PERSYARATAN UTS PBO          
================================================================================

[UJI 1] PEMBUKTIAN VALIDASI & BUSINESS RULES
--------------------------------------------------------------------------------
-> Uji Kasus 1: Mendaftarkan pasien dengan Nama kosong (Harus Ditolak)
  [BERHASIL DICEGAH] Pesan Exception: Nama pasien tidak boleh kosong!

-> Uji Kasus 2: Mendaftarkan pasien dengan Usia negatif (-5) (Harus Ditolak)
  [BERHASIL DICEGAH] Pesan Exception: Usia pasien harus lebih besar dari 0 tahun!

-> Uji Kasus 3: Mendaftarkan pasien dengan ID yang sudah ada (P001) (Harus Ditolak)
  [BERHASIL DICEGAH] Pesan Exception: Pelanggaran Aturan Bisnis: Pasien dengan ID [P001] sudah terdaftar di klinik!

[UJI 2] PEMBUKTIAN FITUR KREATIF: PENGURUTAN ANTREAN PRIORITAS
--------------------------------------------------------------------------------
Kunjungan awal didaftarkan dengan urutan:
  1. P001 didaftarkan lebih awal (Antrean #1, Prioritas 3 - REGULER)
  2. P002 didaftarkan berikutnya (Antrean #2, Prioritas 2 - MENDESAK)
  3. P003 didaftarkan belakangan (Antrean #3, Prioritas 1 - DARURAT)
Hasil antrean setelah diurutkan berdasarkan tingkat urgensi:

================ ANTREAN AKTIF BERDASARKAN PRIORITAS ================
+--------+-------+--------------+------------------+------------------+----------------+-----------+
| ID     | Antre | Prioritas    | Nama Pasien      | Tenaga Medis     | Keluhan        | Status    |
+--------+-------+--------------+------------------+------------------+----------------+-----------+
| KJ003  | #3    | 1 - DARURAT  | Bambang Pamungkas | Ners Andi Pratama, S.Kep | Nyeri Dada Akut & Sesak | MENUNGGU  |
| KJ002  | #2    | 2 - MENDESAK | Dewi Sartika     | Ners Maya Safitri, A.Md.Kep | Demam Tinggi 3 Hari | MENUNGGU  |
| KJ004  | #4    | 2 - MENDESAK | Clara Amanda     | dr. Siti Rahma, Sp.A | Batuk Rejan & Diare | MENUNGGU  |
| KJ001  | #1    | 3 - REGULER  | Ahmad Fauzi      | dr. Budi Santoso, Sp.PD | Kontrol Maag Rutin | MENUNGGU  |
+--------+-------+--------------+------------------+------------------+----------------+-----------+
* Keterangan: Prioritas 1: Darurat > Prioritas 2: Mendesak > Prioritas 3: Reguler
Perhatikan: P003 (Darurat) melompati antrean dan menjadi yang PERTAMA dilayani!

[UJI 3] PEMBUKTIAN POLYMORPHISM & INTERFACE
--------------------------------------------------------------------------------
Memanggil method beriPelayanan() melalui referensi TenagaMedis (Superclass / Interface):
Objek yang ditunjuk berbeda runtime (Perawat vs Dokter), menghasilkan output berbeda:

-> 3A. Melayani Antrean Terdepan (Prioritas 1 - Ditangani oleh PERAWAT):

>>> MEMPROSES PELAYANAN UNTUK KUNJUNGAN: KJ003 <<<
Tingkat Prioritas : 1 - DARURAT
Nomor Antrean     : #3
  -------------------------------------------------------------
  [TINDAKAN ASUHAN KEPERAWATAN]
  Perawat Bertugas : Ners Andi Pratama, S.Kep (Shift: Pagi)
  Ruang Pelayanan  : IGD
  Pasien           : Bambang Pamungkas (60 tahun)
  Keluhan Masuk    : Nyeri Dada Akut & Sesak
  Tindakan Asuhan  : 1. Melakukan triase awal & observasi pasien.
                     2. Memeriksa tanda vital (Tensi, Nadi, Suhu tubuh).
                     3. Memberikan pertolongan pertama / stabilisasi kondisi.
  Status Tindakan  : Pasien telah distabilkan dan siap diobservasi.
  -------------------------------------------------------------
>>> Status kunjungan [KJ003] berhasil diperbarui menjadi SELESAI <<<


-> 3B. Melayani Antrean Prioritas Dokter (KJ004 - Ditangani oleh DOKTER SPESIALIS):

>>> MEMPROSES PELAYANAN UNTUK KUNJUNGAN: KJ004 <<<
Tingkat Prioritas : 2 - MENDESAK
Nomor Antrean     : #4
  -------------------------------------------------------------
  [TINDAKAN MEDIS DOKTER]
  Dokter Pemeriksa : dr. Siti Rahma, Sp.A (SIP: SIP-67890/IDI)
  Pasien           : Clara Amanda (7 tahun)
  Keluhan Utama    : Batuk Rejan & Diare
  Tindakan Medis   : 1. Melakukan pemeriksaan fisik & diagnosis klinis.
                     2. Menetapkan terapi dan menulis resep obat.
  Biaya Konsultasi : Rp 175.000,00
  Status Tindakan  : Pemeriksaan dokter selesai, resep diserahkan.
  -------------------------------------------------------------
>>> Status kunjungan [KJ004] berhasil diperbarui menjadi SELESAI <<<


[UJI 4] PEMBUKTIAN VALIDASI STATUS KUNJUNGAN
--------------------------------------------------------------------------------
-> Uji Kasus: Memproses ulang Kunjungan yang statusnya sudah SELESAI (Harus Ditolak)
  [BERHASIL DICEGAH] Pesan Exception: Pelanggaran Aturan Bisnis: Kunjungan [KJ003] sudah berstatus SELESAI dan tidak dapat diproses ulang!

================================================================================
                   SELURUH PENGUJIAN OTOMATIS SELESAI & LULUS                   
================================================================================
```

---

## 8. Naskah Presentasi Demo (Durasi Maksimal 3 Menit)

Berikut adalah panduan narasi presentasi verbal untuk mahasiswa saat mendemonstrasikan project ini di hadapan dosen penguji:

> **[Menit 0:00 - 0:45] Pembukaan & Alasan Perancangan Class**
> *"Selamat pagi/siang Bapak/Ibu Dosen. Saya akan mempresentasikan project UTS Praktikum PBO Studi Kasus Tipe B: Sistem Informasi Klinik Sehat Medika.*
> *Sistem ini saya rancang menggunakan 7 class dan 1 interface. Alasan perancangannya adalah:*
> *Pertama, saya memisahkan `TenagaMedis` sebagai sebuah **Abstract Class** yang mengimplementasikan interface `PelayananMedis`. Dari class ini, saya membuat dua subclass yaitu `Dokter` dan `Perawat`. Pemisahan ini masuk akal secara medis karena dokter berwenang menegakkan diagnosis dan meresepkan obat, sedangkan perawat berfokus pada triase dan asuhan keperawatan.*
> *Kedua, class `Kunjungan` bertindak sebagai penghubung asosiasi antara pasien dan tenaga medis. Dan class `Klinik` mengelola seluruh data secara terpusat melalui relasi agregasi."*

> **[Menit 0:46 - 1:45] Pembuktian Enkapsulasi, Validasi, dan Polimorfisme**
> *"Seluruh atribut saya proteksi dengan access modifier `private` dan diakses melalui getter/setter yang memiliki validasi bisnis ketat, seperti pencegahan nama kosong, umur negatif, ID duplikat, dan pencegahan pemrosesan ulang kunjungan yang sudah selesai.*
> *Polymorphism terjadi pada method `beriPelayanan()`. Di class `Kunjungan`, method ini dipanggil hanya melalui referensi superclass bertipe `TenagaMedis`. Ketika pasien ditangani oleh objek `Perawat`, program secara otomatis mengeksekusi pemeriksaan tanda vital dan stabilisasi IGD. Namun ketika objek aslinya adalah `Dokter`, program mengeksekusi pemeriksaan diagnosis fisik dan penulisan resep obat. Satu baris pemanggilan yang sama menghasilkan perilaku berbeda pada saat runtime."*

> **[Menit 1:46 - 2:40] Demonstrasi Fitur Kreatif: Antrean Prioritas**
> *"Fitur kreatif yang saya tambahkan adalah **Sistem Antrean Triase Berdasarkan Tingkat Urgensi**: Prioritas 1 untuk Darurat, Prioritas 2 untuk Mendesak, dan Prioritas 3 untuk Reguler.*
> *Fitur ini diimplementasikan dengan antarmuka `Comparable<Kunjungan>`. Pada demonstrasi pengujian, pasien `Bambang Pamungkas` yang mendaftar belakangan dengan nomor antrean 3 namun mengalami nyeri dada akut (Prioritas 1 - Darurat) secara otomatis melompati antrean reguler dan langsung diposisikan di urutan terdepan untuk dilayani terlebih dahulu.*
> *Jika tingkat prioritasnya sama, sistem secara adil mengurutkannya berdasarkan nomor antrean pendaftaran."*

> **[Menit 2:41 - 3:00] Penutup & Kesimpulan**
> *"Seluruh kode telah dikompilasi tanpa error menggunakan Java standar dan diagram kelas telah disesuaikan secara presisi dalam format PNG dan PDF. Demikian presentasi saya, terima kasih."*

import exception.StokTidakCukupException;
import exception.TransaksiInvalidException;
import model.BatchStok;
import model.KatalogProduk;
import model.Produk;
import model.TransaksiPenjualan;

/**
 * Main Class untuk Pengujian Praktikum PBO Modul 11.
 * Topik: Collection dan Pengelolaan Kumpulan Objek (ArrayList & Generics).
 * Proyek: Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi.
 * 
 * Menguji:
 * 1. Pengelolaan kumpulan objek domain (List<Produk>) dalam class KatalogProduk.
 * 2. Operasi CRUD (Tambah, Cari, Tampilkan, Hapus) berbasis kode unik.
 * 3. Pencegahan duplikasi data kode produk dan data invalid (Fail-Fast).
 * 4. Perlindungan Enkapsulasi Collection (Read-Only UnmodifiableList).
 * 5. Integrasi transaksi kasir POS dengan produk yang dikelola dalam koleksi.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("               PRAKTIKUM PEMROGRAMAN BERORIENTASI OBJEK — MODUL 11                        ");
        System.out.println("                   COLLECTION DAN PENGELOLAAN KUMPULAN OBJEK                              ");
        System.out.println("                    SISTEM TEGAR COPYFLOW (TOKO & FOTOKOPI)                               ");
        System.out.println("==========================================================================================\n");

        KatalogProduk katalog = new KatalogProduk();

        // ----------------------------------------------------------------------------------------
        // SKENARIO 1: Menampilkan Katalog Saat Masih Kosong
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [SKENARIO 1] MENAMPILKAN KATALOG SAAT MASIH KOSONG");
        System.out.print("Status Katalog Awal: ");
        katalog.tampilkanKatalog();
        System.out.println("Jumlah Data: " + katalog.getJumlahProduk());
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 2: Menambahkan Objek Produk & Layanan ke Katalog (Operasi Tambah Berhasil)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [SKENARIO 2] MENAMBAHKAN PRODUK & LAYANAN JASA KE KATALOG");
        Produk hvsA4 = new Produk("PRD-001", 1, "Kertas HVS A4 75gr PaperOne", "Kertas", "PRODUCT", "rim", 55000, 5);
        Produk pulpen = new Produk("PRD-002", 2, "Pulpen Standard AE7 Hitam", "Alat Tulis", "PRODUCT", "pcs", 3500, 15);
        Produk spidol = new Produk("PRD-003", 3, "Spidol Snowman Boardmarker", "Alat Tulis", "PRODUCT", "pcs", 9000, 5);
        Produk fotokopi = new Produk("SRV-001", 4, "Jasa Fotokopi Bolak-Balik", "Fotokopi", "SERVICE", "lbr", 350, 150.0);
        Produk jilid = new Produk("SRV-002", 5, "Jasa Jilid Lakban Mika", "Jilid", "SERVICE", "buku", 5000, 1500.0);

        katalog.tambahProduk(hvsA4);
        katalog.tambahProduk(pulpen);
        katalog.tambahProduk(spidol);
        katalog.tambahProduk(fotokopi);
        katalog.tambahProduk(jilid);

        System.out.println("Berhasil menambahkan 5 produk ke dalam koleksi.");
        System.out.println("Jumlah Data: " + katalog.getJumlahProduk());
        System.out.println("------------------------------------------------------------------------------------------\n");

        // Input batch stok persediaan FIFO untuk produk fisik
        hvsA4.tambahBatchStok(101, "2026-11-01", 10, 45000, "Stok Awal Bulan");
        hvsA4.tambahBatchStok(102, "2026-11-05", 15, 48000, "Restock Supplier A");
        pulpen.tambahBatchStok(103, "2026-11-02", 40, 2200, "Kulakan Grosir");
        spidol.tambahBatchStok(104, "2026-11-03", 20, 6500, "Kulakan Grosir");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 3: Menampilkan Seluruh Koleksi Produk Toko (Operasi Tampilkan)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [SKENARIO 3] MENAMPILKAN SELURUH KOLEKSI KATALOG PRODUK");
        katalog.tampilkanKatalog();
        System.out.println("Jumlah Data: " + katalog.getJumlahProduk());
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 4: Mencari Produk Berdasarkan Kode Unik yang Ada (Operasi Cari Berhasil)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [SKENARIO 4] PENCARIAN PRODUK BERDASARKAN KODE UNIK (BERHASIL)");
        String kodeCari = "PRD-001";
        System.out.println("Mencari produk dengan kode: \"" + kodeCari + "\"");
        Produk hasilCari = katalog.cariProduk(kodeCari);
        if (hasilCari != null) {
            System.out.println("Hasil: Ditemukan!");
            System.out.println("  Detail Objek: " + hasilCari);
            System.out.println("  Stok Fisik  : " + hasilCari.hitungTotalStok() + " " + hasilCari.getSatuan());
        } else {
            System.out.println("Hasil: Produk tidak ditemukan.");
        }
        System.out.println("Jumlah Data: " + katalog.getJumlahProduk());
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 5: Mencari Produk dengan Kode yang Tidak Terdaftar (Operasi Cari Gagal)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [SKENARIO 5] PENCARIAN KODE YANG TIDAK TERDAFTAR (GAGAL / NULL)");
        String kodeTidakAda = "PRD-999";
        System.out.println("Mencari produk dengan kode: \"" + kodeTidakAda + "\"");
        Produk hasilKosong = katalog.cariProduk(kodeTidakAda);
        System.out.println("Hasil: " + (hasilKosong == null ? "null (Produk tidak ditemukan)" : hasilKosong.getNama()));
        System.out.println("Jumlah Data: " + katalog.getJumlahProduk() + " (State koleksi tidak berubah)");
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 6: Validasi Kode Duplikat (Operasi Tambah Gagal)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [SKENARIO 6] UJI VALIDASI KODE DUPLIKAT (OPERASI TAMBAH DITOLAK)");
        System.out.println("Mencoba mendaftarkan produk baru dengan kode yang sudah ada: \"PRD-001\"");
        try {
            Produk produkDuplikat = new Produk("PRD-001", 99, "Kertas HVS Tiruan", "Kertas", "PRODUCT", "rim", 45000, 2);
            katalog.tambahProduk(produkDuplikat);
            System.out.println("STATUS: Gagal (Seharusnya exception dilempar)!");
        } catch (IllegalArgumentException e) {
            System.out.println("STATUS: BERHASIL DICEGAH!");
            System.out.println("Pesan Kesalahan: \"" + e.getMessage() + "\"");
        }
        System.out.println("Jumlah Data: " + katalog.getJumlahProduk() + " (Data duplikat tidak tersimpan)");
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 7: Validasi Data Tidak Valid (Objek Null & Kode Kosong)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [SKENARIO 7] UJI VALIDASI INPUT INVALID (OBJEK NULL & KODE KOSONG)");
        System.out.println("[Uji 7a] Menambahkan objek null ke katalog:");
        try {
            katalog.tambahProduk(null);
            System.out.println("STATUS: Gagal (Seharusnya exception dilempar)!");
        } catch (IllegalArgumentException e) {
            System.out.println("  STATUS: BERHASIL DICEGAH -> \"" + e.getMessage() + "\"");
        }

        System.out.println("[Uji 7b] Membuat produk dengan kode kosong:");
        try {
            new Produk("   ", 10, "Barang Tanpa Kode", "Umum", "PRODUCT", "pcs", 1000, 0);
            System.out.println("STATUS: Gagal (Seharusnya exception dilempar)!");
        } catch (IllegalArgumentException e) {
            System.out.println("  STATUS: BERHASIL DICEGAH -> \"" + e.getMessage() + "\"");
        }
        System.out.println("Jumlah Data: " + katalog.getJumlahProduk() + " (Integritas koleksi terjaga)");
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 8: Menghapus Produk dari Koleksi (Operasi Hapus Berhasil & Gagal)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [SKENARIO 8] PENGUJIAN OPERASI HAPUS PRODUK");
        System.out.println("[Uji 8a] Menghapus produk yang terdaftar: \"PRD-003\" (Spidol Snowman)");
        boolean statusHapusAda = katalog.hapusProduk("PRD-003");
        System.out.println("Hasil hapus PRD-003: " + statusHapusAda + " (Berhasil dihapus dari list)");
        System.out.println("Jumlah Data Sekarang: " + katalog.getJumlahProduk());

        System.out.println("[Uji 8b] Menghapus produk yang tidak terdaftar: \"PRD-999\"");
        boolean statusHapusTidakAda = katalog.hapusProduk("PRD-999");
        System.out.println("Hasil hapus PRD-999: " + statusHapusTidakAda + " (Tidak ada data terhapus)");
        System.out.println("Jumlah Data Sekarang: " + katalog.getJumlahProduk());
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 9: Pengujian Enkapsulasi Koleksi (Proteksi Manipulasi Pihak Luar)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [SKENARIO 9] UJI PROTEKSI ENKAPSULASI KOLEKSI (UNMODIFIABLE LIST)");
        System.out.println("Mencoba memodifikasi koleksi internal katalog secara langsung melalui getDaftarProduk().clear():");
        try {
            katalog.getDaftarProduk().clear();
            System.out.println("STATUS: Gagal (Koleksi internal berhasil dibobol)!");
        } catch (UnsupportedOperationException e) {
            System.out.println("STATUS: BERHASIL DIPROTEKSI!");
            System.out.println("Koleksi internal bersifat Read-Only (Unmodifiable). Perubahan wajib melalui method pengelola.");
        }
        System.out.println("Jumlah Data: " + katalog.getJumlahProduk() + " (Koleksi tetap aman)");
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // SKENARIO 10: Integrasi Transaksi Kasir POS Menggunakan Produk dari Koleksi Katalog
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [SKENARIO 10] INTEGRASI TRANSAKSI KASIR POS DENGAN PRODUK DARI KATALOG");
        try {
            TransaksiPenjualan trx = new TransaksiPenjualan(
                    "TRX-20261111-0001", "2026-11-11 10:30:00", "Cash", "Pesanan Sekolah");

            // Mengambil produk dari katalog berbasis kode unik
            Produk produk1 = katalog.cariProduk("PRD-001"); // HVS A4
            Produk produk2 = katalog.cariProduk("PRD-002"); // Pulpen AE7
            Produk layanan1 = katalog.cariProduk("SRV-001"); // Fotokopi

            trx.tambahItem(produk1, 5);      // Beli 5 rim HVS
            trx.tambahItem(produk2, 10);     // Beli 10 pcs Pulpen
            trx.tambahItem(layanan1, 200);   // Jasa 200 lembar Fotokopi

            trx.cetakStruk();
        } catch (StokTidakCukupException | TransaksiInvalidException e) {
            System.err.println("Transaksi gagal: " + e.getMessage());
        }
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // REKAPITULASI AKHIR KATALOG & INVENTARIS TOKO
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [STATUS AKHIR] REKAPITULASI INVENTARIS TOKO SETELAH SELURUH PENGUJIAN");
        katalog.tampilkanKatalog();

        System.out.println("==========================================================================================");
        System.out.println("            SELURUH PENGUJIAN MODUL 11 SELESAI DENGAN SUKSES & VALID!                     ");
        System.out.println("==========================================================================================");
    }
}


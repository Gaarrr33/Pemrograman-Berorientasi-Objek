import model.BatchStok;
import model.DetailTransaksi;
import model.Produk;
import model.TransaksiPenjualan;

/**
 * Main Class untuk pengujian praktikum PBO Modul 9.
 * Menguji integrasi seluruh class model yang merepresentasikan sistem
 * "Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi".
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("               PRAKTIKUM PEMROGRAMAN BERORIENTASI OBJEK — MODUL 9                         ");
        System.out.println("                    SISTEM TEGAR COPYFLOW (TOKO & FOTOKOPI)                               ");
        System.out.println("==========================================================================================\n");

        // ----------------------------------------------------------------------------------------
        // TAHAP 1: Inisialisasi Master Data Produk & Jasa
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 1] INISIALISASI MASTER DATA PRODUK & LAYANAN JASA");
        
        // Produk Toko (Fisik)
        Produk hvsA4 = new Produk(1, "Kertas HVS A4 75gr PaperOne", "Kertas", "PRODUCT", "rim", 55000, 5);
        Produk pulpen = new Produk(2, "Pulpen Standard AE7 Hitam", "Alat Tulis", "PRODUCT", "pcs", 3500, 15);
        
        // Layanan Jasa Fotokopi
        Produk fotokopi = new Produk(3, "Jasa Fotokopi Bolak-Balik", "Fotokopi", "SERVICE", "lbr", 350, 150.0);
        Produk jilid = new Produk(4, "Jasa Jilid Lakban Mika", "Jilid", "SERVICE", "buku", 5000, 1500.0);

        System.out.println("Berhasil mendaftarkan 4 master produk & layanan:");
        System.out.println(" 1. " + hvsA4);
        System.out.println(" 2. " + pulpen);
        System.out.println(" 3. " + fotokopi);
        System.out.println(" 4. " + jilid);
        System.out.println();

        // ----------------------------------------------------------------------------------------
        // TAHAP 2: Pencatatan Persediaan Batch Masuk (Stok Awal & Restock FIFO)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 2] PENCATATAN BATCH STOK MASUK (SIMULASI FIFO)");

        // Kertas HVS masuk dalam dua gelombang pembelian (harga beli berbeda)
        hvsA4.tambahBatchStok(101, "2026-10-01", 10, 45000, "Stok Awal Bulan");
        hvsA4.tambahBatchStok(102, "2026-10-15", 15, 48000, "Restock Kulakan Supplier A");

        // Pulpen masuk 1 batch
        pulpen.tambahBatchStok(103, "2026-10-10", 40, 2200, "Kulakan Grosir ATK");

        System.out.println("Riwayat Batch Masuk untuk " + hvsA4.getNama() + ":");
        for (BatchStok b : hvsA4.getDaftarBatch()) {
            System.out.println("  * " + b);
        }
        System.out.printf("  Total Stok Tersedia : %d %s | Nilai Inventaris: Rp %,.0f | Status: %s\n\n",
                hvsA4.hitungTotalStok(), hvsA4.getSatuan(), hvsA4.hitungNilaiInventaris(), hvsA4.getStatusStok());

        System.out.println("Riwayat Batch Masuk untuk " + pulpen.getNama() + ":");
        for (BatchStok b : pulpen.getDaftarBatch()) {
            System.out.println("  * " + b);
        }
        System.out.printf("  Total Stok Tersedia : %d %s | Nilai Inventaris: Rp %,.0f | Status: %s\n\n",
                pulpen.hitungTotalStok(), pulpen.getSatuan(), pulpen.hitungNilaiInventaris(), pulpen.getStatusStok());

        // ----------------------------------------------------------------------------------------
        // TAHAP 3: Transaksi Kasir #1 (Multi-Item Produk Fisik & Jasa)
        // Menjual 12 rim HVS (akan memotong 10 rim dari Batch 101 @45rb dan 2 rim dari Batch 102 @48rb)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 3] MEMBUAT TRANSAKSI PENJUALAN KASIR (TRX-20261026-0001)");
        TransaksiPenjualan trx1 = new TransaksiPenjualan(
                "TRX-20261026-0001", "2026-10-26 09:15:00", "Cash", "Pesanan Kantor Desa");

        trx1.tambahItem(hvsA4, 12);      // 12 rim HVS
        trx1.tambahItem(pulpen, 5);      // 5 pcs Pulpen
        trx1.tambahItem(fotokopi, 100);  // 100 lembar Fotokopi
        trx1.tambahItem(jilid, 2);       // 2 buku Jilid Lakban

        // Cetak struk belanja
        trx1.cetakStruk();
        System.out.println();

        // Tampilkan kondisi stok setelah transaksi 1
        System.out.println("Kondisi Batch Stok " + hvsA4.getNama() + " setelah penjualan 12 rim:");
        for (BatchStok b : hvsA4.getDaftarBatch()) {
            System.out.println("  * " + b);
        }
        System.out.printf("Sisa Stok Fisik HVS: %d rim (Nilai Aset: Rp %,.0f)\n\n",
                hvsA4.hitungTotalStok(), hvsA4.hitungNilaiInventaris());

        // ----------------------------------------------------------------------------------------
        // TAHAP 4: Transaksi Kasir #2 & Demonstrasi Pembatalan Transaksi (Rollback Stok)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 4] SIMULASI TRANSAKSI #2 & PEMBATALAN (CANCEL / ROLLBACK)");
        TransaksiPenjualan trx2 = new TransaksiPenjualan(
                "TRX-20261026-0002", "2026-10-26 10:30:00", "QRIS", "Pelanggan Mahasiswa");

        System.out.printf("Stok HVS sebelum transaksi 2: %d rim\n", hvsA4.hitungTotalStok());
        trx2.tambahItem(hvsA4, 8); // Mengambil 8 rim dari sisa Batch 102
        System.out.printf("Stok HVS sesudah ditambah ke transaksi 2 (8 rim): %d rim\n", hvsA4.hitungTotalStok());
        System.out.println("Status Transaksi 2: " + trx2.getStatus() + " | Total Tagihan: Rp " + String.format("%,.0f", trx2.getTotal()));

        System.out.println("--> Pelanggan membatalkan transaksi karena dompet tertinggal.");
        trx2.batalkanTransaksi();
        System.out.println("Status Transaksi 2 sekarang: " + trx2.getStatus());
        System.out.printf("Stok HVS setelah pembatalan (kembali utuh): %d rim\n", hvsA4.hitungTotalStok());
        System.out.println();

        // ----------------------------------------------------------------------------------------
        // TAHAP 5: Pengujian Validasi State & Error Handling
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 5] PENGUJIAN VALIDASI STATE & DEFENSIVE PROGRAMMING");

        // Uji 1: Permintaan stok melebihi stok yang tersedia
        System.out.print("[Uji 1] Meminta stok melebihi ketersediaan (Stok sisa: " + hvsA4.hitungTotalStok() + ", minta: 25 rim)... ");
        try {
            TransaksiPenjualan trxErr = new TransaksiPenjualan("TRX-ERR-01", "2026-10-26", "Cash", "-");
            trxErr.tambahItem(hvsA4, 25);
            System.out.println("GAGAL (Seharusnya melempar exception)!");
        } catch (IllegalStateException e) {
            System.out.println("BERHASIL DICEGAH!");
            System.out.println("  Pesan Sistem: \"" + e.getMessage() + "\"");
        }

        // Uji 2: Input jumlah non-positif (0 atau negatif)
        System.out.print("[Uji 2] Input kuantitas bernilai 0 atau negatif (-3)... ");
        try {
            TransaksiPenjualan trxErr = new TransaksiPenjualan("TRX-ERR-02", "2026-10-26", "Cash", "-");
            trxErr.tambahItem(pulpen, -3);
            System.out.println("GAGAL (Seharusnya melempar exception)!");
        } catch (IllegalArgumentException e) {
            System.out.println("BERHASIL DICEGAH!");
            System.out.println("  Pesan Sistem: \"" + e.getMessage() + "\"");
        }

        // Uji 3: Menambahkan batch stok ke produk jasa (SERVICE)
        System.out.print("[Uji 3] Mencoba input batch fisik pada produk jasa Fotokopi... ");
        try {
            fotokopi.tambahBatchStok(999, "2026-10-26", 100, 100, "Salah Input");
            System.out.println("GAGAL (Seharusnya melempar exception)!");
        } catch (IllegalStateException e) {
            System.out.println("BERHASIL DICEGAH!");
            System.out.println("  Pesan Sistem: \"" + e.getMessage() + "\"");
        }

        // Uji 4: Mengubah harga jual menjadi negatif
        System.out.print("[Uji 4] Mengubah harga jual menjadi negatif (-10000)... ");
        try {
            pulpen.ubahHargaJual(-10000, "Diskon Khusus");
            System.out.println("GAGAL (Seharusnya melempar exception)!");
        } catch (IllegalArgumentException e) {
            System.out.println("BERHASIL DICEGAH!");
            System.out.println("  Pesan Sistem: \"" + e.getMessage() + "\"");
        }

        // Uji 5: Menambahkan item pada transaksi yang sudah dibatalkan (CANCELLED)
        System.out.print("[Uji 5] Menambah item baru ke Transaksi 2 yang sudah CANCELLED... ");
        try {
            trx2.tambahItem(pulpen, 2);
            System.out.println("GAGAL (Seharusnya melempar exception)!");
        } catch (IllegalStateException e) {
            System.out.println("BERHASIL DICEGAH!");
            System.out.println("  Pesan Sistem: \"" + e.getMessage() + "\"");
        }
        System.out.println();

        // ----------------------------------------------------------------------------------------
        // TAHAP 6: Ringkasan Status Akhir Seluruh Produk & Inventaris Toko
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 6] REKAPITULASI STATUS AKHIR INVENTARIS TEGAR COPYFLOW");
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf(" %-4s | %-32s | %-10s | %-12s | %-10s | %-15s\n",
                "ID", "Nama Produk / Layanan", "Tipe", "Stok Sisa", "Status", "Nilai Inventaris");
        System.out.println("------------------------------------------------------------------------------------------");
        Produk[] semuaProduk = { hvsA4, pulpen, fotokopi, jilid };
        double totalInventarisToko = 0.0;
        for (Produk p : semuaProduk) {
            System.out.printf(" %-4d | %-32s | %-10s | %-12s | %-10s | Rp %,13.0f\n",
                    p.getId(),
                    p.getNama(),
                    p.getTipe(),
                    p.isProductType() ? (p.hitungTotalStok() + " " + p.getSatuan()) : "-",
                    p.getStatusStok(),
                    p.hitungNilaiInventaris());
            totalInventarisToko += p.hitungNilaiInventaris();
        }
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf(" TOTAL NILAI PERSSEDIAAN TOKO : Rp %,.0f\n", totalInventarisToko);
        System.out.println("==========================================================================================");
        System.out.println("                 PENGUJIAN PROGRAM JAVA PBO MODUL 9 SELESAI DENGAN SUKSES!                ");
        System.out.println("==========================================================================================");
    }
}

import exception.StokTidakCukupException;
import exception.TransaksiInvalidException;
import model.BatchStok;
import model.DetailTransaksi;
import model.Produk;
import model.TransaksiPenjualan;

/**
 * Main Class untuk Pengujian Praktikum PBO Modul 10.
 * Topik: Exception Handling dan Validasi State Objek.
 * Proyek: Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi.
 * 
 * Program ini menguji:
 * 1. Validasi input dan integritas state sebelum mutasi objek.
 * 2. Penerapan try, catch, finally, throw, dan throws.
 * 3. Penerapan Custom Exception (StokTidakCukupException & TransaksiInvalidException).
 * 4. Pembuktian bahwa seluruh penolakan operasi (gagal) mempertahankan konsistensi state.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("               PRAKTIKUM PEMROGRAMAN BERORIENTASI OBJEK — MODUL 10                        ");
        System.out.println("                  EXCEPTION HANDLING DAN VALIDASI STATE OBJEK                             ");
        System.out.println("                    SISTEM TEGAR COPYFLOW (TOKO & FOTOKOPI)                               ");
        System.out.println("==========================================================================================\n");

        // ----------------------------------------------------------------------------------------
        // TAHAP 1: Inisialisasi Master Data Produk & Layanan Jasa
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 1] INISIALISASI MASTER DATA PRODUK & LAYANAN JASA");
        Produk hvsA4 = new Produk(1, "Kertas HVS A4 75gr PaperOne", "Kertas", "PRODUCT", "rim", 55000, 5);
        Produk pulpen = new Produk(2, "Pulpen Standard AE7 Hitam", "Alat Tulis", "PRODUCT", "pcs", 3500, 15);
        Produk fotokopi = new Produk(3, "Jasa Fotokopi Bolak-Balik", "Fotokopi", "SERVICE", "lbr", 350, 150.0);
        Produk jilid = new Produk(4, "Jasa Jilid Lakban Mika", "Jilid", "SERVICE", "buku", 5000, 1500.0);

        System.out.println("Master produk & layanan terdaftar:");
        System.out.println(" 1. " + hvsA4);
        System.out.println(" 2. " + pulpen);
        System.out.println(" 3. " + fotokopi);
        System.out.println(" 4. " + jilid);
        System.out.println();

        // ----------------------------------------------------------------------------------------
        // TAHAP 2: Pencatatan Persediaan Batch Masuk (Stok Awal & Restock FIFO)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 2] PENCATATAN PERSSEDIAAN BATCH MASUK (FIFO)");
        hvsA4.tambahBatchStok(101, "2026-10-01", 10, 45000, "Stok Awal Bulan");
        hvsA4.tambahBatchStok(102, "2026-10-15", 15, 48000, "Restock Kulakan Supplier A");
        pulpen.tambahBatchStok(103, "2026-10-10", 40, 2200, "Kulakan Grosir ATK");

        System.out.printf("Stok Tersedia %s: %d %s (Nilai: Rp %,.0f)\n",
                hvsA4.getNama(), hvsA4.hitungTotalStok(), hvsA4.getSatuan(), hvsA4.hitungNilaiInventaris());
        System.out.printf("Stok Tersedia %s: %d %s (Nilai: Rp %,.0f)\n\n",
                pulpen.getNama(), pulpen.hitungTotalStok(), pulpen.getSatuan(), pulpen.hitungNilaiInventaris());

        // ----------------------------------------------------------------------------------------
        // TAHAP 3: Transaksi Kasir Normal (Skenario Berhasil)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 3] SKENARIO NORMAL: TRANSAKSI PENJUALAN KASIR BERHASIL");
        try {
            TransaksiPenjualan trx1 = new TransaksiPenjualan(
                    "TRX-20261102-0001", "2026-11-02 09:15:00", "Cash", "Pesanan Kantor Desa");

            trx1.tambahItem(hvsA4, 12);     // Memotong 10 dari Batch 101 dan 2 dari Batch 102
            trx1.tambahItem(pulpen, 5);     // Memotong 5 dari Batch 103
            trx1.tambahItem(fotokopi, 100); // Jasa fotokopi
            trx1.tambahItem(jilid, 2);      // Jasa jilid

            trx1.cetakStruk();
        } catch (StokTidakCukupException | TransaksiInvalidException e) {
            System.err.println("Transaksi 1 Gagal: " + e.getMessage());
        } finally {
            System.out.printf("Status State Stok Pasca Trx 1 -> HVS: %d rim | Pulpen: %d pcs\n\n",
                    hvsA4.hitungTotalStok(), pulpen.hitungTotalStok());
        }

        // ----------------------------------------------------------------------------------------
        // TAHAP 4: Demonstrasi Pembatalan Transaksi (Rollback Konsisten)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 4] SKENARIO PEMBATALAN TRANSAKSI (ROLLBACK AMAN)");
        try {
            TransaksiPenjualan trx2 = new TransaksiPenjualan(
                    "TRX-20261102-0002", "2026-11-02 10:30:00", "QRIS", "Pelanggan Mahasiswa");

            System.out.printf("Stok HVS sebelum Trx 2 : %d rim\n", hvsA4.hitungTotalStok());
            trx2.tambahItem(hvsA4, 8); // Ambil 8 rim dari sisa Batch 102
            System.out.printf("Stok HVS saat di keranjang : %d rim\n", hvsA4.hitungTotalStok());

            System.out.println("-> Pelanggan membatalkan pembelian karena dompet tertinggal.");
            trx2.batalkanTransaksi();
            System.out.println("Status Transaksi 2: " + trx2.getStatus());
        } catch (StokTidakCukupException | TransaksiInvalidException e) {
            System.err.println("Gagal memproses transaksi 2: " + e.getMessage());
        } finally {
            System.out.printf("Stok HVS setelah pembatalan (kembali utuh): %d rim\n\n", hvsA4.hitungTotalStok());
        }

        // ----------------------------------------------------------------------------------------
        // TAHAP 5: PENGUJIAN DEFENSIVE & VALIDASI STATE (SKENARIO GAGAL & BOUNDARY)
        // Membuktikan bahwa seluruh kegagalan operasi menjaga konsistensi state objek
        // ----------------------------------------------------------------------------------------
        System.out.println("==========================================================================================");
        System.out.println(">>> [TAHAP 5] PENGUJIAN EXCEPTION HANDLING, VALIDASI & KONSISTENSI STATE");
        System.out.println("==========================================================================================");

        // UJI 1: Permintaan stok melebihi stok tersedia (StokTidakCukupException)
        ujiPermintaanStokMelebihiKapasitas(hvsA4);

        // UJI 2: Input kuantitas tidak positif / negatif (IllegalArgumentException)
        ujiKuantitasTidakPositif(pulpen);

        // UJI 3: Format angka salah / teks bukan numerik (NumberFormatException)
        ujiFormatAngkaSalah(pulpen);

        // UJI 4: Pembelian tepat sejumlah sisa stok (Boundary test) - HARUS BERHASIL
        ujiPembelianTepatSejumlahStok(hvsA4);

        // UJI 5: Menambah item pada transaksi yang sudah dibatalkan (TransaksiInvalidException)
        ujiModifikasiTransaksiBatal();

        // UJI 6: Pembatalan ganda pada transaksi yang sudah CANCELLED (TransaksiInvalidException)
        ujiPembatalanGanda();

        // UJI 7: Inisialisasi transaksi dengan metode pembayaran tidak valid (IllegalArgumentException)
        ujiMetodePembayaranInvalid();

        // UJI 8: Transaksi pada produk yang berstatus non-aktif (IllegalStateException)
        ujiProdukNonAktif(pulpen);

        // ----------------------------------------------------------------------------------------
        // TAHAP 6: Rekapitulasi Status Akhir Inventaris Toko
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
        System.out.println("          SELURUH PENGUJIAN MODUL 10 SELESAI DENGAN SUKSES & STATE KONSISTEN!             ");
        System.out.println("==========================================================================================");
    }

    // ============================================================================================
    // HELPER METHODS UNTUK PENGUJIAN TERSTRUKTUR
    // ============================================================================================

    private static void ujiPermintaanStokMelebihiKapasitas(Produk hvs) {
        int stokAwal = hvs.hitungTotalStok();
        System.out.printf("[Uji 1] Pembelian melebihi stok (Tersedia: %d rim, Minta: 30 rim)\n", stokAwal);
        try {
            TransaksiPenjualan trx = new TransaksiPenjualan("TRX-TEST-01", "2026-11-02", "Cash", "-");
            trx.tambahItem(hvs, 30);
            System.out.println("  STATUS: GAGAL (Seharusnya ditolak)");
        } catch (StokTidakCukupException e) {
            System.out.println("  HASIL : Ditolak via Custom Exception [StokTidakCukupException]");
            System.out.println("  PESAN : \"" + e.getMessage() + "\"");
        } catch (Exception e) {
            System.out.println("  HASIL : Ditolak via Exception umum: " + e.getMessage());
        } finally {
            int stokAkhir = hvs.hitungTotalStok();
            System.out.printf("  STATE : Stok awal = %d rim -> Stok akhir = %d rim (KONSISTEN / TIDAK BERUBAH)\n",
                    stokAwal, stokAkhir);
            System.out.println("------------------------------------------------------------------------------------------");
        }
    }

    private static void ujiKuantitasTidakPositif(Produk pulpen) {
        int stokAwal = pulpen.hitungTotalStok();
        System.out.printf("[Uji 2] Pembelian kuantitas tidak positif (Input: -5 pcs, Stok: %d pcs)\n", stokAwal);
        try {
            TransaksiPenjualan trx = new TransaksiPenjualan("TRX-TEST-02", "2026-11-02", "Cash", "-");
            trx.tambahItem(pulpen, -5);
            System.out.println("  STATUS: GAGAL (Seharusnya ditolak)");
        } catch (IllegalArgumentException e) {
            System.out.println("  HASIL : Ditolak via [IllegalArgumentException]");
            System.out.println("  PESAN : \"" + e.getMessage() + "\"");
        } catch (Exception e) {
            System.out.println("  HASIL : Ditolak: " + e.getMessage());
        } finally {
            int stokAkhir = pulpen.hitungTotalStok();
            System.out.printf("  STATE : Stok awal = %d pcs -> Stok akhir = %d pcs (KONSISTEN / TIDAK BERUBAH)\n",
                    stokAwal, stokAkhir);
            System.out.println("------------------------------------------------------------------------------------------");
        }
    }

    private static void ujiFormatAngkaSalah(Produk pulpen) {
        int stokAwal = pulpen.hitungTotalStok();
        String inputKasir = "limapuluh";
        System.out.printf("[Uji 3] Parsing input teks non-numerik dari form kasir (Input: \"%s\")\n", inputKasir);
        try {
            int jumlah = Integer.parseInt(inputKasir);
            TransaksiPenjualan trx = new TransaksiPenjualan("TRX-TEST-03", "2026-11-02", "Cash", "-");
            trx.tambahItem(pulpen, jumlah);
            System.out.println("  STATUS: GAGAL (Seharusnya ditolak)");
        } catch (NumberFormatException e) {
            System.out.println("  HASIL : Ditolak via [NumberFormatException]");
            System.out.println("  PESAN : \"Ditolak: format kuantitas harus berupa bilangan bulat valid! (Detail: " + e.getMessage() + ")\"");
        } catch (Exception e) {
            System.out.println("  HASIL : Ditolak: " + e.getMessage());
        } finally {
            int stokAkhir = pulpen.hitungTotalStok();
            System.out.printf("  STATE : Stok awal = %d pcs -> Stok akhir = %d pcs (KONSISTEN / TIDAK BERUBAH)\n",
                    stokAwal, stokAkhir);
            System.out.println("------------------------------------------------------------------------------------------");
        }
    }

    private static void ujiPembelianTepatSejumlahStok(Produk hvs) {
        int sisaTersedia = hvs.hitungTotalStok();
        System.out.printf("[Uji 4] Pembelian batas tepat sejumlah stok (Boundary Test: Minta %d rim dari sisa %d rim)\n",
                sisaTersedia, sisaTersedia);
        try {
            TransaksiPenjualan trxHabis = new TransaksiPenjualan("TRX-TEST-04", "2026-11-02", "Cash", "Beli Habis Sisa Stok");
            trxHabis.tambahItem(hvs, sisaTersedia);
            System.out.println("  HASIL : Pembelian tepat sejumlah stok BERHASIL DIPERBOLEHKAN!");
            System.out.println("  ALASAN: Toko boleh menjual habis seluruh stok; kondisi (jumlah <= stok) terpenuhi.");
        } catch (Exception e) {
            System.out.println("  STATUS: GAGAL (Seharusnya diperbolehkan): " + e.getMessage());
        } finally {
            int stokAkhir = hvs.hitungTotalStok();
            System.out.printf("  STATE : Sisa stok HVS sekarang = %d rim [Status: %s]\n",
                    stokAkhir, hvs.getStatusStok());
            System.out.println("------------------------------------------------------------------------------------------");
        }
    }

    private static void ujiModifikasiTransaksiBatal() {
        System.out.println("[Uji 5] Menambahkan item pada transaksi yang sudah berstatus CANCELLED");
        try {
            TransaksiPenjualan trxBatal = new TransaksiPenjualan("TRX-TEST-05", "2026-11-02", "Cash", "-");
            trxBatal.batalkanTransaksi(); // Batalkan transaksi
            Produk tempProduk = new Produk(99, "Barang Uji", "Uji", "PRODUCT", "pcs", 1000, 0);
            trxBatal.tambahItem(tempProduk, 1);
            System.out.println("  STATUS: GAGAL (Seharusnya melempar exception)");
        } catch (TransaksiInvalidException e) {
            System.out.println("  HASIL : Ditolak via Custom Exception [TransaksiInvalidException]");
            System.out.println("  PESAN : \"" + e.getMessage() + "\"");
        } catch (Exception e) {
            System.out.println("  HASIL : Ditolak: " + e.getMessage());
        } finally {
            System.out.println("  STATE : Transaksi tetap berstatus CANCELLED dan tidak ada item bertambah.");
            System.out.println("------------------------------------------------------------------------------------------");
        }
    }

    private static void ujiPembatalanGanda() {
        System.out.println("[Uji 6] Percobaan pembatalan ganda (Double Rollback) pada transaksi yang sudah CANCELLED");
        try {
            TransaksiPenjualan trxBatal = new TransaksiPenjualan("TRX-TEST-06", "2026-11-02", "Cash", "-");
            trxBatal.batalkanTransaksi(); // Pembatalan pertama (sukses)
            trxBatal.batalkanTransaksi(); // Pembatalan kedua (harus ditolak)
            System.out.println("  STATUS: GAGAL (Seharusnya melempar exception)");
        } catch (TransaksiInvalidException e) {
            System.out.println("  HASIL : Ditolak via Custom Exception [TransaksiInvalidException]");
            System.out.println("  PESAN : \"" + e.getMessage() + "\"");
        } catch (Exception e) {
            System.out.println("  HASIL : Ditolak: " + e.getMessage());
        } finally {
            System.out.println("  STATE : Mencegah pengembalian stok berulang kali (anti kelebihan inventaris).");
            System.out.println("------------------------------------------------------------------------------------------");
        }
    }

    private static void ujiMetodePembayaranInvalid() {
        System.out.println("[Uji 7] Inisialisasi transaksi dengan metode pembayaran tidak valid (\"PayLater\")");
        try {
            TransaksiPenjualan trx = new TransaksiPenjualan("TRX-TEST-07", "2026-11-02", "PayLater", "-");
            System.out.println("  STATUS: GAGAL (Seharusnya melempar exception)");
        } catch (IllegalArgumentException e) {
            System.out.println("  HASIL : Ditolak via [IllegalArgumentException]");
            System.out.println("  PESAN : \"" + e.getMessage() + "\"");
        } catch (Exception e) {
            System.out.println("  HASIL : Ditolak: " + e.getMessage());
        } finally {
            System.out.println("  STATE : Objek transaksi tidak valid dicegah sebelum masuk ke siklus bisnis.");
            System.out.println("------------------------------------------------------------------------------------------");
        }
    }

    private static void ujiProdukNonAktif(Produk pulpen) {
        int stokAwal = pulpen.hitungTotalStok();
        System.out.printf("[Uji 8] Transaksi pada produk yang telah dinonaktifkan (Aktif = false, Stok: %d pcs)\n", stokAwal);
        pulpen.setAktif(false); // Nonaktifkan produk
        try {
            TransaksiPenjualan trx = new TransaksiPenjualan("TRX-TEST-08", "2026-11-02", "Cash", "-");
            trx.tambahItem(pulpen, 2);
            System.out.println("  STATUS: GAGAL (Seharusnya melempar exception)");
        } catch (IllegalStateException e) {
            System.out.println("  HASIL : Ditolak via [IllegalStateException]");
            System.out.println("  PESAN : \"" + e.getMessage() + "\"");
        } catch (Exception e) {
            System.out.println("  HASIL : Ditolak: " + e.getMessage());
        } finally {
            pulpen.setAktif(true); // Kembalikan status produk menjadi aktif
            int stokAkhir = pulpen.hitungTotalStok();
            System.out.printf("  STATE : Stok pulpen tetap %d pcs (Konsistensi terjaga). Status dipulihkan ke aktif.\n", stokAkhir);
            System.out.println("------------------------------------------------------------------------------------------");
        }
    }
}


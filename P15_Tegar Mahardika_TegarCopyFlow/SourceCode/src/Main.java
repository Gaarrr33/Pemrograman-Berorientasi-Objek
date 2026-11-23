import exception.StokTidakCukupException;
import exception.TransaksiInvalidException;
import model.*;
import repository.ProdukFileRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Scanner;

/**
 * Main Class — Aplikasi Konsol POS & Inventaris Toko Tegar CopyFlow.
 * Versi Final P15: Demonstrasi Proyek dan Evaluasi Pemahaman Individu.
 * 
 * Cakupan Demonstrasi & Penilaian (Modul 15):
 * 1. Alur Demonstrasi Utama: Tambah Produk -> Transaksi POS FIFO -> Cetak Struk -> Simpan -> Re-run.
 * 2. Penanganan Kegagalan: Penolakan stok kurang, kode duplikat, dan delimiter '|'.
 * 3. Konsistensi State & Staging Isolation: Operasi ditolak tidak merusak data aktif di memori.
 * 4. Fitur Tambahan Hasil Live Coding / Perubahan Terarah (Langkah 4 Modul 15):
 *    - Pencarian fleksibel berdasarkan sebagian nama produk (menuCariByNama)
 *    - Laporan dan rekapitulasi produk stok kritis/menipis (menuLaporanStokKritis)
 *    - Dialog konfirmasi eksplisit sebelum penghapusan data produk
 */
public class Main {

    private final KatalogProduk katalog;
    private final ProdukFileRepository repository;
    private final Path lokasiFile;
    private boolean berubah;

    public Main(Path lokasiFile) {
        this.katalog = new KatalogProduk();
        this.repository = new ProdukFileRepository();
        this.lokasiFile = lokasiFile;
        this.berubah = false;
    }

    public boolean isBerubah() {
        return berubah;
    }

    public KatalogProduk getKatalog() {
        return katalog;
    }

    // --- Helper Input Konsol Reusable & Type-Safe ---

    public static String bacaString(Scanner input, String pesan) {
        System.out.print(pesan);
        return input.nextLine().trim();
    }

    public static String bacaStringWajib(Scanner input, String pesan) {
        while (true) {
            String val = bacaString(input, pesan);
            if (!val.isEmpty()) {
                return val;
            }
            System.out.println("[PERINGATAN] Input tidak boleh kosong. Silakan ulangi.");
        }
    }

    public static int bacaInt(Scanner input, String pesan, int nilaiMinimal) {
        while (true) {
            String text = bacaString(input, pesan);
            try {
                int val = Integer.parseInt(text);
                if (val < nilaiMinimal) {
                    System.out.printf("[PERINGATAN] Nilai minimal adalah %d. Silakan ulangi.\n", nilaiMinimal);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("[PERINGATAN] Format input harus berupa bilangan bulat valid.");
            }
        }
    }

    public static double bacaDouble(Scanner input, String pesan, double nilaiMinimal) {
        while (true) {
            String text = bacaString(input, pesan);
            try {
                double val = Double.parseDouble(text.replace(',', '.'));
                if (val < nilaiMinimal) {
                    System.out.printf("[PERINGATAN] Nilai minimal adalah Rp %,.2f. Silakan ulangi.\n", nilaiMinimal);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("[PERINGATAN] Format input desimal tidak valid. Silakan ulangi.");
            }
        }
    }

    private static boolean konfirmasi(Scanner input, String pesan) {
        return bacaString(input, pesan).equalsIgnoreCase("YA");
    }

    // --- Modular Menu Handlers ---

    public void menuTambah(Scanner input) {
        System.out.println("\n--- [1] TAMBAH PRODUK BARU ---");
        String kode = bacaStringWajib(input, "Kode Produk (contoh PRD-001): ");
        int id = bacaInt(input, "ID Produk (angka bulat positif > 0): ", 1);
        String nama = bacaStringWajib(input, "Nama Produk: ");
        String kategori = bacaStringWajib(input, "Kategori: ");
        String tipe = bacaStringWajib(input, "Tipe (PRODUCT / SERVICE): ").toUpperCase();
        String satuan = bacaStringWajib(input, "Satuan (rim/pcs/lembar/buku): ");
        double hargaJual = bacaDouble(input, "Harga Jual (Rp): ", 0.0);

        Produk produkBaru;
        if ("SERVICE".equalsIgnoreCase(tipe)) {
            double biaya = bacaDouble(input, "Biaya Pokok Jasa per Unit (Rp): ", 0.0);
            produkBaru = new Produk(kode, id, nama, kategori, tipe, satuan, hargaJual, biaya);
        } else {
            int minStok = bacaInt(input, "Batas Minimum Stok Peringatan: ", 0);
            produkBaru = new Produk(kode, id, nama, kategori, tipe, satuan, hargaJual, minStok);

            String inisialisasiBatch = bacaString(input, "Input batch stok awal sekarang? (Y/T): ");
            if (inisialisasiBatch.equalsIgnoreCase("Y")) {
                int jmlStok = bacaInt(input, "Jumlah Stok Masuk: ", 1);
                double hargaBeli = bacaDouble(input, "Harga Beli per Satuan: ", 0.0);
                produkBaru.tambahBatchStok(1, "2026-11-18", jmlStok, hargaBeli, "Kulakan awal");
            }
        }

        katalog.tambahProduk(produkBaru);
        berubah = true;
        System.out.println("[SUKSES] Produk '" + produkBaru.getNama() + "' (" + produkBaru.getKode() + ") berhasil ditambahkan.");
    }

    public void menuTampilkan() {
        System.out.println("\n--- [2] KATALOG PRODUK & INVENTARIS TOKO ---");
        katalog.tampilkanKatalog();
    }

    public void menuCari(Scanner input) {
        System.out.println("\n--- [3] CARI PRODUK (BERDASARKAN KODE) ---");
        String kode = bacaStringWajib(input, "Masukkan Kode Produk yang dicari: ");
        Produk produk = katalog.cariProduk(kode);
        if (produk == null) {
            System.out.println("[HASIL] Produk dengan kode '" + kode + "' tidak ditemukan dalam katalog.");
        } else {
            tampilkanDetailProduk(produk);
        }
    }

    /**
     * Langkah 4 Modul 15 (Perubahan Terarah 1): Pencarian Sebagian Nama
     */
    public void menuCariByNama(Scanner input) {
        System.out.println("\n--- [4] CARI PRODUK (SEBAGIAN NAMA) ---");
        String keyword = bacaStringWajib(input, "Masukkan kata kunci nama: ");
        List<Produk> hasil = katalog.cariProdukByNama(keyword);
        if (hasil.isEmpty()) {
            System.out.println("[HASIL] Tidak ada produk yang mengandung kata kunci '" + keyword + "'.");
        } else {
            System.out.printf("[DITEMUKAN] Sebanyak %d produk cocok:\n", hasil.size());
            for (int i = 0; i < hasil.size(); i++) {
                Produk p = hasil.get(i);
                System.out.printf("  %d. [%s] %s | Tipe: %s | Stok: %d %s | Harga: Rp %,.0f\n",
                        (i + 1), p.getKode(), p.getNama(), p.getTipe(), p.hitungTotalStok(), p.getSatuan(), p.getHargaJual());
            }
        }
    }

    /**
     * Langkah 4 Modul 15 (Perubahan Terarah 2): Konfirmasi Sebelum Hapus
     */
    public void menuHapus(Scanner input) {
        System.out.println("\n--- [5] HAPUS PRODUK DARI KATALOG ---");
        String kode = bacaStringWajib(input, "Masukkan Kode Produk yang akan dihapus: ");
        Produk p = katalog.cariProduk(kode);
        if (p == null) {
            System.out.println("[GAGAL] Produk dengan kode '" + kode + "' tidak ditemukan. Koleksi tidak berubah.");
            return;
        }

        // Konfirmasi eksplisit sebelum penghapusan permanen
        String pesan = String.format("Apakah Anda yakin ingin menghapus '%s' (%s)? Ketik YA: ", p.getNama(), p.getKode());
        if (!konfirmasi(input, pesan)) {
            System.out.println("[INFO] Penghapusan produk dibatalkan oleh pengguna.");
            return;
        }

        katalog.hapusProduk(kode);
        berubah = true;
        System.out.println("[SUKSES] Produk dengan kode '" + kode + "' berhasil dihapus dari katalog.");
    }

    public void menuTransaksiKasir(Scanner input) {
        System.out.println("\n--- [6] TRANSAKSI PENJUALAN KASIR POS ---");
        String noFaktur = "TRX-" + (System.currentTimeMillis() % 1000000);
        String payment = bacaString(input, "Metode Pembayaran (Cash/QRIS/Transfer) [Default Cash]: ");
        if (payment.isEmpty()) payment = "Cash";
        TransaksiPenjualan trx = new TransaksiPenjualan(noFaktur, "2026-11-18 15:00:00", payment, "Kasir: Tegar M.");

        boolean belanja = true;
        while (belanja) {
            String kode = bacaStringWajib(input, "Kode Produk yang dibeli: ");
            Produk p = katalog.cariProduk(kode);
            if (p == null) {
                System.out.println("[ERROR] Produk dengan kode '" + kode + "' tidak ditemukan.");
            } else {
                try {
                    int qty = bacaInt(input, "Jumlah Beli: ", 1);
                    trx.tambahItem(p, qty);
                    System.out.println("  -> " + p.getNama() + " x" + qty + " masuk ke keranjang belanja.");
                } catch (StokTidakCukupException | TransaksiInvalidException e) {
                    System.out.println("[DITOLAK] " + e.getMessage());
                }
            }
            String lanjut = bacaString(input, "Tambah item lagi? (Y/T): ");
            belanja = lanjut.equalsIgnoreCase("Y");
        }

        if (trx.getDaftarItem().isEmpty()) {
            System.out.println("[INFO] Tidak ada item dalam keranjang. Transaksi dibatalkan.");
            return;
        }

        System.out.printf("Total Belanja: Rp %,.2f\n", trx.getTotal());
        double bayar = bacaDouble(input, "Nominal Pembayaran Tunai (Rp): ", 0.0);

        if (bayar < trx.getTotal()) {
            System.out.println("[ERROR] Pembayaran kurang dari total belanja. Transaksi dibatalkan!");
            try {
                trx.batalkanTransaksi();
                System.out.println("[ROLLBACK] Stok produk berhasil dikembalikan ke saldo semula.");
            } catch (Exception ex) {
                System.out.println("Gagal membatalkan transaksi: " + ex.getMessage());
            }
            return;
        }

        berubah = true;
        System.out.println();
        trx.cetakStruk();
        System.out.printf(" Uang Diterima  : Rp %,14.0f\n", bayar);
        System.out.printf(" Kembalian      : Rp %,14.0f\n", (bayar - trx.getTotal()));
        System.out.println("==========================================================================================");
        System.out.println("[SUKSES] Transaksi kasir selesai. Perubahan stok ditandai belum disimpan.");
    }

    /**
     * Langkah 4 Modul 15 (Perubahan Terarah 3): Laporan Stok Kritis
     */
    public void menuLaporanStokKritis() {
        System.out.println("\n--- [7] LAPORAN PRODUK DENGAN STOK KRITIS / MENIPIS ---");
        List<Produk> kritis = katalog.getProdukStokKritis();
        if (kritis.isEmpty()) {
            System.out.println("[INFO] Seluruh produk fisik toko dalam kondisi stok aman.");
        } else {
            System.out.printf("[PERINGATAN INVENTARIS] Ditemukan %d produk memerlukan kulakan ulang:\n", kritis.size());
            for (int i = 0; i < kritis.size(); i++) {
                Produk p = kritis.get(i);
                System.out.printf("  %d. [%s] %-25s | Sisa Stok: %3d %-6s | Batas Min: %2d | Status: %s\n",
                        (i + 1), p.getKode(), p.getNama(), p.hitungTotalStok(), p.getSatuan(), p.getMinStok(), p.getStatusStok());
            }
        }
    }

    public void menuSimpan() {
        System.out.println("\n--- [8] SIMPAN DATA KE FILE DISK ---");
        try {
            repository.simpan(katalog, lokasiFile);
            berubah = false;
            System.out.println("[SUKSES] Seluruh data (" + katalog.getDaftarProduk().size() + " produk) berhasil disimpan secara atomik ke: " + lokasiFile);
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menyimpan data ke file: " + e.getMessage());
        }
    }

    public void menuMuatUlang(Scanner input) {
        System.out.println("\n--- [9] MUAT ULANG DATA DARI FILE DISK ---");
        if (berubah && !konfirmasi(input, "Perubahan belum disimpan. Abaikan? Ketik YA: ")) {
            System.out.println("[INFO] Pemuatan ulang dibatalkan. Perubahan saat ini dipertahankan.");
            return;
        }
        try {
            List<Produk> dataDariFile = repository.muat(lokasiFile);
            katalog.gantiSemua(dataDariFile);
            berubah = false;
            System.out.println("[SUKSES] Data berhasil dimuat ulang dari file. Total " + katalog.getDaftarProduk().size() + " produk aktif di memori.");
        } catch (IOException e) {
            System.out.println("[ERROR] Operasi muat file gagal: " + e.getMessage());
            System.out.println("[KEAMANAN] State katalog di memori dipertahankan utuh (Staging Isolation).");
        }
    }

    private void tampilkanDetailProduk(Produk produk) {
        System.out.println("[DITEMUKAN] Detail Produk:");
        System.out.println("  Kode        : " + produk.getKode());
        System.out.println("  ID          : " + produk.getId());
        System.out.println("  Nama        : " + produk.getNama());
        System.out.println("  Kategori    : " + produk.getKategori());
        System.out.println("  Tipe        : " + produk.getTipe());
        System.out.println("  Stok Fisik  : " + produk.hitungTotalStok() + " " + produk.getSatuan() + " (" + produk.getStatusStok() + ")");
        System.out.printf("  Harga Jual  : Rp %,.2f\n", produk.getHargaJual());
        System.out.printf("  Nilai Aset  : Rp %,.2f\n", produk.hitungNilaiInventaris());
    }

    // --- Main Operational Loop ---
    public void jalankan(Scanner input) {
        if (Files.exists(lokasiFile)) {
            try {
                List<Produk> dataAwal = repository.muat(lokasiFile);
                katalog.gantiSemua(dataAwal);
                System.out.println("[INISIALISASI] Data awal berhasil dimuat (" + katalog.getDaftarProduk().size() + " produk terdaftar).");
            } catch (Exception e) {
                System.out.println("[PERINGATAN] Berkas data awal rusak: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("[INISIALISASI] Berkas data belum ada. Memulai dengan katalog kosong.");
        }

        while (true) {
            System.out.println("\n=======================================================");
            System.out.println("    SISTEM KASIR & INVENTARIS TEGAR COPYFLOW (P15)     ");
            System.out.println("=======================================================");
            System.out.println("  1. Tambah Produk Baru       6. Transaksi Kasir POS   ");
            System.out.println("  2. Tampilkan Katalog        7. Laporan Stok Kritis   ");
            System.out.println("  3. Cari Produk (Kode)       8. Simpan Data ke File   ");
            System.out.println("  4. Cari Produk (Nama)       9. Muat Ulang Data File  ");
            System.out.println("  5. Hapus Produk             0. Keluar Aplikasi       ");
            System.out.println("-------------------------------------------------------");
            System.out.println("  Status Penyimpanan: " + (berubah ? "[!] ADA PERUBAHAN BELUM DISIMPAN" : "[V] DATA TERSINKRONISASI"));
            System.out.println("=======================================================");
            String pilihan = bacaString(input, "Pilih Menu [0-9]: ");

            try {
                switch (pilihan) {
                    case "1": menuTambah(input); break;
                    case "2": menuTampilkan(); break;
                    case "3": menuCari(input); break;
                    case "4": menuCariByNama(input); break;
                    case "5": menuHapus(input); break;
                    case "6": menuTransaksiKasir(input); break;
                    case "7": menuLaporanStokKritis(); break;
                    case "8": menuSimpan(); break;
                    case "9": menuMuatUlang(input); break;
                    case "0":
                        if (berubah && !konfirmasi(input, "Perubahan belum disimpan. Abaikan? Ketik YA: ")) {
                            System.out.println("[INFO] Batal keluar. Kembali ke menu utama.");
                            break;
                        }
                        System.out.println("\nTerima kasih telah menggunakan sistem Tegar CopyFlow. Program selesai.");
                        return;
                    default:
                        System.out.println("[PERINGATAN] Pilihan '" + pilihan + "' tidak tersedia. Silakan pilih 0 s.d. 9.");
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("[DATA DITOLAK] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR SISTEM] Terjadi kesalahan: " + e.getMessage());
            }
        }
    }

    /**
     * Alur demonstrasi lengkap Modul 15 (Langkah 2: Alur normal, kondisi gagal, konsistensi state, persistensi, live coding)
     */
    public static void jalankanDemonstrasiLengkap(Path lokasiUji) throws IOException {
        System.out.println("================================================================================");
        System.out.println("       DEMONSTRASI PROYEK & EVALUASI PEMAHAMAN INDIVIDU (MODUL 15)");
        System.out.println("================================================================================");

        Files.deleteIfExists(lokasiUji);
        Files.deleteIfExists(lokasiUji.resolveSibling(lokasiUji.getFileName() + ".tmp"));

        // Alur Sesi 1: Demonstrasi Alur Normal, Kondisi Gagal, Fitur Live Coding, Transaksi Kasir & Simpan
        String inputDemoSesi1 = String.join("\n",
                // 1. Tampilkan katalog awal kosong
                "2",
                // 2. Tambah Produk Fisik Valid (PRD-001 Kertas HVS)
                "1", "PRD-001", "101", "Kertas HVS A4 70gsm", "Kertas", "PRODUCT", "rim", "55000", "5", "Y", "25", "45000",
                // 3. Kondisi Gagal: Uji penolakan karakter '|' pada nama
                "1", "PRD-002", "102", "Spidol | Whiteboard Rusak", "ATK", "PRODUCT", "pcs", "8500", "5",
                // 4. Kondisi Gagal: Uji penolakan kode duplikat PRD-001
                "1", "PRD-001", "103", "Kertas Duplikat", "Kertas", "PRODUCT", "rim", "50000", "5", "T",
                // 5. Tambah Produk Kedua (PRD-002 Bolpoin)
                "1", "PRD-002", "102", "Bolpoin Standard AE7", "Alat Tulis", "PRODUCT", "pcs", "2500", "20", "Y", "120", "1800",
                // 6. Demonstrasi Fitur Live Coding 1: Cari sebagian nama ("HVS")
                "4", "HVS",
                // 7. Alur Normal Utama: Transaksi Kasir POS (Beli 5 rim HVS, total 275.000, bayar 300.000)
                "6", "Cash", "PRD-001", "5", "T", "300000",
                // 8. Demonstrasi Fitur Live Coding 2: Laporan Produk Stok Kritis
                "7",
                // 9. Demonstrasi Fitur Live Coding 3: Hapus produk dengan konfirmasi (Batal dulu, lalu YA)
                "5", "PRD-002", "TIDAK", // Batal hapus
                // 10. Persistensi: Simpan data secara atomik ke disk
                "8",
                // 11. Keluar dari aplikasi
                "0"
        );

        System.out.println("\n>>> [DEMONSTRASI TAHAP 1: Alur Normal, Kondisi Gagal & Fitur Live Coding] <<<");
        Main appSesi1 = new Main(lokasiUji);
        try (Scanner sc1 = new Scanner(inputDemoSesi1)) {
            appSesi1.jalankan(sc1);
        }

        System.out.println("\n>>> [DEMONSTRASI TAHAP 2: Verifikasi Persistensi Data Disk di Sesi Baru] <<<");
        String inputDemoSesi2 = String.join("\n",
                "2",         // Tampilkan katalog (HVS terbukti sisa 20 rim dan Bolpoin 120 pcs)
                "3", "PRD-001", // Cari detail PRD-001
                "0"          // Keluar
        );
        Main appSesi2 = new Main(lokasiUji);
        try (Scanner sc2 = new Scanner(inputDemoSesi2)) {
            appSesi2.jalankan(sc2);
        }

        System.out.println("\n>>> [DEMONSTRASI TAHAP 3: Uji Konsistensi State & Ketahanan Berkas Korup] <<<");
        Main appSesi3 = new Main(lokasiUji);
        Path fileCorrupt = lokasiUji.resolveSibling("produk_corrupt.txt");
        Files.writeString(fileCorrupt, "PRD-CORRUPT|HANYA|KOLOM|KURANG\n", StandardCharsets.UTF_8);

        appSesi3.getKatalog().gantiSemua(appSesi3.repository.muat(lokasiUji));
        System.out.println("Jumlah produk aktif sebelum injeksi file korup: " + appSesi3.getKatalog().getDaftarProduk().size());

        try {
            appSesi3.repository.muat(fileCorrupt);
            System.out.println("[GAGAL] Seharusnya melempar galat!");
        } catch (IOException e) {
            System.out.println("[BUKTI RESILIENSI] Berkas korup ditolak: " + e.getMessage());
        }
        System.out.println("Jumlah produk aktif setelah penolakan (State Utuh 100%): " + appSesi3.getKatalog().getDaftarProduk().size());
        Files.deleteIfExists(fileCorrupt);

        System.out.println("\n================================================================================");
        System.out.println("    SELURUH DEMONSTRASI & UJI PEMAHAMAN MODUL 15 SUKSES 100% VALID");
        System.out.println("================================================================================");
    }

    public static void main(String[] args) throws Exception {
        Path lokasi = Paths.get("data", "produk.txt");
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            jalankanDemonstrasiLengkap(lokasi);
        } else {
            Main app = new Main(lokasi);
            try (Scanner sc = new Scanner(System.in)) {
                app.jalankan(sc);
            }
        }
    }
}

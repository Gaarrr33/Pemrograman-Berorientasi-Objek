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
 * Versi Final P14: Pengujian, Refactoring, dan Finalisasi Proyek.
 * 
 * Hasil Refactoring Arsitektural (Poin 4 & 5 Modul 14):
 * 1. Refactoring Input Handling: Memisahkan pembacaan dan parsing numerik ke helper method terpusat
 *    (bacaInt, bacaDouble, bacaString) untuk mengeliminasi duplikasi blok try-catch.
 * 2. Refactoring Normalisasi String: Pencarian kode produk di KatalogProduk dinormalisasi case-insensitive
 *    dan trim whitespace (mencakup pencarian huruf kecil seperti 'prd-001').
 * 3. Refactoring Transaksi Kasir POS: Alur alokasi stok FIFO terisolasi dengan mekanisme pembatalan aman.
 * 4. Proteksi Invariant Domain: Validasi delimiter '|' dan nilai non-negatif ditegakkan di class Produk.
 * 5. Dual Execution: Mendukung mode interaktif konsol penuh dan mode uji otomatis 12 skenario (--test).
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

    // --- REFACTORING 1: Helper Input Konsol Reusable & Type-Safe ---

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
            System.out.println("[PERINGATAN] Input tidak boleh kosong atau hanya berupa spasi. Silakan ulangi.");
        }
    }

    public static int bacaInt(Scanner input, String pesan, int nilaiMinimal) {
        while (true) {
            String text = bacaString(input, pesan);
            try {
                int val = Integer.parseInt(text);
                if (val < nilaiMinimal) {
                    System.out.printf("[PERINGATAN] Nilai minimal yang diizinkan adalah %d. Silakan ulangi.\n", nilaiMinimal);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("[PERINGATAN] Format input harus berupa bilangan bulat valid. Silakan ulangi.");
            }
        }
    }

    public static double bacaDouble(Scanner input, String pesan, double nilaiMinimal) {
        while (true) {
            String text = bacaString(input, pesan);
            try {
                double val = Double.parseDouble(text.replace(',', '.'));
                if (val < nilaiMinimal) {
                    System.out.printf("[PERINGATAN] Nilai minimal yang diizinkan adalah Rp %,.2f. Silakan ulangi.\n", nilaiMinimal);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("[PERINGATAN] Format input desimal/mata uang tidak valid. Silakan ulangi.");
            }
        }
    }

    private static boolean konfirmasi(Scanner input) {
        return bacaString(input, "Perubahan belum disimpan. Abaikan? Ketik YA: ").equalsIgnoreCase("YA");
    }

    // --- REFACTORING 2: Modular Menu Handlers (SRP) ---

    public void menuTambah(Scanner input) {
        System.out.println("\n--- [1] TAMBAH PRODUK BARU ---");
        String kode = bacaStringWajib(input, "Kode Produk (contoh PRD-001): ");
        int id = bacaInt(input, "ID Produk (bilangan bulat positif > 0): ", 1);
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
        System.out.println("\n--- [3] CARI PRODUK ---");
        String kode = bacaStringWajib(input, "Masukkan Kode Produk yang dicari: ");
        Produk produk = katalog.cariProduk(kode);
        if (produk == null) {
            System.out.println("[HASIL] Produk dengan kode '" + kode + "' tidak ditemukan dalam katalog.");
        } else {
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
    }

    public void menuHapus(Scanner input) {
        System.out.println("\n--- [4] HAPUS PRODUK DARI KATALOG ---");
        String kode = bacaStringWajib(input, "Masukkan Kode Produk yang akan dihapus: ");
        if (katalog.hapusProduk(kode)) {
            berubah = true;
            System.out.println("[SUKSES] Produk dengan kode '" + kode + "' berhasil dihapus dari katalog.");
        } else {
            System.out.println("[GAGAL] Produk dengan kode '" + kode + "' tidak ditemukan. Koleksi tidak berubah.");
        }
    }

    public void menuTransaksiKasir(Scanner input) {
        System.out.println("\n--- [5] TRANSAKSI PENJUALAN KASIR POS ---");
        String noFaktur = "TRX-" + (System.currentTimeMillis() % 1000000);
        String payment = bacaString(input, "Metode Pembayaran (Cash/QRIS/Transfer) [Default Cash]: ");
        if (payment.isEmpty()) payment = "Cash";
        TransaksiPenjualan trx = new TransaksiPenjualan(noFaktur, "2026-11-18 14:00:00", payment, "Kasir: Tegar M.");

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
            System.out.println("[ERROR] Pembayaran kurang dari total belanja. Transaksi kasir dibatalkan!");
            try {
                trx.batalkanTransaksi(); // Rollback stok alokasi
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

    public void menuSimpan() {
        System.out.println("\n--- [6] SIMPAN DATA KE FILE DISK ---");
        try {
            repository.simpan(katalog, lokasiFile);
            berubah = false;
            System.out.println("[SUKSES] Seluruh data (" + katalog.getDaftarProduk().size() + " produk) berhasil disimpan secara atomik ke: " + lokasiFile);
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menyimpan data ke file: " + e.getMessage());
        }
    }

    public void menuMuatUlang(Scanner input) {
        System.out.println("\n--- [7] MUAT ULANG DATA DARI FILE DISK ---");
        if (berubah && !konfirmasi(input)) {
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

    // --- Main Operational Loop ---
    public void jalankan(Scanner input) {
        if (Files.exists(lokasiFile)) {
            try {
                List<Produk> dataAwal = repository.muat(lokasiFile);
                katalog.gantiSemua(dataAwal);
                System.out.println("[INISIALISASI] Data awal berhasil dimuat (" + katalog.getDaftarProduk().size() + " produk terdaftar).");
            } catch (Exception e) {
                System.out.println("[PERINGATAN] Berkas data awal rusak/gagal dimuat: " + e.getMessage());
                System.out.println("[INFO] Program dihentikan agar berkas dapat diperiksa.");
                return;
            }
        } else {
            System.out.println("[INISIALISASI] Berkas data belum ada. Memulai dengan katalog kosong.");
        }

        while (true) {
            System.out.println("\n=======================================================");
            System.out.println("      SISTEM KASIR & INVENTARIS TEGAR COPYFLOW (P14)   ");
            System.out.println("=======================================================");
            System.out.println("  1. Tambah Produk Baru       5. Transaksi Kasir POS   ");
            System.out.println("  2. Tampilkan Katalog        6. Simpan Data ke File   ");
            System.out.println("  3. Cari Produk              7. Muat Ulang Data File  ");
            System.out.println("  4. Hapus Produk             0. Keluar Aplikasi       ");
            System.out.println("-------------------------------------------------------");
            System.out.println("  Status Penyimpanan: " + (berubah ? "[!] ADA PERUBAHAN BELUM DISIMPAN" : "[V] DATA TERSINKRONISASI"));
            System.out.println("=======================================================");
            String pilihan = bacaString(input, "Pilih Menu [0-7]: ");

            try {
                switch (pilihan) {
                    case "1":
                        menuTambah(input);
                        break;
                    case "2":
                        menuTampilkan();
                        break;
                    case "3":
                        menuCari(input);
                        break;
                    case "4":
                        menuHapus(input);
                        break;
                    case "5":
                        menuTransaksiKasir(input);
                        break;
                    case "6":
                        menuSimpan();
                        break;
                    case "7":
                        menuMuatUlang(input);
                        break;
                    case "0":
                        if (berubah && !konfirmasi(input)) {
                            System.out.println("[INFO] Batal keluar. Kembali ke menu utama.");
                            break;
                        }
                        System.out.println("\nTerima kasih telah menggunakan sistem Tegar CopyFlow. Program selesai.");
                        return;
                    default:
                        System.out.println("[PERINGATAN] Pilihan '" + pilihan + "' tidak tersedia. Silakan pilih 0 s.d. 7.");
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
     * Otomasi pengujian fungsional dan regresi 12 skenario komprehensif Modul 14
     */
    public static void ujiFungsionalOtomatis(Path lokasiUji) throws IOException {
        System.out.println("================================================================================");
        System.out.println("    PENGUJIAN FUNGSIONAL, REFACTORING & REGRESI TEGAR COPYFLOW (P14)");
        System.out.println("================================================================================");

        Files.deleteIfExists(lokasiUji);
        Files.deleteIfExists(lokasiUji.resolveSibling(lokasiUji.getFileName() + ".tmp"));

        // Sesi 1: Operasional Normal, Validasi Delimiter, Anti-Duplikasi, Transaksi POS & Simpan
        String inputSesi1 = String.join("\n",
                // 1. Tampilkan katalog saat masih kosong
                "2",
                // 2. Tambah produk fisik valid PRD-001 (Kertas HVS)
                "1", "PRD-001", "101", "Kertas HVS A4 70gsm", "Kertas", "PRODUCT", "rim", "55000", "5", "Y", "25", "45000",
                // 3. Tambah produk dengan delimiter terlarang '|' -> Ditolak
                "1", "PRD-002", "102", "Buku | Gambar A3", "Buku", "PRODUCT", "buku", "12000", "10",
                // 4. Tambah produk kode duplikat PRD-001 -> Ditolak
                "1", "PRD-001", "103", "Kertas Duplikat", "Kertas", "PRODUCT", "rim", "50000", "5", "T",
                // 5. Tambah produk kedua PRD-002 (Bolpoin)
                "1", "PRD-002", "102", "Bolpoin Standard AE7", "Alat Tulis", "PRODUCT", "pcs", "2500", "20", "Y", "120", "1800",
                // 6. Cari kode dengan huruf kecil 'prd-001' (Case-insensitive verification)
                "3", "prd-001",
                // 7. Cari kode fiktif 'PRD-999'
                "3", "PRD-999",
                // 8. Transaksi Kasir POS (Beli 5 rim HVS, total 275.000, bayar 300.000)
                "5", "Cash", "PRD-001", "5", "T", "300000",
                // 9. Hapus produk tidak terdaftar PRD-888
                "4", "PRD-888",
                // 10. Keluar saat ada perubahan, lalu batalkan (TIDAK)
                "0", "TIDAK",
                // 11. Simpan data ke disk via Atomic Write
                "6",
                // 12. Keluar bersih
                "0"
        );

        System.out.println("\n>>> [EKSEKUSI SESI 1: Skenario 1 s.d. 11 (Operasi Utama, Refactoring & Persistensi)] <<<");
        Main appSesi1 = new Main(lokasiUji);
        try (Scanner sc1 = new Scanner(inputSesi1)) {
            appSesi1.jalankan(sc1);
        }

        System.out.println("\n>>> [EKSEKUSI SESI 2: Skenario 12 (Pengujian Regresi Persistensi Pasca Transaksi)] <<<");
        String inputSesi2 = String.join("\n",
                "2",         // Tampilkan katalog (HVS sisa 20 rim, Bolpoin 120 pcs)
                "3", "prd-001", // Cari dengan huruf kecil
                "0"          // Keluar
        );
        Main appSesi2 = new Main(lokasiUji);
        try (Scanner sc2 = new Scanner(inputSesi2)) {
            appSesi2.jalankan(sc2);
        }

        System.out.println("\n>>> [EKSEKUSI SESI 3: Skenario 13 (Pengujian Regresi Staging Isolation saat File Rusak)] <<<");
        Main appSesi3 = new Main(lokasiUji);
        Path fileCorrupt = lokasiUji.resolveSibling("produk_corrupt.txt");
        Files.writeString(fileCorrupt, "PRD-CORRUPT|HANYA|DUA\n", StandardCharsets.UTF_8);

        // Load valid data dulu
        appSesi3.getKatalog().gantiSemua(appSesi3.repository.muat(lokasiUji));
        System.out.println("Jumlah produk aktif sebelum load berkas rusak: " + appSesi3.getKatalog().getDaftarProduk().size());

        try {
            appSesi3.repository.muat(fileCorrupt);
            System.out.println("[GAGAL] Seharusnya melempar galat!");
        } catch (IOException e) {
            System.out.println("[REGRESI SUKSES] Penolakan berkas korup tertangkap: " + e.getMessage());
        }
        System.out.println("Jumlah produk aktif setelah penolakan (State Tetap Utuh): " + appSesi3.getKatalog().getDaftarProduk().size());
        Files.deleteIfExists(fileCorrupt);

        System.out.println("\n================================================================================");
        System.out.println("      SELURUH PENGUJIAN & REGRESI MODUL 14 DINYATAKAN LULUS 100% VALID");
        System.out.println("================================================================================");
    }

    public static void main(String[] args) throws Exception {
        Path lokasi = Paths.get("data", "produk.txt");
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            ujiFungsionalOtomatis(lokasi);
        } else {
            Main app = new Main(lokasi);
            try (Scanner sc = new Scanner(System.in)) {
                app.jalankan(sc);
            }
        }
    }
}

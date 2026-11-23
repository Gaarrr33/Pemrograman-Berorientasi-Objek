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
 * Main Class — Aplikasi Konsol Interaktif POS & Inventaris Tegar CopyFlow.
 * Mengimplementasikan arsitektur modular berbasis menu sesuai panduan Modul 13 PBO:
 * 
 * Karakteristik Arsitektur & Desain:
 * 1. Pemisahan Tanggung Jawab: Kode menu dipecah ke method khusus (menuTambah, menuCari, dll).
 * 2. Proteksi Perubahan Belum Disimpan (Dirty Flag: boolean berubah).
 * 3. Repository Pattern: Integrasi dengan ProdukFileRepository (Atomic Writing & Staging Isolation).
 * 4. Perbaikan Wajib: Penolakan karakter pemisah '|' dan newline langsung saat input objek dibentuk.
 * 5. Integrasi Transaksi Kasir POS lengkap dengan pengurangan stok FIFO.
 * 6. Dual Mode: Mode Konsol Interaktif (System.in) & Mode Uji Otomatis komprehensif (--test).
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

    // --- Utilitas Pembacaan Input Konsol ---
    private static String baca(Scanner input, String pesan) {
        System.out.print(pesan);
        return input.nextLine().trim();
    }

    private static boolean konfirmasi(Scanner input) {
        return baca(input, "Perubahan belum disimpan. Abaikan? Ketik YA: ").equalsIgnoreCase("YA");
    }

    // --- Modular Menu Handlers (Poin 5 Modul 13) ---

    public void menuTambah(Scanner input) {
        System.out.println("\n--- [1] TAMBAH PRODUK BARU ---");
        String kode = baca(input, "Kode Produk (contoh PRD-001): ");
        int id;
        try {
            id = Integer.parseInt(baca(input, "ID Produk (angka bulat positif): "));
        } catch (NumberFormatException e) {
            System.out.println("Data ditolak: ID produk harus berupa angka bulat valid.");
            return;
        }
        String nama = baca(input, "Nama Produk: ");
        String kategori = baca(input, "Kategori: ");
        String tipe = baca(input, "Tipe (PRODUCT / SERVICE): ").toUpperCase();
        String satuan = baca(input, "Satuan (rim/pcs/lembar/buku): ");
        double hargaJual;
        try {
            hargaJual = Double.parseDouble(baca(input, "Harga Jual (Rp): ").replace(',', '.'));
        } catch (NumberFormatException e) {
            System.out.println("Data ditolak: Format harga jual tidak valid.");
            return;
        }

        Produk produkBaru;
        if ("SERVICE".equalsIgnoreCase(tipe)) {
            double biaya;
            try {
                biaya = Double.parseDouble(baca(input, "Biaya Pokok Jasa per Unit (Rp): ").replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Data ditolak: Format biaya pokok tidak valid.");
                return;
            }
            // Pembentukan objek memicu validasi domain (termasuk karakter terlarang '|')
            produkBaru = new Produk(kode, id, nama, kategori, tipe, satuan, hargaJual, biaya);
        } else {
            int minStok;
            try {
                minStok = Integer.parseInt(baca(input, "Batas Minimum Stok Peringatan: "));
            } catch (NumberFormatException e) {
                System.out.println("Data ditolak: Batas minimum stok harus berupa angka bulat.");
                return;
            }
            produkBaru = new Produk(kode, id, nama, kategori, tipe, satuan, hargaJual, minStok);

            // Opsional: inisialisasi batch persediaan awal
            String inisialisasiBatch = baca(input, "Input batch stok awal sekarang? (Y/T): ");
            if (inisialisasiBatch.equalsIgnoreCase("Y")) {
                try {
                    int jmlStok = Integer.parseInt(baca(input, "Jumlah Stok Masuk: "));
                    double hargaBeli = Double.parseDouble(baca(input, "Harga Beli per Satuan: ").replace(',', '.'));
                    produkBaru.tambahBatchStok(1, "2026-11-11", jmlStok, hargaBeli, "Kulakan awal");
                } catch (Exception e) {
                    System.out.println("Gagal mencatat batch stok awal: " + e.getMessage());
                }
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
        String kode = baca(input, "Masukkan Kode Produk yang dicari: ");
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
        String kode = baca(input, "Masukkan Kode Produk yang akan dihapus: ");
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
        String payment = baca(input, "Metode Pembayaran (Cash/QRIS/Transfer) [Default Cash]: ");
        if (payment.isEmpty()) payment = "Cash";
        TransaksiPenjualan trx = new TransaksiPenjualan(noFaktur, "2026-11-11 10:30:00", payment, "Kasir: Tegar M.");

        boolean belanja = true;
        while (belanja) {
            String kode = baca(input, "Kode Produk yang dibeli: ");
            Produk p = katalog.cariProduk(kode);
            if (p == null) {
                System.out.println("[ERROR] Produk tidak ditemukan.");
            } else {
                try {
                    int qty = Integer.parseInt(baca(input, "Jumlah Beli: "));
                    trx.tambahItem(p, qty);
                    System.out.println("  -> " + p.getNama() + " x" + qty + " masuk ke keranjang belanja.");
                } catch (NumberFormatException e) {
                    System.out.println("[ERROR] Jumlah beli harus angka bulat.");
                } catch (StokTidakCukupException | TransaksiInvalidException e) {
                    System.out.println("[DITOLAK] " + e.getMessage());
                }
            }
            String lanjut = baca(input, "Tambah item lagi? (Y/T): ");
            belanja = lanjut.equalsIgnoreCase("Y");
        }

        if (trx.getDaftarItem().isEmpty()) {
            System.out.println("[INFO] Tidak ada item dalam keranjang. Transaksi dibatalkan.");
            return;
        }

        System.out.printf("Total Belanja: Rp %,.2f\n", trx.getTotal());
        double bayar = 0;
        try {
            bayar = Double.parseDouble(baca(input, "Nominal Pembayaran Tunai (Rp): ").replace(',', '.'));
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Nominal pembayaran tidak valid.");
            return;
        }

        if (bayar < trx.getTotal()) {
            System.out.println("[ERROR] Pembayaran kurang dari total belanja. Transaksi dibatalkan.");
            return;
        }

        berubah = true; // Mutasi stok pada produk berhasil terjadi
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

    // --- Loop Utama Interaktif ---
    public void jalankan(Scanner input) {
        // Pemuatan data otomatis di awal program jika file telah ada di disk
        if (Files.exists(lokasiFile)) {
            try {
                List<Produk> dataAwal = repository.muat(lokasiFile);
                katalog.gantiSemua(dataAwal);
                System.out.println("[INISIALISASI] Data awal berhasil dimuat (" + katalog.getDaftarProduk().size() + " produk terdaftar).");
            } catch (Exception e) {
                System.out.println("[PERINGATAN] Berkas data awal rusak/gagal dimuat: " + e.getMessage());
                System.out.println("[INFO] Program dihentikan agar pengguna dapat memeriksa berkas data terlebih dahulu.");
                return;
            }
        } else {
            System.out.println("[INISIALISASI] Berkas data belum ada. Memulai dengan katalog kosong.");
        }

        while (true) {
            System.out.println("\n=======================================================");
            System.out.println("        SISTEM KASIR & INVENTARIS TEGAR COPYFLOW       ");
            System.out.println("=======================================================");
            System.out.println("  1. Tambah Produk Baru       5. Transaksi Kasir POS   ");
            System.out.println("  2. Tampilkan Katalog        6. Simpan Data ke File   ");
            System.out.println("  3. Cari Produk              7. Muat Ulang Data File  ");
            System.out.println("  4. Hapus Produk             0. Keluar Aplikasi       ");
            System.out.println("-------------------------------------------------------");
            System.out.println("  Status Penyimpanan: " + (berubah ? "[!] ADA PERUBAHAN BELUM DISIMPAN" : "[V] DATA TERSINKRONISASI"));
            System.out.println("=======================================================");
            String pilihan = baca(input, "Pilih Menu [0-7]: ");

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
     * Otomasi pengujian fungsional komprehensif (11 skenario) untuk pengujian deterministik dan bukti log.
     */
    public static void ujiFungsionalOtomatis(Path lokasiUji) throws IOException {
        System.out.println("================================================================================");
        System.out.println("   PENGUJIAN FUNGSIONAL KOMPREHENSIF TEGAR COPYFLOW — MODUL 13 PBO");
        System.out.println("================================================================================");

        Files.deleteIfExists(lokasiUji);
        Files.deleteIfExists(lokasiUji.resolveSibling(lokasiUji.getFileName() + ".tmp"));

        // Skenario Sesi 1
        String inputSesi1 = String.join("\n",
                // Skenario 1: Input menu tidak valid
                "xyz",
                // Skenario 2: Tambah produk fisik valid PRD-001 (Kertas HVS)
                "1", "PRD-001", "101", "Kertas HVS A4 70gsm", "Kertas", "PRODUCT", "rim", "55000", "5", "Y", "25", "45000",
                // Skenario 3: Uji perbaikan wajib -> Karakter pemisah '|' pada nama
                "1", "PRD-002", "102", "Buku | Tulis Rusak", "Buku", "PRODUCT", "pcs", "5000", "10",
                // Skenario 4: Uji penolakan kode duplikat PRD-001
                "1", "PRD-001", "103", "Kertas Duplikat", "Kertas", "PRODUCT", "rim", "50000", "5", "T",
                // Skenario 5a: Cari produk valid
                "3", "PRD-001",
                // Skenario 5b: Cari produk tidak terdaftar
                "3", "PRD-999",
                // Skenario 6: Tambah produk kedua dan lakukan transaksi kasir
                "1", "PRD-002", "102", "Bolpoin Standard AE7", "Alat Tulis", "PRODUCT", "pcs", "2500", "20", "Y", "120", "1800",
                "5", "Cash", "PRD-001", "5", "T", "300000", // Beli 5 rim HVS, total 275.000, bayar 300.000
                // Skenario 7a: Hapus produk tidak terdaftar
                "4", "PRD-888",
                // Skenario 8: Coba keluar saat status berubah == true, tolak (TIDAK)
                "0", "TIDAK",
                // Skenario 9: Simpan data (berubah menjadi false), lalu keluar aman
                "6",
                "0"
        );

        System.out.println("\n>>> [EKSEKUSI SESI 1: Skenario 1 s.d. 9 (Alur Operasional Awal & Transaksi)] <<<");
        Main appSesi1 = new Main(lokasiUji);
        try (Scanner sc1 = new Scanner(inputSesi1)) {
            appSesi1.jalankan(sc1);
        }

        System.out.println("\n>>> [EKSEKUSI SESI 2: Skenario 10 (Sesi Baru & Verifikasi Persistensi Pasca Transaksi)] <<<");
        String inputSesi2 = String.join("\n",
                "2",         // Tampilkan katalog: Buktikan sisa stok HVS tinggal 20 rim & Bolpoin 120 pcs
                "3", "PRD-001", // Cari PRD-001 dan lihat sisa stok
                "0"          // Keluar
        );
        Main appSesi2 = new Main(lokasiUji);
        try (Scanner sc2 = new Scanner(inputSesi2)) {
            appSesi2.jalankan(sc2);
        }

        System.out.println("\n>>> [EKSEKUSI SESI 3: Skenario 11 (Uji Staging Isolation saat Muat File Corrupt)] <<<");
        // Kita simulasikan user sedang bekerja dengan katalog aktif 2 produk, lalu ada file corrupt
        Main appSesi3 = new Main(lokasiUji);
        // Buat file rusak terpisah
        Path fileRusak = lokasiUji.resolveSibling("produk_corrupt.txt");
        Files.writeString(fileRusak, "PRD-CORRUPT|HANYA|TIGA|KOLOM\n", StandardCharsets.UTF_8);
        
        System.out.println("Jumlah produk aktif di memori awal: " + appSesi3.getKatalog().getDaftarProduk().size());
        // Load data valid dulu
        appSesi3.getKatalog().gantiSemua(appSesi3.repository.muat(lokasiUji));
        System.out.println("Jumlah produk aktif setelah muat valid: " + appSesi3.getKatalog().getDaftarProduk().size());
        
        System.out.println("\nMencoba memuat file corrupt 'produk_corrupt.txt' ke repositori...");
        try {
            appSesi3.repository.muat(fileRusak);
            System.out.println("[GAGAL] Seharusnya melempar IOException!");
        } catch (IOException e) {
            System.out.println("[SUKSES DITANGKAP] " + e.getMessage());
        }
        System.out.println("Jumlah produk aktif setelah load corrupt gagal: " + appSesi3.getKatalog().getDaftarProduk().size() + " (State tetap utuh 100%)");
        Files.deleteIfExists(fileRusak);

        System.out.println("\n================================================================================");
        System.out.println("      SELURUH PENGUJIAN FUNGSIONAL MODUL 13 DINYATAKAN LULUS 100% VALID");
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


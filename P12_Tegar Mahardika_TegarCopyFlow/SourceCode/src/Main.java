import exception.StokTidakCukupException;
import exception.TransaksiInvalidException;
import model.BatchStok;
import model.KatalogProduk;
import model.Produk;
import model.TransaksiPenjualan;
import repository.ProdukFileRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main Class untuk Pengujian Praktikum PBO Modul 12.
 * Topik: Penyimpanan dan Pembacaan Data ke File (Persistensi Data & NIO.Files).
 * Proyek: Tegar CopyFlow — Pencatatan Barang Toko & Fotokopi.
 * 
 * Pengujian komprehensif meliputi:
 * 1. Penyimpanan koleksi domain ke file teks berbasis format pemisah '|' (Atomic Writing).
 * 2. Pembacaan dan rekonstruksi objek secara presisi pada sesi runtime baru.
 * 3. Validasi ketat saat pemuatan: penanganan file tidak ada, format kolom salah, angka invalid, dan kode duplikat.
 * 4. Pembuktian integritas dan konsistensi state: kegagalan pemuatan tidak menghapus koleksi aktif.
 * 5. Integrasi transaksi kasir POS menggunakan objek produk hasil pembacaan file.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==========================================================================================");
        System.out.println("               PRAKTIKUM PEMROGRAMAN BERORIENTASI OBJEK — MODUL 12                        ");
        System.out.println("                     PENYIMPANAN DAN PEMBACAAN DATA KE FILE                               ");
        System.out.println("                    SISTEM TEGAR COPYFLOW (TOKO & FOTOKOPI)                               ");
        System.out.println("==========================================================================================\n");

        ProdukFileRepository repository = new ProdukFileRepository();
        Path fileProdukUtama = Paths.get("data", "produk.txt");

        // ----------------------------------------------------------------------------------------
        // TAHAP 1: Inisialisasi Master Data Katalog Awal
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 1] INISIALISASI MASTER DATA PRODUK & JASA PADA MEMORI");
        KatalogProduk katalogAwal = new KatalogProduk();

        Produk p1 = new Produk("PRD-001", 1, "Kertas HVS A4 75gr PaperOne", "Kertas", "PRODUCT", "rim", 55000, 5);
        Produk p2 = new Produk("PRD-002", 2, "Pulpen Standard AE7 Hitam", "Alat Tulis", "PRODUCT", "pcs", 3500, 15);
        Produk p3 = new Produk("PRD-003", 3, "Spidol Snowman Boardmarker", "Alat Tulis", "PRODUCT", "pcs", 9000, 5);
        Produk p4 = new Produk("SRV-001", 4, "Jasa Fotokopi Bolak-Balik", "Fotokopi", "SERVICE", "lbr", 350, 150.0);
        Produk p5 = new Produk("SRV-002", 5, "Jasa Jilid Lakban Mika", "Jilid", "SERVICE", "buku", 5000, 1500.0);

        katalogAwal.tambahProduk(p1);
        katalogAwal.tambahProduk(p2);
        katalogAwal.tambahProduk(p3);
        katalogAwal.tambahProduk(p4);
        katalogAwal.tambahProduk(p5);

        System.out.println("Master produk berhasil didaftarkan ke memori (Total: " + katalogAwal.getJumlahProduk() + " item).");
        katalogAwal.tampilkanKatalog();
        System.out.println();

        // ----------------------------------------------------------------------------------------
        // TAHAP 2: Menyimpan Koleksi Katalog ke File Teks (Atomic Writing)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 2] PERSISTENSI DATA: MENYIMPAN KATALOG KE FILE TEKS (data/produk.txt)");
        try {
            repository.simpan(katalogAwal, fileProdukUtama);
            System.out.println("STATUS: BERHASIL! Data berhasil disimpan secara atomik ke " + fileProdukUtama.toAbsolutePath());
            System.out.println("Isi File Hasil Penyimpanan (Format: kode|id|nama|kategori|tipe|satuan|hargaJual|minStok|biayaPerUnit|aktif):");
            List<String> barisTersimpan = Files.readAllLines(fileProdukUtama, StandardCharsets.UTF_8);
            for (String baris : barisTersimpan) {
                System.out.println("  " + baris);
            }
        } catch (IOException e) {
            System.err.println("Gagal menyimpan file: " + e.getMessage());
        }
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // TAHAP 3: Simulasi Sesi Baru — Membaca File & Merekonstruksi Objek Produk
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 3] MEMBACA FILE PADA SESI BARU (REKONSTRUKSI KOLEKSI PRODUK)");
        KatalogProduk katalogSesiBaru = new KatalogProduk();
        try {
            System.out.println("Memuat data dari file " + fileProdukUtama + "...");
            List<Produk> hasilMuat = repository.muat(fileProdukUtama);
            katalogSesiBaru.gantiSemua(hasilMuat);

            System.out.println("STATUS: BERHASIL! Berhasil merekonstruksi " + katalogSesiBaru.getJumlahProduk() + " objek produk.");
            katalogSesiBaru.tampilkanKatalog();
        } catch (IOException e) {
            System.err.println("Gagal memuat file: " + e.getMessage());
        }
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // TAHAP 4: Pengujian Ketahanan File & Penanganan Exception (Defensive Testing)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 4] PENGUJIAN DEFENSIVE TESTING & VALIDASI PEMBACAAN FILE");

        // Uji 1: File Tidak Ditemukan
        System.out.println("[Uji 1] Membaca file yang tidak ada (data/file_fiktif.txt)");
        try {
            repository.muat(Paths.get("data", "file_fiktif.txt"));
            System.out.println("  STATUS: GAGAL (Seharusnya melempar IOException)");
        } catch (IOException e) {
            System.out.println("  STATUS: BERHASIL DITANGANI!");
            System.out.println("  Pesan Kesalahan: \"" + e.getMessage() + "\"");
        }

        // Uji 2: Format Kolom Salah (Jumlah kolom tidak sama dengan 10)
        System.out.println("\n[Uji 2] Membaca file dengan format kolom tidak lengkap (hanya 3 kolom)");
        Path fileFormatSalah = Paths.get("data", "produk_format_salah.txt");
        try {
            Files.write(fileFormatSalah, List.of("PRD-001|1|Kertas HVS"), StandardCharsets.UTF_8);
            repository.muat(fileFormatSalah);
            System.out.println("  STATUS: GAGAL (Seharusnya melempar IOException)");
        } catch (IOException e) {
            System.out.println("  STATUS: BERHASIL DITANGANI!");
            System.out.println("  Pesan Kesalahan: \"" + e.getMessage() + "\"");
        }

        // Uji 3: Tipe Data Numerik Tidak Valid (Huruf pada kolom angka)
        System.out.println("\n[Uji 3] Membaca file dengan data angka rusak (\"limapuluhribu\" pada kolom harga)");
        Path fileAngkaRusak = Paths.get("data", "produk_angka_rusak.txt");
        try {
            String barisRusak = "PRD-001|1|Kertas HVS A4|Kertas|PRODUCT|rim|limapuluhribu|5|0.0|true";
            Files.write(fileAngkaRusak, List.of(barisRusak), StandardCharsets.UTF_8);
            repository.muat(fileAngkaRusak);
            System.out.println("  STATUS: GAGAL (Seharusnya melempar IOException)");
        } catch (IOException e) {
            System.out.println("  STATUS: BERHASIL DITANGANI!");
            System.out.println("  Pesan Kesalahan: \"" + e.getMessage() + "\"");
        }

        // Uji 4: Kode Produk Duplikat di Dalam File
        System.out.println("\n[Uji 4] Membaca file yang memuat kode produk duplikat (PRD-001 dua kali)");
        Path fileDuplikat = Paths.get("data", "produk_duplikat.txt");
        try {
            List<String> barisDuplikat = List.of(
                    "PRD-001|1|Kertas HVS A4|Kertas|PRODUCT|rim|55000.0|5|0.0|true",
                    "PRD-001|2|Kertas Fotokopi Buram|Kertas|PRODUCT|rim|35000.0|2|0.0|true"
            );
            Files.write(fileDuplikat, barisDuplikat, StandardCharsets.UTF_8);
            repository.muat(fileDuplikat);
            System.out.println("  STATUS: GAGAL (Seharusnya melempar IOException)");
        } catch (IOException e) {
            System.out.println("  STATUS: BERHASIL DITANGANI!");
            System.out.println("  Pesan Kesalahan: \"" + e.getMessage() + "\"");
        }

        // Uji 5: Pelanggaran Invarian Domain Bisnis (Harga Jual Negatif)
        System.out.println("\n[Uji 5] Membaca file dengan harga jual negatif (-15000.0)");
        Path fileHargaNegatif = Paths.get("data", "produk_harga_negatif.txt");
        try {
            String barisMinus = "PRD-009|9|Penggaris Besi|Alat Tulis|PRODUCT|pcs|-15000.0|5|0.0|true";
            Files.write(fileHargaNegatif, List.of(barisMinus), StandardCharsets.UTF_8);
            repository.muat(fileHargaNegatif);
            System.out.println("  STATUS: GAGAL (Seharusnya melempar IOException)");
        } catch (IOException e) {
            System.out.println("  STATUS: BERHASIL DITANGANI!");
            System.out.println("  Pesan Kesalahan: \"" + e.getMessage() + "\"");
        }

        // Uji 6: Membaca File Kosong
        System.out.println("\n[Uji 6] Membaca file kosong (data/produk_kosong.txt)");
        Path fileKosong = Paths.get("data", "produk_kosong.txt");
        try {
            Files.write(fileKosong, List.of(), StandardCharsets.UTF_8);
            List<Produk> hasilKosong = repository.muat(fileKosong);
            System.out.println("  STATUS: BERHASIL! File kosong terbaca sebagai koleksi berukuran: " + hasilKosong.size());
        } catch (IOException e) {
            System.out.println("  STATUS: GAGAL -> " + e.getMessage());
        }
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // TAHAP 5: Pembuktian Konsistensi State (Atomic Replacement / Staging Protection)
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 5] PEMBUKTIAN KONSISTENSI STATE (ATOMIC REPLACEMENT)");
        System.out.println("Menyiapkan katalog aktif dengan 2 produk terdaftar:");
        KatalogProduk katalogProteksi = new KatalogProduk();
        katalogProteksi.tambahProduk(p1);
        katalogProteksi.tambahProduk(p2);
        katalogProteksi.tampilkanKatalog();

        System.out.println("Mencoba memuat file rusak (produk_format_salah.txt) ke dalam katalog aktif...");
        try {
            List<Produk> dataBaru = repository.muat(fileFormatSalah);
            katalogProteksi.gantiSemua(dataBaru);
            System.out.println("GAGAL: Data rusak berhasil masuk ke katalog!");
        } catch (IOException e) {
            System.out.println("Exception tertangkap saat pemuatan: \"" + e.getMessage() + "\"");
        }

        System.out.println("\nState Katalog Aktif Setelah Pemuatan Gagal:");
        katalogProteksi.tampilkanKatalog();
        System.out.println("PEMBUKTIAN: Jumlah produk tetap " + katalogProteksi.getJumlahProduk() + " item.");
        System.out.println("Koleksi aktif di memori TIDAK KORUP dan data awal tetap utuh (Staging terbukti berhasil).");
        System.out.println("------------------------------------------------------------------------------------------\n");

        // ----------------------------------------------------------------------------------------
        // TAHAP 6: Integrasi Transaksi Kasir Menggunakan Objek Hasil Persistensi File
        // ----------------------------------------------------------------------------------------
        System.out.println(">>> [TAHAP 6] INTEGRASI TRANSAKSI KASIR POS DENGAN PRODUK DARI FILE");
        try {
            // Ambil produk dari katalog yang dimuat dari file
            Produk hvs = katalogSesiBaru.cariProduk("PRD-001");
            Produk pulpen = katalogSesiBaru.cariProduk("PRD-002");
            Produk fotokopi = katalogSesiBaru.cariProduk("SRV-001");

            // Beri batch kulakan masuk
            hvs.tambahBatchStok(201, "2026-11-11", 20, 46000, "Kulakan P12");
            pulpen.tambahBatchStok(202, "2026-11-11", 50, 2300, "Kulakan P12");

            TransaksiPenjualan trx = new TransaksiPenjualan(
                    "TRX-20261111-P12", "2026-11-11 11:45:00", "QRIS", "Belanja ATK Kantor");

            trx.tambahItem(hvs, 5);      // 5 rim HVS
            trx.tambahItem(pulpen, 12);  // 12 pcs Pulpen
            trx.tambahItem(fotokopi, 50);// 50 lbr fotokopi

            trx.cetakStruk();
            System.out.printf("Sisa Stok HVS: %d rim | Sisa Stok Pulpen: %d pcs\n",
                    hvs.hitungTotalStok(), pulpen.hitungTotalStok());
        } catch (StokTidakCukupException | TransaksiInvalidException e) {
            System.err.println("Transaksi kasir gagal: " + e.getMessage());
        }

        System.out.println("==========================================================================================");
        System.out.println("         SELURUH PENGUJIAN MODUL 12 SELESAI DENGAN SUKSES & VALIDITAS TERJAGA!            ");
        System.out.println("==========================================================================================");
    }
}


package repository;

import model.KatalogProduk;
import model.Produk;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Class ProdukFileRepository bertanggung jawab memisahkan logika I/O persistensi file
 * dari model bisnis sesuai prinsip Single Responsibility (SRP) dan Modul 12.
 * 
 * Fitur Unggulan Arsitektur:
 * 1. Format File Teks Terstruktur: Memisahkan 10 atribut data produk dengan pemisah pipa '|'.
 * 2. Atomic Writing (Penyimpanan Aman): Menulis ke file sementara (.tmp) terlebih dahulu
 *    sebelum mengganti file asli, mencegah kerusakan file jika program terhenti saat penulisan.
 * 3. Atomic Replacement (Pemuatan Aman): Memvalidasi seluruh baris data menggunakan
 *    koleksi sementara (staging), sehingga koleksi aktif tidak rusak jika file korup.
 */
public class ProdukFileRepository {

    /**
     * Menyimpan seluruh produk dari KatalogProduk ke file teks secara atomik.
     * 
     * @param katalog objek KatalogProduk yang berisi daftar produk
     * @param lokasi path tujuan file teks
     * @throws IOException jika terjadi kegagalan I/O atau format karakter ilegal
     */
    public void simpan(KatalogProduk katalog, Path lokasi) throws IOException {
        if (katalog == null) {
            throw new IllegalArgumentException("Katalog produk tidak boleh null.");
        }
        if (lokasi == null) {
            throw new IllegalArgumentException("Path lokasi penyimpanan tidak boleh null.");
        }

        List<String> barisList = new ArrayList<>();
        for (Produk p : katalog.getDaftarProduk()) {
            // Validasi: pastikan atribut teks tidak mengandung karakter pemisah terlarang
            validasiKarakterTerlarang(p.getKode(), "Kode");
            validasiKarakterTerlarang(p.getNama(), "Nama");
            validasiKarakterTerlarang(p.getKategori(), "Kategori");
            validasiKarakterTerlarang(p.getSatuan(), "Satuan");

            barisList.add(p.toFileLine());
        }

        // Pastikan direktori induk tersedia
        if (lokasi.getParent() != null) {
            Files.createDirectories(lokasi.getParent());
        }

        // Tulis terlebih dahulu ke file sementara (.tmp) untuk Atomic Writing
        Path tempPath = lokasi.resolveSibling(lokasi.getFileName() + ".tmp");
        Files.write(tempPath, barisList, StandardCharsets.UTF_8);

        // Timpa file target dengan file sementara secara atomik
        try {
            Files.move(tempPath, lokasi, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tempPath, lokasi, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Membaca dan merekonstruksi kumpulan objek Produk dari file teks.
     * Menerapkan validasi domain ketat dan staging untuk menjaga konsistensi state.
     * 
     * @param lokasi path file teks sumber
     * @return List<Produk> kumpulan objek produk yang telah tervalidasi
     * @throws IOException jika file tidak ditemukan, format salah, atau data invalid
     */
    public List<Produk> muat(Path lokasi) throws IOException {
        if (lokasi == null) {
            throw new IllegalArgumentException("Path lokasi pemuatan tidak boleh null.");
        }
        if (!Files.exists(lokasi)) {
            throw new IOException("File tidak ditemukan: " + lokasi.toAbsolutePath());
        }

        List<String> barisList = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
        KatalogProduk sementara = new KatalogProduk();

        for (int i = 0; i < barisList.size(); i++) {
            String baris = barisList.get(i).trim();
            if (baris.isEmpty()) {
                continue; // Lewati baris kosong jika ada
            }

            // Split dengan limit -1 agar kolom kosong di ujung tetap terdeteksi
            String[] bagian = baris.split("\\|", -1);
            if (bagian.length != 10) {
                throw new IOException(String.format(
                        "Format salah pada baris %d: Ditemukan %d kolom (diharapkan 10 kolom)",
                        (i + 1), bagian.length));
            }

            try {
                String kode = bagian[0];
                int id = Integer.parseInt(bagian[1]);
                String nama = bagian[2];
                String kategori = bagian[3];
                String tipe = bagian[4];
                String satuan = bagian[5];
                double hargaJual = Double.parseDouble(bagian[6].replace(',', '.'));
                int minStok = Integer.parseInt(bagian[7].trim());
                double biayaPerUnit = Double.parseDouble(bagian[8].replace(',', '.'));
                boolean aktif = Boolean.parseBoolean(bagian[9].trim());

                // Rekonstruksi objek Produk dan validasi integritas domain
                Produk produk = new Produk(kode, id, nama, kategori, tipe, satuan, hargaJual, minStok, biayaPerUnit, aktif);
                if (produk.isProductType() && biayaPerUnit > 0) {
                    double estimasiHpp = (hargaJual > 0) ? (0.75 * hargaJual) : 0.0;
                    produk.tambahBatchStok(1, "2026-11-11", (int) biayaPerUnit, estimasiHpp, "Saldo Terbawa dari File");
                }
                sementara.tambahProduk(produk); // Memvalidasi keunikan kode produk
            } catch (NumberFormatException e) {
                throw new IOException(String.format(
                        "Data numerik tidak valid pada baris %d: \"%s\"", (i + 1), e.getMessage()), e);
            } catch (IllegalArgumentException e) {
                throw new IOException(String.format(
                        "Data domain tidak valid pada baris %d: %s", (i + 1), e.getMessage()), e);
            }
        }

        return new ArrayList<>(sementara.getDaftarProduk());
    }

    private void validasiKarakterTerlarang(String teks, String namaKolom) {
        if (teks != null && (teks.contains("|") || teks.contains("\n") || teks.contains("\r"))) {
            throw new IllegalArgumentException("Kolom " + namaKolom + " mengandung karakter pemisah terlarang (| atau newline).");
        }
    }
}

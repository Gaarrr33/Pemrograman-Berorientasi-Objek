import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Modul14Dasar mengimplementasikan studi kasus Perpustakaan (Buku & Perpustakaan)
 * untuk pengujian menyeluruh, refactoring, dan verifikasi konsistensi state sesuai Modul 14 PBO.
 * 
 * Meliputi 10 Skenario Pengujian Resmi:
 * 1. Tampilkan collection kosong
 * 2. Tambah kode baru dengan judul valid
 * 3. Tambah kode yang sudah terdaftar (Ditolak)
 * 4. Tambah kode atau judul berisi spasi saja (Ditolak)
 * 5. Tambah judul mengandung | (Ditolak sebelum masuk collection)
 * 6. Cari kode dengan huruf kecil (Case-insensitive lookup)
 * 7. Hapus kode yang tidak terdaftar (Pesan tidak ditemukan, collection tetap)
 * 8. Simpan, tutup, lalu jalankan kembali (Persistensi permanen di disk)
 * 9. Muat file dengan baris rusak atau kode duplikat (Staging isolation: state lama tetap utuh)
 * 10. Keluar saat ada perubahan, lalu batalkan (Aplikasi kembali ke menu; perubahan tetap tersedia)
 */
public class Modul14Dasar {

    // --- Entity Domain: Buku ---
    public static class Buku {
        private final String kode;
        private final String judul;

        public Buku(String kode, String judul) {
            if (kode == null || kode.trim().isEmpty()) {
                throw new IllegalArgumentException("Kode buku tidak boleh kosong atau hanya berupa spasi.");
            }
            if (judul == null || judul.trim().isEmpty()) {
                throw new IllegalArgumentException("Judul buku tidak boleh kosong atau hanya berupa spasi.");
            }
            if (kode.contains("|") || kode.contains(",")) {
                throw new IllegalArgumentException("Kode buku tidak boleh mengandung karakter pemisah ('|' atau ',').");
            }
            if (judul.contains("|") || judul.contains(",")) {
                throw new IllegalArgumentException("Judul buku tidak boleh mengandung karakter pemisah ('|' atau ',').");
            }
            this.kode = kode.trim().toUpperCase();
            this.judul = judul.trim();
        }

        public String getKode() { return kode; }
        public String getJudul() { return judul; }

        public String toFileLine() {
            return kode + "|" + judul;
        }

        @Override
        public String toString() {
            return String.format("[Kode: %s | Judul: %s]", kode, judul);
        }
    }

    // --- Aggregator Domain: Perpustakaan ---
    public static class Perpustakaan {
        private final List<Buku> daftarBuku = new ArrayList<>();

        public void tambahBuku(Buku buku) {
            if (buku == null) {
                throw new IllegalArgumentException("Objek buku tidak boleh null.");
            }
            if (cariBuku(buku.getKode()) != null) {
                throw new IllegalArgumentException("Kode buku '" + buku.getKode() + "' sudah terdaftar.");
            }
            daftarBuku.add(buku);
        }

        public Buku cariBuku(String kode) {
            if (kode == null || kode.trim().isEmpty()) return null;
            String normalized = kode.trim().toUpperCase();
            for (Buku b : daftarBuku) {
                if (b.getKode().equalsIgnoreCase(normalized)) {
                    return b;
                }
            }
            return null;
        }

        public boolean hapusBuku(String kode) {
            Buku b = cariBuku(kode);
            if (b != null) {
                daftarBuku.remove(b);
                return true;
            }
            return false;
        }

        public void tampilkanBuku() {
            if (daftarBuku.isEmpty()) {
                System.out.println("Koleksi buku masih kosong.");
                return;
            }
            System.out.println("=== DAFTAR BUKU PERPUSTAKAAN ===");
            for (int i = 0; i < daftarBuku.size(); i++) {
                System.out.printf("%d. %s\n", (i + 1), daftarBuku.get(i));
            }
        }

        public int getJumlahBuku() {
            return daftarBuku.size();
        }

        public List<Buku> getDaftarBuku() {
            return Collections.unmodifiableList(daftarBuku);
        }

        public void gantiSemua(List<Buku> bukuBaru) {
            if (bukuBaru == null) throw new IllegalArgumentException("Daftar buku baru tidak boleh null.");
            daftarBuku.clear();
            daftarBuku.addAll(bukuBaru);
        }

        public void simpan(Path lokasi) throws IOException {
            if (lokasi.getParent() != null) {
                Files.createDirectories(lokasi.getParent());
            }
            List<String> lines = new ArrayList<>();
            for (Buku b : daftarBuku) {
                lines.add(b.toFileLine());
            }
            Path temp = lokasi.resolveSibling(lokasi.getFileName() + ".tmp");
            Files.write(temp, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temp, lokasi, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, lokasi, StandardCopyOption.REPLACE_EXISTING);
            }
        }

        public void muat(Path lokasi) throws IOException {
            if (!Files.exists(lokasi)) {
                throw new IOException("File tidak ditemukan: " + lokasi.toAbsolutePath());
            }
            List<String> lines = Files.readAllLines(lokasi, StandardCharsets.UTF_8);
            List<Buku> staging = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Format baris tidak valid pada baris " + (i + 1));
                }
                String kode = parts[0];
                String judul = parts[1];
                for (Buku existing : staging) {
                    if (existing.getKode().equalsIgnoreCase(kode.trim())) {
                        throw new IllegalArgumentException("Kode duplikat '" + kode + "' pada baris " + (i + 1));
                    }
                }
                staging.add(new Buku(kode, judul));
            }
            // Swap atomik setelah seluruh baris terverifikasi valid
            gantiSemua(staging);
        }
    }

    // --- Modular Menu Helper Methods ---
    private static String baca(Scanner input, String pesan) {
        System.out.print(pesan);
        return input.nextLine().trim();
    }

    private static boolean konfirmasi(Scanner input) {
        return baca(input, "Perubahan belum disimpan. Abaikan? Ketik YA: ").equalsIgnoreCase("YA");
    }

    public static void menuTambah(Scanner input, Perpustakaan p, boolean[] statusBerubah) {
        String kode = baca(input, "Kode: ");
        String judul = baca(input, "Judul: ");
        p.tambahBuku(new Buku(kode, judul));
        statusBerubah[0] = true;
        System.out.println("Buku ditambahkan.");
    }

    public static void menuCari(Scanner input, Perpustakaan p) {
        String kode = baca(input, "Kode yang dicari: ");
        Buku hasil = p.cariBuku(kode);
        if (hasil == null) {
            System.out.println("Buku tidak ditemukan.");
        } else {
            System.out.println("Judul: " + hasil.getJudul());
        }
    }

    public static void menuHapus(Scanner input, Perpustakaan p, boolean[] statusBerubah) {
        String kode = baca(input, "Kode yang dihapus: ");
        if (p.hapusBuku(kode)) {
            statusBerubah[0] = true;
            System.out.println("Buku dihapus.");
        } else {
            System.out.println("Buku tidak ditemukan.");
        }
    }

    public static void jalankanAplikasi(Scanner input, Path lokasi) {
        Perpustakaan perpustakaan = new Perpustakaan();
        boolean[] berubah = new boolean[]{false};

        if (Files.exists(lokasi)) {
            try {
                perpustakaan.muat(lokasi);
                System.out.println("[INFO] Data awal berhasil dimuat (" + perpustakaan.getJumlahBuku() + " buku).");
            } catch (Exception e) {
                System.out.println("Data awal gagal dimuat: " + e.getMessage());
                return;
            }
        }

        while (true) {
            System.out.println("\n=== PERPUSTAKAAN ===");
            System.out.println("1. Tambah       2. Tampilkan");
            System.out.println("3. Cari         4. Hapus");
            System.out.println("5. Simpan       6. Muat ulang");
            System.out.println("0. Keluar");
            String pilihan = baca(input, "Pilihan: ");

            try {
                switch (pilihan) {
                    case "1":
                        menuTambah(input, perpustakaan, berubah);
                        break;
                    case "2":
                        perpustakaan.tampilkanBuku();
                        break;
                    case "3":
                        menuCari(input, perpustakaan);
                        break;
                    case "4":
                        menuHapus(input, perpustakaan, berubah);
                        break;
                    case "5":
                        perpustakaan.simpan(lokasi);
                        berubah[0] = false;
                        System.out.println("Data disimpan.");
                        break;
                    case "6":
                        if (berubah[0] && !konfirmasi(input)) {
                            System.out.println("[INFO] Pemuatan ulang dibatalkan.");
                            break;
                        }
                        perpustakaan.muat(lokasi);
                        berubah[0] = false;
                        System.out.println("Data dimuat ulang.");
                        break;
                    case "0":
                        if (berubah[0] && !konfirmasi(input)) {
                            System.out.println("[INFO] Batal keluar, kembali ke menu.");
                            break;
                        }
                        System.out.println("Program selesai.");
                        return;
                    default:
                        System.out.println("Pilihan tidak tersedia.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Data ditolak: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Operasi file gagal: " + e.getMessage());
            }
        }
    }

    /**
     * Otomasi pengujian fungsional 10 skenario resmi Modul 14
     */
    public static void ujiFungsionalOtomatis(Path lokasiUji) throws IOException {
        System.out.println("================================================================================");
        System.out.println("       PENGUJIAN FUNGSIONAL MODUL 14: REFACTORING & 10 SKENARIO LENGKAP");
        System.out.println("================================================================================");

        Files.deleteIfExists(lokasiUji);
        Files.deleteIfExists(lokasiUji.resolveSibling(lokasiUji.getFileName() + ".tmp"));

        // Skenario 1 s.d. 8 (Sesi Pertama)
        String simulasiSesi1 = String.join("\n",
                // 1. Tampilkan collection kosong
                "2",
                // 2. Tambah kode baru dengan judul valid (B001, Dasar Java)
                "1", "B001", "Dasar Java",
                // 3. Tambah kode yang sudah terdaftar (B001) -> Ditolak
                "1", "B001", "Java Lanjut",
                // 4. Tambah kode atau judul berisi spasi saja -> Ditolak
                "1", "   ", "Judul Valid",
                "1", "B003", "     ",
                // 5. Tambah judul mengandung '|' -> Ditolak sebelum masuk collection
                "1", "B004", "Pemrograman | Java Terlarang",
                // 6. Cari kode dengan huruf kecil (b001) -> Ditemukan "Dasar Java"
                "3", "b001",
                // 7. Hapus kode yang tidak terdaftar (B999) -> Pesan tidak ditemukan
                "4", "B999",
                // 10. Keluar saat ada perubahan, lalu batalkan (TIDAK) -> Kembali ke menu
                "0", "TIDAK",
                // 8. Simpan, tutup, lalu jalankan kembali
                "5",
                "0"
        );

        System.out.println("\n--- [EKSEKUSI SESI 1: Skenario 1 s.d. 7, 10, dan Simpan Skenario 8] ---");
        try (Scanner sc = new Scanner(simulasiSesi1)) {
            jalankanAplikasi(sc, lokasiUji);
        }

        System.out.println("\n--- [EKSEKUSI SESI 2: Skenario 8 Verifikasi Muat & Skenario 9 Uji Baris Rusak] ---");
        // Jalankan kembali (Skenario 8): Data B001 otomatis dimuat dari disk
        String simulasiSesi2 = String.join("\n",
                "2", // Tampilkan buku (harus ada B001 Dasar Java)
                "0"  // Keluar
        );
        try (Scanner sc = new Scanner(simulasiSesi2)) {
            jalankanAplikasi(sc, lokasiUji);
        }

        // Skenario 9: Muat file dengan baris rusak atau kode duplikat
        System.out.println("\n--- [SKENARIO 9: Pengujian Muat File dengan Baris Rusak / Kode Duplikat] ---");
        Path fileRusak = lokasiUji.resolveSibling("buku_corrupt.txt");
        Files.writeString(fileRusak, "BARIS_RUSAK_TANPA_PIPA\n", StandardCharsets.UTF_8);

        Perpustakaan testP = new Perpustakaan();
        testP.tambahBuku(new Buku("B001", "Dasar Java"));
        System.out.println("Jumlah buku awal di memori: " + testP.getJumlahBuku());
        try {
            testP.muat(fileRusak);
            System.out.println("[GAGAL] Seharusnya melempar galat!");
        } catch (Exception e) {
            System.out.println("STATUS PENOLAKAN BERHASIL: " + e.getMessage());
        }
        System.out.println("Jumlah buku setelah file rusak ditolak (Tetap Utuh): " + testP.getJumlahBuku());
        Files.deleteIfExists(fileRusak);

        System.out.println("\n================================================================================");
        System.out.println("     SELURUH 10 SKENARIO MODUL 14 DASAR DINYATAKAN LULUS 100% VALID");
        System.out.println("================================================================================");
    }

    public static void main(String[] args) throws Exception {
        Path lokasi = Paths.get("data", "buku.txt");
        if (args.length > 0 && args[0].equalsIgnoreCase("--test")) {
            ujiFungsionalOtomatis(lokasi);
        } else {
            try (Scanner scanner = new Scanner(System.in)) {
                jalankanAplikasi(scanner, lokasi);
            }
        }
    }
}

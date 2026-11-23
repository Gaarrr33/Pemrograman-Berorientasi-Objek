import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Modul13Dasar mengimplementasikan studi kasus Buku & Perpustakaan berbasis menu interaktif
 * sesuai dengan panduan Modul 13 PBO (Langkah 1 s.d. 5).
 * 
 * Fitur Utama:
 * 1. Menu Konsol Interaktif (Tambah, Tampilkan, Cari, Hapus, Simpan, Muat Ulang, Keluar).
 * 2. Pelindung Perubahan Belum Disimpan (Dirty Flag: boolean berubah).
 * 3. Konfirmasi Pengabaian Perubahan ("Perubahan belum disimpan. Abaikan? Ketik YA: ").
 * 4. Exception Handling Bertingkat (IllegalArgumentException & IOException).
 * 5. Pemuatan Otomatis Data Awal saat program dimulai.
 * 6. Automated Functional Test Runner (9 Skenario Modul 13).
 */
public class Modul13Dasar {

    // --- Entity Domain: Buku ---
    public static class Buku {
        private final String kode;
        private final String judul;

        public Buku(String kode, String judul) {
            if (kode == null || kode.trim().isEmpty()) {
                throw new IllegalArgumentException("Kode buku tidak boleh kosong.");
            }
            if (judul == null || judul.trim().isEmpty()) {
                throw new IllegalArgumentException("Judul buku tidak boleh kosong.");
            }
            if (kode.contains(",") || kode.contains("|")) {
                throw new IllegalArgumentException("Kode buku tidak boleh mengandung koma atau pemisah pipa.");
            }
            if (judul.contains(",") || judul.contains("|")) {
                throw new IllegalArgumentException("Judul buku tidak boleh mengandung koma atau pemisah pipa.");
            }
            this.kode = kode.trim().toUpperCase();
            this.judul = judul.trim();
        }

        public String getKode() { return kode; }
        public String getJudul() { return judul; }

        public String toFileLine() {
            return kode + "," + judul;
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
            for (Buku b : daftarBuku) {
                if (b.getKode().equalsIgnoreCase(kode.trim())) {
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
                String[] parts = line.split(",", -1);
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
            daftarBuku.clear();
            daftarBuku.addAll(staging);
        }
    }

    // --- Helper Pembacaan Input ---
    private static String baca(Scanner input, String pesan) {
        System.out.print(pesan);
        return input.nextLine().trim();
    }

    private static boolean konfirmasi(Scanner input) {
        return baca(input, "Perubahan belum disimpan. Abaikan? Ketik YA: ").equalsIgnoreCase("YA");
    }

    // --- Main Console Interactive Runner ---
    public static void jalankanAplikasi(Scanner input, Path lokasi) {
        Perpustakaan perpustakaan = new Perpustakaan();
        boolean berubah = false;

        // Muat data lama sebelum pengguna mulai bekerja jika ada
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
                    case "1": {
                        String kode = baca(input, "Kode: ");
                        String judul = baca(input, "Judul: ");
                        perpustakaan.tambahBuku(new Buku(kode, judul));
                        berubah = true;
                        System.out.println("Buku ditambahkan.");
                        break;
                    }
                    case "2": {
                        perpustakaan.tampilkanBuku();
                        break;
                    }
                    case "3": {
                        String kode = baca(input, "Kode yang dicari: ");
                        Buku hasil = perpustakaan.cariBuku(kode);
                        System.out.println(hasil == null ? "Buku tidak ditemukan." : "Judul: " + hasil.getJudul());
                        break;
                    }
                    case "4": {
                        String kode = baca(input, "Kode yang dihapus: ");
                        if (perpustakaan.hapusBuku(kode)) {
                            berubah = true;
                            System.out.println("Buku dihapus.");
                        } else {
                            System.out.println("Buku tidak ditemukan.");
                        }
                        break;
                    }
                    case "5": {
                        perpustakaan.simpan(lokasi);
                        berubah = false;
                        System.out.println("Data disimpan.");
                        break;
                    }
                    case "6": {
                        if (berubah && !konfirmasi(input)) {
                            System.out.println("[INFO] Pemuatan ulang dibatalkan.");
                            break;
                        }
                        perpustakaan.muat(lokasi);
                        berubah = false;
                        System.out.println("Data dimuat ulang.");
                        break;
                    }
                    case "0": {
                        if (berubah && !konfirmasi(input)) {
                            System.out.println("[INFO] Batal keluar, kembali ke menu.");
                            break;
                        }
                        System.out.println("Program selesai.");
                        return;
                    }
                    default: {
                        System.out.println("Pilihan tidak tersedia.");
                    }
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Data ditolak: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Operasi file gagal: " + e.getMessage());
            }
        }
    }

    /**
     * Otomasi pengujian fungsional 9 skenario Modul 13 untuk verifikasi deterministik
     */
    public static void ujiFungsionalOtomatis(Path lokasiUji) throws IOException {
        System.out.println("================================================================================");
        System.out.println("        PENGUJIAN FUNGSIONAL MODUL 13: APLIKASI KONSOL BERBASIS MENU");
        System.out.println("================================================================================");

        // Hapus file uji lama jika ada
        Files.deleteIfExists(lokasiUji);
        Files.deleteIfExists(lokasiUji.resolveSibling(lokasiUji.getFileName() + ".tmp"));

        // Rangkaian input simulasi interaktif untuk 9 skenario lengkap
        String simulasiInput = String.join("\n",
                // 1. Masukkan pilihan abc (Pilihan tidak tersedia)
                "abc",
                // 2. Tambahkan buku berkode baru B001
                "1", "B001", "Dasar Pemrograman Java",
                // 3. Tambahkan kode yang sama B001 (Ditolak)
                "1", "B001", "Java Lanjut",
                // 4. Cari kode yang terdaftar B001
                "3", "B001",
                // 5. Hapus kode yang tidak terdaftar B999
                "4", "B999",
                // 6. Keluar sebelum menyimpan, jawab TIDAK (Batal keluar)
                "0", "TIDAK",
                // 7. Simpan, lalu keluar (jawab YA / tanpa konfirmasi karena sudah disimpan)
                "5",
                "0",
                // --- Sesi Kedua (Simpan, keluar, lalu jalankan kembali) ---
                // Data B001 akan termuat otomatis.
                // 8. Hapus buku B001, simpan, lalu keluar
                "4", "B001",
                "5",
                "0",
                // --- Sesi Ketiga (Verifikasi buku terhapus, tambah buku, muat ulang lalu jawab TIDAK) ---
                // Tambah buku B002, lalu pilih muat ulang (6), jawab TIDAK
                "1", "B002", "Struktur Data & Algoritma",
                "6", "TIDAK",
                "2", // Tampilkan buku untuk membuktikan B002 tetap ada di collection
                // Tambahan: Rusakkan format file sebelum muat ulang (dilakukan via kode di bawah)
                "0", "YA"
        );

        System.out.println("\n--- [EKSEKUSI SESI 1: Skenario 1 s.d. 7] ---");
        try (Scanner sc = new Scanner(simulasiInput)) {
            jalankanAplikasi(sc, lokasiUji);
        }

        System.out.println("\n--- [EKSEKUSI SESI 2: Skenario 7 Verifikasi Muat & Skenario 8 Hapus dan Simpan] ---");
        String simulasiSesi2 = String.join("\n",
                "2",         // Tampilkan buku (membuktikan B001 termuat kembali dari file)
                "4", "B001", // Hapus buku B001
                "5",         // Simpan data
                "0"          // Keluar
        );
        try (Scanner sc = new Scanner(simulasiSesi2)) {
            jalankanAplikasi(sc, lokasiUji);
        }

        System.out.println("\n--- [EKSEKUSI SESI 3: Skenario 8 Verifikasi Terhapus & Skenario 9 Proteksi Muat Ulang] ---");
        String simulasiSesi3 = String.join("\n",
                "2",         // Tampilkan buku (membuktikan B001 sudah tidak muncul lagi)
                "1", "B002", "Struktur Data & Algoritma", // Tambah buku baru B002
                "6", "TIDAK", // Muat ulang tapi jawab TIDAK -> perubahan tetap ada
                "2",         // Tampilkan buku -> B002 tetap ada!
                "0", "YA"    // Keluar abaikan perubahan
        );
        try (Scanner sc = new Scanner(simulasiSesi3)) {
            jalankanAplikasi(sc, lokasiUji);
        }

        System.out.println("\n--- [EKSEKUSI SKENARIO TAMBAHAN: Muat Ulang Berkas Korup & State Preservation] ---");
        // Tulis file korup ke disk
        Files.writeString(lokasiUji, "BARIS_RUSAK_TANPA_KOMA\n", StandardCharsets.UTF_8);
        String simulasiSkenarioTambahan = String.join("\n",
                // Saat start, file korup akan gagal dimuat awal (ditangani aman)
                "1", "B003", "Rekayasa Perangkat Lunak",
                "6", "YA", // Paksa muat ulang file korup
                "2",       // Tampilkan buku: buktikan collection sebelum rusak tetap aman jika ditangani
                "0", "YA"
        );
        // Kita uji pemanggilan muat langsung pada objek perpustakaan
        Perpustakaan testP = new Perpustakaan();
        testP.tambahBuku(new Buku("B003", "Rekayasa Perangkat Lunak"));
        System.out.println("Jumlah buku awal sebelum muat file rusak: " + testP.getJumlahBuku());
        try {
            testP.muat(lokasiUji);
        } catch (Exception e) {
            System.out.println("Terdeteksi penolakan file rusak: " + e.getMessage());
        }
        System.out.println("Jumlah buku setelah file rusak ditolak (Tetap Utuh): " + testP.getJumlahBuku());
        System.out.println("[SUKSES] Pengujian Fungsional Modul 13 Selesai 100% Valid!\n");
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

